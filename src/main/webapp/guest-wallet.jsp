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
    <div class="wallet-container">
        <div class="wallet-section">
            <h2><i class="fas fa-ticket-alt"></i> I Tuoi Biglietti (Ospite)</h2>
            <div class="wallet-section-content">
                <div class="guest-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    <div>
                        <strong>Modalità Ospite Attiva!</strong> I tuoi biglietti sono salvati temporaneamente nel browser.
                        <a href="${pageContext.request.contextPath}/register">Registrati ora</a> per non perderli!
                    </div>
                </div>
                
                <div class="ticket-section"></div>
            </div>
        </div>
        
        <div class="wallet-section">
            <h3><i class="fas fa-info-circle"></i> Informazioni Importante</h3>
            <div class="wallet-section-content">
                <div class="info-card">
                    <h4>Come funziona il portafoglio ospite?</h4>
                    <ul>
                        <li><strong>Temporaneo:</strong> I biglietti sono salvati solo nel tuo browser</li>
                        <li><strong>Durata:</strong> I biglietti rimangono fino alla chiusura del browser</li>
                        <li><strong>Dispositivo:</strong> Disponibili solo su questo dispositivo</li>
                        <li><strong>Backup:</strong> Salva il QR code per sicurezza</li>
                    </ul>
                </div>
                
                <div class="action-card">
                    <h4>Vuoi conservare permanentemente i tuoi biglietti?</h4>
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">
                            <i class="fas fa-user-plus"></i> Registrati Gratuitamente
                        </a>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                            <i class="fas fa-sign-in-alt"></i> Ho già un account
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

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
                display: grid;
                gap: 20px;
            }
            
            .guest-tickets {
                list-style: none;
                padding: 0;
                margin: 0;
            }
            
            .ticket-item {
                background: white;
                border: 1px solid #e5e7eb;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 15px;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }
            
            .ticket-item::before {
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                width: 4px;
                height: 100%;
                background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            }
            
            .ticket-item:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(255, 193, 7, 0.15);
                border-color: #ffc107;
            }
            
            .ticket-info {
                margin-bottom: 15px;
            }
            
            .ticket-info p {
                margin: 8px 0;
                color: #495057;
            }
            
            .ticket-info strong {
                color: #228b22;
                font-weight: 600;
            }
            
            .ticket-price {
                font-size: 1.3em;
                font-weight: bold;
                color: #32cd32;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9f7e9 100%);
                padding: 8px 15px;
                border-radius: 8px;
                display: inline-block;
            }
            
            .tickets-summary {
                text-align: center;
                margin-top: 25px;
                padding: 20px;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9f7e9 100%);
                border-radius: 12px;
                border: 1px solid #32cd32;
            }
            
            .tickets-summary strong {
                color: #228b22;
                font-size: 1.1em;
            }
            
            .guest-warning {
                background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
                border: 2px solid #ffc107;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
                gap: 15px;
                position: relative;
                overflow: hidden;
            }
            
            .guest-warning::before {
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                width: 4px;
                height: 100%;
                background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            }
            
            .guest-warning i {
                color: #e0a800;
                font-size: 1.5em;
                flex-shrink: 0;
            }
            
            .guest-warning a {
                color: #32cd32;
                font-weight: 600;
                text-decoration: none;
                transition: color 0.3s ease;
            }
            
            .guest-warning a:hover {
                color: #228b22;
                text-decoration: underline;
            }
            
            .info-card, .action-card {
                background: #f8f9fa;
                border-radius: 12px;
                padding: 25px;
                margin-bottom: 20px;
                border-left: 4px solid #32cd32;
                transition: all 0.3s ease;
            }
            
            .info-card:hover, .action-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(50, 205, 50, 0.1);
            }
            
            .info-card h4, .action-card h4 {
                color: #228b22;
                margin-top: 0;
                margin-bottom: 15px;
                font-size: 1.2em;
                font-weight: 600;
            }
            
            .info-card ul {
                margin: 0;
                padding-left: 20px;
            }
            
            .info-card li {
                margin-bottom: 8px;
                color: #495057;
            }
            
            .action-buttons {
                display: flex;
                gap: 15px;
                flex-wrap: wrap;
                justify-content: center;
            }
            
            .btn {
                padding: 12px 24px;
                border: none;
                border-radius: 8px;
                font-size: 1rem;
                font-weight: 500;
                text-decoration: none;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 8px;
            }
            
            .btn-primary {
                background: linear-gradient(135deg, #32cd32 0%, #228b22 100%);
                color: white;
            }
            
            .btn-primary:hover {
                background: linear-gradient(135deg, #228b22 0%, #1a6b1a 100%);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(50, 205, 50, 0.3);
                color: white;
                text-decoration: none;
            }
            
            .btn-secondary {
                background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
                color: white;
            }
            
            .btn-secondary:hover {
                background: linear-gradient(135deg, #5a6268 0%, #495057 100%);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
                color: white;
                text-decoration: none;
            }
            
            /* Responsive */
            @media (max-width: 768px) {
                .action-buttons {
                    flex-direction: column;
                }
                
                .btn {
                    width: 100%;
                    justify-content: center;
                }
                
                .guest-warning {
                    flex-direction: column;
                    text-align: center;
                    gap: 10px;
                }
            }
        </style>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
</body>
</html>
