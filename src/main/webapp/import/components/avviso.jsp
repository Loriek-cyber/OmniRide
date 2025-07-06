<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Avvisi" %>

<%
    Avvisi avviso = (Avvisi) request.getAttribute("avviso");
    if (avviso != null) {
%>
<div>
    <p>ID: <%= avviso.getId() %></p>
    <p>ID Azienda: <%= avviso.getIdAzienda() %></p>
    <p>Titolo: <%= avviso.getTitolo() %></p>
    <p>Messaggio: <%= avviso.getMessaggio() %></p>
    <p>Data: <%= avviso.getData() %></p>
</div>
<%
    }
%>
