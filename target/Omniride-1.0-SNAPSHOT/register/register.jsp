<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Registrazione</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/main.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <div class="form-container">
        <h2>Crea un nuovo account</h2>

        <%-- Mostra un messaggio di errore se presente --%>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <p>${errorMessage}</p>
            </div>
        </c:if>
        
        <%-- Mostra un messaggio di successo se presente --%>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <p>${successMessage}</p>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group user">
                <label for="nome">Nome:</label>
                <input type="text" id="nome" name="nome" required>
            </div>
            <div class="form-group user">
                <label for="cognome">Cognome:</label>
                <input type="text" id="cognome" name="cognome" required>
            </div>
            <div class="form-group email">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group password">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn">Registrati</button>
        </form>
        <p>Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi</a></p>
    </div>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
