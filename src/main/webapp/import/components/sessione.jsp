<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Sessione" %>

<%
    Sessione sessione = (Sessione) request.getAttribute("sessione");
    if (sessione != null) {
%>
<div>
    <p>ID: <%= sessione.getId() %></p>
    <p>ID Utente: <%= sessione.getIdUtente() %></p>
    <p>Token: <%= sessione.getToken() %></p>
    <p>Scadenza: <%= sessione.getScadenza() %></p>
</div>
<%
    }
%>
