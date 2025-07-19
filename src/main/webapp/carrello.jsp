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
            <c:when test="${not empty carrello}">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Percorso</th>
                            <th>Data</th>
                            <th>Orario</th>
                            <th>Quantità</th>
                            <th>Prezzo</th>
                            <th>Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${carrello}" varStatus="status">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.percorsoJson != null}">
                                            Percorso personalizzato
                                        </c:when>
                                        <c:otherwise>
                                            Tratta diretta
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${item.data}</td>
                                <td>${item.orario}</td>
                                <td>${item.quantita}</td>
                                <td><fmt:formatNumber value="${item.prezzo}" type="currency" currencySymbol="€"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/carrello" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="rimuovi">
                                        <input type="hidden" name="indice" value="${status.index}">
                                        <button type="submit" class="btn btn-danger btn-sm">Rimuovi</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="4" style="text-align: right;"><strong>Totale:</strong></td>
                            <td><strong><fmt:formatNumber value="${totale}" type="currency" currencySymbol="€"/></strong></td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>

                <div style="text-align: right; margin-top: 20px;">
                    <form action="${pageContext.request.contextPath}/carrello" method="post" style="display: inline; margin-right: 10px;">
                        <input type="hidden" name="action" value="svuota">
                        <button type="submit" class="btn btn-secondary">Svuota Carrello</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/checkout" method="post" style="display: inline;">
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
