<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Crea Nuovo Utente - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <style>
        .admin-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .form-container {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        .form-control {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }
        .form-control:focus {
            border-color: #007bff;
            outline: none;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
        }
        .btn {
            padding: 12px 20px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
        }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-secondary { background-color: #6c757d; color: white; }
        .message {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
        }
        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .form-row {
            display: flex;
            gap: 20px;
        }
        .form-row .form-group {
            flex: 1;
        }
    </style>
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
                
                <div class="form-group" style="text-align: center; margin-top: 30px;">
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
