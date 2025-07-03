<%--
  Created by IntelliJ IDEA.
  User: Arjel
  Date: 02/07/2025
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
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
            <h3>🎫 Nessun biglietto trovato</h3>
            <p>Non hai ancora acquistato nessun biglietto. Inizia a pianificare il tuo prossimo viaggio!</p>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Cerca Tratte</a>
        </div>
    </div>
</main>
    <%@include file="import/footer.jsp"%>
</body>
</html>
