<%@ page import="error.ErrorPage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    ErrorPage error = (ErrorPage) request.getAttribute("error");
    int errorCode = error != null ? error.getCode() : 500;
    String errorMessage = error != null ? error.getMessage() : "Si è verificato un errore imprevisto.";
%>
<head>
    <%@ include file="import/metadata.jsp" %>
    <title>Errore <%= errorCode %></title>
    <style>
        .error-section {
            text-align: center;
            padding: 80px 20px;
            font-family: 'Segoe UI', sans-serif;
        }
        .error-section h1 {
            font-size: 100px;
            margin: 0;
            color: #e74c3c;
        }
        .error-section p {
            font-size: 20px;
            color: #555;
        }
        .error-section a {
            display: inline-block;
            margin-top: 30px;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 8px;
        }
        .error-section a:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
<jsp:include page="/import/header.jsp" />
<main class="content">
    <div class="error-section">
        <h1><%= errorCode %></h1>
        <p><%= errorMessage %></p>
        <a href="<%= request.getContextPath() %>/">Torna alla home</a>
    </div>
</main>
<jsp:include page="/import/footer.jsp" />
</body>
</html>
