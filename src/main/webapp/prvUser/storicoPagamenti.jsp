<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Storico Pagamenti - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
</head>
<body class="user-dashboard-layout" data-page="storico">
    <c:if test="${not empty sessionScope.utente}">
        <!-- Include sidebar -->
        <jsp:include page="/import/userSidebar.jsp"/>
        
        <!-- Main content -->
        <div class="user-main-content">
            <jsp:include page="/import/header.jsp"/>
            <main>
                <div class="content-header">
                    <h1>Storico Pagamenti</h1>
                    <div class="breadcrumb">Area Utente / Storico Pagamenti</div>
                </div>
                
                <div class="user-content">
                    <c:if test="${empty biglietti}">
                        <p>Non ci sono pagamenti registrati.</p>
                    </c:if>
                    <c:if test="${not empty biglietti}">
                        <table class="payments-table">
                            <thead>
                                <tr>
                                    <th>Data</th>
                                    <th>Importo</th>
                                    <th>Descrizione</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="biglietto" items="${biglietti}">
                                    <tr>
                                        <td>${biglietto.dataAcquisto}</td>
                                        <td>${biglietto.importo}</td>
                                        <td>${biglietto.descrizione}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
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

