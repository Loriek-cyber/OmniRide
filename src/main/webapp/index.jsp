
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <%@ include file="import/metadata.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/home.css">
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>

    <div class="search-box">
        <nav class="search-tabs">
            <button class="tab-link active" onclick="openTab(event, 'Biglietti')">Biglietti</button>
            <button class="tab-link" onclick="openTab(event, 'Abbonamenti')">Abbonamenti</button>
        </nav>

        <div id="Biglietti" class="tab-content active">
            <form method="get" action="DBtestServlet">
                <div class="form-row">
                    <div class="form-group">
                        <label for="start">Partenza</label>
                        <input type="text" id="start" name="start" placeholder="Da dove vuoi partire?">
                    </div>
                    <div class="form-group">
                        <label for="end">Arrivo</label>
                        <input type="text" id="end" name="end" placeholder="Dove vuoi arrivare?">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="andata">Andata</label>
                        <input type="date" id="andata" name="andata">
                    </div>
                    <div class="form-group">
                        <label for="ritorno">Ritorno</label>
                        <input type="date" id="ritorno" name="ritorno">
                    </div>
                </div>
                <button type="submit" class="btn-search-main">Cerca</button>
            </form>
        </div>

        <div id="Abbonamenti" class="tab-content">
            <p>Ricerca abbonamenti non ancora disponibile.</p>
        </div>
    </div>

</main>

<%@ include file="import/footer.jsp" %>

<script>
function openTab(evt, tabName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tab-link");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
}

// Imposta la prima tab come attiva di default
document.addEventListener("DOMContentLoaded", function() {
    document.getElementsByClassName('tab-link')[0].click();
});
</script>

</body>
</html>