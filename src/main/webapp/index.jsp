<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>OmniRide - Home</title>
    <jsp:include page="import/metadata.jsp"/>
    
</head>
<body>
<jsp:include page="import/header.jsp"/>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="Styles/search-form.css">

<main>
    <div class="main-content-right">
        <div class="content-wrapper">
            <div class="search-container">
                <div class="search-tabs">
                    <button class="tab-link active">Biglietti</button>
                </div>
                <form class="search-form" action="#" method="post" id="searchForm">
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
                                <input type="time" id="orario" name="orario" required>
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
                    <button type="submit" class="btn-search">Cerca Tratte</button>
                </form>
            </div>
            
            <!-- Sezione risultati fermate -->
            <div class="results-container" id="resultsContainer" style="display: none;">
                <h3>Risultati della ricerca</h3>
                <div class="loading" id="loadingSpinner" style="display: none;">
                    <i class="fas fa-spinner fa-spin"></i> Ricerca in corso...
                </div>
                <div id="resultsContent"></div>
            </div>
            
            <div class="registration-prompt">
                <p>Non sei ancora registrato? <a href="${pageContext.request.contextPath}/register">Registrati ora!</a></p>
            </div>
        </div>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>