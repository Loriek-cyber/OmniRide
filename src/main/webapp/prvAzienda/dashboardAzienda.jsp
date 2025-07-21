<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Dashboard Azienda - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        <!-- Contenuto principale -->
        <main class="main-content">
            <!-- Toggle button per mobile -->
            <button id="sidebarToggle" class="sidebar-toggle">☰</button>

            <!-- Sezione 1: Panoramica -->
            <div id="content-sezione1" class="content-section active">
                <div class="content-header">
                    <h1>Panoramica</h1>
                    <div class="breadcrumb">Dashboard > Panoramica</div>
                </div>

                <!-- Statistiche rapide -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-value"><c:out value="${tratteAttive}" default="0"/></div>
                        <div class="stat-label">Tratte Attive</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">€<c:out value="${ricaviStimati}" default="0.00"/></div>
                        <div class="stat-label">Ricavi Stimati</div>
                    </div>
                </div>

                <!-- Sezioni rapide -->
                <div class="section-grid">
                    <div class="section-card">
                        <div class="section-number">Accesso Rapido 1</div>
                        <div class="section-title">Gestione Tratte</div>
                        <div class="section-description">
                            Visualizza e modifica le tratte della tua azienda, aggiungi nuove rotte e gestisci gli orari.
                        </div>
                        <div class="section-actions">
                            <a href="${pageContext.request.contextPath}/prvAzienda/addTratta" class="btn btn-primary">Aggiungi Tratta</a>
                            <a href="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" class="btn btn-secondary">Visualizza Tutte</a>
                        </div>
                    </div>
                    <div class="section-card">
                        <div class="section-number">Accesso Rapido 2</div>
                        <div class="section-title">Gestione Fermate</div>
                        <div class="section-description">
                            Aggiungi nuove fermate, modifica quelle esistenti e gestisci la rete di trasporti.
                        </div>
                        <div class="section-actions">
                            <a href="${pageContext.request.contextPath}/prvAzienda/addFermata" class="btn btn-primary">Aggiungi Fermata</a>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Sezioni 2-10: Coming Soon -->
            <c:forEach var="i" begin="2" end="10">
                <div id="content-sezione${i}" class="content-section" style="display: none;">
                    <div class="content-header">
                        <h1>Sezione ${i}</h1>
                        <div class="breadcrumb">Dashboard > Sezione ${i}</div>
                    </div>
                    
                    <div class="section-grid">
                        <div class="section-card coming-soon">
                            <div class="section-number">Sezione ${i}</div>
                            <div class="section-title">In Sviluppo</div>
                            <div class="coming-soon-text">Coming Soon</div>
                            <div class="section-description">
                                Questa sezione è attualmente in fase di sviluppo. 
                                Presto sarà disponibile con tutte le funzionalità necessarie per la gestione aziendale.
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>
</body>
</html>
