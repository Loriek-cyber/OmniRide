<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Tratte - Omniride</title>
    <link href="<c:url value='/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/dashboard.css'/>" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/routeManagement.css'/>" rel="stylesheet">
</head>
<body class="bg-light">
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="<c:url value='/prvAzienda/dashboard'/>">
                <i class="fas fa-bus"></i> Omniride - Azienda
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    Benvenuto, ${sessionScope.utente.nome}!
                </span>
                <a class="nav-link" href="<c:url value='/logout'/>">
                    <i class="fas fa-sign-out-alt"></i> Esci
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <nav class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse">
                <div class="position-sticky pt-3">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/prvAzienda/dashboard'/>">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" href="<c:url value='/prvAzienda/routes'/>">
                                <i class="fas fa-route"></i> Gestione Tratte
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/prvAzienda/employees'/>">
                                <i class="fas fa-users"></i> Gestione Dipendenti
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">
                        <i class="fas fa-route"></i> Gestione Tratte
                        <c:if test="${not empty company}">
                            <small class="text-muted">- ${company.nome}</small>
                        </c:if>
                    </h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <div class="btn-group me-2">
                            <a href="<c:url value='/prvAzienda/routes?action=create'/>" 
                               class="btn btn-success">
                                <i class="fas fa-plus"></i> Nuova Tratta
                            </a>
                        </div>
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
                                                                <i class="fas fa-edit"></i>
                                                            </a>

                                                            <!-- Toggle Status Button -->
                                                            <button type="button" 
                                                                    class="btn btn-outline-warning btn-sm"
                                                                    title="${route.attiva ? 'Disattiva' : 'Attiva'}"
                                                                    onclick="toggleRoute(${route.id}, '${route.nome}', ${route.attiva})">
                                                                <i class="fas fa-${route.attiva ? 'pause' : 'play'}"></i>
                                                            </button>

                                                            <!-- Delete Button -->
                                                            <button type="button" 
                                                                    class="btn btn-outline-danger btn-sm"
                                                                    title="Elimina"
                                                                    onclick="deleteRoute(${route.id}, '${route.nome}')">
                                                                <i class="fas fa-trash"></i>
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
            </main>
        </div>
    </div>

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
    <script src="<c:url value='/js/bootstrap.bundle.min.js'/>"></script>
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
