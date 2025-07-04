<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="import/metadata.jsp"%>
<html>
<head>
    <title>Biglietti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/userSidebar.css">
</head>
<body class="user-dashboard-layout" data-page="biglietti">
    <c:if test="${not empty sessionScope.utente}">
        <!-- Include sidebar -->
        <jsp:include page="/import/userSidebar.jsp"/>
        
        <!-- Contenuto principale -->
        <div class="user-main-content">
            <%@include file="import/header.jsp"%>
            
            <main>
                <div class="content-header">
                    <h1>I Miei Biglietti</h1>
                    <div class="breadcrumb">Area Utente / Biglietti</div>
                </div>
                
                <div class="user-content">
                    <h2>I tuoi Biglietti</h2>
                    <p>Qui potrai visualizzare e gestire i tuoi biglietti acquistati.</p>
                    
                    <div class="empty-state">
                        <h3>Nessun Biglietto trovato</h3>
                        <p>Non hai ancora acquistato nessun biglietto. Inizia a pianificare il tuo prossimo viaggio!</p>
                        <div class="dashboard-actions">
                            <a href="${pageContext.request.contextPath}/tratte.jsp" class="btn">Cerca Tratte</a>
                            <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Torna alla Home</a>
                        </div>
                    </div>
                </div>
            </main>
            
            <%@include file="import/footer.jsp"%>
        </div>
    </c:if>
    
    <c:if test="${empty sessionScope.utente}">
        <%@include file="import/header.jsp"%>
        <main>
            <div class="content">
                <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
            </div>
        </main>
        <%@include file="import/footer.jsp"%>
    </c:if>
</body>
</html>
