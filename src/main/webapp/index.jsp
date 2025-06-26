
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message = (String) request.getAttribute("message");
    if (message == null) {
        message = "Benvenuto in Omniride";
    }
%>
<%
    String message = "Benvenuto in Omniride";
%>
<!DOCTYPE html>
<html>
<head>
    <title>${message}</title>
    <%@ include file="import/metadata.jsp" %>
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>

    <h1>
        ${message}
    </h1>

</main>

<%@ include file="import/footer.jsp" %>
</body>
</html>