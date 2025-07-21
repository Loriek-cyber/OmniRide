<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/userSidebar.css">
</head>
<body class="user-dashboard-layout" data-page="dashboard">
    <c:if test="${not empty sessionScope.utente}">
        <!-- Include sidebar -->
        <jsp:include page="/import/userSidebar.jsp"/>
        
        <!-- Contenuto principale -->
        <div class="user-main-content">
            <jsp:include page="/import/header.jsp"/>
            
            <main>
                <div class="content-header">
                    <h1>Dashboard</h1>
                    <div class="breadcrumb">Area Utente / Dashboard</div>
                </div>
                
                <div class="user-content">
                    <h2>Benvenuto, <c:out value="${sessionScope.utente.nome}"/>!</h2>

                    <%-- Sezione Utente Normale --%>
                    <c:if test="${sessionScope.utente.ruolo == 'utente'}">
                        <h3>La tua area personale</h3>
                        <p>Da qui puoi gestire il tuo profilo, visualizzare i tuoi biglietti e accedere a tutte le funzionalità di Omniride.</p>
                        
                        <div class="dashboard-actions">
                            <a href="${pageContext.request.contextPath}/updateProfile" class="btn">Modifica Profilo</a>
                            <a href="${pageContext.request.contextPath}/wallet" class="btn">I Miei Biglietti</a>
                            <a href="${pageContext.request.contextPath}/storicoBiglietti" class="btn btn-primary">Storico Acquisti</a>
                            <a href="${pageContext.request.contextPath}/visualizzaTrattep" class="btn">Cerca Tratte</a>
                        </div>
                    </c:if>

                    <%-- Sezione Azienda --%>
                    <c:if test="${sessionScope.utente.ruolo == 'azienda'}">
                        <h3>Area gestione azienda: <strong><c:out value="${sessionScope.utente.nome}"/></strong></h3>
                        <p>Gestisci la tua azienda di trasporti e monitora le tue tratte.</p>
                        
                        <div class="dashboard-actions">
                            <a href="${pageContext.request.contextPath}/prvAzienda/dashboard" class="btn">Dashboard Azienda Completa</a>
                            <a href="${pageContext.request.contextPath}/prvAdmin/gestoreTratte" class="btn">Gestisci le Tue Tratte</a>
                            <a href="${pageContext.request.contextPath}/tratte.jsp" class="btn">Visualizza Tutte le Tratte</a>
                            <a href="${pageContext.request.contextPath}/prvUser/editProfile.jsp" class="btn">Impostazioni Account</a>
                        </div>
                    </c:if>
                </div>
            </main>
            
            <jsp:include page="/import/footer.jsp"/>
        </div>
    </c:if>
    
    <c:if test="${empty sessionScope.utente}">
        <jsp:include page="/import/header.jsp"/>
        <main>
            <div class="content">
                <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
            </div>
        </main>
    </c:if>
</body>
</html>
