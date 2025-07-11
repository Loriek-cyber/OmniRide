<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../import/metadata.jsp"/>
    <title>Il Mio Portafoglio - Omniride</title>
</head>
<body>
<jsp:include page="../import/header.jsp"/>

<main>
    <div class="wallet-container">
        <h2>Il Mio Portafoglio</h2>

        <div class="wallet-section">
            <h3>I Miei Biglietti</h3>
            <c:choose>
                <c:when test="${not empty biglietti}">
                    <c:forEach var="biglietto" items="${biglietti}">
                        <div class="ticket-card">
                            <div class="ticket-info">
                                <p><strong>Tratta:</strong> ${biglietto.tratta.nome}</p>
                                <p><strong>Acquistato il:</strong> <fmt:formatDate value="${biglietto.dataAcquisto}" pattern="dd/MM/yyyy HH:mm"/></p>
                                <p><strong>Stato:</strong> <span class="ticket-status ${biglietto.stato eq 'ACQUISTATO' ? 'inactive' : (biglietto.stato eq 'CONVALIDATO' ? 'active' : 'expired')}">${biglietto.stato}</span></p>
                            </div>
                            <div class="ticket-actions">
                                <c:if test="${biglietto.stato eq 'ACQUISTATO'}">
                                    <form action="${pageContext.request.contextPath}/wallet" method="post">
                                        <input type="hidden" name="action" value="activateTicket">
                                        <input type="hidden" name="idBiglietto" value="${biglietto.id}">
                                        <button type="submit" class="btn-activate">Attiva Biglietto</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>Nessun biglietto nel tuo portafoglio.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="wallet-section">
            <h3>I Miei Abbonamenti</h3>
            <div class="empty-state">
                <p>Nessun abbonamento attivo al momento.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

        <div class="wallet-section">
            <h3>Carte di Credito Salvate</h3>
            <div class="empty-state">
                <p>Nessuna carta di credito salvata.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

    </div>
</main>

<jsp:include page="../import/footer.jsp"/>
</body>
</html>