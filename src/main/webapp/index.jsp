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
                    <button class="tab-link">Abbonamenti</button>
                </div>
                <form class="search-form" action="#" method="post">
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
                <a href="${pageContext.request.contextPath}/visualizzaAvvisi" class="btn-avviso">
                    <i class="fas fa-exclamation-triangle"></i> Visualizza Avvisi
                </a>
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