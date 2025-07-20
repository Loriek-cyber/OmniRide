<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="tratte-container">
    <h2>Elenco Tratte Disponibili</h2>
    <p class="subtitle">Scopri tutte le tratte attive e i loro orari di servizio</p>

    <c:choose>
        <c:when test="${empty tratte}">
            <div class="empty-state">
                <p>Nessuna tratta disponibile al momento.</p>
                <small>Le tratte verranno mostrate qui non appena saranno disponibili.</small>
            </div>
        </c:when>
        <c:otherwise>
            <div class="tratte-stats">
                <div class="stat-item">
                    <span class="stat-number">${fn:length(tratte)}</span>
                    <span class="stat-label">Tratte Attive</span>
                </div>
            </div>
            
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
                                    <span class="info-label">🏢 Azienda:</span>
                                    <span class="info-value">${tratta.azienda.nome}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">💰 Costo:</span>
                                    <span class="info-value costo">
                                        <fmt:formatNumber value="${tratta.costo}" type="currency" currencySymbol="€" />
                                    </span>
                                </div>
                                <c:if test="${not empty tratta.fermataTrattaList}">
                                    <div class="info-item">
                                        <span class="info-label">🚏 Fermate:</span>
                                        <span class="info-value">${fn:length(tratta.fermataTrattaList)} fermate</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty tratta.orari}">
                                    <div class="info-item">
                                        <span class="info-label">⏰ Orari:</span>
                                        <span class="info-value">${fn:length(tratta.orari)} servizi</span>
                                    </div>
                                </c:if>
                            </div>

                            <c:if test="${not empty tratta.fermataTrattaList}">
                                <div class="percorso-section">
                                    <div class="percorso-title">Percorso Completo</div>
                                    <div class="percorso-path">
                                        <c:forEach var="fermataTratta" items="${tratta.fermataTrattaList}" varStatus="status">
                                            <div class="fermata-wrapper">
                                                <span class="fermata-chip">
                                                    ${fermataTratta.fermata.nome}
                                                    <c:if test="${not empty fermataTratta.fermata.indirizzo}">
                                                        <small class="fermata-indirizzo">${fermataTratta.fermata.indirizzo}</small>
                                                    </c:if>
                                                </span>
                                                <c:if test="${not status.last}">
                                                    <div class="connection-info">
                                                        <span class="freccia">→</span>
                                                        <c:if test="${fermataTratta.tempoProssimaFermata > 0}">
                                                            <span class="tempo-percorrenza">${fermataTratta.tempoProssimaFermata} min</span>
                                                        </c:if>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${not empty tratta.orari}">
                                <div class="orari-section">
                                    <div class="orari-title">Orari di Servizio</div>
                                    <div class="orari-grid">
                                        <c:forEach var="orario" items="${tratta.orari}">
                                            <div class="orario-item ${orario.attivo ? 'attivo' : 'inattivo'}">
                                                <div class="orario-header">
                                                    <span class="orario-giorni">${orario.giorniDescrizione}</span>
                                                    <c:if test="${not empty orario.tipoServizio}">
                                                        <span class="tipo-servizio ${orario.tipoServizio.toString().toLowerCase()}">
                                                            ${orario.tipoServizio.descrizione}
                                                        </span>
                                                    </c:if>
                                                </div>
                                                <div class="orario-content">
                                                    <div class="orario-tempi">
                                                        <span class="partenza">${orario.oraPartenza}</span>
                                                        <span class="separator">→</span>
                                                        <span class="arrivo">${orario.oraArrivo}</span>
                                                    </div>
                                                    <c:if test="${not empty orario.note}">
                                                        <div class="orario-note">
                                                            <small>📝 ${orario.note}</small>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="tratta-actions">
                                <form action="${pageContext.request.contextPath}/carrello" method="post" style="display: inline-block; margin-right: 5px;">
                                    <input type="hidden" name="action" value="addTratta">
                                    <input type="hidden" name="trattaId" value="${tratta.id}">
                                    <button type="submit" class="btn btn-primary">
                                         🛒 Aggiungi al Carrello
                                    </button>
                                </form>
                                <c:if test="${not empty sessionScope.utente}">
                                    <form action="${pageContext.request.contextPath}/trattePreferiti" method="post" style="display: inline-block; margin-right: 5px;">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="trattaId" value="${tratta.id}">
                                        <button type="submit" class="btn btn-success">
                                            ❤️ Aggiungi ai Preferiti
                                        </button>
                                    </form>
                                </c:if>
                                <button class="btn btn-secondary" onclick="mostraDettagli(${tratta.id})">
                                     Vedi Dettagli
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
function selezionaTratta(trattaId) {
    // Reindirizza alla pagina di prenotazione con l'ID della tratta
    window.location.href = '${pageContext.request.contextPath}/biglietti.jsp?trattaId=' + trattaId;
}

function mostraDettagli(trattaId) {
    // Mostra i dettagli della tratta (da implementare)
    alert('Dettagli per la tratta ID: ' + trattaId);
}
</script>
