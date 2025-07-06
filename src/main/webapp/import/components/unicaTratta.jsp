<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.sdata.UnicaTratta" %>

<%
    UnicaTratta unicaTratta = (UnicaTratta) request.getAttribute("unicaTratta");
    if (unicaTratta != null) {
%>
<div>
    <p>ID: <%= unicaTratta.getId() %></p>
    <p>Nome: <%= unicaTratta.getNome() %></p>
    <p>ID Azienda: <%= unicaTratta.getIdAzienda() %></p>
    <p>Prezzo: <%= unicaTratta.getPrezzo() %></p>
    <p>Capolinea di Partenza: <%= unicaTratta.getCapolineaPartenza() %></p>
    <p>Capolinea di Arrivo: <%= unicaTratta.getCapolineaArrivo() %></p>
</div>
<%
    }
%>
