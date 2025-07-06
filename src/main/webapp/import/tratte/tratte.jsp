<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="tratte-container">
    <h2>Elenco Tratte</h2>

    <c:choose>
        <c:when test="${empty tratte}">
            <div class="empty-state">
                <p>Nessuna tratta disponibile al momento.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="tratte-grid">
                <c:forEach var="tratta" items="${tratte}">
                    <div class="tratta-card">
                        <div class="tratta-header">
                            <h3>${tratta.nome}</h3>
                            <span class="status-badge ${tratta.attiva ? 'attiva' : 'disattiva'}">
                                    ${tratta.attiva ? 'Attiva' : 'Disattivata'}
                            </span>
                        </div>

                        <div class="tratta-body">
                            <div class="tratta-info">
                                <div class="info-item">
                                    <span class="info-label">Azienda:</span>
                                    <span class="info-value">${tratta.azienda.nome}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Costo:</span>
                                    <span class="info-value costo">
                                        <fmt:formatNumber value="${tratta.costo}" type="currency" currencySymbol="€" />
                                    </span>
                                </div>
                            </div>

                            <c:if test="${not empty tratta.fermataTrattaList}">
                                <div class="percorso-section">
                                    <div class="percorso-title">Percorso</div>
                                    <div class="percorso-path">
                                        <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="status">
                                            <span class="fermata-chip">${fermata.fermata.nome}</span>
                                            <c:if test="${not status.last}">
                                                <span class="freccia">-></span>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${not empty tratta.orari}">
                                <div class="orari-section">
                                    <div class="orari-title">Orari di Servizio</div>
                                    <ul class="orari-list">
                                        <c:forEach var="orario" items="${tratta.orari}">
                                            <li class="orario-item">
                                                <div class="orario-giorni">${orario.giorniSettimana}</div>
                                                <div class="orario-tempi">${orario.oraPartenza} → ${orario.oraArrivo}</div>
                                                <c:if test="${not empty orario.frequenzaMinuti}">
                                                    <div class="orario-frequenza">Ogni ${orario.frequenzaMinuti} min</div>
                                                </c:if>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>