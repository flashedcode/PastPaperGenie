#!/usr/bin/env python3
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
import argparse
import base64
import hashlib
import socketserver
import ssl
import os
import subprocess
import struct
import threading
import time
from urllib.parse import parse_qs, urlparse
from functools import partial


SERVER_PORT = 27312
WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
OPCODE_PING = 0
OPCODE_LOGIN = 1
OPCODE_CONNECT = 2
OPCODE_DISCONNECT = 3
OPCODE_SCAN = 4
OPCODE_SCAN_COMPLETE = 5
OPCODE_CONNECT_BSSID = 6
OPCODE_CHAT = 7
LOGIN_SIZE = 1 + 6 + 128 + 9
CONNECT_SIZE = 1 + 8
CHAT_SIZE = 1 + 64


def _fixed_text(data):
    return data.split(b"\0", 1)[0].decode("utf-8", "replace")


def _mac_text(data):
    return ":".join(f"{b:02x}" for b in data)


class AdhocUser:
    next_ip = 2

    def __init__(self, handler):
        self.handler = handler
        self.ip = AdhocUser.next_ip
        AdhocUser.next_ip += 1
        if AdhocUser.next_ip > 254:
            AdhocUser.next_ip = 2
        self.mac = b"\0" * 6
        self.name = b""
        self.game = b""
        self.group = None
        self.rx = bytearray()
        self.last_recv = time.time()

    @property
    def ip_u32(self):
        return struct.unpack("<I", bytes([10, 0, 0, self.ip]))[0]

    @property
    def ip_text(self):
        return f"10.0.0.{self.ip}"

    def send(self, payload):
        self.handler.send(payload)


class AdhocLobby:
    def __init__(self):
        self.lock = threading.RLock()
        self.users = set()
        self.ip_to_mac = {}
        self.udp = {}
        self.tcp_listeners = {}
        self.next_tcp_id = 1

    def add(self, user):
        with self.lock:
            self.users.add(user)

    def remove(self, user):
        with self.lock:
            self.disconnect(user, notify=True)
            self.users.discard(user)
            self.ip_to_mac.pop(user.ip_text, None)

    def login(self, user, packet):
        user.mac = packet[1:7]
        user.name = packet[7:135]
        user.game = packet[135:144]
        self.ip_to_mac[user.ip_text] = _mac_text(user.mac)
        print(f"Adhoc WS: {_fixed_text(user.name)} logged in as {_mac_text(user.mac)} for {_fixed_text(user.game)}", flush=True)

    def connect(self, user, group):
        with self.lock:
            if user.group:
                self.disconnect(user, notify=False)
            peers = [u for u in self.users if u is not user and u.game == user.game and u.group == group]
            bssid = peers[-1].mac if peers else user.mac
            for peer in peers:
                peer.send(struct.pack("<B128s6sI", OPCODE_CONNECT, user.name, user.mac, user.ip_u32))
                user.send(struct.pack("<B128s6sI", OPCODE_CONNECT, peer.name, peer.mac, peer.ip_u32))
            user.group = group
            user.send(struct.pack("<B6s", OPCODE_CONNECT_BSSID, bssid))
            print(f"Adhoc WS: {_fixed_text(user.name)} joined {_fixed_text(group)} ({len(peers) + 1} player(s))", flush=True)

    def disconnect(self, user, notify):
        with self.lock:
            if not user.group:
                return
            old_group = user.group
            user.group = None
            if notify:
                packet = struct.pack("<BI", OPCODE_DISCONNECT, user.ip_u32)
                for peer in list(self.users):
                    if peer is not user and peer.game == user.game and peer.group == old_group:
                        peer.send(packet)

    def scan(self, user):
        with self.lock:
            seen = {}
            for peer in self.users:
                if peer.game == user.game and peer.group and peer.group not in seen:
                    seen[peer.group] = peer.mac
            for group, mac in seen.items():
                user.send(struct.pack("<B8s6s", OPCODE_SCAN, group, mac))
            user.send(bytes([OPCODE_SCAN_COMPLETE]))

    def chat(self, user, message):
        with self.lock:
            packet = struct.pack("<B64s128s", OPCODE_CHAT, message, user.name)
            for peer in self.users:
                if peer is not user and peer.game == user.game and peer.group == user.group:
                    peer.send(packet)

    def register_udp(self, mac, port, handler):
        with self.lock:
            self.udp[(mac, port)] = handler
            print(f"Adhoc WS: UDP relay registered {mac}:{port}", flush=True)

    def unregister_udp(self, mac, port, handler):
        with self.lock:
            if self.udp.get((mac, port)) is handler:
                del self.udp[(mac, port)]

    def relay_udp(self, source_mac, source_port, dst_host, dst_port, payload):
        with self.lock:
            dst_mac = self.ip_to_mac.get(dst_host)
            target = self.udp.get((dst_mac, dst_port)) if dst_mac else None
            source_host = next((ip for ip, mac in self.ip_to_mac.items() if mac == source_mac), None)
        if target and source_host:
            target.send_udp(source_host, source_port, payload)

    def register_tcp_listener(self, mac, port, handler):
        with self.lock:
            self.tcp_listeners[(mac, port)] = handler
            print(f"Adhoc WS: PTP relay listening {mac}:{port}", flush=True)

    def unregister_tcp_listener(self, mac, port, handler):
        with self.lock:
            if self.tcp_listeners.get((mac, port)) is handler:
                del self.tcp_listeners[(mac, port)]

    def connect_tcp(self, source_mac, source_port, dst_host, dst_port, connector):
        with self.lock:
            dst_mac = self.ip_to_mac.get(dst_host)
            listener = self.tcp_listeners.get((dst_mac, dst_port)) if dst_mac else None
            source_host = next((ip for ip, mac in self.ip_to_mac.items() if mac == source_mac), None)
            if not listener or not source_host:
                return 0
            conn_id = self.next_tcp_id
            self.next_tcp_id += 1
            connector.tcp_peer = listener
            connector.tcp_conn_id = conn_id
            listener.tcp_peers[conn_id] = connector
        listener.send_tcp_open(conn_id, source_host, source_port)
        print(f"Adhoc WS: PTP relay connected {source_mac}:{source_port} -> {dst_host}:{dst_port}", flush=True)
        return conn_id


