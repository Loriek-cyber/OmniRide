<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.sdata.Fermata" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>OmniRide - Home</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="Styles/search-form.css">

<main>
    <div class="main-content-right">
        <div class="content-wrapper">
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <strong>Errore:</strong> ${errorMessage}
                    <c:if test="${not empty errorDetails}">
                        <br><small>${errorDetails}</small>
                    </c:if>
                </div>
            </c:if>
            <div class="search-container">
                <div class="search-tabs">
                    <button class="tab-link active">Biglietti</button>
                </div>
                <form class="search-form" action="${pageContext.request.contextPath}/ricercaPercorsi" method="get" id="searchForm">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="partenza">Partenza</label>
                            <input type="text" id="partenza" name="partenza" placeholder="Da dove vuoi partire?" required>
                        </div>
                        <div class="form-group">
                            <label for="arrivo">Arrivo</label>
                            <input type="text" id="arrivo" name="arrivo" placeholder="Dove vuoi arrivare?" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="orario">Orario di partenza</label>
                            <div class="time-input-container">
                            <input type="time" id="orario" name="orario" step="60" required>
                                <i class="fas fa-clock time-icon"></i>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="data">Data</label>
                            <div class="date-input-container">
                                <input type="date" id="data" name="data" required>
                                <i class="fas fa-calendar-alt calendar-icon"></i>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Filtri avanzati -->
                    <div class="form-row advanced-filters" style="display: none;">
                        <div class="form-group">
                            <label for="prezzoMax">Prezzo massimo (€)</label>
                            <input type="number" id="prezzoMax" name="prezzoMax" min="0" step="0.01" placeholder="Es: 15.00">
                        </div>
                        <div class="form-group">
                            <label for="durataMax">Durata massima (ore)</label>
                            <input type="number" id="durataMax" name="durataMax" min="0" step="0.5" placeholder="Es: 2.5">
                        </div>
                        <div class="form-group">
                            <label for="ordinamento">Ordina per</label>
                            <select id="ordinamento" name="ordinamento">
                                <option value="prezzo">Prezzo più basso</option>
                                <option value="durata">Durata più breve</option>
                                <option value="partenza">Orario di partenza</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="button" class="btn-filters" onclick="toggleAdvancedFilters()">
                            <i class="fas fa-filter"></i> Filtri avanzati
                        </button>
                        <button type="submit" class="btn-search">Cerca Tratte</button>
                    </div>
                </form>
            </div>
            
            <div class="registration-prompt">
                <p class="registration-prompt-text">Non sei ancora registrato? <a href="${pageContext.request.contextPath}/register">Registrati ora!</a></p>
            </div>
        </div>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
    <script src="Scripts/search.js"></script>
    <script src="Scripts/ticket-modal.js"></script>
    <link rel="stylesheet" href="Styles/ticket-modal.css">

<script>
$(document).ready(function() {
    // Prepara l'array delle fermate dal context
    var fermate = [
        <c:forEach var="fermata" items="${applicationScope.fermate}" varStatus="status">
            "${fermata.nome}"<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    
    // Configura autocomplete per i campi partenza e arrivo
    $("#partenza, #arrivo").autocomplete({
        source: fermate,
        minLength: 2,
        delay: 300,
        select: function(event, ui) {
            $(this).val(ui.item.value);
            return false;
        }
    });
    
    // Il form ora viene inviato normalmente senza AJAX
});

// Funzione per mostrare/nascondere filtri avanzati
function toggleAdvancedFilters() {
    $('.advanced-filters').slideToggle();
}
</script>
</body>
</html>
