<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.FermataTratta" %>

<%
    FermataTratta fermataTratta = (FermataTratta) request.getAttribute("fermataTratta");
    if (fermataTratta != null) {
%>
<div>
    <p>ID Tratta: <%= fermataTratta.getIdTratta() %></p>
    <p>ID Fermata: <%= fermataTratta.getIdFermata() %></p>
    <p>Numero Fermata: <%= fermataTratta.getNumeroFermata() %></p>
</div>
<%
    }
%>
