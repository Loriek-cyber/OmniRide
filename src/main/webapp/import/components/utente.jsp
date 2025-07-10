<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Utente" %>

<%
    Utente utente = (Utente) request.getAttribute("utente");
    if (utente != null) {
%>
<div>
    <p>Nome: <%= utente.getNome() %></p>
    <p>Cognome: <%= utente.getCognome() %></p>
    <p>Email: <%= utente.getEmail() %></p>
</div>
<%
    }
%>
