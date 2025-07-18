<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Comunicazioni - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/unified.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="main-with-sidebar">
        <div class="container">
            <h1 class="text-center mb-lg">Comunicazioni</h1>
            
            <div class="two-columns">
                <div class="list-column">
                    <h3 class="mb-md">Messaggi Ricevuti</h3>
                    
                    <div class="card text-center">
                        <h4 class="card-title">Nessun Messaggio</h4>
                        <p class="text-muted">Non hai ricevuto ancora nessun messaggio.</p>
                    </div>
                </div>
                
                <div class="details-column">
                    <div class="card">
                        <div class="card-header">
                            <h4 class="card-title">Invia Comunicazione</h4>
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Destinatario</label>
                            <select class="form-input">
                                <option>Seleziona destinatario</option>
                                <option>Tutti gli utenti</option>
                                <option>Amministratori</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Oggetto</label>
                            <input type="text" class="form-input" placeholder="Inserisci l'oggetto">
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">Messaggio</label>
                            <textarea class="form-input" rows="5" placeholder="Inserisci il messaggio"></textarea>
                        </div>
                        
                        <button class="btn btn-primary">Invia Messaggio</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
