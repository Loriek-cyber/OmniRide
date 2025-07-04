<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Biglietti</title>
    <%@include file="import/metadata.jsp"%>
</head>
<body>
    <%@include file="import/header.jsp"%>
    <main>
    <div class="content">
        <h2>I tuoi Biglietti</h2>
        <p>Qui potrai visualizzare i tuoi biglietti acquistati.</p>
        <div class="empty-state">
            <h3>🎫 Nessun Biglietto trovato</h3>
            <p>Non hai ancora acquistato nessun biglietto. Inizia a pianificare il tuo prossimo viaggio!</p>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Cerca Tratte</a>
        </div>
    </div>
</main>
    <%@include file="import/footer.jsp"%>
</body>
</html>
