<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Gestione Avvisi - Admin</title>
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
        
        .avvisi-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .avvisi-table th, .avvisi-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .avvisi-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .avvisi-table tr:hover {
            background-color: #f5f5f5;
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
        
        .avviso-description {
            max-width: 300px;
            word-wrap: break-word;
        }
        
        .tratte-badge {
            background-color: #007bff;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
            margin: 2px;
            display: inline-block;
        }
        
        .stats-container {
            display: flex;
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
            flex: 1;
            text-align: center;
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #007bff;
        }
    </style>
</head>
<body>
    <div class="admin-container">
        <div class="admin-header">
            <h1>🔔 Gestione Avvisi</h1>
            <div>
                <a href="${pageContext.request.contextPath}/prvAdmin/dashboard" class="btn btn-secondary">← Dashboard</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/avvisi?action=create" class="btn btn-primary">+ Nuovo Avviso</a>
            </div>
        </div>
        
        <!-- Messaggi di successo/errore -->
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <!-- Statistiche rapide -->
        <div class="stats-container">
            <div class="stat-card">
                <h3>📊 Totale Avvisi</h3>
                <div class="stat-number">${fn:length(avvisi)}</div>
            </div>
            <div class="stat-card">
                <h3>🚌 Tratte Totali</h3>
                <div class="stat-number">${fn:length(tratte)}</div>
            </div>
            <div class="stat-card">
                <h3>⚠️ Avvisi Attivi</h3>
                <div class="stat-number">
                    <c:set var="avvisiAttivi" value="0"/>
                    <c:forEach var="avviso" items="${avvisi}">
                        <c:if test="${not empty avviso.id_tratte_coinvolte}">
                            <c:set var="avvisiAttivi" value="${avvisiAttivi + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${avvisiAttivi}
                </div>
            </div>
        </div>
        
        <!-- Barra di ricerca -->
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="Cerca avvisi per descrizione...">
        </div>
        
        <!-- Tabella avvisi -->
        <table class="avvisi-table" id="avvisiTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descrizione</th>
                    <th>Tratte Coinvolte</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="avviso" items="${avvisi}">
                    <tr>
                        <td>#${avviso.id}</td>
                        <td class="avviso-description">${avviso.descrizione}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty avviso.id_tratte_coinvolte}">
                                    <c:forEach var="trattaId" items="${avviso.id_tratte_coinvolte}">
                                        <c:forEach var="tratta" items="${tratte}">
                                            <c:if test="${tratta.id == trattaId}">
                                                <span class="tratte-badge">${tratta.nome}</span>
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <em style="color: #6c757d;">Nessuna tratta specificata</em>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/prvAdmin/avvisi?action=edit&id=${avviso.id}" 
                               class="btn btn-warning btn-sm">✏️ Modifica</a>
                            
                            <form style="display: inline;" method="post" action="${pageContext.request.contextPath}/prvAdmin/avvisi">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${avviso.id}">
                                <button type="submit" class="btn btn-danger btn-sm" 
                                        onclick="return confirm('Sei sicuro di voler eliminare questo avviso? Questa azione non può essere annullata.')">
                                    🗑️ Elimina
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <c:if test="${empty avvisi}">
            <div style="text-align: center; padding: 50px; color: #6c757d;">
                <h3>🔔 Nessun avviso trovato</h3>
                <p>Non ci sono avvisi registrati nel sistema.</p>
                <a href="${pageContext.request.contextPath}/prvAdmin/avvisi?action=create" class="btn btn-primary">
                    + Crea il primo avviso
                </a>
            </div>
        </c:if>
    </div>

    <script>
        // Funzione di ricerca
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const table = document.getElementById('avvisiTable');
            const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
            
            for (let row of rows) {
                const cells = row.getElementsByTagName('td');
                let found = false;
                
                // Cerca nella descrizione (colonna 1)
                if (cells[1] && cells[1].textContent.toLowerCase().includes(searchTerm)) {
                    found = true;
                }
                
                // Cerca anche nelle tratte (colonna 2)
                if (cells[2] && cells[2].textContent.toLowerCase().includes(searchTerm)) {
                    found = true;
                }
                
                row.style.display = found ? '' : 'none';
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
    </script>
</body>
</html>
