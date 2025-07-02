<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@include file="import/metadata.jsp" %>
    <title>Risultati</title>
</head>
<body>
<%@include file="import/header.jsp" %>

<main>
    <div id="content">
        <h2>Risultati della Ricerca</h2>
        <div id="results">
            <%
                // Sezione di reindirizzamenti in mancanza di dati
                for (int i = 0; i < 3; i++) { // Ridotto a 3 per un esempio più conciso
            %>
            <p>Risultato di esempio <%= i + 1 %></p>
            <%
                }
            %>
            <div class="empty-state">
                <h3>🔍 Nessun risultato trovato</h3>
                <p>Siamo spiacenti, la tua ricerca non ha prodotto risultati. Prova a modificare i criteri.</p>
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Nuova Ricerca</a>
            </div>
        </div>
    </div>
</main>

<%@include file="import/footer.jsp" %>
</body>
</html>
