<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Overlay per mobile -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<!-- Sidebar -->
<div class="sidebar" id="userSidebar">
    <div class="sidebar-header">
        <h3>Area Utente</h3>
        <div class="user-name">
            <c:out value="${sessionScope.utente.nome}"/> <c:out value="${sessionScope.utente.cognome}"/>
        </div>
    </div>
    
    <nav class="sidebar-nav">
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/prvUser/dashboard" class="nav-link" data-page="dashboard">
                <span class="nav-icon">▶</span>
                Dashboard
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/biglietti.jsp" class="nav-link" data-page="biglietti">
                <span class="nav-icon">▶</span>
                I Miei Biglietti
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/prvUser/editProfile.jsp" class="nav-link" data-page="profile">
                <span class="nav-icon">▶</span>
                Profilo
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/prvUser/wallet" class="nav-link" data-page="wallet">
                <span class="nav-icon">▶</span>
                Portafoglio
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/tratte.jsp" class="nav-link" data-page="search">
                <span class="nav-icon">▶</span>
                Cerca Tratte
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/prvUser/history" class="nav-link" data-page="history">
                <span class="nav-icon">▶</span>
                Storico Viaggi
            </a>
        </div>
        
        <div class="nav-divider"></div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}//visualizzaAvvisi" class="nav-link" data-page="notifications">
                <span class="nav-icon">▶</span>
                Avvisi
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/about.jsp" class="nav-link" data-page="help">
                <span class="nav-icon">▶</span>
                Aiuto
            </a>
        </div>
        
        <div class="nav-divider"></div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/logout" class="nav-link logout-link">
                <span class="nav-icon">▶</span>
                Logout
            </a>
        </div>
    </nav>
</div>

<!-- Toggle button per mobile -->
<button class="sidebar-toggle" id="sidebarToggle">
    <span class="toggle-icon">☰</span>
</button>

<script>
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
</script>
