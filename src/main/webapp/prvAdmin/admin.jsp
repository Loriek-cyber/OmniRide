<%--
  Created by IntelliJ IDEA.
  User: Arjel
  Date: 02/07/2025
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Mode</title>
    <jsp:include page="/import/metadata.jsp"/>
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <main>
        <div id="content" class="content">
            <h2>Area Amministratore</h2>
            <p>Benvenuto nell'area di amministrazione. Qui puoi gestire utenti, tratte e altre impostazioni del sistema.</p>
            <p>Funzionalità disponibili:</p>
            <ul>
                <li><a href="#">Gestione Utenti</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/gestoreTratte">Gestione Tratte</a></li>
                <li><a href="addTratta.jsp">Aggiungi Tratta</a></li>
                <li><a href="addFermata.jsp">Aggiungi Fermata</a></li>
                <li><a href="#">Report e Statistiche</a></li>
            </ul>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Logout Admin</a>
        </div>
    </main>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
