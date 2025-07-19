<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../import/metadata.jsp"/>
    <title>Il Mio Portafoglio - Omniride</title>
</head>
<body>
<jsp:include page="../import/header.jsp"/>

<main>
    <div class="wallet-container">
        <h2>Il Mio Portafoglio</h2>

<div class="wallet-section">
            <h3>I Miei Biglietti</h3>
            <div id="guest-tickets-warning" class="warning-message" style="display: none; background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; margin-bottom: 20px; border-radius: 5px;">
                <strong>⚠️ Attenzione!</strong> Se non sei registrato potresti perdere i tuoi biglietti. <a href="${pageContext.request.contextPath}/register">Registrati ora</a> o salva il QR code dei tuoi biglietti.
            </div>
            <div id="tickets-container">
                <c:choose>
                    <c:when test="${not empty biglietti}">
                        <c:forEach var="biglietto" items="${biglietti}">
                            <div class="ticket-card">
                                <div class="ticket-info">
                                    <p><strong>Tratta:</strong> ${biglietto.tratta.nome}</p>
                                    <p><strong>Acquistato il:</strong> <fmt:formatDate value="${biglietto.dataAcquisto}" pattern="dd/MM/yyyy HH:mm"/></p>
                                    <p><strong>Stato:</strong> <span class="ticket-status ${biglietto.stato eq 'ACQUISTATO' ? 'inactive' : (biglietto.stato eq 'CONVALIDATO' ? 'active' : 'expired')}">${biglietto.stato}</span></p>
                                </div>
                                <div class="ticket-actions">
                                    <c:if test="${biglietto.stato eq 'ACQUISTATO'}">
                                        <form action="${pageContext.request.contextPath}/wallet" method="post">
                                            <input type="hidden" name="action" value="activateTicket">
                                            <input type="hidden" name="idBiglietto" value="${biglietto.id}">
                                            <button type="submit" class="btn-activate">Attiva Biglietto</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state" id="empty-state">
                            <p>Nessun biglietto nel tuo portafoglio.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="wallet-section">
            <h3>I Miei Abbonamenti</h3>
            <div class="empty-state">
                <p>Nessun abbonamento attivo al momento.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

        <div class="wallet-section">
            <h3>Carte di Credito Salvate</h3>
            <div class="empty-state">
                <p>Nessuna carta di credito salvata.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

    </div>
</main>

