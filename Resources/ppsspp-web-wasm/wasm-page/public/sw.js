/**
 * PPSSPP Web – Service Worker
 *
 * Strategy:
 *  - App shell (index.html, manifest, sw itself) → Network-first with cache fallback
 *  - WASM / JS / asset files                     → Cache-first (immutable builds)
 *  - Everything else                              → Network-only (pass-through)
 *
 * The cache is versioned; old caches are pruned on activate.
 */

const CACHE_VERSION = "ppsspp-angular-v3";

// Files that form the app shell – fetched fresh every time if online
const SHELL_FILES = [
  "./",
  "./index.html",
  "./manifest.webmanifest",
  "./ppsspp-runtime.js",
];

// Extensions considered immutable build artifacts → cache-first
const IMMUTABLE_EXTS = /\.(wasm|js|data|mem|zim|meta|png|svg|ico|webp|json|txt|css)$/i;

// ── Install ──────────────────────────────────────────────────────────────────
self.addEventListener("install", event => {
  event.waitUntil(
    caches.open(CACHE_VERSION)
      .then(cache => cache.addAll(SHELL_FILES))
      .then(() => self.skipWaiting())
  );
});

// ── Activate  ────────────────────────────────────────────────────────────────
self.addEventListener("activate", event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(
        keys
          .filter(key => key !== CACHE_VERSION)
          .map(key => caches.delete(key))
      )
    ).then(() => self.clients.claim())
  );
});

// ── Fetch ────────────────────────────────────────────────────────────────────
self.addEventListener("fetch", event => {
  const { request } = event;
  const url = new URL(request.url);

  // Only handle same-origin GET requests
  if (request.method !== "GET" || url.origin !== self.location.origin) return;

  const pathname = url.pathname;

  // App shell → network-first, fallback to cache
  const isShell = pathname.endsWith("/") ||
                  pathname.endsWith("/index.html") ||
                  pathname.endsWith("/manifest.webmanifest") ||
                  pathname.endsWith("/ppsspp-runtime.js");

  if (isShell) {
    event.respondWith(networkFirst(request));
    return;
  }

  // Immutable build artifacts → cache-first
  if (IMMUTABLE_EXTS.test(pathname)) {
    event.respondWith(cacheFirst(request));
    return;
  }

  // Everything else (serve.py API, etc.) → network only
});

// ── Strategies ───────────────────────────────────────────────────────────────

async function networkFirst(request) {
  const cache = await caches.open(CACHE_VERSION);
  try {
    const response = withCrossOriginIsolation(await fetch(request));
    if (response.ok) cache.put(request, response.clone());
    return response;
  } catch (_) {
    const cached = await cache.match(request);
    return cached ? withCrossOriginIsolation(cached) : withCrossOriginIsolation(new Response("Offline – PPSSPP Web is not cached yet.", {
      status: 503,
      headers: { "Content-Type": "text/plain" },
    }));
  }
}

async function cacheFirst(request) {
  const cache  = await caches.open(CACHE_VERSION);
  const cached = await cache.match(request);
  if (cached) return withCrossOriginIsolation(cached);
  try {
    const response = withCrossOriginIsolation(await fetch(request));
    if (response.ok) cache.put(request, response.clone());
    return response;
  } catch (err) {
    return withCrossOriginIsolation(new Response("Network error: " + err.message, {
      status: 503,
      headers: { "Content-Type": "text/plain" },
    }));
  }
}

function withCrossOriginIsolation(response) {
  if (!response || response.type === "opaque") return response;

  const headers = new Headers(response.headers);
  headers.set("Cross-Origin-Opener-Policy", "same-origin");
  headers.set("Cross-Origin-Embedder-Policy", "require-corp");
  headers.set("Cross-Origin-Resource-Policy", "same-origin");

  return new Response(response.body, {
    status: response.status,
    statusText: response.statusText,
    headers,
  });
}
