<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Coordinate" %>

<%
    Coordinate coordinata = (Coordinate) request.getAttribute("coordinata");
    if (coordinata != null) {
%>
<div>
    <p>Latitudine: <%= coordinata.getLatitudine() %></p>
    <p>Longitudine: <%= coordinata.getLongitudine() %></p>
</div>
<%
    }
%>