<jsp:include page="../import/footer.jsp"/>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Funzione per salvare biglietti acquistati dal carrello nella sessione
    function saveTicketToSession(ticketData) {
        let tickets = JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
        
        const ticket = {
            id: Date.now(), // ID temporaneo
            tipo: ticketData.tipo || ticketData.ticketType || 'NORMALE',
            prezzo: ticketData.prezzo || ticketData.price,
            data: ticketData.data || new Date().toISOString().split('T')[0],
            orario: ticketData.orario || new Date().toTimeString().split(' ')[0],
            percorso: ticketData.percorso || `${ticketData.partenza || 'Partenza'} - ${ticketData.arrivo || 'Arrivo'}`,
            quantita: ticketData.quantita || 1,
            dataAcquisto: new Date().toISOString(),
            stato: 'ACQUISTATO',
            codice: 'OMR' + Math.random().toString(36).substr(2, 9).toUpperCase()
        };
        
        tickets.push(ticket);
        sessionStorage.setItem('guestTickets', JSON.stringify(tickets));
        
        // Aggiorna il badge nel header
        updateTicketsBadge(tickets.length);
    }
    
    // Funzione per recuperare biglietti ospite dalla sessione
    function getGuestTicketsFromSession() {
        return JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
    }
    
    // Funzione per mostrare biglietti ospite
    function displayGuestTickets() {
        const tickets = getGuestTicketsFromSession();
        const ticketsContainer = document.getElementById('tickets-container');
        const emptyState = document.getElementById('empty-state');
        const warningMessage = document.getElementById('guest-tickets-warning');
        
        if (tickets.length > 0) {
            // Mostra l'avviso per gli utenti ospite
            if (warningMessage) {
                warningMessage.style.display = 'block';
            }
            
            // Nasconde lo stato vuoto
            if (emptyState) {
                emptyState.style.display = 'none';
            }
            
            // Crea HTML per i biglietti ospite
            const guestTicketsHTML = tickets.map(ticket => `
                <div class="ticket-card guest-ticket">
                    <div class="ticket-info">
                        <p><strong>Tipo:</strong> ${ticket.tipo}</p>
                        <p><strong>Percorso:</strong> ${ticket.percorso}</p>
                        <p><strong>Prezzo:</strong> ${ticket.prezzo}€</p>
                        <p><strong>Data viaggio:</strong> ${ticket.data}</p>
                        <p><strong>Orario:</strong> ${ticket.orario}</p>
                        <p><strong>Acquistato il:</strong> ${new Date(ticket.dataAcquisto).toLocaleDateString('it-IT')}</p>
                        <p><strong>Stato:</strong> <span class="ticket-status inactive">${ticket.stato}</span></p>
                    </div>
                    <div class="ticket-actions">
                        <button class="btn-qr" onclick="generateQR('${ticket.id}')" title="Genera QR Code">📱 Salva QR</button>
                        <button class="btn-remove" onclick="removeGuestTicket('${ticket.id}')" title="Rimuovi Biglietto">🗑️</button>
                    </div>
                </div>
            `).join('');
            
            // Inserisce i biglietti ospite nel container
            if (ticketsContainer) {
                ticketsContainer.insertAdjacentHTML('afterbegin', guestTicketsHTML);
            }
            
            // Aggiorna il badge
            updateTicketsBadge(tickets.length);
        }
    }
    
    // Funzione per rimuovere un biglietto ospite
    function removeGuestTicket(ticketId) {
        let tickets = getGuestTicketsFromSession();
        tickets = tickets.filter(ticket => ticket.id != ticketId);
        sessionStorage.setItem('guestTickets', JSON.stringify(tickets));
        
        // Ricarica la pagina per aggiornare la visualizzazione
        location.reload();
    }
    
    // Funzione per generare QR code (placeholder)
    function generateQR(ticketId) {
        const tickets = getGuestTicketsFromSession();
        const ticket = tickets.find(t => t.id == ticketId);
        
        if (ticket) {
            // Crea dati per QR code
            const qrData = JSON.stringify({
                id: ticket.id,
                tipo: ticket.tipo,
                percorso: ticket.percorso,
                prezzo: ticket.prezzo,
                data: ticket.data,
                orario: ticket.orario
            });
            
            // Per ora mostra i dati in un alert
            // TODO: Implementare la generazione del QR code
            alert('Dati biglietto per QR Code:\n' + qrData + '\n\nSalva questi dati per non perdere il tuo biglietto!');
        }
    }
    
    // Funzione per aggiornare il badge dei biglietti nel header
    function updateTicketsBadge(count) {
        const badge = document.querySelector('.btnHeader-icon[title="Portafoglio"] .badge');
        if (badge) {
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        }
    }
    
    // Rende le funzioni globali per l'uso negli onclick
    window.saveTicketToSession = saveTicketToSession;
    window.removeGuestTicket = removeGuestTicket;
    window.generateQR = generateQR;
    
    // Controlla se ci sono biglietti ospite da mostrare
    const isLoggedIn = ${not empty sessionScope.utente ? 'true' : 'false'};
    if (!isLoggedIn) {
        displayGuestTickets();
    }
    
    // Inizializza il badge anche se l'utente è loggato (per biglietti ospite precedenti)
    const guestTickets = getGuestTicketsFromSession();
    if (guestTickets.length > 0) {
        updateTicketsBadge(guestTickets.length);
    }
});
</script>

</body>
</html>
