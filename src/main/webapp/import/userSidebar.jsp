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
        <%--
            Questa è la side bard tutti gli elementi
        --%>
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
            <a href="${pageContext.request.contextPath}/prvUser/card" class="nav-link" data-page="wallet">
                <span class="nav-icon">▶</span>
                Portafoglio
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/visualizzaTratte" class="nav-link" data-page="search">
                <span class="nav-icon">▶</span>
                Cerca Tratte
            </a>
        </div>
        
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}/storicoPagamenti" class="nav-link" data-page="storico">
                <span class="nav-icon">💳</span>
                Storico Pagamenti
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

<!--
    Questo Elemento è per il mobile in modo che non sia tutto in una pagina
-->
<button class="sidebar-toggle" id="sidebarToggle">
    <span class="toggle-icon">☰</span>
</button>

<script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>