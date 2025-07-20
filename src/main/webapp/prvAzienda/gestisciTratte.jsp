<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Gestione Tratte - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/gestisci-tratte.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        <button id="sidebar-toggle" class="sidebar-toggle">
            <i class="fas fa-bars"></i>
        </button>
        <div class="main-content">
            <div class="container">
                <div class="page-header">
                    <h1 class="page-title">Gestione Tratte</h1>
                    <p class="page-subtitle">Gestisci le tue tratte di trasporto</p>
                </div>
                
                <div class="content-grid">
                    <div class="section-card routes-overview">
                        <div class="section-header">
                            <h2 class="section-title">Le tue Tratte</h2>
                            <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Aggiungi Tratta
                            </a>
                        </div>
                    
                    <c:choose>
                        <c:when test="${empty tratte}">
                            <div class="card">
                                <div class="card-header">
                                    <h4 class="card-title">Nessuna Tratta</h4>
                                </div>
                                <p class="text-muted">Non hai ancora creato nessuna tratta.</p>
                                <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">Aggiungi Tratta</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="tratta" items="${tratte}">
                                <div class="list-item" onclick="showTrattaDetails('${tratta.id}')">
                                    <h4>${tratta.nome}</h4>
                                    <p>Status:
                                        <c:choose>
                                            <c:when test="${tratta.attiva}">
                                                <span style="color: green;">Attiva</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: red;">Disattiva</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div class="details-column">
                    <div class="details-panel empty" id="tratta-details">
                        <p>Seleziona una tratta dalla lista per visualizzarne i dettagli</p>
                    </div>
                </div>
            </div>
            
                <c:if test="${not empty tratte}">
                    <div class="section-card routes-management">
                        <div class="section-header">
                            <h2 class="section-title">
                                <i class="fas fa-cogs"></i> Gestione Avanzata
                            </h2>
                        </div>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nome Tratta</th>
                                        <th>Partenza</th>
                                        <th>Arrivo</th>
                                        <th>Stato</th>
                                        <th>Azioni</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="tratta" items="${tratte}">
                                        <tr class="table-row">
                                            <td class="route-id">#${tratta.id}</td>
                                            <td class="route-name-cell">
                                                <strong>${tratta.nome}</strong>
                                            </td>
                                            <td class="location-cell">
                                                <i class="fas fa-map-marker-alt text-primary"></i> ${tratta.partenza}
                                            </td>
                                            <td class="location-cell">
                                                <i class="fas fa-flag-checkered text-success"></i> ${tratta.arrivo}
                                            </td>
                                            <td class="status-cell">
                                                <c:choose>
                                                    <c:when test="${tratta.attiva}">
                                                        <span class="status-badge status-active">
                                                            <i class="fas fa-check-circle"></i> Attiva
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-inactive">
                                                            <i class="fas fa-times-circle"></i> Disattiva
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="actions-cell">
                                                <form action="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" method="post" class="action-form">
                                                    <input type="hidden" name="trattaId" value="${tratta.id}" />
                                                    <c:choose>
                                                        <c:when test="${tratta.attiva}">
                                                            <button type="submit" name="action" value="disattiva" class="btn btn-warning btn-sm" title="Disattiva tratta">
                                                                <i class="fas fa-pause"></i> Disattiva
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="submit" name="action" value="attiva" class="btn btn-success btn-sm" title="Attiva tratta">
                                                                <i class="fas fa-play"></i> Attiva
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:if>
        </div>
    </div>
    
    <script>
        // Sidebar toggle functionality
        document.addEventListener('DOMContentLoaded', function() {
            const sidebarToggle = document.getElementById('sidebar-toggle');
            const sidebar = document.querySelector('.sidebar');
            const mainContent = document.querySelector('.main-content');
            
            if (sidebarToggle) {
                sidebarToggle.addEventListener('click', function() {
                    sidebar.classList.toggle('hidden');
                    mainContent.classList.toggle('full-width');
                });
            }
        });
        
        function showTrattaDetails(trattaId) {
            // Fetch route details from server or display cached data
            const detailsPanel = document.getElementById('tratta-details');
            
            // Find the selected route from the current list
            const routeCards = document.querySelectorAll('.route-card');
            routeCards.forEach(card => card.classList.remove('selected'));
            
            // Add selected class to clicked card
            event.currentTarget.classList.add('selected');
            
            // Create detailed view content
            const detailsContent = `
                <div class="route-details-content">
                    <div class="detail-header">
                        <h3>Dettagli Tratta #${trattaId}</h3>
                        <div class="detail-actions">
                            <button class="btn btn-primary btn-sm" onclick="editRoute(${trattaId})">
                                <i class="fas fa-edit"></i> Modifica
                            </button>
                        </div>
                    </div>
                    <div class="detail-content">
                        <div class="detail-item">
                            <label>ID Tratta:</label>
                            <span>#${trattaId}</span>
                        </div>
                        <div class="detail-item">
                            <label>Stato:</label>
                            <span class="status-indicator">Caricamento...</span>
                        </div>
                        <div class="detail-item">
                            <label>Informazioni aggiuntive:</label>
                            <span>Caricamento dettagli...</span>
                        </div>
                    </div>
                    <div class="detail-actions-panel">
                        <button class="btn btn-secondary" onclick="clearSelection()">
                            <i class="fas fa-times"></i> Chiudi
                        </button>
                    </div>
                </div>
            `;
            
            detailsPanel.innerHTML = detailsContent;
            detailsPanel.classList.remove('empty');
        }
        
        function clearSelection() {
            const detailsPanel = document.getElementById('tratta-details');
            const routeCards = document.querySelectorAll('.route-card');
            
            routeCards.forEach(card => card.classList.remove('selected'));
            
            detailsPanel.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">
                        <i class="fas fa-info-circle fa-2x"></i>
                    </div>
                    <p>Seleziona una tratta dalla lista per visualizzarne i dettagli</p>
                </div>
            `;
            detailsPanel.classList.add('empty');
        }
        
        function editRoute(routeId) {
            // Redirect to edit page or open modal
            window.location.href = '${pageContext.request.contextPath}/prvAzienda/editTratta.jsp?id=' + routeId;
        }
    </script>
    
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
