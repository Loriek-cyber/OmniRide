<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String page_title = "";
    String page_content = "";
%>


<html>
<head>
    <title><%= page_title %></title>
    <%@ include file="import/metadata.jsp" %>
</head>
<body>
    <%@include file="import/header.jsp"%>
    <main>
        <%= page_content %>
    </main>
    <%@include file="import/footer.jsp"%>
</body>
</html>
