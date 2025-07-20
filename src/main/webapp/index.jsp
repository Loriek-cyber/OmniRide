<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>OmniRide - Home</title>
    <jsp:include page="import/metadata.jsp"/>
    
</head>
<body>
<jsp:include page="import/header.jsp"/>
<link rel="stylesheet" href="Styles/avvisiHome.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="Styles/search-form.css">

<main>
    <!-- Sezione superiore con ricerca allineata a destra -->
    <div class="top-section">
        <div class="search-wrapper">
            <div class="search-container">
                <div class="search-tabs">
                    <button class="tab-link active">Biglietti</button>
                    <button class="tab-link">Abbonamenti</button>
                </div>
                <form class="search-form" action="${pageContext.request.contextPath}/ricercaPercorsi" method="post">
                    <input type="hidden" name="action" value="cerca">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="partenza">Partenza</label>
                            <input type="text" id="partenza" name="partenza" placeholder="Da dove vuoi partire?">
                        </div>
                        <div class="form-group">
                            <label for="arrivo">Arrivo</label>
                            <input type="text" id="arrivo" name="arrivo" placeholder="Dove vuoi arrivare?">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="andata">Andata</label>
                            <div class="date-input-container">
                                <input type="text" id="andata" name="andata" placeholder="gg / mm / aaaa">
                                <i class="fas fa-calendar-alt calendar-icon"></i>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="ritorno">Ritorno</label>
                            <div class="date-input-container">
                                <input type="text" id="ritorno" name="ritorno" placeholder="gg / mm / aaaa">
                                <i class="fas fa-calendar-alt calendar-icon"></i>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn-search">Cerca</button>
                </form>
            </div>
            <div class="avviso-button-container">
                <a href="#avvisi-section" class="btn-avviso" onclick="scrollToAvvisi(event)">
                    <i class="fas fa-exclamation-triangle"></i> Visualizza Avvisi
                </a>
            </div>
            <div class="registration-prompt">
                <p>Non sei ancora registrato? <a href="${pageContext.request.contextPath}/register">Registrati ora!</a></p>
            </div>
        </div>
    </div>
    
    <!-- Pannello degli avvisi - Full Width -->
    <div id="avvisi-section" class="avvisi-container">
        <h3>Avvisi Recenti</h3>
        <div class="avvisi-list" id="avvisiList">
<!-- Avvisi caricati staticamente dalla servlet -->
            <c:choose>
                <c:when test="${not empty avvisiArricchiti}">
                    <c:forEach var="avviso" items="${avvisiArricchiti}">
                        <div class="avviso-item">
                            <div class="avviso-header" onclick="toggleAvviso('${avviso.id}')">
                                <button class="toggle-description" data-avviso-id="${avviso.id}">+</button>
                                <div class="avviso-left">
                                    <span class="avviso-title">${avviso.nomeTratta}</span>
                                    <span class="avviso-status ${avviso.trattaAttiva ? 'attiva' : 'non-attiva'}">
                                        ${avviso.trattaAttiva ? 'Attiva' : 'Non Attiva'}
                                    </span>
                                </div>
                            </div>
                            <div class="avviso-description" id="description-${avviso.id}" style="display: none;">
                                <p>${avviso.descrizione}</p>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-avvisi">
                        <i class="fas fa-info-circle"></i>
                        <p>Nessun avviso disponibile al momento.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="view-all">
            <a href="${pageContext.request.contextPath}/visualizzaAvvisi" class="btn-view-all">VISUALIZZA TUTTI GLI AVVISI</a>
        </div>
    </div>
</main>

<script>
// Funzione per toggleare la descrizione dell'avviso
function toggleAvviso(id) {
    const desc = document.getElementById('description-' + id);
    const toggleBtn = document.querySelector('[data-avviso-id="' + id + '"]');
    
    if (desc.style.display === 'none' || desc.style.display === '') {
        desc.style.display = 'block';
        toggleBtn.innerHTML = '−';
    } else {
        desc.style.display = 'none';
        toggleBtn.innerHTML = '+';
    }
}

// Funzione per scrollare verso la sezione degli avvisi
function scrollToAvvisi(event) {
    event.preventDefault();
    const avvisiSection = document.getElementById('avvisi-section');
    if (avvisiSection) {
        avvisiSection.scrollIntoView({ 
            behavior: 'smooth',
            block: 'start'
        });
    }
}
</script>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
