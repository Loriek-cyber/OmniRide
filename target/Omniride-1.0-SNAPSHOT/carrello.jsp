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

        <c:choose>
            <c:when test="${not empty sessionScope.carrello && not empty sessionScope.carrello.values()}">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Tratta</th>
                            <th>Azienda</th>
                            <th>Prezzo (stimato)</th>
                            <th>Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="prezzoTotale" value="0"/>
                        <c:forEach var="item" items="${sessionScope.carrello.values()}">
                            <c:set var="prezzoTratta" value="2.50"/> <%-- Prezzo placeholder --%>
                            <c:set var="prezzoTotale" value="${prezzoTotale + prezzoTratta}"/>
                            <tr>
                                <td>${item.nome}</td>
                                <td>${item.azienda.nome}</td>
                                <td><fmt:formatNumber value="${prezzoTratta}" type="currency" currencySymbol="€"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/carrello" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="idTratta" value="${item.id}">
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
                    <form action="${pageContext.request.contextPath}/checkout" method="post">
                        <button type="submit" class="btn btn-primary">Procedi all'Acquisto</button>
                    </form>
                </div>

            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h3>Il tuo carrello è vuoto.</h3>
                    <p>Aggiungi delle tratte al carrello per poterle acquistare.</p>
                    <a href="${pageContext.request.contextPath}/tratte" class="btn">Esplora le Tratte</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
