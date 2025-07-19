<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Gestione Tratte - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/unified.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="main-with-sidebar">
        <div class="container">
            <h1 class="text-center mb-lg">Gestione Tratte</h1>
            
            <div class="two-columns">
                <div class="list-column">
                    <h3 class="mb-md">Le tue Tratte</h3>
                    
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
            
            <!-- Tabella per gestione tratte -->
            <c:if test="${not empty tratte}">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Gestione Tratte</h3>
                    </div>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                                <th>Partenza</th>
                                <th>Arrivo</th>
                                <th>Status</th>
                                <th>Azione</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="tratta" items="${tratte}">
                                <tr>
                                    <td>${tratta.id}</td>
                                    <td>${tratta.nome}</td>
                                    <td>${tratta.partenza}</td>
                                    <td>${tratta.arrivo}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${tratta.attiva}">
                                                <span style="color: green;">Attiva</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: red;">Disattiva</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" method="post" style="display: inline;">
                                            <input type="hidden" name="trattaId" value="${tratta.id}" />
                                            <c:choose>
                                                <c:when test="${tratta.attiva}">
                                                    <button type="submit" name="action" value="disattiva">Disattiva</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" name="action" value="attiva">Attiva</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
    
    <script>
        function showTrattaDetails(trattaId) {
            // Qui potrai implementare la logica per mostrare i dettagli della tratta selezionata
            const detailsPanel = document.getElementById('tratta-details');
            detailsPanel.innerHTML = '<p>Dettagli per la tratta ID: ' + trattaId + '</p>';
            detailsPanel.classList.remove('empty');
        }
    </script>
    
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
