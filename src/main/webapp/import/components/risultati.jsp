<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.Risultati" %>
<%@ page import="model.sdata.Percorso" %>
<%@ page import="java.util.List" %>

<%
    Risultati risultati = (Risultati) request.getAttribute("risultati");
    if (risultati != null) {
        List<Percorso> percorsi = risultati.getRisultati();
%>
<div>
    <h2>Risultati della Ricerca</h2>
    <% for (Percorso percorso : percorsi) { %>
        <jsp:include page="percorso.jsp">
            <jsp:param name="percorso" value="<%= percorso %>"/>
        </jsp:include>
    <% } %>
</div>
<%
    }
%>
