
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <%@ include file="import/metadata.jsp" %>
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>
    <form method="get" action="DBtestServlet">
        Partenza<br>
        <input type="text" name="start">
        Destinazione<br>
        <input type="text" name="end">
        <input type="submit" value="🔎Cerca">
    </form>
</main>

<%@ include file="import/footer.jsp" %>
</body>
</html>