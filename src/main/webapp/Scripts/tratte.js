document.addEventListener("DOMContentLoaded", () => {
    console.log("📦 tratte.js caricato correttamente.");

    // Placeholder: aggiungi qui interazioni JS future
    // Esempio: toggle dettagli orari o percorso
    const toggleButtons = document.querySelectorAll(".toggle-dettagli");

    toggleButtons.forEach(button => {
        button.addEventListener("click", () => {
            const dettagliBox = button.closest(".tratta-box").querySelector(".dettagli");
            dettagliBox.classList.toggle("hidden");
            button.textContent = dettagliBox.classList.contains("hidden") ? "Mostra dettagli" : "Nascondi dettagli";
        });
    });
});