class AdhocWebSocketHandler(socketserver.BaseRequestHandler):
    lobby = AdhocLobby()

    def setup(self):
        self.user = AdhocUser(self)
        self.alive = True
        self.mode = "control"
        self.udp_mac = ""
        self.udp_port = 0
        self.tcp_mac = ""
        self.tcp_port = 0
        self.tcp_dst_host = ""
        self.tcp_dst_port = 0
        self.tcp_peer = None
        self.tcp_conn_id = 0
        self.tcp_peers = {}

    def handle(self):
        if not self._handshake():
            return
        if self.mode == "udp":
            self._handle_udp()
            return
        if self.mode == "tcp-listen":
            self._handle_tcp_listen()
            return
        if self.mode == "tcp-connect":
            self._handle_tcp_connect()
            return
        self.lobby.add(self.user)
        try:
            while self.alive:
                payload = self._read_frame()
                if payload is None:
                    break
                if payload:
                    self.user.rx.extend(payload)
                    self.user.last_recv = time.time()
                    self._drain_packets()
        finally:
            self.lobby.remove(self.user)

    def _handle_udp(self):
        if not self.udp_mac or not self.udp_port:
            return
        self.lobby.register_udp(self.udp_mac, self.udp_port, self)
        try:
            while self.alive:
                payload = self._read_frame()
                if payload is None:
                    break
                if len(payload) < 7 or payload[:3] != b"PU1":
                    continue
                host_len = payload[3]
                header_len = 4 + host_len + 2
                if len(payload) < header_len:
                    continue
                dst_host = payload[4:4 + host_len].decode("ascii", "ignore")
                dst_port = struct.unpack(">H", payload[4 + host_len:header_len])[0]
                self.lobby.relay_udp(self.udp_mac, self.udp_port, dst_host, dst_port, payload[header_len:])
        finally:
            self.lobby.unregister_udp(self.udp_mac, self.udp_port, self)

    def send_udp(self, source_host, source_port, payload):
        host = source_host.encode("ascii")
        packet = b"PU1" + bytes([len(host)]) + host + struct.pack(">H", source_port) + payload
        self.send(packet)

    def _handle_tcp_listen(self):
        if not self.tcp_mac or not self.tcp_port:
            return
        self.lobby.register_tcp_listener(self.tcp_mac, self.tcp_port, self)
        try:
            while self.alive:
                payload = self._read_frame()
                if payload is None:
                    break
                if len(payload) < 8 or payload[:3] != b"PT1":
                    continue
                kind = payload[3]
                conn_id = struct.unpack(">I", payload[4:8])[0]
                peer = self.tcp_peers.get(conn_id)
                if kind == 2 and peer:
                    peer.send(payload[8:])
                elif kind == 3:
                    if peer:
                        peer.alive = False
                        peer.send(b"")
                    self.tcp_peers.pop(conn_id, None)
        finally:
            self.lobby.unregister_tcp_listener(self.tcp_mac, self.tcp_port, self)
            for peer in list(self.tcp_peers.values()):
                peer.alive = False
            self.tcp_peers.clear()

    def _handle_tcp_connect(self):
        conn_id = self.lobby.connect_tcp(self.tcp_mac, self.tcp_port, self.tcp_dst_host, self.tcp_dst_port, self)
        if not conn_id:
            self.alive = False
            return
        try:
            while self.alive:
                payload = self._read_frame()
                if payload is None:
                    break
                if self.tcp_peer:
                    self.tcp_peer.send_tcp_data(conn_id, payload)
        finally:
            if self.tcp_peer:
                self.tcp_peer.send_tcp_close(conn_id)
                self.tcp_peer.tcp_peers.pop(conn_id, None)

    def send_tcp_open(self, conn_id, source_host, source_port):
        host = source_host.encode("ascii")
        packet = b"PT1" + bytes([1]) + struct.pack(">I", conn_id) + bytes([len(host)]) + host + struct.pack(">H", source_port)
        self.send(packet)

    def send_tcp_data(self, conn_id, payload):
        self.send(b"PT1" + bytes([2]) + struct.pack(">I", conn_id) + payload)

    def send_tcp_close(self, conn_id):
        self.send(b"PT1" + bytes([3]) + struct.pack(">I", conn_id))

    def send(self, payload):
        try:
            self.request.sendall(self._frame(payload))
        except OSError:
            self.alive = False

    def _handshake(self):
        data = b""
        while b"\r\n\r\n" not in data:
            chunk = self.request.recv(4096)
            if not chunk:
                return False
            data += chunk
            if len(data) > 16384:
                return False
        lines = data.decode("latin1", "replace").split("\r\n")
        request_parts = lines[0].split(" ")
        if len(request_parts) >= 2:
            parsed = urlparse(request_parts[1])
            query = parse_qs(parsed.query)
            if parsed.path == "/udp":
                self.mode = "udp"
                self.udp_mac = query.get("mac", [""])[0].lower()
                try:
                    self.udp_port = int(query.get("port", ["0"])[0])
                except ValueError:
                    self.udp_port = 0
            elif parsed.path == "/tcp-listen":
                self.mode = "tcp-listen"
                self.tcp_mac = query.get("mac", [""])[0].lower()
                try:
                    self.tcp_port = int(query.get("port", ["0"])[0])
                except ValueError:
                    self.tcp_port = 0
            elif parsed.path == "/tcp-connect":
                self.mode = "tcp-connect"
                self.tcp_mac = query.get("mac", [""])[0].lower()
                self.tcp_dst_host = query.get("dst", [""])[0]
                try:
                    self.tcp_port = int(query.get("port", ["0"])[0])
                    self.tcp_dst_port = int(query.get("dstport", ["0"])[0])
                except ValueError:
                    self.tcp_port = 0
                    self.tcp_dst_port = 0
        print(f"Adhoc WS: {self.mode} connection from {self.client_address[0]}:{self.client_address[1]}", flush=True)
        headers = {}
        for line in lines[1:]:
            if ":" in line:
                k, v = line.split(":", 1)
                headers[k.strip().lower()] = v.strip()
        key = headers.get("sec-websocket-key")
        if not key:
            return False
        accept = base64.b64encode(hashlib.sha1((key + WS_GUID).encode("ascii")).digest()).decode("ascii")
        response = [
            "HTTP/1.1 101 Switching Protocols",
            "Upgrade: websocket",
            "Connection: Upgrade",
            f"Sec-WebSocket-Accept: {accept}",
            "Sec-WebSocket-Protocol: binary",
            "\r\n",
        ]
        self.request.sendall("\r\n".join(response).encode("ascii"))
        return True

    def _read_exact(self, size):
        data = bytearray()
        while len(data) < size:
            chunk = self.request.recv(size - len(data))
            if not chunk:
                return None
            data.extend(chunk)
        return bytes(data)

    def _read_frame(self):
        header = self._read_exact(2)
        if not header:
            return None
        opcode = header[0] & 0x0F
        masked = header[1] & 0x80
        length = header[1] & 0x7F
        if length == 126:
            length = struct.unpack(">H", self._read_exact(2))[0]
        elif length == 127:
            length = struct.unpack(">Q", self._read_exact(8))[0]
        mask = self._read_exact(4) if masked else b"\0\0\0\0"
        payload = self._read_exact(length) if length else b""
        if payload is None:
            return None
        if masked:
            payload = bytes(b ^ mask[i % 4] for i, b in enumerate(payload))
        if opcode == 8:
            return None
        if opcode == 9:
            self.request.sendall(self._frame(payload, opcode=10))
            return b""
        if opcode != 2:
            return b""
        return payload

    def _frame(self, payload, opcode=2):
        length = len(payload)
        if length < 126:
            return bytes([0x80 | opcode, length]) + payload
        if length < 65536:
            return bytes([0x80 | opcode, 126]) + struct.pack(">H", length) + payload
        return bytes([0x80 | opcode, 127]) + struct.pack(">Q", length) + payload

    def _drain_packets(self):
        user = self.user
        while user.rx:
            opcode = user.rx[0]
            if opcode == OPCODE_LOGIN:
                if len(user.rx) < LOGIN_SIZE:
                    return
                packet = bytes(user.rx[:LOGIN_SIZE])
                del user.rx[:LOGIN_SIZE]
                self.lobby.login(user, packet)
            elif opcode == OPCODE_PING:
                del user.rx[:1]
            elif opcode == OPCODE_CONNECT:
                if len(user.rx) < CONNECT_SIZE:
                    return
                group = bytes(user.rx[1:CONNECT_SIZE])
                del user.rx[:CONNECT_SIZE]
                self.lobby.connect(user, group)
            elif opcode == OPCODE_DISCONNECT:
                del user.rx[:1]
                self.lobby.disconnect(user, notify=True)
            elif opcode == OPCODE_SCAN:
                del user.rx[:1]
                self.lobby.scan(user)
            elif opcode == OPCODE_CHAT:
                if len(user.rx) < CHAT_SIZE:
                    return
                message = bytes(user.rx[1:CHAT_SIZE])
                del user.rx[:CHAT_SIZE]
                self.lobby.chat(user, message)
            else:
                print(f"Adhoc WS: invalid opcode 0x{opcode:02x}, closing client", flush=True)
                self.alive = False
                return


class ThreadingAdhocWebSocketServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    allow_reuse_address = True
    daemon_threads = True


class WasmThreadingHandler(SimpleHTTPRequestHandler):
    """
    Serves the web repo and a separate PPSSPP wasm repo together.

    Requests for / or /index.html are transparently rewritten to the Angular build
    in /wasm-page/dist/ppsspp-web. Static assets first resolve from that build,
    then from /wasm-page/public for local development. Build outputs like
    /build-wasm/... are served from the wasm repo.
    """
    PUBLIC_FILES = {
        "/favicon.ico",
        "/manifest.webmanifest",
        "/ppsspp-runtime.js",
        "/sw.js",
    }

    def __init__(self, *args, web_root=None, wasm_root=None, **kwargs):
        self.web_root = os.path.abspath(web_root)
        self.wasm_root = os.path.abspath(wasm_root)
        self.angular_root = os.path.join(self.web_root, "wasm-page", "dist", "ppsspp-web")
        super().__init__(*args, directory=self.web_root, **kwargs)

    def translate_path(self, path):
        request_path = urlparse(path).path

        if request_path in ("/", "/index.html"):
            angular_index = os.path.join(self.angular_root, "index.html")
            if os.path.isfile(angular_index):
                return self._translate_under(self.angular_root, "/index.html")
            return self._translate_under(self.web_root, "/wasm-page/src/index.html")
        angular_path = self._translate_under(self.angular_root, request_path)
        if os.path.isfile(angular_path):
            return angular_path
        public_path = self._translate_under(self.web_root, "/wasm-page/public" + request_path)
        if request_path in self.PUBLIC_FILES or request_path.startswith("/icons/"):
            return public_path
        if request_path == "/assets-manifest.txt":
            return public_path
        if request_path.startswith(("/wasm-page/", "/server/")):
            return self._translate_under(self.web_root, request_path)

        return self._translate_under(self.wasm_root, request_path)

    def _translate_under(self, root, path):
        previous = self.directory
        self.directory = root
        try:
            return super().translate_path(path)
        finally:
            self.directory = previous

    def end_headers(self):
        self.send_header("Cross-Origin-Opener-Policy", "same-origin")
        self.send_header("Cross-Origin-Embedder-Policy", "require-corp")
        self.send_header("Cross-Origin-Resource-Policy", "same-origin")
        super().end_headers()


