<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Fermata" %>

<%
    Fermata fermata = (Fermata) request.getAttribute("fermata");
    if (fermata != null) {
%>
<div>
    <p>ID: <%= fermata.getId() %></p>
    <p>Nome: <%= fermata.getNome() %></p>
    <p>Latitudine: <%= fermata.getLat() %></p>
    <p>Longitudine: <%= fermata.getLon() %></p>
</div>
<%
    }
%>
