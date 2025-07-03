<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Login</title>
    <jsp:include page="/import/metadata.jsp"/>
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <div class="form-container">
        <h2>Accedi al tuo account</h2>

        <%-- Mostra un messaggio di errore se presente --%>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <p>${errorMessage}</p>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group email">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group password">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn">Accedi</button>
        </form>
        <p>Non hai un account? <a href="${pageContext.request.contextPath}/register">Registrati</a></p>
    </div>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
