<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Gestisci Tratte - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/route-management.css">
</head>
<body data-page="gestisciTratte">
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        <div class="main-content">
            <div class="route-management-container">
                <div class="route-management-header">
                    <h1 class="route-management-title">
                        <i class="fas fa-route"></i> Gestione Tratte
                    </h1>
                    <div class="route-management-actions">
                        <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Aggiungi Tratta
                        </a>
                    </div>
                </div>
                
                <c:if test="${not empty messaggio}">
                    <div class="message success">${messaggio}</div>
                </c:if>
                <c:if test="${not empty errore}">
                    <div class="message error">${errore}</div>
                </c:if>
                
                <!-- Statistiche -->
                <div class="stats-grid">
                    <div class="stat-card routes-total">
                        <div class="stat-value">${totaleTratte != null ? totaleTratte : 0}</div>
                        <div class="stat-label">Tratte Totali</div>
                    </div>
                    <div class="stat-card routes-active">
                        <div class="stat-value">${tratteAttive != null ? tratteAttive : 0}</div>
                        <div class="stat-label">Tratte Attive</div>
                    </div>
                    <div class="stat-card routes-inactive">
                        <div class="stat-value">${(totaleTratte != null && tratteAttive != null) ? (totaleTratte - tratteAttive) : 0}</div>
                        <div class="stat-label">Tratte Inattive</div>
                    </div>
                </div>
                
                <!-- Tabella delle tratte -->
                <div class="routes-table-container">
                    <div class="routes-table-header">
                        <h2 class="routes-table-title">
                            <i class="fas fa-list"></i> Le tue Tratte
                            <span class="routes-count">${totaleTratte != null ? totaleTratte : 0} tratte</span>
                        </h2>
                    </div>
                    
                    <c:choose>
                        <c:when test="${empty tratte}">
                            <div class="empty-state">
                                <div class="empty-state-icon">
                                    <i class="fas fa-route"></i>
                                </div>
                                <h3>Nessuna Tratta Trovata</h3>
                                <p>Non hai ancora creato nessuna tratta di trasporto. Inizia creando la tua prima tratta!</p>
                                <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Crea Prima Tratta
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="routes-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nome Tratta</th>
                                        <th>Partenza</th>
                                        <th>Arrivo</th>
                                        <th>Prezzo</th>
                                        <th>Stato</th>
                                        <th>Azioni</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="tratta" items="${tratte}">
                                        <tr class="route-row">
                                            <td>
                                                <span class="id-badge">#${tratta.id}</span>
                                            </td>
                                            <td>
                                                <strong>${tratta.nome}</strong>
                                            </td>
                                            <td>
                                                <i class="fas fa-map-marker-alt" style="color: #007bff;"></i> 
                                                <c:choose>
                                                    <c:when test="${not empty tratta.partenza}">
                                                        ${tratta.partenza}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Non specificato</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <i class="fas fa-flag-checkered" style="color: #28a745;"></i> 
                                                <c:choose>
                                                    <c:when test="${not empty tratta.arrivo}">
                                                        ${tratta.arrivo}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Non specificato</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty tratta.costo}">
                                                        <span class="price-display">€<fmt:formatNumber value="${tratta.costo}" pattern="#,##0.00"/></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">N/A</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${tratta.attiva}">
                                                        <span class="status-badge active">
                                                            <i class="fas fa-check-circle"></i> Attiva
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge inactive">
                                                            <i class="fas fa-times-circle"></i> Inattiva
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="actions-group">
                                                    <form action="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" method="post" style="display: inline;">
                                                        <input type="hidden" name="trattaId" value="${tratta.id}" />
                                                        <c:choose>
                                                            <c:when test="${tratta.attiva}">
                                                                <button type="submit" name="action" value="disattiva" class="action-btn toggle" title="Disattiva tratta">
                                                                    <i class="fas fa-pause"></i> Disattiva
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="submit" name="action" value="attiva" class="action-btn toggle" title="Attiva tratta">
                                                                    <i class="fas fa-play"></i> Attiva
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/import/footer.jsp"/>
    
    <!-- JavaScript per gestione tratte -->
    <script src="${pageContext.request.contextPath}/Scripts/gestisci-tratte.js"></script>
</body>
</html>
