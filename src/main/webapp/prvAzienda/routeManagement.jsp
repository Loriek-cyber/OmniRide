<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Gestione Tratte - Azienda</title>
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
    </style>
</head>
<body class="admin-layout">
    <div class="admin-container">
        <div class="admin-header">
            <h1>🚌 Gestione Tratte</h1>
            <div>
                <a href="${pageContext.request.contextPath}/prvAzienda/dashboard" class="btn btn-secondary">← Dashboard</a>
                <a href="${pageContext.request.contextPath}/prvAzienda/addTratta.jsp" class="btn btn-primary">➕ Aggiungi Tratta</a>
            </div>
        </div>

                <!-- Alerts -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> ${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Routes Table -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-list"></i> Le tue Tratte
                            <span class="badge bg-primary ms-2">${routes.size()} tratte</span>
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty routes}">
                                <div class="text-center py-5">
                                    <i class="fas fa-route fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">Nessuna tratta trovata</h5>
                                    <p class="text-muted">Inizia creando la tua prima tratta.</p>
                                    <a href="<c:url value='/prvAzienda/routes?action=create'/>" 
                                       class="btn btn-success">
                                        <i class="fas fa-plus"></i> Crea Prima Tratta
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th><i class="fas fa-hashtag"></i> ID</th>
                                                <th><i class="fas fa-route"></i> Nome Tratta</th>
                                                <th><i class="fas fa-euro-sign"></i> Costo</th>
                                                <th><i class="fas fa-toggle-on"></i> Stato</th>
                                                <th><i class="fas fa-cogs"></i> Azioni</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="route" items="${routes}">
                                                <tr>
                                                    <td>
                                                        <span class="badge bg-secondary">${route.id}</span>
                                                    </td>
                                                    <td>
                                                        <strong>${route.nome}</strong>
                                                    </td>
                                                    <td>
                                                        <span class="text-success">
                                                            <i class="fas fa-euro-sign"></i> 
                                                            <fmt:formatNumber value="${route.costo}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${route.attiva}">
                                                                <span class="badge bg-success">
                                                                    <i class="fas fa-check-circle"></i> Attiva
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-danger">
                                                                    <i class="fas fa-times-circle"></i> Inattiva
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <!-- Edit Button -->
                                                            <a href="<c:url value='/prvAzienda/routes?action=edit&id=${route.id}'/>" 
                                                               class="btn btn-outline-primary btn-sm"
                                                               title="Modifica">
                                                                <i class="fas fa-edit"></i> Modifica
                                                            </a>

                                                            <!-- Toggle Status Button -->
                                                            <button type="button" 
                                                                    class="btn btn-outline-warning btn-sm"
                                                                    title="${route.attiva ? 'Disattiva' : 'Attiva'}"
                                                                    onclick="toggleRoute(${route.id}, '${route.nome}', ${route.attiva})">
                                                                <i class="fas fa-${route.attiva ? 'pause' : 'play'}">${route.attiva ? 'Disattiva' : 'Attiva'}</i>
                                                            </button>

                                                            <!-- Delete Button -->
                                                            <button type="button" 
                                                                    class="btn btn-outline-danger btn-sm"
                                                                    title="Elimina"
                                                                    onclick="deleteRoute(${route.id}, '${route.nome}')">
                                                                <i class="fas fa-trash"></i> Elimina
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Statistics Cards -->
                <div class="row mt-4">
                    <div class="col-md-3">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h6 class="card-title">Tratte Totali</h6>
                                        <h4>${routes.size()}</h4>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-route fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card bg-success text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h6 class="card-title">Tratte Attive</h6>
                                        <h4>
                                            <c:set var="activeRoutes" value="0"/>
                                            <c:forEach var="route" items="${routes}">
                                                <c:if test="${route.attiva}">
                                                    <c:set var="activeRoutes" value="${activeRoutes + 1}"/>
                                                </c:if>
                                            </c:forEach>
                                            ${activeRoutes}
                                        </h4>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-check-circle fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card bg-warning text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h6 class="card-title">Tratte Inattive</h6>
                                        <h4>
                                            <c:set var="inactiveRoutes" value="0"/>
                                            <c:forEach var="route" items="${routes}">
                                                <c:if test="${!route.attiva}">
                                                    <c:set var="inactiveRoutes" value="${inactiveRoutes + 1}"/>
                                                </c:if>
                                            </c:forEach>
                                            ${inactiveRoutes}
                                        </h4>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-pause-circle fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card bg-info text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h6 class="card-title">Prezzo Medio</h6>
                                        <h4>
                                            <c:if test="${not empty routes}">
                                                <c:set var="totalCost" value="0"/>
                                                <c:forEach var="route" items="${routes}">
                                                    <c:set var="totalCost" value="${totalCost + route.costo}"/>
                                                </c:forEach>
                                                <fmt:formatNumber value="${totalCost / routes.size()}" type="number" minFractionDigits="2" maxFractionDigits="2"/>€
                                            </c:if>
                                            <c:if test="${empty routes}">0.00€</c:if>
                                        </h4>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="fas fa-euro-sign fa-2x"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>

    <!-- Delete Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">
                        <i class="fas fa-exclamation-triangle text-danger"></i> Conferma Eliminazione
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Sei sicuro di voler eliminare la tratta <strong id="deleteRouteName"></strong>?</p>
                    <p class="text-warning">
                        <i class="fas fa-warning"></i> Questa azione non può essere annullata.
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annulla</button>
                    <form id="deleteForm" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" id="deleteRouteId">
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-trash"></i> Elimina
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Toggle Status Modal -->
    <div class="modal fade" id="toggleModal" tabindex="-1" aria-labelledby="toggleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="toggleModalLabel">
                        <i class="fas fa-toggle-on text-warning"></i> Cambia Stato Tratta
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Vuoi <strong id="toggleAction"></strong> la tratta <strong id="toggleRouteName"></strong>?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annulla</button>
                    <form id="toggleForm" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" id="toggleRouteId">
                        <button type="submit" class="btn btn-warning" id="toggleSubmitBtn">
                            <i class="fas fa-toggle-on"></i> <span id="toggleSubmitText"></span>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
        // Delete Route Function
        function deleteRoute(routeId, routeName) {
            document.getElementById('deleteRouteId').value = routeId;
            document.getElementById('deleteRouteName').textContent = routeName;
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }

        // Toggle Route Status Function
        function toggleRoute(routeId, routeName, isActive) {
            const action = isActive ? 'disattivare' : 'attivare';
            const submitText = isActive ? 'Disattiva' : 'Attiva';
            
            document.getElementById('toggleRouteId').value = routeId;
            document.getElementById('toggleRouteName').textContent = routeName;
            document.getElementById('toggleAction').textContent = action;
            document.getElementById('toggleSubmitText').textContent = submitText;
            
            new bootstrap.Modal(document.getElementById('toggleModal')).show();
        }

        // Auto-hide alerts after 5 seconds
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                setTimeout(function() {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }, 5000);
            });
        });

        // Sidebar active link highlighting
        document.addEventListener('DOMContentLoaded', function() {
            const currentPath = window.location.pathname;
            const navLinks = document.querySelectorAll('.nav-link');
            
            navLinks.forEach(function(link) {
                if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
                    link.classList.add('active');
                } else if (!link.classList.contains('active')) {
                    link.classList.remove('active');
                }
            });
        });
    </script>
</body>
</html>
