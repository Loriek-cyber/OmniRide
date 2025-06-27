<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.sdata.User" %>


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
            ArrayList<User> content = (ArrayList<User>) request.getAttribute("content");
            if (content == null || content.isEmpty()) {
        %>
        <p>Nessun risultato disponibile.</p>
        <%
        } else {
            for (User user : content) {
        %>
        <p><%= user.getNome() %> <%= user.getCognome() %> - ID: <%= user.getId() %></p>
        <%
                }
            }
        %>
    </div>
</main>

<%@include file="import/footer.jsp" %>
</body>
</html>
