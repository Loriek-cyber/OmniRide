<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Statistiche - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        <main class="main-content">
            <!-- Toggle button per mobile -->
            <button id="sidebarToggle" class="sidebar-toggle">☰</button>

            <!-- Header del contenuto -->
            <div class="content-header">
                <h1>Statistiche Azienda</h1>
                <div class="breadcrumb">Dashboard > Statistiche</div>
            </div>

            <!-- Statistiche principali -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value"><c:out value="${tratteAttive}" default="0"/></div>
                    <div class="stat-label">Tratte Attive</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">€<c:out value="${totaleRicavi}" default="0.00"/></div>
                    <div class="stat-label">Ricavi Totali</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><c:out value="${bigliettiVenduti}" default="0"/></div>
                    <div class="stat-label">Biglietti Venduti</div>
                </div>
            </div>

            <!-- Sezioni dettagliate -->
            <div class="section-grid">
                <div class="section-card">
                    <div class="section-number">Analisi 1</div>
                    <div class="section-title">Performance Tratte</div>
                    <div class="section-description">
                        Visualizza le performance delle tue tratte più popolari e identifica quelle con maggiori ricavi.
                    </div>
                </div>
                <div class="section-card">
                    <div class="section-number">Analisi 2</div>
                    <div class="section-title">Trend Vendite</div>
                    <div class="section-description">
                        Monitora l'andamento delle vendite nel tempo e identifica i periodi di maggiore affluenza.
                    </div>
                </div>
                <div class="section-card coming-soon">
                    <div class="section-number">Grafici</div>
                    <div class="section-title">Visualizzazioni Avanzate</div>
                    <div class="coming-soon-text">Coming Soon</div>
                    <div class="section-description">
                        Grafici interattivi e dashboard avanzate per l'analisi dei dati aziendali.
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <jsp:include page="/import/footer.jsp"/>
    <script src="${pageContext.request.contextPath}/Scripts/commonSidebar.js"></script>
</body>
</html>
