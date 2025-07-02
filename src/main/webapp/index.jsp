
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
    <div id="content">
        <form method="get" action="DBtestServlet">
            <h2>Cerca la tua tratta</h2>
            <div class="input-group search">
                <label for="start">Partenza:</label>
                <input type="text" id="start" name="start" placeholder="Città di partenza" required>
            </div>
            <div class="input-group search">
                <label for="end">Destinazione:</label>
                <input type="text" id="end" name="end" placeholder="Città di destinazione" required>
            </div>
            <button type="submit" class="btn">🔎 Cerca</button>
        </form>
    </div>
</main>

<%@ include file="import/footer.jsp" %>
</body>
</html>