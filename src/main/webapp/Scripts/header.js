// Gestione dropdown utente
function toggleDropdown(event) {
    event.preventDefault();
    const dropdown = document.getElementById('userDropdown');
    dropdown.classList.toggle('show');
}

// Chiudi il dropdown quando si clicca fuori
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('userDropdown');
    const button = document.querySelector('.user-dropdown-btn');

    if (!button || !dropdown) return;

    if (!button.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// Chiudi il dropdown quando si preme ESC
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const dropdown = document.getElementById('userDropdown');
        if (dropdown) {
            dropdown.classList.remove('show');
        }
    }
});

// Gestione responsive del menu mobile (se necessario in futuro)
function initMobileMenu() {
    // Placeholder per eventuali funzionalità mobile future
    console.log('Mobile menu initialized');
}

// Inizializza quando il DOM è caricato
document.addEventListener('DOMContentLoaded', function() {
    initMobileMenu();
});

// Gestione smooth scroll per i link interni (opzionale)
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});