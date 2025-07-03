<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.dao.TrattaDAO" %>
<%@ page import="model.sdata.Tratta" %>
<%@ page import="model.udata.Azienda" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>

<%
    List<Tratta> trattas = null;
    String errorMessage = null;

    try {
        trattas = TrattaDAO.getAllTratte();
        request.setAttribute("trattas", trattas);
    } catch (SQLException e) {
        errorMessage = "Errore nel caricamento delle tratte: " + e.getMessage();
        request.setAttribute("errorMessage", errorMessage);
        // Log dell'errore per debugging
        System.err.println("Errore SQL in tratte.jsp: " + e.getMessage());
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <%@include file="import/metadata.jsp" %>
    <title>Gestione Tratte - Sistema di Trasporto Pubblico</title>
</head>
<body>
<%@include file="import/header.jsp" %>

<main>
    <div class="container">
        <div class="header-section">
            <div>
                <h1>Gestione Tratte</h1>
                <p>Visualizza e gestisci tutte le tratte del sistema di trasporto pubblico</p>
            </div>
            <div>
                <a href="nuova-tratta.jsp" class="btn">
                    ➕ Nuova Tratta
                </a>
            </div>
        </div>

        <!-- Statistiche -->
        <div class="stats-container">
            <div class="stat-card">
                <span class="stat-number">${trattas != null ? trattas.size() : 0}</span>
                <span class="stat-label">Tratte Totali</span>
            </div>
            <div class="stat-card">
                    <span class="stat-number">
                        <c:set var="totaleAziende" value="0"/>
                        <c:forEach var="tratta" items="${trattas}">
                            <c:if test="${tratta.azienda != null}">
                                <c:set var="totaleAziende" value="${totaleAziende + 1}"/>
                            </c:if>
                        </c:forEach>
                        ${totaleAziende}
                    </span>
                <span class="stat-label">Aziende Attive</span>
            </div>
            <div class="stat-card">
                    <span class="stat-number">
                        <c:set var="totaleFermate" value="0"/>
                        <c:forEach var="tratta" items="${trattas}">
                            <c:if test="${tratta.fermataTrattaList != null}">
                                <c:set var="totaleFermate" value="${totaleFermate + tratta.fermataTrattaList.size()}"/>
                            </c:if>
                        </c:forEach>
                        ${totaleFermate}
                    </span>
                <span class="stat-label">Fermate Totali</span>
            </div>
        </div>

        <!-- Messaggio di errore -->
        <c:if test="${not empty errorMessage}">
            <div class="error-message">
                <strong>⚠️ Errore:</strong> ${errorMessage}
            </div>
        </c:if>

        <!-- Sezione di ricerca -->
        <div class="search-section">
            <input type="text"
                   id="searchInput"
                   class="search-input"
                   placeholder="🔍 Cerca tratte per nome o azienda..."
                   onkeyup="filterTratte()">
        </div>

        <!-- Tabella tratte -->
        <c:choose>
            <c:when test="${not empty trattas}">
                <table class="tratte-table" id="tratteTable">
                    <thead>
                    <tr>
                        <th>Nome Tratta</th>
                        <th>Azienda</th>
                        <th>Fermate</th>
                        <th>Azioni</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="tratta" items="${trattas}" varStatus="status">
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
                                        <div class="fermate-container">
                                            <table class="fermate-table">
                                                <thead>
                                                <tr>
                                                    <th>Fermata</th>
                                                    <th>Indirizzo</th>
                                                    <th>Tempo</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="fermataTratta" items="${tratta.fermataTrattaList}" varStatus="fermataStatus">
                                                    <tr>
                                                        <td>
                                                            <strong>${not empty fermataTratta.fermata.nome ? fermataTratta.fermata.nome : 'N/A'}</strong>
                                                        </td>
                                                        <td>
                                                                ${not empty fermataTratta.fermata.indirizzo ? fermataTratta.fermata.indirizzo : 'Indirizzo non disponibile'}
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not fermataStatus.last}">
                                                                                <span class="tempo-badge">
                                                                                    ${fermataTratta.tempoProssimaFermata} min
                                                                                </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span style="color: #999; font-size: 11px;">Destinazione</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-data">
                                            <em>Nessuna fermata configurata</em>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="actions-column">
                                <button class="action-btn view-btn"
                                        onclick="viewTratta(${tratta.id})"
                                        title="Visualizza dettagli">
                                    👁️
                                </button>
                                <button class="action-btn edit-btn"
                                        onclick="editTratta(${tratta.id})"
                                        title="Modifica tratta">
                                    ✏️
                                </button>
                                <button class="action-btn delete-btn"
                                        onclick="deleteTratta(${tratta.id}, '${tratta.nome}')"
                                        title="Elimina tratta">
                                    🗑️
                                </button>
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
                    <a href="nuova-tratta.jsp" class="btn">Crea la prima tratta</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@include file="import/footer.jsp" %>
</body>
</html>