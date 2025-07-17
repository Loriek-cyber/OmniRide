<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>OmniRide - Risultati Ricerca</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="Styles/search-results.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="container">
        <div class="search-summary">
            <h1>Risultati della ricerca</h1>
            <p class="route-info">
                <i class="fas fa-route"></i>
                <strong>${param.partenza}</strong> → <strong>${param.arrivo}</strong>
            </p>
            <p class="date-time-info">
                <i class="fas fa-calendar"></i> ${param.data} 
                <i class="fas fa-clock"></i> Partenza dopo le ${param.orario}
            </p>
        </div>

        <!-- Filtri attivi -->
        <c:if test="${not empty param.prezzoMax or not empty param.durataMax}">
            <div class="active-filters">
                <h3>Filtri attivi:</h3>
                <div class="filter-tags">
                    <c:if test="${not empty param.prezzoMax}">
                        <span class="filter-tag">
                            <i class="fas fa-euro-sign"></i> Max €${param.prezzoMax}
                        </span>
                    </c:if>
                    <c:if test="${not empty param.durataMax}">
                        <span class="filter-tag">
                            <i class="fas fa-clock"></i> Max ${param.durataMax}h
                        </span>
                    </c:if>
                </div>
            </div>
        </c:if>

        <!-- Nuova ricerca -->
        <div class="new-search-link">
            <a href="${pageContext.request.contextPath}/" class="btn-new-search">
                <i class="fas fa-search"></i> Nuova ricerca
            </a>
        </div>

        <!-- Risultati -->
        <div class="results-section">
            <c:choose>
                <c:when test="${not empty tratte}">
                    <div class="results-count">
                        <p>Trovate <strong>${tratte.size()}</strong> tratte disponibili</p>
                    </div>
                    
                    <div class="results-list">
                        <c:forEach var="tratta" items="${tratte}">
                            <div class="route-card">
                                <div class="route-header">
                                    <div class="route-time">
                                        <c:if test="${not empty tratta.orarioPartenza}">
                                            <span class="departure-time">${tratta.orarioPartenza}</span>
                                        </c:if>
                                        <c:if test="${empty tratta.orarioPartenza}">
                                            <span class="departure-time">--:--</span>
                                        </c:if>
                                        <i class="fas fa-arrow-right"></i>
                                        <c:if test="${not empty tratta.orari and not empty tratta.orari[0].oraArrivo}">
                                            <span class="arrival-time">${tratta.orari[0].oraArrivo}</span>
                                        </c:if>
                                        <c:if test="${empty tratta.orari or empty tratta.orari[0].oraArrivo}">
                                            <span class="arrival-time">--:--</span>
                                        </c:if>
                                    </div>
                                    <div class="route-duration">
                                        <i class="fas fa-clock"></i> ${tratta.durata} min
                                    </div>
                                </div>
                                
                                <div class="route-stations">
                                    <div class="station">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <c:if test="${not empty tratta.fermataTrattaList and not empty tratta.fermataTrattaList[0].fermata}">
                                            <span>${tratta.fermataTrattaList[0].fermata.nome}</span>
                                        </c:if>
                                        <c:if test="${empty tratta.fermataTrattaList or empty tratta.fermataTrattaList[0].fermata}">
                                            <span>${param.partenza}</span>
                                        </c:if>
                                    </div>
                                    <div class="route-line"></div>
                                    <div class="station">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <c:if test="${not empty tratta.fermataTrattaList and tratta.fermataTrattaList.size() > 0}">
                                            <span>${tratta.fermataTrattaList[tratta.fermataTrattaList.size() - 1].fermata.nome}</span>
                                        </c:if>
                                        <c:if test="${empty tratta.fermataTrattaList}">
                                            <span>${param.arrivo}</span>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <div class="route-info">
                                    <span class="transport-type">
                                        <c:choose>
                                            <c:when test="${tratta.azienda.tipo == 'BUS'}">
                                                <i class="fas fa-bus"></i> Autobus
                                            </c:when>
                                            <c:when test="${tratta.azienda.tipo == 'TRAM'}">
                                                <i class="fas fa-train-tram"></i> Tram
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-train"></i> ${tratta.azienda.tipo}
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                    <span class="route-number">${tratta.nome}</span>
                                </div>
                                
                                <div class="route-footer">
                                    <div class="price">
                                        <i class="fas fa-euro-sign"></i>
                                        <span class="price-value"><fmt:formatNumber value="${tratta.costo}" type="currency" currencySymbol="€"/></span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/dettaglioTratta?id=${tratta.id}" class="btn-details">
                                        Dettagli e Acquisto <i class="fas fa-chevron-right"></i>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-results">
                        <i class="fas fa-search-minus"></i>
                        <h2>Nessuna tratta trovata</h2>
                        <p>Non sono state trovate tratte che corrispondono ai criteri di ricerca.</p>
                        <p>Prova a:</p>
                        <ul>
                            <li>Modificare l'orario di partenza</li>
                            <li>Cambiare la data del viaggio</li>
                            <li>Rimuovere alcuni filtri</li>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>

<script>
// Evidenzia la card al passaggio del mouse
document.querySelectorAll('.route-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-2px)';
    });
    card.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
    });
});
</script>

</body>
</html>
