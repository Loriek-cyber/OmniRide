<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Il Tuo Carrello - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="container">
        <h2>Il Tuo Carrello</h2>
        
        <!-- Messaggi di successo/errore -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">${successMessage}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <c:choose>
            <c:when test="${not empty sessionScope.carrello}">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Tratta</th>
                            <th>Azienda</th>
                            <th>Prezzo</th>
                            <th>Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="prezzoTotale" value="0"/>
                        <c:forEach var="tratta" items="${sessionScope.carrello}" varStatus="status">
                            <c:set var="prezzoTratta" value="${tratta.costo > 0 ? tratta.costo : 2.50}"/>
                            <c:set var="prezzoTotale" value="${prezzoTotale + prezzoTratta}"/>
                            <tr>
                                <td>${tratta.nome}</td>
                                <td>${tratta.azienda.nome}</td>
                                <td><fmt:formatNumber value="${prezzoTratta}" type="currency" currencySymbol="€"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/carrello" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="removeById">
                                        <input type="hidden" name="trattaId" value="${tratta.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Rimuovi</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="2" style="text-align: right;"><strong>Totale:</strong></td>
                            <td><strong><fmt:formatNumber value="${prezzoTotale}" type="currency" currencySymbol="€"/></strong></td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>

                <div style="text-align: right; margin-top: 20px;">
                    <form action="${pageContext.request.contextPath}/carrello" method="post" style="display: inline-block; margin-right: 10px;">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn btn-secondary" onclick="return confirm('Sei sicuro di voler svuotare il carrello?')">Svuota Carrello</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/checkout" method="post" style="display: inline-block;">
                        <button type="submit" class="btn btn-primary">Procedi all'Acquisto</button>
                    </form>
                </div>

            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h3>Il tuo carrello è vuoto.</h3>
                    <p>Aggiungi delle tratte al carrello per poterle acquistare.</p>
                    <a href="${pageContext.request.contextPath}/visualizzaTratte" class="btn">Esplora le Tratte</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
