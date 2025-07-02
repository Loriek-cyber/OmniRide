<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@include file="import/metadata.jsp" %>
    <title>Risultati</title>
</head>
<body>
<%@include file="import/header.jsp" %>

<main>
    <div id="results">
        <%
            // Sezione di reindirizzamenti in mancanza di dati
            for (int i = 0; i < 10; i++) {
        %>
        <p><%= i %></p>
        <%
            }
        %>
    </div>
</main>

<%@include file="import/footer.jsp" %>
</body>
</html>
