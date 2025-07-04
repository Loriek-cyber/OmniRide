<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Registra la Tua Azienda - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/form.css">
</head>
<body>
<jsp:include page="/import/header.jsp"/>

<main>
    <div class="form-container">
        <h2>Registra la Tua Azienda di Trasporti</h2>
        <p>Crea un account per la tua azienda per iniziare a gestire tratte e fermate.</p>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <c:if test="${not empty successMessage}">
            <div class="success-message">${successMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/registerAzienda" method="post" onsubmit="return validateAziendaForm()">
            <fieldset>
                <legend>Dati Azienda</legend>
                <div class="form-group">
                    <label for="nomeAzienda">Nome Azienda</label>
                    <input type="text" id="nomeAzienda" name="nomeAzienda" required minlength="2" maxlength="100">
                </div>
                <div class="form-group">
                    <label for="tipoAzienda">Tipo di Servizio</label>
                    <select id="tipoAzienda" name="tipoAzienda" required>
                        <option value="">Seleziona...</option>
                        <option value="urbana">Urbana</option>
                        <option value="extraurbana">Extraurbana</option>
                        <option value="turistica">Turistica</option>
                        <option value="ferroviaria">Ferroviaria</option>
                    </select>
                </div>
            </fieldset>

            <fieldset>
                <legend>Dati Account Amministratore</legend>
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" required minlength="2" maxlength="50">
                </div>
                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" required minlength="2" maxlength="50">
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required maxlength="100">
                </div>
                <div class="form-group">
                    <label for="password">Password (min. 6 caratteri)</label>
                    <input type="password" id="password" name="password" required minlength="6" maxlength="100">
                    <small class="form-text">La password deve contenere almeno 6 caratteri</small>
                </div>
            </fieldset>

            <button type="submit" class="btn btn-primary">Registra Azienda</button>
        </form>
        
        <script>
        function validateAziendaForm() {
            const nomeAzienda = document.getElementById('nomeAzienda').value.trim();
            const tipoAzienda = document.getElementById('tipoAzienda').value;
            const nome = document.getElementById('nome').value.trim();
            const cognome = document.getElementById('cognome').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            
            if (nomeAzienda.length < 2) {
                alert('Il nome dell\'azienda deve contenere almeno 2 caratteri');
                return false;
            }
            
            if (!tipoAzienda) {
                alert('Seleziona un tipo di azienda');
                return false;
            }
            
            if (nome.length < 2) {
                alert('Il nome deve contenere almeno 2 caratteri');
                return false;
            }
            
            if (cognome.length < 2) {
                alert('Il cognome deve contenere almeno 2 caratteri');
                return false;
            }
            
            if (!isValidEmail(email)) {
                alert('Inserisci un indirizzo email valido');
                return false;
            }
            
            if (password.length < 6) {
                alert('La password deve contenere almeno 6 caratteri');
                return false;
            }
            
            return true;
        }
        
        function isValidEmail(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        }
        </script>
        <div class="form-link">
            <p>Sei un utente? <a href="${pageContext.request.contextPath}/register">Registrati qui</a>.</p>
        </div>
    </div>
</main>

<jsp:include page="/import/footer.jsp"/>
</body>
</html>
