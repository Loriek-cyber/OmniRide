<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="tratta-container">
    <h2 class="tratta-title">
        Tratta: ${tratta.nome} (ID: ${tratta.id})
    </h2>

    <form action="acquistoBiglietto" method="post" style="margin-top: 10px;">
        <input type="hidden" name="idTratta" value="${tratta.id}">
        <input type="hidden" name="prezzo" value="${tratta.costo}">
        <button type="submit">Acquista Biglietto (€${tratta.costo})</button>
    </form>

    <c:choose>
        <c:when test="${empty orariTratta}">
            <p class="tratta-warning">→ Nessun orario associato a questa tratta.</p>
        </c:when>
        <c:when test="${empty fermateTratta}">
            <p class="tratta-warning">→ Nessuna fermata associata a questa tratta.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="orario" items="${orariTratta}">
                <div class="orario-blocco">
                    <h3 class="orario-header">
                        ➤ Partenza alle ${orario.oraPartenza} (${orario.giorniSettimana})
                    </h3>

                    <ul class="fermate-list">
                        <c:set var="oraCorrente" value="${orario.oraPartenza}" />
                        <c:forEach var="ft" items="${fermateTratta}">
                            <li class="fermata-item">
                                🚌 ${ft.fermata.nome} –
                                <span class="orario-passaggio">
                                        ${oraCorrente}
                                </span>
                            </li>
                            <c:set var="oraCorrente"
                                   value="${oraCorrente.plusMinutes(ft.tempoProssimaFermata)}" />
                        </c:forEach>
                        <li class="fermata-item finale">
                            🛑 Arrivo al capolinea – <span class="orario-passaggio">${oraCorrente}</span>
                        </li>
                    </ul>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
