<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Gestione Utenti - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <style>
        .admin-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .admin-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        .btn {
            padding: 10px 20px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-success { background-color: #28a745; color: white; }
        .btn-danger { background-color: #dc3545; color: white; }
        .btn-warning { background-color: #ffc107; color: black; }
        .btn-secondary { background-color: #6c757d; color: white; }
        
        .users-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .users-table th, .users-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .users-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .users-table tr:hover {
            background-color: #f5f5f5;
        }
        .role-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .role-admin { background-color: #dc3545; color: white; }
        .role-azienda { background-color: #17a2b8; color: white; }
        .role-utente { background-color: #28a745; color: white; }
        
        .message {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
        }
        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .search-container {
            margin-bottom: 20px;
        }
        .search-input {
            padding: 10px;
            width: 300px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
    </style>
</head>
<body class="admin-layout">
    <div class="admin-container">
        <div class="admin-header">
            <h1>Gestione Utenti</h1>
            <div>
                <a href="${pageContext.request.contextPath}/prvAdmin/dashboard" class="btn btn-secondary">← Dashboard</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/users?action=create" class="btn btn-primary">+ Nuovo Utente</a>
            </div>
        </div>
        
        <!-- Messaggi di successo/errore -->
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <!-- Barra di ricerca -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="Cerca utenti per nome, cognome o email...">
        </div>
        
        <!-- Statistiche rapide -->
        <div class="stats-container" style="display: flex; gap: 20px; margin-bottom: 30px;">
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>Totale Utenti</h3>
                <p style="font-size: 24px; font-weight: bold; color: #007bff;">${fn:length(users)}</p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>Amministratori</h3>
                <p style="font-size: 24px; font-weight: bold; color: #dc3545;">
                    <c:set var="adminCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.ruolo eq 'admin'}">
                            <c:set var="adminCount" value="${adminCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${adminCount}
                </p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>Aziende</h3>
                <p style="font-size: 24px; font-weight: bold; color: #17a2b8;">
                    <c:set var="aziendaCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.ruolo eq 'azienda'}">
                            <c:set var="aziendaCount" value="${aziendaCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${aziendaCount}
                </p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>Utenti Normali</h3>
                <p style="font-size: 24px; font-weight: bold; color: #28a745;">
                    <c:set var="utenteCount" value="0"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.ruolo eq 'utente'}">
                            <c:set var="utenteCount" value="${utenteCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${utenteCount}
                </p>
            </div>
        </div>
        
        <!-- Tabella utenti -->
        <table class="users-table" id="usersTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Cognome</th>
                    <th>Email</th>
                    <th>Ruolo</th>
                    <th>Data Registrazione</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.nome}</td>
                        <td>${user.cognome}</td>
                        <td>${user.email}</td>
                        <td>
                            <span class="role-badge role-${user.ruolo}">
                                ${user.ruolo}
                            </span>
                        </td>
                        <td>${user.dataRegistrazione}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/prvAdmin/users?action=edit&id=${user.id}" 
                               class="btn btn-warning btn-sm">✏️ Modifica</a>
                            
                            <c:if test="${user.ruolo eq 'utente'}">
                                <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/users">
                                    <input type="hidden" name="action" value="promote">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button type="submit" class="btn btn-success btn-sm" 
                                            onclick="return confirm('Promuovere questo utente ad amministratore?')">
                                        ⬆️ Promuovi
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${user.ruolo eq 'admin' && user.id != sessionScope.utente.id}">
                                <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/users">
                                    <input type="hidden" name="action" value="demote">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button type="submit" class="btn btn-warning btn-sm" 
                                            onclick="return confirm('Retrocedere questo amministratore?')">
                                        ⬇️ Retroced
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${user.id != sessionScope.utente.id}">
                                <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/users">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button type="submit" class="btn btn-danger btn-sm" 
                                            onclick="return confirm('Sei sicuro di voler eliminare questo utente? Questa azione non può essere annullata.')">
                                        🗑️ Elimina
                                    </button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${empty users}">
            <div style="text-align: center; padding: 50px; color: #6c757d;">
                <h3>Nessun utente trovato</h3>
                <p>Non ci sono utenti registrati nel sistema.</p>
            </div>
        </c:if>
    </div>

    <script>
        // Funzione di ricerca
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const table = document.getElementById('usersTable');
            const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                let found = false;
                
                // Cerca in nome, cognome e email (colonne 1, 2, 3)
                for (let i = 1; i <= 3; i++) {
                    if (cells[i] && cells[i].textContent.toLowerCase().includes(searchTerm)) {
                        found = true;
                        break;
                    }
                }
                
                row.style.display = found ? '' : 'none';
            }
        });
    </script>
</body>
</html>
