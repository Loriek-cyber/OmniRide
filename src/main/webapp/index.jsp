<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String message = "Il mondo fa schifo!";
%>

<!DOCTYPE html>
<html>
<head>
    <title><%= message %></title>
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>

    <h1>
        <%= message %>
    </h1>

</main>

<%@ include file="import/footer.jsp" %>
</body>
</html>