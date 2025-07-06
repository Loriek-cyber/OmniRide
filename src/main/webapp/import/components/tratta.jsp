<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Tratta" %>

<%
    Tratta tratta = (Tratta) request.getAttribute("tratta");
    if (tratta != null) {
%>
<div>
    <p>ID: <%= tratta.getId() %></p>
    <p>Nome: <%= tratta.getNome() %></p>
    <p>ID Azienda: <%= tratta.getIdAzienda() %></p>
</div>
<%
    }
%>
