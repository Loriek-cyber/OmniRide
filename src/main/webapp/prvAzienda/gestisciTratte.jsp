<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Gestione Tratte - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/unified.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="main-with-sidebar">
        <div class="container">
            <h1 class="text-center mb-lg">Gestione Tratte</h1>
            
            <div class="two-columns">
                <div class="list-column">
                    <h3 class="mb-md">Le tue Tratte</h3>
                    
                    <div class="card">
                        <div class="card-header">
                            <h4 class="card-title">Nessuna Tratta</h4>
                        </div>
                        <p class="text-muted">Non hai ancora creato nessuna tratta.</p>
                        <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">Aggiungi Tratta</a>
                    </div>
                </div>
                
                <div class="details-column">
                    <div class="details-panel empty">
                        <p>Seleziona una tratta dalla lista per visualizzarne i dettagli</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
