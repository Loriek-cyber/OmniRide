<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp" />
    <title>Gestione Tratte - Sistema di Trasporto Pubblico</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/tratte.css">
</head>
<body>
<jsp:include page="import/header.jsp" />

<main>
    <div class="container">
        <div class="header-section">
            <div>
                <h1>Elenco Tratte</h1>
                <p>Visualizza tutte le tratte disponibili nel nostro sistema.</p>
            </div>
        </div>

        <!-- Statistiche -->
        <div class="stats-container">
            <div class="stat-card">
                <span class="stat-number">${not empty tratte ? tratte.size() : 0}</span>
                <span class="stat-label">Tratte Totali</span>
            </div>
        </div>

        <!-- Messaggio di errore -->
        <c:if test="${not empty errorMessage}">
            <div class="error-message">
                <strong>⚠️ Errore:</strong> ${errorMessage}
            </div>
        </c:if>

        <!-- Tabella tratte -->
        <c:choose>
            <c:when test="${not empty tratte}">
                <table class="tratte-table" id="tratteTable">
                    <thead>
                    <tr>
                        <th>Nome Tratta</th>
                        <th>Azienda</th>
                        <th>Fermate</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="tratta" items="${tratte}" varStatus="status">
                        <tr class="tratta-row">
                            <td>
                                <strong>${not empty tratta.nome ? tratta.nome : 'Nome non disponibile'}</strong>
                                <br>
                                <small style="color: #666;">ID: ${tratta.id}</small>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty tratta.azienda && not empty tratta.azienda.nome}">
                                        <span class="azienda-badge">${tratta.azienda.nome}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #999; font-style: italic;">Azienda non specificata</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty tratta.fermataTrattaList}">
                                        <ul class="fermate-list">
                                            <c:forEach var="fermataTratta" items="${tratta.fermataTrattaList}">
                                                <li>${fermataTratta.fermata.nome}</li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-data">
                                            <em>Nessuna fermata</em>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <h3>📋 Nessuna tratta trovata</h3>
                    <p>Non sono ancora state configurate tratte nel sistema.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="import/footer.jsp" />
</body>
</html>
