<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        <form action="${pageContext.request.contextPath}/registerAzienda" method="post">
            <fieldset>
                <legend>Dati Azienda</legend>
                <div class="form-group">
                    <label for="nomeAzienda">Nome Azienda</label>
                    <input type="text" id="nomeAzienda" name="nomeAzienda" required>
                </div>
                <div class="form-group">
                    <label for="tipoAzienda">Tipo di Servizio</label>
                    <select id="tipoAzienda" name="tipoAzienda" required>
                        <option value="AUTOBUS">Autobus</option>
                        <option value="TRENO">Treno</option>
                        <option value="METROPOLITANA">Metropolitana</option>
                        <option value="TRAM">Tram</option>
                    </select>
                </div>
            </fieldset>

            <fieldset>
                <legend>Dati Account Amministratore</legend>
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" required>
                </div>
                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" required>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
            </fieldset>

            <button type="submit" class="btn btn-primary">Registra Azienda</button>
        </form>
        <div class="form-link">
            <p>Sei un utente? <a href="${pageContext.request.contextPath}/register">Registrati qui</a>.</p>
        </div>
    </div>
</main>

<jsp:include page="/import/footer.jsp"/>
</body>
</html>
