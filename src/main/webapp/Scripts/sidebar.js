
document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('userSidebar');
    const toggle = document.getElementById('sidebarToggle');
    const overlay = document.getElementById('sidebarOverlay');
    const navLinks = document.querySelectorAll('.nav-link');

    // Toggle sidebar per mobile
    if (toggle) {
        toggle.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            overlay.classList.toggle('active');
        });
    }

    // Chiudi sidebar quando si clicca sull'overlay
    if (overlay) {
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        });
    }

    // Evidenzia la pagina corrente
    const currentPath = window.location.pathname;
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });

    // Auto-evidenzia in base al data-page se disponibile
    const pageIndicator = document.body.getAttribute('data-page');
    if (pageIndicator) {
        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('data-page') === pageIndicator) {
                link.classList.add('active');
            }
        });
    }
});