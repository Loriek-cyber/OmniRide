<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Fermata" %>

<%
    Fermata fermata = (Fermata) request.getAttribute("fermata");
    if (fermata != null) {
%>
<div class="fermata-item">
    <p>Nome: <%= fermata.getNome() %></p>
    <p>Indirizzo <%= fermata.getIndirizzo()%></p>
</div>
<%}
%>
