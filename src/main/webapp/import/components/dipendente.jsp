<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Dipendenti" %>

<%
    Dipendenti dipendente = (Dipendenti) request.getAttribute("dipendente");
    if (dipendente != null) {
%>
<div>
    <p>ID: <%= dipendente.getId() %></p>
    <p>ID Azienda: <%= dipendente.getIdAzienda() %></p>
    <p>ID Utente: <%= dipendente.getIdUtente() %></p>
</div>
<%
    }
%>
