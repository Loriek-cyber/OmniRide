document.addEventListener('DOMContentLoaded', function() {
    const userSidebar = document.getElementById('userSidebar');
    const aziendaSidebar = document.getElementById('sidebar'); // Changed from aziendaSidebar to sidebar
    const toggle = document.getElementById('sidebarToggle');
    const overlay = document.getElementById('sidebarOverlay') || document.getElementById('sidebar-overlay'); // Handle both IDs
    const navLinks = document.querySelectorAll('.nav-link');

    function closeSidebar(sidebarElement) {
        if (sidebarElement && sidebarElement.classList.contains('active')) {
            sidebarElement.classList.remove('active');
            if (overlay) {
                overlay.classList.remove('active');
            }
            if (toggle) {
                toggle.style.display = 'block'; // Show the toggle button
            }
        }
    }

    function openSidebar(sidebarElement) {
        if (sidebarElement) {
            sidebarElement.classList.add('active');
            if (overlay) {
                overlay.classList.add('active');
            }
            if (toggle) {
                toggle.style.display = 'none'; // Hide the toggle button
            }
        }
    }

    // Toggle sidebar for mobile
    if (toggle) {
        toggle.addEventListener('click', function() {
            if (userSidebar && !userSidebar.classList.contains('active')) {
                openSidebar(userSidebar);
            } else if (aziendaSidebar && !aziendaSidebar.classList.contains('active')) {
                openSidebar(aziendaSidebar);
            } else {
                closeSidebar(userSidebar || aziendaSidebar);
            }
        });
    }

    // Close sidebar when clicking on the overlay
    if (overlay) {
        overlay.addEventListener('click', function() {
            closeSidebar(userSidebar || aziendaSidebar);
        });
    }

    // Close sidebar when clicking outside of it
    document.addEventListener('click', function(event) {
        const target = event.target;
        const isClickInsideSidebar = (userSidebar && userSidebar.contains(target)) || (aziendaSidebar && aziendaSidebar.contains(target));
        const isClickOnToggle = toggle && toggle.contains(target);

        if (!isClickInsideSidebar && !isClickOnToggle) {
            closeSidebar(userSidebar || aziendaSidebar);
        }
    });

    // Highlight current page
    const currentPath = window.location.pathname;
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });

    // Auto-highlight based on data-page if available
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