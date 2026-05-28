const cards = Array.from(document.querySelectorAll(".card"));

cards.forEach((c) => {
  c.addEventListener("click", () => {
    cards.forEach((c) => c.classList.remove("active"));
    c.classList.toggle("active");
  });
});

const h1 = document.querySelector("h1");
console.log(h1);
document.addEventListener("scroll", () => {
  const y = scrollY;
  h1.style.marginTop = `${200 - y}px`;
  h1.style.marginLeft = `${200 - y / 4}px`;
});