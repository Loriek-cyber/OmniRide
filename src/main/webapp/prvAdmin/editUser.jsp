<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Modifica Utente - Admin</title>
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
        .form-control:disabled {
            background-color: #e9ecef;
            opacity: 1;
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
        .btn-warning { background-color: #ffc107; color: black; }
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
        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .form-row {
            display: flex;
            gap: 20px;
        }
        .form-row .form-group {
            flex: 1;
        }
        .user-info {
            background: #e9ecef;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 25px;
        }
        .user-info h3 {
            margin: 0 0 10px 0;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="admin-container">
        <h1>Modifica Utente</h1>
        
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty editUser}">
                <!-- Informazioni correnti utente -->
                <div class="user-info">
                    <h3>👤 Informazioni Correnti</h3>
                    <p><strong>ID:</strong> ${editUser.id}</p>
                    <p><strong>Nome Completo:</strong> ${editUser.nome} ${editUser.cognome}</p>
                    <p><strong>Email:</strong> ${editUser.email}</p>
                    <p><strong>Ruolo Attuale:</strong> 
                        <span style="background: #007bff; color: white; padding: 2px 8px; border-radius: 4px; text-transform: uppercase;">
                            ${editUser.ruolo}
                        </span>
                    </p>
                    <p><strong>Data Registrazione:</strong> ${editUser.dataRegistrazione}</p>
                </div>
                
                <div class="form-container">
                    <form method="post" action="${pageContext.request.contextPath}/prvAdmin/users">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${editUser.id}">
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="nome">Nome *</label>
                                <input type="text" id="nome" name="nome" class="form-control" 
                                       value="${editUser.nome}" required>
                            </div>
                            <div class="form-group">
                                <label for="cognome">Cognome *</label>
                                <input type="text" id="cognome" name="cognome" class="form-control" 
                                       value="${editUser.cognome}" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email *</label>
                            <input type="email" id="email" name="email" class="form-control" 
                                   value="${editUser.email}" required>
                            <small>⚠️ Modificare l'email potrebbe richiedere una nuova verifica</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="password">Nuova Password (opzionale)</label>
                            <input type="password" id="password" name="password" class="form-control" 
                                   minlength="6" placeholder="Lascia vuoto per mantenere la password attuale">
                            <small>💡 Inserisci solo se vuoi cambiare la password (min. 6 caratteri)</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="ruolo">Ruolo *</label>
                            <select id="ruolo" name="ruolo" class="form-control" required>
                                <option value="utente" ${editUser.ruolo == 'utente' ? 'selected' : ''}>👤 Utente Normale</option>
                                <option value="azienda" ${editUser.ruolo == 'azienda' ? 'selected' : ''}>🏢 Azienda</option>
                                <option value="admin" ${editUser.ruolo == 'admin' ? 'selected' : ''}>👑 Amministratore</option>
                            </select>
                            <small>⚠️ Attenzione: modificare il ruolo può cambiare i permessi dell'utente</small>
                        </div>
                        
                        <div class="form-group" style="text-align: center; margin-top: 30px;">
                            <button type="submit" class="btn btn-primary">💾 Salva Modifiche</button>
                            <button type="button" class="btn btn-warning" onclick="resetForm()">🔄 Ripristina</button>
                            <a href="${pageContext.request.contextPath}/prvAdmin/users" class="btn btn-secondary">❌ Annulla</a>
                        </div>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <div class="message error">
                    <h3>❌ Utente non trovato</h3>
                    <p>L'utente richiesto non esiste o non è accessibile.</p>
                    <a href="${pageContext.request.contextPath}/prvAdmin/users" class="btn btn-primary">
                        ← Torna alla Lista Utenti
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        // Valori originali per il reset
        const originalValues = {
            nome: '${editUser.nome}',
            cognome: '${editUser.cognome}',
            email: '${editUser.email}',
            ruolo: '${editUser.ruolo}'
        };
        
        // Funzione per ripristinare i valori originali
        function resetForm() {
            if (confirm('Sei sicuro di voler ripristinare tutti i campi ai valori originali?')) {
                document.getElementById('nome').value = originalValues.nome;
                document.getElementById('cognome').value = originalValues.cognome;
                document.getElementById('email').value = originalValues.email;
                document.getElementById('ruolo').value = originalValues.ruolo;
                document.getElementById('password').value = '';
            }
        }
        
        // Validazione client-side
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const email = document.getElementById('email').value;
            const nome = document.getElementById('nome').value;
            const cognome = document.getElementById('cognome').value;
            
            // Controlla che nome e cognome non siano vuoti
            if (!nome.trim() || !cognome.trim()) {
                alert('Nome e cognome sono obbligatori');
                e.preventDefault();
                return false;
            }
            
            // Se è stata inserita una password, controlla la lunghezza
            if (password && password.length < 6) {
                alert('Se inserisci una nuova password, deve contenere almeno 6 caratteri');
                e.preventDefault();
                return false;
            }
            
            // Controlla formato email
            if (!email.includes('@') || !email.includes('.')) {
                alert('Inserisci un indirizzo email valido');
                e.preventDefault();
                return false;
            }
            
            // Conferma se si sta cambiando il ruolo
            const currentRole = '${editUser.ruolo}';
            const newRole = document.getElementById('ruolo').value;
            if (currentRole !== newRole) {
                const roleNames = {
                    'utente': 'Utente Normale',
                    'azienda': 'Azienda', 
                    'admin': 'Amministratore'
                };
                
                if (!confirm(`Stai cambiando il ruolo da "${roleNames[currentRole]}" a "${roleNames[newRole]}". Continuare?`)) {
                    e.preventDefault();
                    return false;
                }
            }
        });
        
        // Evidenzia i campi modificati
        document.addEventListener('DOMContentLoaded', function() {
            const fields = ['nome', 'cognome', 'email', 'ruolo'];
            
            fields.forEach(fieldName => {
                const field = document.getElementById(fieldName);
                const originalValue = originalValues[fieldName];
                
                field.addEventListener('input', function() {
                    if (this.value !== originalValue) {
                        this.style.borderLeft = '4px solid #ffc107';
                    } else {
                        this.style.borderLeft = '';
                    }
                });
            });
            
            // Per il campo password
            document.getElementById('password').addEventListener('input', function() {
                if (this.value) {
                    this.style.borderLeft = '4px solid #ffc107';
                } else {
                    this.style.borderLeft = '';
                }
            });
        });
    </script>
</body>
</html>
