<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Modifica Tratta - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/addTratta.css">
</head>
<body data-page="editTratta">
    <jsp:include page="/import/header.jsp"/>
    <div class="dashboard-layout">
        <jsp:include page="sidebarAzienda.jsp"/>
        
        <main class="main-content">
            <div class="content-section active">
                <div class="content-header">
                    <h1>Modifica Tratta: ${tratta.nome}</h1>
                    <div class="breadcrumb">Dashboard > Gestione Tratte > Modifica Tratta</div>
                </div>
                
                <c:if test="${not empty messaggio}">
                    <div class="alert alert-success">${messaggio}</div>
                </c:if>
                <c:if test="${not empty errore}">
                    <div class="alert alert-error">${errore}</div>
                </c:if>
                
                <div class="add-tratta-container">
                    <form action="${pageContext.request.contextPath}/prvAzienda/editTratta" method="post" id="editTrattaForm">
                        <input type="hidden" name="trattaId" value="${tratta.id}">
                        
                        <!-- Informazioni Base -->
                        <div class="form-section">
                            <h2>Informazioni Base</h2>
                            <div class="form-group">
                                <label for="nome">Nome Tratta *:</label>
                                <input type="text" id="nome" name="nome" class="form-input" required 
                                       value="${tratta.nome}" placeholder="Es: Linea 1 - Centro-Periferia">
                                <div class="help-text">Modifica il nome descrittivo della tratta</div>
                            </div>

                            <div class="form-group">
                                <label for="costo">Costo (€) *:</label>
                                <input type="number" id="costo" name="costo" class="form-input"
                                       step="0.01" min="0.01" required value="${tratta.costo}" 
                                       placeholder="Es: 2.50">
                                <div class="help-text">Modifica il costo del biglietto per l'intera tratta</div>
                            </div>
                            
                            <div class="form-group">
                                <label for="attiva">Stato Tratta:</label>
                                <select id="attiva" name="attiva" class="form-input">
                                    <option value="true" ${tratta.attiva ? 'selected' : ''}>Attiva</option>
                                    <option value="false" ${!tratta.attiva ? 'selected' : ''}>Disattiva</option>
                                </select>
                                <div class="help-text">Le tratte disattive non saranno visibili ai clienti</div>
                            </div>
                        </div>

                        <!-- Percorso Attuale -->
                        <div class="form-section">
                            <h2>Percorso Attuale</h2>
                            <div class="current-route-display">
                                <c:forEach var="fermata" items="${tratta.fermataTrattaList}" varStatus="status">
                                    <div class="route-step">
                                        <div class="step-number">${fermata.sequenza}</div>
                                        <div class="step-info">
                                            <div class="step-name">${fermata.fermata.nome}</div>
                                            <div class="step-address">${fermata.fermata.indirizzo}</div>
                                            <c:if test="${!status.last}">
                                                <div class="step-time">Tempo alla prossima fermata: ${fermata.tempoProssimaFermata} minuti</div>
                                            </c:if>
                                        </div>
                                        <c:if test="${!status.last}">
                                            <div class="step-arrow">→</div>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="help-text">
                                <strong>Nota:</strong> Per modificare il percorso (fermate e tempi), 
                                contatta l'amministratore del sistema o crea una nuova tratta.
                            </div>
                        </div>

                        <!-- Orari Attuali e Modifica -->
                        <div class="form-section">
                            <h2>Gestione Orari</h2>
                            
                            <!-- Orari Esistenti -->
                            <div class="existing-schedules">
                                <h3>Orari Attuali</h3>
                                <c:choose>
                                    <c:when test="${not empty tratta.orari}">
                                        <div class="schedules-grid">
                                            <c:forEach var="orario" items="${tratta.orari}" varStatus="status">
                                                <div class="schedule-card ${orario.attivo ? 'active' : 'inactive'}">
                                                    <div class="schedule-info">
                                                        <div class="schedule-time">
                                                            <i class="fas fa-clock"></i>
                                                            <fmt:formatDate value="${orario.oraPartenza}" pattern="HH:mm" type="time" var="partenza"/>
                                                            <fmt:formatDate value="${orario.oraArrivo}" pattern="HH:mm" type="time" var="arrivo"/>
                                                            ${partenza} → ${arrivo}
                                                        </div>
                                                        <div class="schedule-days">${orario.giorniSettimana}</div>
                                                        <div class="schedule-status">
                                                            <span class="status-badge ${orario.attivo ? 'active' : 'inactive'}">
                                                                ${orario.attivo ? 'Attivo' : 'Disattivo'}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div class="schedule-actions">
                                                        <button type="button" class="btn-sm ${orario.attivo ? 'btn-warning' : 'btn-success'}" 
                                                                onclick="toggleSchedule(${orario.id}, ${orario.attivo})">
                                                            ${orario.attivo ? 'Disattiva' : 'Attiva'}
                                                        </button>
                                                        <button type="button" class="btn-sm btn-danger" 
                                                                onclick="deleteSchedule(${orario.id})">
                                                            Elimina
                                                        </button>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-schedules">
                                            <p>Nessun orario configurato per questa tratta.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- Aggiungi Nuovi Orari -->
                            <div class="add-schedules-section">
                                <h3>Aggiungi Nuovi Orari</h3>
                                <div class="form-group">
                                    <label>Nuovi Orari di Partenza:</label>
                                    <div class="orari-container" id="orariContainer">
                                        <!-- Orari verranno aggiunti qui dinamicamente -->
                                    </div>
                                    <button type="button" class="add-orario-btn" id="addOrarioBtn">+ Aggiungi Orario</button>
                                    <div class="help-text">Aggiungi nuovi orari di partenza per la tratta.</div>
                                </div>

                                <div class="form-group">
                                    <label>Giorni di Servizio per i nuovi orari:</label>
                                    <div class="days-container">
                                        <div class="day-item">
                                            <input type="checkbox" id="lunedi" name="giorni" value="LUN">
                                            <label for="lunedi">Lunedì</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="martedi" name="giorni" value="MAR">
                                            <label for="martedi">Martedì</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="mercoledi" name="giorni" value="MER">
                                            <label for="mercoledi">Mercoledì</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="giovedi" name="giorni" value="GIO">
                                            <label for="giovedi">Giovedì</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="venerdi" name="giorni" value="VEN">
                                            <label for="venerdi">Venerdì</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="sabato" name="giorni" value="SAB">
                                            <label for="sabato">Sabato</label>
                                        </div>
                                        <div class="day-item">
                                            <input type="checkbox" id="domenica" name="giorni" value="DOM">
                                            <label for="domenica">Domenica</label>
                                        </div>
                                    </div>
                                    <div class="help-text">Seleziona i giorni per i nuovi orari</div>
                                </div>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="submit-btn">Salva Modifiche</button>
                            <a href="${pageContext.request.contextPath}/prvAzienda/gestisciTratte" class="btn-secondary">Annulla</a>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/Scripts/addTratta.js"></script>
    <script>
        // Funzioni per gestire gli orari esistenti
        function toggleSchedule(orarioId, isActive) {
            if (confirm(`Sei sicuro di voler ${isActive ? 'disattivare' : 'attivare'} questo orario?`)) {
                fetch('${pageContext.request.contextPath}/prvAzienda/manageSchedule', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `action=toggle&orarioId=${orarioId}&trattaId=${tratta.id}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Errore: ' + data.error);
                    }
                });
            }
        }
        
        function deleteSchedule(orarioId) {
            if (confirm('Sei sicuro di voler eliminare definitivamente questo orario?')) {
                fetch('${pageContext.request.contextPath}/prvAzienda/manageSchedule', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `action=delete&orarioId=${orarioId}&trattaId=${tratta.id}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert('Errore: ' + data.error);
                    }
                });
            }
        }
    </script>
</body>
</html>
