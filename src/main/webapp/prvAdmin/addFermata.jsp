<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Aggiungi Fermata</title>
</head>
<body>
    <h1>Aggiungi Nuova Fermata</h1>
    <form action="addFermata" method="post">
        <label for="nome">Nome:</label><br>
        <input type="text" id="nome" name="nome" required><br>
        <label for="lat">Latitudine:</label><br>
        <input type="text" id="lat" name="lat" required><br>
        <label for="lon">Longitudine:</label><br>
        <input type="text" id="lon" name="lon" required><br><br>
        <input type="submit" value="Aggiungi Fermata">
    </form>
</body>
</html>
