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

    <main>
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

            <form action="${pageContext.request.contextPath}/register" method="post" onsubmit="return validateForm()">
                <div class="form-group user">
                    <label for="nome">Nome:</label>
                    <input type="text" id="nome" name="nome" required minlength="2" maxlength="50">
                </div>
                <div class="form-group user">
                    <label for="cognome">Cognome:</label>
                    <input type="text" id="cognome" name="cognome" required minlength="2" maxlength="50">
                </div>
                <div class="form-group email">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required maxlength="100">
                </div>
                <div class="form-group password">
                    <label for="password">Password (min. 6 caratteri):</label>
                    <input type="password" id="password" name="password" required minlength="6" maxlength="100">
                    <small class="form-text">La password deve contenere almeno 6 caratteri</small>
                </div>
                <button type="submit" class="btn">Registrati</button>
            </form>
            
            <script>
            function validateForm() {
                const nome = document.getElementById('nome').value.trim();
                const cognome = document.getElementById('cognome').value.trim();
                const email = document.getElementById('email').value.trim();
                const password = document.getElementById('password').value;
                
                if (nome.length < 2) {
                    alert('Il nome deve contenere almeno 2 caratteri');
                    return false;
                }
                
                if (cognome.length < 2) {
                    alert('Il cognome deve contenere almeno 2 caratteri');
                    return false;
                }
                
                if (!isValidEmail(email)) {
                    alert('Inserisci un indirizzo email valido');
                    return false;
                }
                
                if (password.length < 6) {
                    alert('La password deve contenere almeno 6 caratteri');
                    return false;
                }
                
                return true;
            }
            
            function isValidEmail(email) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                return emailRegex.test(email);
            }
            </script>
            <p>Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi</a></p>
        </div>
    </main>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
