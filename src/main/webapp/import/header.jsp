<%--
Header principale dell'applicazione Omniride
Contiene navigazione responsive e gestione utenti
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
    model.User currentUser = (model.User) session.getAttribute("user");
    boolean userLoggedIn = (isLoggedIn != null && isLoggedIn);
    boolean isAdmin = userLoggedIn && currentUser != null && currentUser.isAdmin();

    // Get current page for active navigation
    String currentPage = request.getRequestURI();
    String contextPath = request.getContextPath();
    if (currentPage.startsWith(contextPath)) {
        currentPage = currentPage.substring(contextPath.length());
    }
%>
<header class="main-header">
    <div class="header-container">
        <div class="logo-section">
            <a href="index.jsp" class="logo-link">
                <img src="Images/logo.png" alt="Omniride Logo" class="logo-img">
                <span class="logo-text">Omniride</span>
            </a>
        </div>

        <!-- Mobile menu toggle -->
        <button class="mobile-menu-toggle" id="mobileMenuToggle" aria-label="Toggle navigation">
            <span class="hamburger-line"></span>
            <span class="hamburger-line"></span>
            <span class="hamburger-line"></span>
        </button>

        <nav class="main-navigation" id="mainNavigation">
            <div class="nav-links">
                <a href="index.jsp" class="nav-link <%= currentPage.endsWith("index.jsp") ? "active" : "" %>">
                    <i class="nav-icon">🏠</i>Home
                </a>
                <a href="products" class="nav-link <%= currentPage.contains("products") ? "active" : "" %>">
                    <i class="nav-icon">🎫</i>Prodotti
                </a>
                <a href="biglietti.jsp" class="nav-link <%= currentPage.endsWith("biglietti.jsp") ? "active" : "" %>">
                    <i class="nav-icon">🚌</i>Biglietti
                </a>
                <a href="about.jsp" class="nav-link <%= currentPage.endsWith("about.jsp") ? "active" : "" %>">
                    <i class="nav-icon">ℹ️</i>Chi Siamo
                </a>
            </div>

            <div class="user-section">
                <% if (userLoggedIn) { %>
                    <div class="user-menu">
                        <% if (isAdmin) { %>
                            <a href="admin/dashboard.jsp" class="nav-link admin-link">
                                <i class="nav-icon">⚙️</i>Admin
                            </a>
                        <% } %>
                        <a href="cart.jsp" class="nav-link cart-link">
                            <i class="nav-icon">🛒</i>Carrello
                            <span class="cart-count" id="cartCount">0</span>
                        </a>
                        <a href="orders.jsp" class="nav-link">
                            <i class="nav-icon">📋</i>Ordini
                        </a>
                        <div class="user-dropdown">
                            <button class="user-welcome" id="userDropdownToggle">
                                <i class="nav-icon">👤</i>
                                Ciao, <%= currentUser != null ? currentUser.getFirstName() : "Utente" %>!
                                <span class="dropdown-arrow">▼</span>
                            </button>
                            <div class="dropdown-menu" id="userDropdownMenu">
                                <a href="profile.jsp" class="dropdown-item">
                                    <i class="nav-icon">👤</i>Profilo
                                </a>
                                <a href="settings.jsp" class="dropdown-item">
                                    <i class="nav-icon">⚙️</i>Impostazioni
                                </a>
                                <hr class="dropdown-divider">
                                <a href="logout" class="dropdown-item logout-item">
                                    <i class="nav-icon">🚪</i>Logout
                                </a>
                            </div>
                        </div>
                    </div>
                <% } else { %>
                    <div class="auth-links">
                        <a href="login.jsp" class="nav-link login-link <%= currentPage.endsWith("login.jsp") ? "active" : "" %>">
                            <i class="nav-icon">🔑</i>Login
                        </a>
                        <a href="register.jsp" class="nav-link register-link <%= currentPage.endsWith("register.jsp") ? "active" : "" %>">
                            <i class="nav-icon">📝</i>Registrati
                        </a>
                    </div>
                <% } %>
            </div>
        </nav>
    </div>
</header>

<script>
// Mobile menu toggle functionality
document.addEventListener('DOMContentLoaded', function() {
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const mainNavigation = document.getElementById('mainNavigation');
    const userDropdownToggle = document.getElementById('userDropdownToggle');
    const userDropdownMenu = document.getElementById('userDropdownMenu');

    // Mobile menu toggle
    if (mobileMenuToggle && mainNavigation) {
        mobileMenuToggle.addEventListener('click', function() {
            mainNavigation.classList.toggle('mobile-open');
            mobileMenuToggle.classList.toggle('active');
        });
    }

    // User dropdown toggle
    if (userDropdownToggle && userDropdownMenu) {
        userDropdownToggle.addEventListener('click', function(e) {
            e.preventDefault();
            userDropdownMenu.classList.toggle('show');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!userDropdownToggle.contains(e.target) && !userDropdownMenu.contains(e.target)) {
                userDropdownMenu.classList.remove('show');
            }
        });
    }

    // Update cart count (placeholder - should be implemented with actual cart data)
    function updateCartCount() {
        // This should be replaced with actual cart count logic
        const cartCount = document.getElementById('cartCount');
        if (cartCount) {
            // Example: get count from session or localStorage
            const count = sessionStorage.getItem('cartItemCount') || '0';
            cartCount.textContent = count;
            cartCount.style.display = count === '0' ? 'none' : 'inline';
        }
    }

    updateCartCount();
});
</script>
