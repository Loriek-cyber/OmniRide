<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Avvisi" %>

<%-- Assicurati di avere la lista di avvisi e l'ID della tratta come attributi della richiesta --%>
<% List<Avvisi> avvisi = (List<Avvisi>) request.getAttribute("avvisi"); %>
<% Long idTratta = (Long) request.getAttribute("idTratta"); %>

<div class="avvisi-filter">
  <form action="#" method="GET"> <%-- Sostituisci # con l'URL del tuo servlet --%>
    <label for="id_tratta">ID Tratta:</label>
    <input type="number" id="id_tratta" name="id_tratta" value="<%= (idTratta != null) ? idTratta : "" %>" required>
    <button type="submit">Filtra Avvisi</button>
  </form>
</div>

<div class="avvisi-list">
  <% if (avvisi != null && !avvisi.isEmpty()) { %>
    <h3>Avvisi per la tratta <%= idTratta %></h3>
    <ul>
      <% for (Avvisi avviso : avvisi) { %>
        <%-- Controlla se l'avviso appartiene alla tratta specificata --%>
        <% if (avviso.getId_tratte_coinvolte() != null && avviso.getId_tratte_coinvolte().contains(idTratta)) { %>
          <li><%= avviso.getDescrizione() %></li>
        <% } %>
      <% } %>
    </ul>
  <% } else { %>
    <p>Nessun avviso trovato per questa tratta.</p>
  <% } %>
</div>