def generate_self_signed_cert(cert_file, key_file):
    """Generate a self-signed certificate using openssl."""
    subprocess.run([
        "openssl", "req", "-x509", "-newkey", "rsa:2048",
        "-keyout", key_file, "-out", cert_file,
        "-days", "365", "-nodes",
        "-subj", "/CN=localhost"
    ], check=True)
    print(f"Self-signed certificate generated: {cert_file}, {key_file}", flush=True)


def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    web_root = os.path.dirname(script_dir)
    default_wasm_root = os.path.abspath(os.path.join(web_root, "..", "ppsspp-wasm"))
    if not os.path.isdir(default_wasm_root):
        default_wasm_root = web_root

    parser = argparse.ArgumentParser(description="Serve PPSSPP wasm with pthread-compatible headers.")
    parser.add_argument("--bind", "--address", dest="bind", default="192.168.1.170")
    parser.add_argument("--port", type=int, default=8081)
    parser.add_argument("--https", action="store_true", help="Enable HTTPS (TLS)")
    parser.add_argument("--cert", default="cert.pem", help="TLS certificate file (PEM)")
    parser.add_argument("--key", default="key.pem", help="TLS private key file (PEM)")
    parser.add_argument("--web-root", default=web_root, help="ppsspp-web checkout root.")
    parser.add_argument("--wasm-root", "--dir", dest="wasm_root", default=default_wasm_root, help="ppsspp-wasm checkout root containing build-wasm/ and build-wasm-release/.")
    parser.add_argument("--adhoc-ws", action="store_true", help="Enable the browser WebSocket ad hoc lobby server.")
    parser.add_argument("--adhoc-ws-port", type=int, default=SERVER_PORT, help="WebSocket ad hoc lobby port.")
    args = parser.parse_args()

    web_root = os.path.abspath(args.web_root)
    wasm_root = os.path.abspath(args.wasm_root)
    handler = partial(WasmThreadingHandler, web_root=web_root, wasm_root=wasm_root)

    server = ThreadingHTTPServer((args.bind, args.port), handler)

    if args.https:
        if not os.path.isabs(args.cert):
            args.cert = os.path.join(script_dir, args.cert)
        if not os.path.isabs(args.key):
            args.key = os.path.join(script_dir, args.key)
        if not os.path.exists(args.cert) or not os.path.exists(args.key):
            generate_self_signed_cert(args.cert, args.key)
        ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        ctx.load_cert_chain(certfile=args.cert, keyfile=args.key)
        server.socket = ctx.wrap_socket(server.socket, server_side=True)
        scheme = "https"
    else:
        ctx = None
        scheme = "http"

    adhoc_server = None
    if args.adhoc_ws:
        adhoc_server = ThreadingAdhocWebSocketServer((args.bind, args.adhoc_ws_port), AdhocWebSocketHandler)
        if ctx:
            adhoc_server.socket = ctx.wrap_socket(adhoc_server.socket, server_side=True)
        threading.Thread(target=adhoc_server.serve_forever, name="AdhocWebSocketServer", daemon=True).start()
        print(f"Adhoc WebSocket lobby on {'wss' if ctx else 'ws'}://{args.bind}:{args.adhoc_ws_port}/", flush=True)

    print(f"Serving on {scheme}://{args.bind}:{args.port}/", flush=True)
    print(f"  web root:  {web_root}", flush=True)
    print(f"  wasm root: {wasm_root}", flush=True)
    try:
        server.serve_forever()
    finally:
        if adhoc_server:
            adhoc_server.shutdown()


if __name__ == "__main__":
    main()
