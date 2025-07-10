<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>OmniRide - Home</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="Styles/home.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <section class="hero-section">
        <div class="hero-content">
            <h1>Benvenuto in OmniRide</h1>
            <p>La tua soluzione completa per la mobilità urbana. Trova le tratte, acquista i biglietti e viaggia senza pensieri.</p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/visualizzaTratte" class="btn btn-primary">Scopri le Tratte</a>
                <a href="${pageContext.request.contextPath}/avvisi" class="btn btn-secondary">Avvisi</a>
            </div>
        </div>
        <div class="hero-image">
            <img src="Images/background.png" alt="Autobus in città">
        </div>
    </section>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>