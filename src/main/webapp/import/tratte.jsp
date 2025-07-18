<%@ page import="java.util.*" %>
<%@ page import="model.sdata.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Layout a due colonne -->
<div class="tratte-layout">
    <!-- Colonna sinistra - Lista tratte -->
    <div class="tratte-list-container">
        <div class="tratte-list-header">
            <h2>🚌 Tratte Disponibili</h2>
            <span class="tratte-count">${fn:length(tratte)} tratte totali</span>
        </div>
        
        <!-- Verifica se ci sono tratte -->
        <c:choose>
            <c:when test="${empty tratte}">
                <div class="no-data">
                    <div class="no-data-icon">🚫</div>
                    <p>Nessuna tratta disponibile al momento</p>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Lista delle tratte -->
                <div class="tratte-list">
                    <c:forEach var="tratta" items="${tratte}" varStatus="status">
                        <div class="tratta-item" data-tratta-id="${status.index}" data-tratta-real-id="${tratta.id}" onclick="selectTratta(${status.index})">
                            <div class="tratta-header">
                                <div class="tratta-name">${tratta.nome}</div>
                                <div class="tratta-actions">
                                </div>
                    </div>

                    <div class="purchase-section">
                        <a href="acquistaBiglietto?trattaId=${tratta.id}&prezzo=${tratta.costo}" class="btn btn-primary btn-lg">
                            Acquista Biglietto
                        </a>
                    </div>

                    <div class="info-section">
                                <div class="tratta-stat">
                                    <span class="stat-icon">🚏</span>
                                    <span class="stat-value">${fn:length(tratta.fermataTrattaList)} fermate</span>
                                </div>
                                <div class="tratta-stat">
                                    <span class="stat-icon">🕐</span>
                                    <span class="stat-value">${fn:length(tratta.orari)} orari</span>
                                </div>
                            </div>
                            
                            <div class="tratta-preview">
                                <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                                    <c:if test="${fermataStatus.index < 3}">
                                        <span class="fermata-preview">${fermata.fermata.nome}</span>
                                        <c:if test="${!fermataStatus.last && fermataStatus.index < 2}"> → </c:if>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${fn:length(tratta.fermataTrattaList) > 3}">
                                    <span class="more-stops">... +${fn:length(tratta.fermataTrattaList) - 3} altre</span>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <!-- Colonna destra - Dettagli tratta selezionata -->
    <div class="tratta-details-container">
        <div class="tratta-details-header">
            <h3>📋 Dettagli Tratta</h3>
        </div>
        
        <div id="tratta-details" class="tratta-details-content">
            <div class="no-selection">
                <div class="no-selection-icon">👈</div>
                <p>Seleziona una tratta dalla lista per visualizzarne i dettagli</p>
            </div>
        </div>
    </div>
</div>

<!-- Script per gestire i dati delle tratte -->
<script>
    // Prepara i dati delle tratte per JavaScript
    var tratteData = [
        <c:forEach var="tratta" items="${tratte}" varStatus="status">
            {
                id: ${tratta.id},
                nome: "${fn:escapeXml(tratta.nome)}",
                attiva: ${tratta.attiva},
                fermate: [
                    <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                        "${fn:escapeXml(fermata.fermata.nome)}"<c:if test="${!fermataStatus.last}">,</c:if>
                    </c:forEach>
                ],
                orari: [
                    <c:forEach var="orario" items="${orariFormattati[status.index]}" varStatus="orarioStatus">
                        "${fn:escapeXml(orario)}"<c:if test="${!orarioStatus.last}">,</c:if>
                    </c:forEach>
                ],
                tempiPercorrenza: [
                    <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                        ${fermata.tempoProssimaFermata != null ? fermata.tempoProssimaFermata : 0}<c:if test="${!fermataStatus.last}">,</c:if>
                    </c:forEach>
                ]
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
</script>

<!-- Inclusione del file JavaScript per la gestione delle tratte -->
<script src="Scripts/tratte.js"></script>

