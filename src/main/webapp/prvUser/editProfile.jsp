<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Modifica Profilo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/form.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <main>
        <div class="form-container">
            <h2>Modifica il tuo Profilo</h2>

            <c:if test="${not empty sessionScope.utente}">
                <%-- Messaggi di feedback --%>
                <c:if test="${not empty requestScope.successMessage}">
                    <p class="success-message"><c:out value="${requestScope.successMessage}"/></p>
                </c:if>
                <c:if test="${not empty requestScope.errorMessage}">
                    <p class="error-message"><c:out value="${requestScope.errorMessage}"/></p>
                </c:if>

                <form action="${pageContext.request.contextPath}/updateProfile" method="post" class="profile-form">
                    <div class="form-group">
                        <label for="nome">Nome:</label>
                        <input type="text" id="nome" name="nome" value="<c:out value="${sessionScope.utente.nome}"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="cognome">Cognome:</label>
                        <input type="text" id="cognome" name="cognome" value="<c:out value="${sessionScope.utente.cognome}"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" value="<c:out value="${sessionScope.utente.email}"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Nuova Password (lascia vuoto per non cambiare):</label>
                        <input type="password" id="password" name="password">
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Conferma Nuova Password:</label>
                        <input type="password" id="confirmPassword" name="confirmPassword">
                    </div>
                    <button type="submit" class="btn">Salva Modifiche</button>
                </form>
            </c:if>

            <c:if test="${empty sessionScope.utente}">
                <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
            </c:if>
        </div>
    </main>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
