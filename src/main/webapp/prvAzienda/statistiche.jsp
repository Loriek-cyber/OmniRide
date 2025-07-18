<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Statistiche - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/unified.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="main-with-sidebar">
        <div class="container">
            <h1 class="text-center mb-lg">Statistiche Azienda</h1>
            
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Dashboard Statistiche</h3>
                </div>
                <div class="two-columns">
                    <div class="list-column">
                        <h4>Metriche Principali</h4>
                        <ul>
                            <li>Numero tratte attive: <strong>0</strong></li>
                            <li>Passeggeri trasportati: <strong>0</strong></li>
                            <li>Ricavi totali: <strong>€0.00</strong></li>
                            <li>Biglietti venduti: <strong>0</strong></li>
                        </ul>
                    </div>
                    <div class="details-column">
                        <h4>Grafici</h4>
                        <div class="details-panel">
                            <p class="text-muted">I grafici delle statistiche saranno disponibili quando avrai dati sufficienti da analizzare.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
