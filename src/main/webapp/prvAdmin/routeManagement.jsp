<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Gestione Tratte - Admin</title>
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
        .btn-info { background-color: #17a2b8; color: white; }
        
        .routes-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .routes-table th, .routes-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .routes-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .routes-table tr:hover {
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
        .company-badge {
            background: #17a2b8;
            color: white;
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
        }
    </style>
</head>
<body class="admin-layout">
    <div class="admin-container">
        <div class="admin-header">
            <h1>🚌 Gestione Tratte</h1>
            <div>
                <a href="${pageContext.request.contextPath}/prvAdmin/dashboard" class="btn btn-secondary">← Dashboard</a>
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
            <input type="text" id="searchInput" class="search-input" placeholder="Cerca tratte per nome o azienda...">
        </div>
        
        <!-- Statistiche rapide -->
        <div class="stats-container" style="display: flex; gap: 20px; margin-bottom: 30px;">
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>🚌 Tratte Totali</h3>
                <p style="font-size: 24px; font-weight: bold; color: #007bff;">${fn:length(routes)}</p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>✅ Tratte Attive</h3>
                <p style="font-size: 24px; font-weight: bold; color: #28a745;">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="route" items="${routes}">
                        <c:if test="${route.attiva}">
                            <c:set var="activeCount" value="${activeCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${activeCount}
                </p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>❌ Tratte Inattive</h3>
                <p style="font-size: 24px; font-weight: bold; color: #dc3545;">
                    <c:set var="inactiveCount" value="0"/>
                    <c:forEach var="route" items="${routes}">
                        <c:if test="${!route.attiva}">
                            <c:set var="inactiveCount" value="${inactiveCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${inactiveCount}
                </p>
            </div>
            <div class="stat-card" style="padding: 20px; background: #f8f9fa; border-radius: 8px; flex: 1;">
                <h3>🏢 Aziende Coinvolte</h3>
                <p style="font-size: 24px; font-weight: bold; color: #17a2b8;">${fn:length(companies)}</p>
            </div>
        </div>
        
        <!-- Tabella tratte -->
        <table class="routes-table" id="routesTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome Tratta</th>
                    <th>Azienda</th>
                    <th>Costo</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="route" items="${routes}">
                    <tr data-route-id="${route.id}">
                        <td>${route.id}</td>
                        <td><strong>${route.nome}</strong></td>
                        <td>
                            <c:forEach var="company" items="${companies}">
                                <c:if test="${company.id == route.azienda.id}">
                                    <span class="company-badge">${company.nome}</span>
                                </c:if>
                            </c:forEach>
                        </td>
                        <td>€${route.costo}</td>
                        <td>
                            <span class="status-badge ${route.attiva ? 'status-active' : 'status-inactive'}">
                                ${route.attiva ? 'ATTIVA' : 'INATTIVA'}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/prvAdmin/routes?action=edit&id=${route.id}" 
                               class="btn btn-warning btn-sm">✏️ Modifica</a>
                            
                            <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/routes">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="id" value="${route.id}">
                                <button type="submit" class="btn ${route.attiva ? 'btn-warning' : 'btn-success'} btn-sm" 
                                        onclick="return confirm('${route.attiva ? 'Disattivare' : 'Attivare'} questa tratta?')">
                                    ${route.attiva ? '⏸️ Disattiva' : '▶️ Attiva'}
                                </button>
                            </form>
                            
                            <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/routes">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${route.id}">
                                <button type="submit" class="btn btn-danger btn-sm" 
                                        onclick="return confirm('Sei sicuro di voler eliminare questa tratta? Questa azione non può essere annullata.')">
                                    🗑️ Elimina
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${empty routes}">
            <div style="text-align: center; padding: 50px; color: #6c757d;">
                <h3>🚌 Nessuna tratta trovata</h3>
                <p>Non ci sono tratte registrate nel sistema.</p>
                <a href="${pageContext.request.contextPath}/prvAdmin/routes?action=create" class="btn btn-primary">
                    + Crea la prima tratta
                </a>
            </div>
        </c:if>
    </div>

    <script>
        // Funzione di ricerca
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const table = document.getElementById('routesTable');
            const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                let found = false;
                
                // Cerca in nome tratta e azienda (colonne 1 e 2)
                for (let i = 1; i <= 2; i++) {
                    if (cells[i] && cells[i].textContent.toLowerCase().includes(searchTerm)) {
                        found = true;
                        break;
                    }
                }
                
                row.style.display = found ? '' : 'none';
            }
        });

        // Conferma eliminazione con nome tratta
        document.querySelectorAll('form[action*="routes"] button[onclick*="eliminare"]').forEach(button => {
            button.addEventListener('click', function(e) {
                const row = this.closest('tr');
                const routeName = row.querySelector('td:nth-child(2) strong').textContent;
                const confirmed = confirm(`Sei sicuro di voler eliminare la tratta "${routeName}"? Questa azione non può essere annullata.`);
                if (!confirmed) {
                    e.preventDefault();
                }
            });
        });

        // Animazioni per le statistiche
        document.addEventListener('DOMContentLoaded', function() {
            const statNumbers = document.querySelectorAll('.stat-card p');
            statNumbers.forEach(stat => {
                const target = parseInt(stat.textContent);
                if (!isNaN(target)) {
                    let current = 0;
                    const increment = target / 30;
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
    </script>
</body>
</html>
