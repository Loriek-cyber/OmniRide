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
            <h2>Tratte Disponibili</h2>
        </div>
        
        <!-- Lista delle tratte -->
        <div class="tratte-list">
            <c:forEach var="tratta" items="${tratte}" varStatus="status">
                <div class="tratta-item" data-tratta-id="${status.index}" onclick="selectTratta(${status.index})">
                    <div class="tratta-name">${tratta.nome}</div>
                    <div class="tratta-preview">
                        <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                            <c:if test="${fermataStatus.index < 3}">
                                <span class="fermata-preview">${fermata.fermata.nome}</span>
                                <c:if test="${!fermataStatus.last && fermataStatus.index < 2}"> → </c:if>
                            </c:if>
                        </c:forEach>
                        <c:if test="${fn:length(tratta.fermataTrattaList) > 3}">
                            <span class="more-stops">...</span>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <!-- Colonna destra - Dettagli tratta selezionata -->
    <div class="tratta-details-container">
        <div class="tratta-details-header">
            <h3>Dettagli Tratta</h3>
        </div>
        
        <div id="tratta-details" class="tratta-details-content">
            <div class="no-selection">
                <p>Seleziona una tratta dalla lista per visualizzarne i dettagli</p>
            </div>
        </div>
    </div>
</div>

<!-- Dati JSON per JavaScript -->
<script>
    const tratteData = [
        <c:forEach var="tratta" items="${tratte}" varStatus="status">
            {
                "nome": "${tratta.nome}",
                "fermate": [
                    <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                        "${fermata.fermata.nome}"<c:if test="${!fermataStatus.last}">,</c:if>
                    </c:forEach>
                ],
                "orari": [
                    <c:if test="${not empty times && status.index < fn:length(times)}">
                        <c:forEach var="timeGroup" items="${times[status.index]}" varStatus="timeStatus">
                            <c:forEach var="timeSubGroup" items="${timeGroup}" varStatus="subStatus">
                                <c:forEach var="ora" items="${timeSubGroup}" varStatus="oraStatus">
                                    "<fmt:formatDate value='${ora}' pattern='HH:mm'/>"<c:if test="${!oraStatus.last || !subStatus.last || !timeStatus.last}">,</c:if>
                                </c:forEach>
                            </c:forEach>
                        </c:forEach>
                    </c:if>
                ]
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
</script>
