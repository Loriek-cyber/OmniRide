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
      <a href="${pageContext.request.contextPath}/prvAzienda/dashboardAzienda.jsp" class="nav-link active">
        Panoramica
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" class="nav-link">
        Gestione Tratte
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/flotta" class="nav-link">
        Gestione Flotta
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/report" class="nav-link">
        Report Finanziari
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/dipendenti" class="nav-link">
        Gestione Dipendenti
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/comunicazioni.jsp" class="nav-link">
        Comunicazioni
      </a>
    </li>
    <li class="nav-item">
      <a href="${pageContext.request.contextPath}/prvAzienda/impostazioni.jsp" class="nav-link">
        Impostazioni
      </a>
    </li>
  </ul>
</nav>

<!-- Overlay per mobile -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>
<button class="sidebar-toggle" id="sidebarToggle">☰</button>
<script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>