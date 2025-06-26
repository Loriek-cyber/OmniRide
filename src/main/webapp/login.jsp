<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String email = request.getParameter("email");
    if (email == null) email = "";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Omniride</title>
    <%@ include file="import/metadata.jsp" %>
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>
    <div class="login-container">
        <h1>Accedi al tuo account</h1>

        <% if (error != null) { %>
            <div class="error-message">
                <%= error %>
            </div>
        <% } %>

        <% if (success != null) { %>
            <div class="success-message">
                <%= success %>
            </div>
        <% } %>

        <form action="login" method="post" class="login-form" id="loginForm">
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required 
                       value="<%= email %>" placeholder="Inserisci la tua email">
                <span class="error-text" id="emailError"></span>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required 
                       placeholder="Inserisci la tua password">
                <span class="error-text" id="passwordError"></span>
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Accedi</button>
            </div>
        </form>

        <div class="login-links">
            <p>Non hai un account? <a href="register.jsp">Registrati qui</a></p>
            <p><a href="index.jsp">Torna alla home</a></p>
        </div>
    </div>
</main>

<%@ include file="import/footer.jsp" %>

<script>
document.getElementById('loginForm').addEventListener('submit', function(e) {
    let isValid = true;

    // Clear previous errors
    document.getElementById('emailError').textContent = '';
    document.getElementById('passwordError').textContent = '';

    // Validate email
    const email = document.getElementById('email').value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) {
        document.getElementById('emailError').textContent = 'Email è obbligatoria';
        isValid = false;
    } else if (!emailRegex.test(email)) {
        document.getElementById('emailError').textContent = 'Formato email non valido';
        isValid = false;
    }

    // Validate password
    const password = document.getElementById('password').value;
    if (!password) {
        document.getElementById('passwordError').textContent = 'Password è obbligatoria';
        isValid = false;
    } else if (password.length < 6) {
        document.getElementById('passwordError').textContent = 'Password deve essere di almeno 6 caratteri';
        isValid = false;
    }

    if (!isValid) {
        e.preventDefault();
    }
});
</script>

</body>
</html>
