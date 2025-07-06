<%@ page import="error.ErrorPage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    ErrorPage error = (ErrorPage) request.getAttribute("error");
%>
<head>
    <title><%=error.getCode()%></title>
</head>
<body>
    <jsp:include page="${ContextPath}/import/header.jsp}"/>
    <main class="content">
        <div class="error Section">
            <h1><%=error.getCode()%></h1>
            <p><%=error.getMassage()%></p>
        </div>
    </main>
    <jsp:include page="${ContextPath}/import/footer.jsp"/>
</body>
</html>
