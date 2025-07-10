<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Avvisi" %>
<%@ page import="model.dao.TrattaDAO" %>
<%@ page import="java.sql.SQLException" %>

<%
    List<Avvisi> avvisi = (List<Avvisi>) request.getAttribute("avvisi");
    if (avvisi != null && !avvisi.isEmpty()) {
%>
<div class="avvisi-container">
    <h2>Avvisi Recenti</h2>
    <ul>
        <% for (Avvisi avviso : avvisi) { %>
        <li>
            <strong><%= avviso.getTipo() %>:</strong>
            <%= avviso.getDescrizione() %>
            <% if (avviso.getId_tratte_coinvolte() != null && !avviso.getId_tratte_coinvolte().isEmpty()) { %>
            <br>
            <small>
                Tratte coinvolte:
                <% for (Long trattaId : avviso.getId_tratte_coinvolte()) { %>
                <%
                    String nomeTratta = "N/D";
                    try {
                        nomeTratta = TrattaDAO.getTrattaNameByID(trattaId);
                    } catch (SQLException e) {
                        // Logga l'errore o gestiscilo
                    }
                %>
                <span><%= nomeTratta %></span>
                <% } %>
            </small>
            <% } %>
        </li>
        <% } %>
    </ul>
</div>
<% } else { %>
<p>Nessun avviso recente.</p>
<% } %>