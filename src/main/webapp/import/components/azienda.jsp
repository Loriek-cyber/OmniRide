<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.udata.Azienda" %>

<%
    Azienda azienda = (Azienda) request.getAttribute("azienda");
    if (azienda != null) {
%>
<div>
    <p>ID: <%= azienda.getId() %></p>
</div>
<%
    }
%>
