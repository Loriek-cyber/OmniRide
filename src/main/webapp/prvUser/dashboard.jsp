<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <main>
        <div class="container">
            <c:if test="${not empty sessionScope.utente}">
                <h2>Dashboard</h2>
                <h3>Benvenuto, <c:out value="${sessionScope.utente.nome}"/>!</h3>

                <%-- Sezione Utente Normale --%>
                <c:if test="${sessionScope.utente.ruolo == 'utente'}">
                    <p>Questa è la tua area personale. Da qui puoi gestire il tuo profilo e i tuoi biglietti.</p>
                    <div class="dashboard-actions">
                        <a href="${pageContext.request.contextPath}/prvUser/editProfile.jsp" class="btn">Modifica Profilo</a>
                        <a href="${pageContext.request.contextPath}/biglietti.jsp" class="btn">I Miei Biglietti</a>
                        <a href="#" class="btn">Ricarica Credito</a>
                    </div>
                </c:if>

                <%-- Sezione Azienda --%>
                <c:if test="${sessionScope.utente.ruolo == 'azienda'}">
                    <p>Questa è l'area di gestione per la tua azienda: <strong><c:out value="${sessionScope.utente.azienda.nome}"/></strong>.</p>
                    <div class="dashboard-actions">
                        <a href="${pageContext.request.contextPath}/prvAzienda/gestoreTratte.jsp" class="btn">Gestisci le Tue Tratte</a>
                        <a href="${pageContext.request.contextPath}/prvAzienda/gestoreFermate.jsp" class="btn">Gestisci Fermate (Pubbliche)</a>
                        <a href="${pageContext.request.contextPath}/prvUser/editProfile.jsp" class="btn">Impostazioni Account</a>
                    </div>
                </c:if>

            </c:if>

            <c:if test="${empty sessionScope.utente}">
                <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
            </c:if>
        </div>
    </main>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>