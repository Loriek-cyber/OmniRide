<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!-- Sidebar -->
<nav class="sidebar" id="sidebar">
  <div class="sidebar-header">
    <h3>Dashboard Azienda</h3>
    <div class="company-name">
      <c:if test="${not empty azienda}">
        <c:out value="${azienda.nome}"/>
        <small><c:out value="${sessionScope.utente.nome} ${sessionScope.utente.cognome}"/></small>
      </c:if>
      <c:if test="${empty azienda}">
        <c:out value="${sessionScope.utente.nome} ${sessionScope.utente.cognome}"/>
      </c:if>
    </div>
  </div>

  <ul class="sidebar-nav">
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/dashboardAzienda.jsp" class="nav-link" data-page="dashboard">
        <i class="fas fa-tachometer-alt"></i> Panoramica
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" class="nav-link" data-page="gestisciTratte">
        <i class="fas fa-route"></i> Gestione Tratte
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="nav-link" data-page="addTratta">
        <i class="fas fa-plus-circle"></i> Aggiungi Tratta
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/statistiche.jsp" class="nav-link" data-page="statistiche">
        <i class="fas fa-chart-bar"></i> Statistiche
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/comunicazioni.jsp" class="nav-link" data-page="comunicazioni">
        <i class="fas fa-bullhorn"></i> Comunicazioni
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/employeeManagement.jsp" class="nav-link" data-page="employeeManagement">
        <i class="fas fa-users"></i> Gestione Dipendenti
      </a>
    </li>
  </ul>
</nav>

<!-- Overlay per mobile -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<button class="sidebar-toggle" id="sidebarToggle">☰</button>
<script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>