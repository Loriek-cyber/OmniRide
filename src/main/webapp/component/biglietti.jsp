<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="model.udata.Biglietto" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  Map<Long, Tratta> map = (Map<Long, Tratta>) request.getAttribute("mapTratta");
  List<Biglietto> biglietti = (List<Biglietto>) request.getAttribute("biglietti");
%>

<div class="tickets-container">
  <% if (biglietti != null && !biglietti.isEmpty()) { %>
    <% for (Biglietto biglietto : biglietti) { %>
      <div class="ticket card">
        <div class="card-header">
          <h3>Biglietto #<%= biglietto.getId() %></h3>
        </div>
        <div class="card-body">
          <p><strong>Stato:</strong> <span class="status <%= biglietto.getStato().toString().toLowerCase() %>"><%= biglietto.getStato() %></span></p>
          <p><strong>Prezzo:</strong> <%= String.format("%.2f", biglietto.getPrezzo()) %> €</p>
          <p><strong>Data Acquisto:</strong> <%= biglietto.getDataAcquisto() %></p>
          <% if (biglietto.getDataConvalida() != null) { %>
            <p><strong>Data Convalida:</strong> <%= biglietto.getDataConvalida() %></p>
          <% } %>
          <div class="tratte">
            <h4>Tratte Incluse:</h4>
            <ul>
              <%-- Assumendo che la mappa nel biglietto contenga gli ID delle tratte --%>
              <%-- Sarà necessario recuperare i dettagli della tratta usando la mapTratta --%>
              <%-- Questo codice è un'ipotesi di come potrebbe funzionare --%>
              <%-- for (Long idTratta : biglietto.getMap().keySet()) { --%>
              <%--   Tratta tratta = map.get(idTratta); --%>
              <%--   if (tratta != null) { --%>
              <%--     <li><%= tratta.getNome() %></li> --%>
              <%--   } --%>
              <%-- } --%>
            </ul>
          </div>
        </div>
        <div class="card-footer">
          <p>Valido fino al: <%= biglietto.getDataFine() %></p>
        </div>
      </div>
    <% } %>
  <% } else { %>
    <p>Nessun biglietto da mostrare.</p>
  <% } %>
</div>