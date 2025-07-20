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
            // Funzione per leggere i cookie
            function getCookie(name) {
                let value = "; " + document.cookie;
                let parts = value.split("; " + name + "=");
                if (parts.length === 2) return parts.pop().split(";").shift();
                return null;
            }
            
            document.addEventListener('DOMContentLoaded', function () {
                const ticketSection = document.querySelector('.ticket-section');
                
                // Prima prova a leggere dai cookie
                const guestTicketIds = getCookie('guestTicketIds');
                
                if (guestTicketIds && guestTicketIds !== '') {
                    console.log('[GUEST_WALLET DEBUG] ID biglietti trovati nei cookie:', guestTicketIds);
                    
                    // Recupera i dettagli dei biglietti dal server
                    fetch('${pageContext.request.contextPath}/guest-tickets?action=getByIds&ids=' + encodeURIComponent(guestTicketIds))
                        .then(response => response.json())
                        .then(data => {
                            if (data.success && data.tickets && data.tickets.length > 0) {
                                console.log('[GUEST_WALLET DEBUG] Biglietti caricati dal server:', data.tickets);
                                displayTickets(data.tickets, ticketSection);
                                showGuestWarning();
                            } else {
                                showNoTicketsMessage(ticketSection);
                            }
                        })
                        .catch(error => {
                            console.error('Errore nel caricamento biglietti:', error);
                            // Fallback: prova con sessionStorage
                            trySessionStorage(ticketSection);
                        });
                } else {
                    // Fallback: prova con sessionStorage
                    trySessionStorage(ticketSection);
                }
            });
            
            function trySessionStorage(ticketSection) {
                const tickets = JSON.parse(sessionStorage.getItem('tickets') || '[]');
                console.log('[GUEST_WALLET DEBUG] Biglietti da sessionStorage:', tickets);
                
                if (tickets && tickets.length > 0) {
                    displayTickets(tickets, ticketSection);
                    showGuestWarning();
                } else {
                    showNoTicketsMessage(ticketSection);
                }
            }
            
            function displayTickets(tickets, container) {
                const ticketList = tickets.map(ticket => {
                    const tipo = ticket.tipo || 'NORMALE';
                    const data = ticket.dataAcquisto || new Date().toLocaleDateString();
                    const prezzo = ticket.prezzo || '0.00';
                    const id = ticket.id || 'N/A';
                    const stato = ticket.stato || 'ACQUISTATO';
                    
                    return `<li class="ticket-item">
                        <div class="ticket-info">
                            <strong>Biglietto ${tipo}</strong><br>
                            <small>ID: ${id}</small><br>
                            <small>Data: ${data}</small><br>
                            <small>Stato: ${stato}</small>
                        </div>
                        <div class="ticket-price">€${prezzo}</div>
                    </li>`;
                }).join('');
                
                container.innerHTML = `
                    <div class="tickets-list">
                        <ul class="guest-tickets">${ticketList}</ul>
                        <div class="tickets-summary">
                            <p><strong>Totale biglietti:</strong> ${tickets.length}</p>
                        </div>
                    </div>
                `;
            }
            
            function showGuestWarning() {
                const warningDiv = document.createElement('div');
                warningDiv.className = 'guest-warning';
                warningDiv.innerHTML = `
                    <p><i class="fas fa-exclamation-triangle"></i> 
                    <strong>Attenzione:</strong> Se non sei registrato potresti perdere i tuoi biglietti. 
                    Si consiglia di registrarsi per salvare i biglietti in modo permanente.</p>
                `;
                document.querySelector('.content-wrapper').appendChild(warningDiv);
            }
            
            function showNoTicketsMessage(container) {
                container.innerHTML = '<p>Nessun biglietto trovato. <a href="${pageContext.request.contextPath}/visualizzaTratte">Pianifica il tuo viaggio!</a></p>';
            }
        </script>
        
        <style>
            .tickets-list {
                max-width: 800px;
                margin: 0 auto;
            }
            
            .guest-tickets {
                list-style: none;
                padding: 0;
            }
            
            .ticket-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 15px;
                margin-bottom: 10px;
                border: 1px solid #ddd;
                border-radius: 8px;
                background: #f9f9f9;
            }
            
            .ticket-info {
                flex: 1;
            }
            
            .ticket-price {
                font-size: 1.2em;
                font-weight: bold;
                color: #007bff;
            }
            
            .tickets-summary {
                text-align: center;
                margin-top: 20px;
                padding: 10px;
                background: #e9ecef;
                border-radius: 5px;
            }
            
            .guest-warning {
                margin-top: 20px;
                padding: 15px;
                background: #fff3cd;
                border: 1px solid #ffeaa7;
                border-radius: 5px;
                color: #856404;
            }
            
            .guest-warning i {
                color: #f39c12;
                margin-right: 8px;
            }
        </style>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
