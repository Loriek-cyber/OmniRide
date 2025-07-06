<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Coordinate" %>

<%
    Coordinate coordinata = (Coordinate) request.getAttribute("coordinata");
    if (coordinata != null) {
%>
<div>
    <p>Latitudine: <%= coordinata.getLat() %></p>
    <p>Longitudine: <%= coordinata.getLon() %></p>
</div>
<%
    }
%>
