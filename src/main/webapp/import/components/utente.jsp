<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Utente" %>

<%
    Utente utente = (Utente) request.getAttribute("utente");
    if (utente != null) {
%>
<div>
    <p>ID: <%= utente.getId() %></p>
    <p>Nome: <%= utente.getNome() %></p>
    <p>Cognome: <%= utente.getCognome() %></p>
    <p>Email: <%= utente.getEmail() %></p>
    <p>Data di Nascita: <%= utente.getDataNascita() %></p>
</div>
<%
    }
%>
