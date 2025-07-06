<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Fermata" %>
<html>
<head>
    <title>Aggiungi Tratta</title>
</head>
<body>
    <h1>Aggiungi Nuova Tratta</h1>
    <form action="addTratta" method="post">
        <label for="nome">Nome Tratta:</label><br>
        <input type="text" id="nome" name="nome" required><br><br>

        <h3>Seleziona Fermate</h3>
        <%
            List<Fermata> fermate = (List<Fermata>) request.getAttribute("fermate");
            if (fermate != null && !fermate.isEmpty()) {
        %>
        <select name="fermateSelezionate" multiple required>
            <% for (Fermata fermata : fermate) { %>
            <option value="<%= fermata.getId() %>"><%= fermata.getNome() %></option>
            <% } %>
        </select>
        <%
            } else {
        %>
        <p>Nessuna fermata disponibile. Aggiungine prima qualcuna.</p>
        <%
            }
        %>
        <br><br>
        <input type="submit" value="Aggiungi Tratta">
    </form>
</body>
</html>
