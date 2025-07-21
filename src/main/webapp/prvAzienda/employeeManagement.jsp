<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Gestione Dipendenti - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/employee-management.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        
        <!-- Contenuto principale -->
        <main class="main-content">
            <!-- Toggle button per mobile -->
            <button id="sidebarToggle" class="sidebar-toggle">☰</button>
            
            <div class="content-section active">
                <div class="content-header">
                    <h1>Gestione Dipendenti</h1>
                    <div class="breadcrumb">Dashboard > Gestione Dipendenti</div>
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
                    <small>Inserisci l'email dell'utente che vuoi assumere</small>
                </div>
            </form>
        </div>
        
        <!-- Barra di ricerca -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="Cerca dipendenti per nome o email...">
        </div>
        
        <!-- Statistiche rapide -->
        <div class="stats-container">
            <div class="stat-card stat-total">
                <h3>👥 Dipendenti Totali</h3>
                <p class="stat-number stat-total-number">${fn:length(employees)}</p>
            </div>
            <div class="stat-card stat-active">
                <h3>✅ Dipendenti Attivi</h3>
                <p class="stat-number stat-active-number">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="employee" items="${employees}">
                        <c:if test="${employee.attivo}">
                            <c:set var="activeCount" value="${activeCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${activeCount}
                </p>
            </div>
            <div class="stat-card stat-inactive">
                <h3>⏸️ Dipendenti Sospesi</h3>
                <p class="stat-number stat-inactive-number">
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
            <div class="empty-employees">
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
            const statNumbers = document.querySelectorAll('.stat-number');
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
            </div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>
</body>
</html>
