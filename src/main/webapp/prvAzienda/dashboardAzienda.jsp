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
        <!-- Sidebar -->
        <nav class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <h3>Dashboard Azienda</h3>
                    <div class="company-name">
                        <c:if test="${not empty azienda}">
                            <c:out value="${azienda.nome}"/>
                            <small><c:out value="${sessionScope.utente.nome} ${sessionScope.utente.cognome}"/></small>
                        </c:if>
                        <c:if test="${empty azienda}">
                            <c:out value="${sessionScope.utente.nome} ${sessionScope.utente.cognome}"/>
                        </c:if>
                    </div>
                </div>
            
            <ul class="sidebar-nav">
                <li class="nav-item">
                    <a href="#sezione1" class="nav-link active" onclick="showSection(1)">
                        Panoramica
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione2" class="nav-link" onclick="showSection(2)">
                        Gestione Tratte
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione3" class="nav-link" onclick="showSection(3)">
                        Gestione Fermate
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione4" class="nav-link" onclick="showSection(4)">
                        Statistiche
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione5" class="nav-link" onclick="showSection(5)">
                        Orari e Programmazione
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione6" class="nav-link" onclick="showSection(6)">
                        Gestione Flotta
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione7" class="nav-link" onclick="showSection(7)">
                        Report Finanziari
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione8" class="nav-link" onclick="showSection(8)">
                        Gestione Dipendenti
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione9" class="nav-link" onclick="showSection(9)">
                        Comunicazioni
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#sezione10" class="nav-link" onclick="showSection(10)">
                        Impostazioni
                    </a>
                </li>
            </ul>
        </nav>

        <!-- Overlay per mobile -->
        <div class="sidebar-overlay" id="sidebar-overlay" onclick="toggleSidebar()"></div>

        <!-- Contenuto principale -->
        <main class="main-content">
            <!-- Toggle button per mobile -->
            <button class="sidebar-toggle" onclick="toggleSidebar()">☰</button>

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
                        <div class="stat-value"><c:out value="${totaleFermate}" default="0"/></div>
                        <div class="stat-label">Fermate Totali</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">€<c:out value="${ricaviStimati}" default="0.00"/></div>
                        <div class="stat-label">Ricavi Stimati</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value"><c:out value="${totaleOrari}" default="0"/></div>
                        <div class="stat-label">Orari Configurati</div>
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
                            <a href="addTratta" class="btn btn-primary">Aggiungi Tratta</a>
                            <a href="#sezione2" class="btn btn-secondary" onclick="showSection(2)">Visualizza Tutte</a>
                        </div>
                    </div>
                    <div class="section-card">
                        <div class="section-number">Accesso Rapido 2</div>
                        <div class="section-title">Gestione Fermate</div>
                        <div class="section-description">
                            Aggiungi nuove fermate, modifica quelle esistenti e gestisci la rete di trasporti.
                        </div>
                        <div class="section-actions">
                            <a href="addFermata" class="btn btn-primary">Aggiungi Fermata</a>
                            <a href="#sezione3" class="btn btn-secondary" onclick="showSection(3)">Visualizza Tutte</a>
                        </div>
                    </div>
                    <div class="section-card">
                        <div class="section-number">Accesso Rapido 3</div>
                        <div class="section-title">Statistiche in Tempo Reale</div>
                        <div class="section-description">
                            Monitora le performance delle tue tratte, il numero di passeggeri e i ricavi giornalieri.
                        </div>
                        <div class="section-actions">
                            <a href="#sezione4" class="btn btn-primary" onclick="showSection(4)">Visualizza Report</a>
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

    <script>
        // Gestione navigazione sidebar
        function showSection(sectionNumber) {
            // Nascondi tutte le sezioni
            const allSections = document.querySelectorAll('.content-section');
            allSections.forEach(section => {
                section.style.display = 'none';
            });

            // Rimuovi classe active da tutti i link
            const allLinks = document.querySelectorAll('.nav-link');
            allLinks.forEach(link => {
                link.classList.remove('active');
            });

            // Mostra la sezione selezionata
            const targetSection = document.getElementById(`content-sezione${sectionNumber}`);
            if (targetSection) {
                targetSection.style.display = 'block';
            }

            // Aggiungi classe active al link corrente
            const targetLink = document.querySelector(`a[onclick="showSection(${sectionNumber})"]`);
            if (targetLink) {
                targetLink.classList.add('active');
            }

            // Chiudi sidebar su mobile dopo la selezione
            if (window.innerWidth <= 768) {
                toggleSidebar();
            }
        }

        // Gestione toggle sidebar per mobile
        function toggleSidebar() {
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('sidebar-overlay');
            
            sidebar.classList.toggle('active');
            overlay.classList.toggle('active');
        }

        // Chiudi sidebar quando si clicca fuori (solo mobile)
        document.addEventListener('click', function(event) {
            const sidebar = document.getElementById('sidebar');
            const toggle = document.querySelector('.sidebar-toggle');
            
            if (window.innerWidth <= 768 && 
                !sidebar.contains(event.target) && 
                !toggle.contains(event.target) && 
                sidebar.classList.contains('active')) {
                toggleSidebar();
            }
        });

        // Gestione resize window
        window.addEventListener('resize', function() {
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('sidebar-overlay');
            
            if (window.innerWidth > 768) {
                sidebar.classList.remove('active');
                overlay.classList.remove('active');
            }
        });
    </script>
    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
