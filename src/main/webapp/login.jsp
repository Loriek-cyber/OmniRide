<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="import/meta.jsp" />
<title>Insert title here</title>
</head>
<body>
<jsp:include page="import/header.jsp" />

<main>

	<div class="login-container">
    <h2>Login</h2>
    <form action="/login" method="post">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit">Login</button>
    </form>
    <p>Don't have an account? <a href="/register">Register here</a></p>

</main>
<jsp:include page="import/footer.jsp" />
</body>
</html>