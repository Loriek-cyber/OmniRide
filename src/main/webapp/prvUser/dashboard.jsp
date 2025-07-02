<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Dashboard Utente</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/main.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <div class="form-container">
        <h2>La tua Dashboard</h2>

        <%-- Controlliamo se l'utente è in sessione prima di salutarlo --%>
        <c:if test="${not empty sessionScope.utente}">
            <h3>Benvenuto, <c:out value="${sessionScope.utente.nome}"/> <c:out value="${sessionScope.utente.cognome}"/>!</h3>
            <p>Questa è la tua area personale. Da qui potrai gestire i tuoi dati e i tuoi biglietti.</p>
            
            <a href="${pageContext.request.contextPath}/logout" class="btn">Logout</a>
        </c:if>

        <%-- Se per qualche motivo l'utente arriva qui senza sessione, mostriamo un messaggio --%>
        <c:if test="${empty sessionScope.utente}">
            <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
        </c:if>

    </div>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
