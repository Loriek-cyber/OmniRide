<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../import/metadata.jsp"/>
    <title>Il Mio Portafoglio - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/dashboard.css"> <%-- Potrebbe essere utile per lo stile --%>
    <style>
        /* Stili specifici per il portafoglio */
        .wallet-container {
            max-width: 900px;
            margin: 40px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .wallet-section {
            margin-bottom: 30px;
            border-bottom: 1px solid #eee;
            padding-bottom: 20px;
        }
        .wallet-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }
        .wallet-section h3 {
            color: #333;
            margin-bottom: 15px;
            font-size: 1.8em;
            border-left: 4px solid #007bff;
            padding-left: 10px;
        }
        .ticket-card, .subscription-card, .card-item {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .ticket-info, .subscription-info, .card-info {
            flex-grow: 1;
        }
        .ticket-actions, .subscription-actions, .card-actions {
            margin-left: 20px;
        }
        .ticket-status {
            font-weight: bold;
            color: #28a745; /* Green for active */
        }
        .ticket-status.inactive {
            color: #ffc107; /* Yellow for inactive */
        }
        .ticket-status.expired {
            color: #dc3545; /* Red for expired */
        }
        .btn-activate {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn-activate:hover {
            background-color: #0056b3;
        }
        .empty-state {
            text-align: center;
            color: #666;
            padding: 20px;
            border: 1px dashed #ddd;
            border-radius: 5px;
        }
    </style>
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