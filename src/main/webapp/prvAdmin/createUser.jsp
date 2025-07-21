<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Crea Nuovo Utente - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/user-management.css">
</head>
<body>
    <div class="admin-container">
        <h1>Crea Nuovo Utente</h1>
        
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <div class="form-container">
            <form method="post" action="${pageContext.request.contextPath}/prvAdmin/users">
                <input type="hidden" name="action" value="create">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="nome">Nome *</label>
                        <input type="text" id="nome" name="nome" class="form-control" 
                               value="${param.nome}" required>
                    </div>
                    <div class="form-group">
                        <label for="cognome">Cognome *</label>
                        <input type="text" id="cognome" name="cognome" class="form-control" 
                               value="${param.cognome}" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           value="${param.email}" required>
                    <small>L'email deve essere unica nel sistema</small>
                </div>
                
                <div class="form-group">
                    <label for="password">Password *</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           minlength="6" required>
                    <small>La password deve contenere almeno 6 caratteri</small>
                </div>
                
                <div class="form-group">
                    <label for="ruolo">Ruolo *</label>
                    <select id="ruolo" name="ruolo" class="form-control" required>
                        <option value="">Seleziona un ruolo</option>
                        <option value="utente" ${param.ruolo == 'utente' ? 'selected' : ''}>Utente Normale</option>
                        <option value="azienda" ${param.ruolo == 'azienda' ? 'selected' : ''}>Azienda</option>
                        <option value="admin" ${param.ruolo == 'admin' ? 'selected' : ''}>Amministratore</option>
                    </select>
                </div>
                
                <div class="form-group text-center mt-30">
                    <button type="submit" class="btn btn-primary">✅ Crea Utente</button>
                    <a href="${pageContext.request.contextPath}/prvAdmin/users" class="btn btn-secondary">❌ Annulla</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Validazione client-side
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const email = document.getElementById('email').value;
            
            if (password.length < 6) {
                alert('La password deve contenere almeno 6 caratteri');
                e.preventDefault();
                return false;
            }
            
            if (!email.includes('@')) {
                alert('Inserisci un indirizzo email valido');
                e.preventDefault();
                return false;
            }
        });
    </script>
</body>
</html>
