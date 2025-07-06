<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.OrarioTratta" %>

<%
    OrarioTratta orarioTratta = (OrarioTratta) request.getAttribute("orarioTratta");
    if (orarioTratta != null) {
%>
<div>
    <p>ID Tratta: <%= orarioTratta.getIdTratta() %></p>
    <p>Orario: <%= orarioTratta.getOrario() %></p>
</div>
<%
    }
%>
