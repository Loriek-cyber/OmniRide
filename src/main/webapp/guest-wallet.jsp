<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Biglietti Ospite - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
</head>
<body class="user-dashboard-layout" data-page="guest-wallet">
<jsp:include page="import/header.jsp"/>

<main>
    <div class="content-wrapper">
        <h2>I Tuoi Biglietti (Ospite)</h2>
        <div class="ticket-section"></div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const tickets = JSON.parse(sessionStorage.getItem('tickets'));
                const ticketSection = document.querySelector('.ticket-section');
                if (tickets && tickets.length > 0) {
                    ticketSection.innerHTML = '<ul>' + tickets.map(ticket => `<li>${ticket.tipo} - ${ticket.data} - ${ticket.prezzo}€</li>`).join('') + '</ul>';
                    alert('Se non sei registrato potresti perdere i tuoi biglietti. Si consiglia di registrarsi o salvare il QR.');
                } else {
                    ticketSection.innerHTML = '<p>Nessun biglietto trovato. Pianifica il tuo viaggio!</p>';
                }
            });
        </script>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
