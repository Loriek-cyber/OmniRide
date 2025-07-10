<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Biglietto" %>
<%@ page import="model.udata.Azienda" %>

<%
    Biglietto biglietto = (Biglietto) request.getAttribute("biglietto");
    if (biglietto != null) {
%>
<div>
    <p>ID: <%= biglietto.getId() %></p>
    <p>ID Utente: <%= biglietto.getIdUtente() %></p>
    <p>ID Tratta: <%= biglietto.getIdTratta() %></p>
    <p>Prezzo: <%= biglietto.getPrezzo() %></p>
    <p>Data Acquisto: <%= biglietto.getDataAcquisto() %></p>
</div>
<%
    }
%>
