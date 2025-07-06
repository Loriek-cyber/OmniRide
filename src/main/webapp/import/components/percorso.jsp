<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Percorso" %>
<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %>

<%
    Percorso percorso = (Percorso) request.getAttribute("percorso");
    if (percorso != null) {
        List<Tratta> tratte = percorso.getTratte();
%>
<div>
    <h3>Percorso</h3>
    <% for (Tratta tratta : tratte) { %>
        <jsp:include page="tratta.jsp">
            <jsp:param name="tratta" value="<%= tratta %>"/>
        </jsp:include>
    <% } %>
</div>
<%
    }
%>
