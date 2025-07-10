<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Dipendenti" %>

<%
    Dipendenti dipendente = (Dipendenti) request.getAttribute("dipendente");
    if (dipendente != null) {
%>
<div>
    <p>Nome: <%= dipendente.getUtente().getNome() + " " +dipendente.getUtente().getCognome()%></p>
    <p>Ruolo: <%= dipendente.getLavoro().toString()%></p>
</div>
<%
    }
%>
