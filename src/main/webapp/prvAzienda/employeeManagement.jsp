<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Gestione Dipendenti - Azienda</title>
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
        
        .employees-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .employees-table th, .employees-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .employees-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .employees-table tr:hover {
            background-color: #f5f5f5;
        }
        .status-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .status-active { background-color: #28a745; color: white; }
        .status-inactive { background-color: #dc3545; color: white; }
        .role-badge {
            background: #17a2b8;
            color: white;
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
        }
        
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
        
        .hire-form {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .form-row {
            display: flex;
            gap: 15px;
            align-items: end;
        }
        
        .form-group {
            flex: 1;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
    </style>
</head>
<body class="admin-layout">
    <div class="admin-container">
        <div class="admin-header">
            <h1>👷 Gestione Dipendenti</h1>
            <div>
                <a href="${pageContext.request.contextPath}/prvAzienda/dashboard" class="btn btn-secondary">← Dashboard</a>
                <a href="${pageContext.request.contextPath}/prvAzienda/routes" class="btn btn-primary">🚌 Le Mie Tratte</a>
            </div>
        </div>
        
        <!-- Messaggi di successo/errore -->
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <!-- Form per assumere dipendenti -->
        <div class="hire-form">
            <h3>📧 Assumi Nuovo Dipendente</h3>
            <form method="post" action="${pageContext.request.contextPath}/prvAzienda/employees">
                <input type="hidden" name="action" value="hire">
                <div class="form-row">
                    <div class="form-group">
                        <label for="userEmail">Email Utente *</label>
                        <input type="email" id="userEmail" name="userEmail" class="form-control" 
                               placeholder="mario.rossi@email.com" required>
                        <small>Inserisci l'email dell'utente che vuoi assumere</small>
                    </div>
                    <div class="form-group">
                        <label for="ruolo">Ruolo *</label>
                        <select id="ruolo" name="ruolo" class="form-control" required>
                            <option value="">Seleziona ruolo</option>
                            <option value="AUTISTA">Autista</option>
                            <option value="CONTROLLORE">Controllore</option>
                            <option value="GESTORE">Gestore</option>
                            <option value="SUPERVISORE">Supervisore</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-success">✅ Assumi</button>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Barra di ricerca -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="Cerca dipendenti per nome o email...">
        </div>
        
        <!-- Statistiche rapide -->
        <div class="stats-container" style="display: flex; gap: 20px; margin-bottom: 30px;">
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>👥 Dipendenti Totali</h3>
                <p style="font-size: 24px; font-weight: bold; color: #007bff;">${fn:length(employees)}</p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>✅ Dipendenti Attivi</h3>
                <p style="font-size: 24px; font-weight: bold; color: #28a745;">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="employee" items="${employees}">
                        <c:if test="${employee.attivo}">
                            <c:set var="activeCount" value="${activeCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${activeCount}
                </p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>⏸️ Dipendenti Sospesi</h3>
                <p style="font-size: 24px; font-weight: bold; color: #dc3545;">
                    <c:set var="inactiveCount" value="0"/>
                    <c:forEach var="employee" items="${employees}">
                        <c:if test="${!employee.attivo}">
                            <c:set var="inactiveCount" value="${inactiveCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${inactiveCount}
                </p>
            </div>
        </div>
        
        <!-- Tabella dipendenti -->
        <table class="employees-table" id="employeesTable">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Ruolo</th>
                    <th>Data Assunzione</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="employee" items="${employees}">
                    <c:set var="employeeUser" value="${null}"/>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.id == employee.id_utente}">
                            <c:set var="employeeUser" value="${user}"/>
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${employeeUser != null}">
                        <tr data-employee-id="${employee.id_utente}">
                            <td><strong>${employeeUser.nome} ${employeeUser.cognome}</strong></td>
                            <td>${employeeUser.email}</td>
                            <td>
                                <span class="role-badge">${employee.ruolo}</span>
                            </td>
                            <td>${employee.dataAssunzione}</td>
                            <td>
                                <span class="status-badge ${employee.attivo ? 'status-active' : 'status-inactive'}">
                                    ${employee.attivo ? 'ATTIVO' : 'SOSPESO'}
                                </span>
                            </td>
                            <td>
                                <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAzienda/employees">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="userId" value="${employee.id_utente}">
                                    <button type="submit" class="btn ${employee.attivo ? 'btn-warning' : 'btn-success'} btn-sm" 
                                            onclick="return confirm('${employee.attivo ? 'Sospendere' : 'Riattivare'} questo dipendente?')">
                                        ${employee.attivo ? '⏸️ Sospendi' : '▶️ Riattiva'}
                                    </button>
                                </form>
                                
                                <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAzienda/employees">
                                    <input type="hidden" name="action" value="fire">
                                    <input type="hidden" name="userId" value="${employee.id_utente}">
                                    <button type="submit" class="btn btn-danger btn-sm" 
                                            onclick="return confirm('Sei sicuro di voler licenziare ${employeeUser.nome} ${employeeUser.cognome}? Questa azione non può essere annullata.')">
                                        🗑️ Licenzia
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${empty employees}">
            <div style="text-align: center; padding: 50px; color: #6c757d;">
                <h3>👷 Nessun dipendente trovato</h3>
                <p>La tua azienda non ha ancora dipendenti.</p>
                <p><strong>💡 Suggerimento:</strong> Usa il form sopra per assumere il tuo primo dipendente inserendo la sua email!</p>
            </div>
        </c:if>
    </div>

    <script>
        // Funzione di ricerca
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const table = document.getElementById('employeesTable');
            const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                let found = false;
                
                // Cerca in nome e email (colonne 0 e 1)
                for (let i = 0; i <= 1; i++) {
                    if (cells[i] && cells[i].textContent.toLowerCase().includes(searchTerm)) {
                        found = true;
                        break;
                    }
                }
                
                row.style.display = found ? '' : 'none';
            }
        });

        // Validazione email in tempo reale
        document.getElementById('userEmail').addEventListener('input', function() {
            const email = this.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            
            if (email && !emailRegex.test(email)) {
                this.style.borderColor = '#dc3545';
                this.title = 'Email non valida';
            } else {
                this.style.borderColor = '#28a745';
                this.title = '';
            }
        });

        // Animazioni per le statistiche
        document.addEventListener('DOMContentLoaded', function() {
            const statNumbers = document.querySelectorAll('.stat-card p');
            statNumbers.forEach(stat => {
                const target = parseInt(stat.textContent);
                if (!isNaN(target)) {
                    let current = 0;
                    const increment = target / 20;
                    const timer = setInterval(() => {
                        current += increment;
                        if (current >= target) {
                            stat.textContent = target;
                            clearInterval(timer);
                        } else {
                            stat.textContent = Math.floor(current);
                        }
                    }, 50);
                }
            });
        });

        // Suggerimenti per i ruoli
        document.getElementById('ruolo').addEventListener('change', function() {
            const descriptions = {
                'AUTISTA': 'Guida i veicoli sulle tratte assegnate',
                'CONTROLLORE': 'Verifica e convalida i biglietti',
                'GESTORE': 'Gestisce operazioni e logistica',
                'SUPERVISORE': 'Supervisiona le attività del personale'
            };
            
            const desc = descriptions[this.value];
            if (desc) {
                this.title = desc;
            }
        });
    </script>
</body>
</html>
