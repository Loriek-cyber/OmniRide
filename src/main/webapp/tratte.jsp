<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp" />
    <title>Tratte</title>
    <%
        List<Tratta> tratte = (List<Tratta>) request.getAttribute("tratte");
    %>
</head>
<body>
<jsp:include page="import/header.jsp" />
<jsp:include page="import/tratte.jsp" />
<jsp:include page="import/footer.jsp" />
</body>
</html>
