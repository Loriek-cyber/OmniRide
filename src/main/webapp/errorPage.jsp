<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>404 - Pagina non trovata</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 40px;
            text-align: center;
        }
        h1 {
            font-size: 3em;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        p {
            font-size: 1.2em;
            color: #333;
        }
        a {
            color: #3498db;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <h1>404 - Pagina non trovata</h1>
    <p>La pagina che stai cercando non esiste o è stata rimossa.</p>
    <p><a href="<%= request.getContextPath() %>/">Torna alla homepage</a></p>
</body>
</html>