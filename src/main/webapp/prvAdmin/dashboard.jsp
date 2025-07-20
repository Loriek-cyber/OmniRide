<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Dashboard Amministratore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin-dashboard.css">
</head>
<body class="admin-layout">
    <div class="admin-container">
        <!-- Header con navigazione -->
        <div class="admin-header">
            <div class="header-content">
                <h1>🛡️ Dashboard Amministratore</h1>
                <div class="admin-info">
                    <span>Benvenuto, <strong>${sessionScope.utente.nome} ${sessionScope.utente.cognome}</strong></span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">🚪 Logout</a>
                </div>
            </div>
            
            <!-- Menu di navigazione -->
            <div class="admin-nav">
                <a href="${pageContext.request.contextPath}/prvAdmin/dashboard" class="nav-link active">📊 Dashboard</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/users" class="nav-link">👥 Gestione Utenti</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/routes" class="nav-link">🚌 Gestione Tratte</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/employees" class="nav-link">👷 Gestione Dipendenti</a>
                <a href="${pageContext.request.contextPath}/prvAdmin/tickets" class="nav-link">🎫 Gestione Biglietti</a>
            </div>
        </div>
        
        <!-- Messaggi -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        
        <!-- Statistiche principali -->
        <div class="stats-grid">
            <div class="stat-card primary">
                <div class="stat-icon">👥</div>
                <div class="stat-info">
                    <h3>Utenti Totali</h3>
                    <p class="stat-number">${stats.totalUsers}</p>
                    <small>+${stats.newUsersThisMonth} questo mese</small>
                </div>
            </div>
            
            <div class="stat-card success">
                <div class="stat-icon">🎫</div>
                <div class="stat-info">
                    <h3>Biglietti Venduti</h3>
                    <p class="stat-number">${stats.totalTickets}</p>
                    <small>${stats.activeTickets} attivi</small>
                </div>
            </div>
            
            <div class="stat-card warning">
                <div class="stat-icon">💰</div>
                <div class="stat-info">
                    <h3>Fatturato Totale</h3>
                    <p class="stat-number">${stats.totalRevenue}</p>
                    <small>€${stats.monthlyRevenue} questo mese</small>
                </div>
            </div>
            
            <div class="stat-card info">
                <div class="stat-icon">🚌</div>
                <div class="stat-info">
                    <h3>Tratte Attive</h3>
                    <p class="stat-number">${stats.activeRoutes}</p>
                    <small>su ${stats.totalRoutes} totali</small>
                </div>
            </div>
            
            <div class="stat-card danger">
                <div class="stat-icon">🏢</div>
                <div class="stat-info">
                    <h3>Aziende Partner</h3>
                    <p class="stat-number">${stats.totalCompaniesCount}</p>
                    <small>${stats.activeCompanies} attive</small>
                </div>
            </div>
            
            <div class="stat-card secondary">
                <div class="stat-icon">👷</div>
                <div class="stat-info">
                    <h3>Dipendenti</h3>
                    <p class="stat-number">${stats.totalEmployees}</p>
                    <small>${stats.activeEmployees} attivi</small>
                </div>
            </div>
        </div>
        
        <!-- Sezione dati recenti -->
        <div class="content-grid">
            <!-- Ultimi utenti registrati -->
            <div class="content-card">
                <div class="card-header">
                    <h3>🆕 Ultimi Utenti Registrati</h3>
                    <a href="${pageContext.request.contextPath}/prvAdmin/users" class="btn btn-sm btn-primary">Vedi Tutti</a>
                </div>
                <div class="card-content">
                    <c:choose>
                        <c:when test="${not empty recentUsers}">
                            <div class="user-list">
                                <c:forEach var="user" items="${recentUsers}" varStatus="status" end="4">
                                    <div class="user-item">
                                        <div class="user-avatar">👤</div>
                                        <div class="user-info">
                                            <strong>${user.nome} ${user.cognome}</strong>
                                            <small>${user.email}</small>
                                            <span class="role-badge role-${user.ruolo}">${user.ruolo}</span>
                                        </div>
                                        <div class="user-date">
                                            <small>${user.dataRegistrazione}</small>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-data">Nessun utente registrato di recente</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Ultimi biglietti -->
            <div class="content-card">
                <div class="card-header">
                    <h3>🎫 Attività Biglietti</h3>
                    <a href="${pageContext.request.contextPath}/prvAdmin/tickets" class="btn btn-sm btn-primary">Vedi Tutti</a>
                </div>
                <div class="card-content">
                    <c:choose>
                        <c:when test="${not empty recentTickets}">
                            <div class="ticket-list">
                                <c:forEach var="ticket" items="${recentTickets}" varStatus="status" end="4">
                                    <div class="ticket-item">
                                        <div class="ticket-info">
                                            <strong>Biglietto #${ticket.id}</strong>
                                            <small>€${ticket.prezzo}</small>
                                            <span class="status-badge status-${fn:toLowerCase(ticket.stato)}">${ticket.stato}</span>
                                        </div>
                                        <div class="ticket-type">
                                            <small>${ticket.tipo}</small>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-data">Nessun biglietto venduto di recente</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Grafici e analisi -->
        <div class="analytics-section">
            <div class="content-card full-width">
                <div class="card-header">
                    <h3>📈 Analisi Sistema</h3>
                </div>
                <div class="card-content">
                    <div class="analysis-grid">
                        <div class="analysis-item">
                            <h4>Distribuzione Utenti</h4>
                            <div class="progress-bars">
                                <div class="progress-item">
                                    <span>Utenti Normali</span>
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: ${(stats.totalRegularUsers * 100) / stats.totalUsers}%"></div>
                                    </div>
                                    <span>${stats.totalRegularUsers}</span>
                                </div>
                                <div class="progress-item">
                                    <span>Aziende</span>
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: ${(stats.totalCompanies * 100) / stats.totalUsers}%"></div>
                                    </div>
                                    <span>${stats.totalCompanies}</span>
                                </div>
                                <div class="progress-item">
                                    <span>Amministratori</span>
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: ${(stats.totalAdmins * 100) / stats.totalUsers}%"></div>
                                    </div>
                                    <span>${stats.totalAdmins}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="analysis-item">
                            <h4>Stato Biglietti</h4>
                            <div class="stats-summary">
                                <div class="summary-item success">
                                    <span>Attivi</span>
                                    <strong>${stats.activeTickets}</strong>
                                </div>
                                <div class="summary-item warning">
                                    <span>Scaduti</span>
                                    <strong>${stats.expiredTickets}</strong>
                                </div>
                                <div class="summary-item info">
                                    <span>Convalidati Oggi</span>
                                    <strong>${stats.validatedToday}</strong>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/Scripts/admin-dashboard.js"></script>
</body>
</html>
