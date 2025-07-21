<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Acquisto Completato - OmniRide</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body>
<jsp:include page="/import/header.jsp"/>

<main class="success-page">
    <div class="success-container">
        <div class="success-icon">✅</div>
        
        <h1 class="success-title">Acquisto Completato!</h1>
        
        <div class="success-message">
            Il tuo acquisto è stato elaborato con successo. I tuoi biglietti sono pronti per l'uso.
        </div>

        <div class="ticket-info">
            <h3>📋 Riepilogo Acquisto</h3>
            
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Data Acquisto</div>
                    <div class="info-value">
                        <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy HH:mm"/>
                    </div>
                </div>
                
                <div class="info-item">
                    <div class="info-label">Numero Biglietti</div>
                    <div class="info-value">
                        <c:choose>
                            <c:when test="${not empty sessionScope.guestTicketIds}">
                                ${sessionScope.guestTicketIds.size()}
                            </c:when>
                            <c:otherwise>
                                1
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="info-item">
                    <div class="info-label">Tipo Acquirente</div>
                    <div class="info-value">Ospite (Guest)</div>
                </div>
            </div>

            <c:if test="${not empty sessionScope.guestTicketIds}">
                <div class="tickets-list">
                    <h4>I tuoi biglietti:</h4>
                    <c:forEach var="ticketId" items="${sessionScope.guestTicketIds}" varStatus="status">
                        <div class="ticket-item">
                            <div class="ticket-id">Biglietto #${ticketId}</div>
                            <p style="margin: 0; color: #636e72; font-size: 0.9rem;">
                                Codice: OM${String.format("%07d", ticketId)}
                            </p>
                            <!-- QR Code Container -->
                            <div id="qrcode-${ticketId}" style="width:120px; height:120px; margin:15px auto;"></div>
                            <script>
                                document.addEventListener('DOMContentLoaded', function() {
                                    new QRCode(document.getElementById("qrcode-${ticketId}"), {
                                        text: "OM${String.format("%07d", ticketId)}",
                                        width: 120,
                                        height: 120
                                    });
                                });
                            </script>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <div class="guest-notice">
            <h4>⚠️ Importante per gli Ospiti</h4>
            <ul>
                <li><strong>Conserva questa pagina:</strong> Stampa o fai uno screenshot di questa pagina per i tuoi archivi</li>
                <li><strong>Nessun account richiesto:</strong> I tuoi biglietti sono validi anche senza registrazione</li>
                <li><strong>Validazione:</strong> Dovrai convalidare i biglietti prima dell'uso</li>
                <li><strong>Supporto:</strong> Per assistenza, contatta il nostro servizio clienti</li>
            </ul>
        </div>

        <div id="ticketSaveStatus" style="margin: 20px 0; padding: 15px; border-radius: 8px; display: none; text-align: center;">
            <!-- Status message will be inserted here by JavaScript -->
        </div>
        
        <div class="actions-section">
            <a href="${pageContext.request.contextPath}/wallet" class="btn btn-primary" style="background: linear-gradient(135deg, #32cd32 0%, #228b22 100%);">💼 Visualizza Portafoglio</a>
            <button onclick="window.print()" class="btn btn-secondary">
                🖨️ Stampa Biglietti
            </button>
            
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                🏠 Torna alla Home
            </a>
            
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary" style="background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);">👤 Registrati Ora</a>
        </div>
    </div>

    <!-- Additional information section -->
    <div style="background: #f8f9fa; border-radius: 8px; padding: 2rem; margin-top: 2rem;">
        <h3>📞 Hai bisogno di aiuto?</h3>
        <p style="margin-bottom: 1rem; color: #636e72;">
            Se hai domande sui tuoi biglietti o necessiti di assistenza, contattaci:
        </p>
        <div style="display: flex; gap: 2rem; justify-content: center; flex-wrap: wrap;">
            <div style="text-align: center;">
                <strong>📞 Telefono</strong><br>
                <span style="color: #32cd32; font-size: 1.1rem;">+39 02 1234 5678</span>
            </div>
            <div style="text-align: center;">
                <strong>📧 Email</strong><br>
                <span style="color: #32cd32; font-size: 1.1rem;">supporto@omniride.it</span>
            </div>
            <div style="text-align: center;">
                <strong>🕒 Orari</strong><br>
                <span style="color: #636e72;">Lun-Ven: 8:00-20:00</span>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/import/footer.jsp"/>

<script>
// Auto-scroll to top on page load
window.addEventListener('load', function() {
    window.scrollTo(0, 0);
});

// Salva i biglietti nel sessionStorage per utenti guest
document.addEventListener('DOMContentLoaded', function() {
    // Recupera i biglietti dal server e li salva nel sessionStorage
    const tickets = [
        <c:forEach var="biglietto" items="${purchasedTickets}" varStatus="status">
        {
            id: '${biglietto.id}',
            tipo: '${biglietto.tipo}',
            nome: '${biglietto.nome}',
            prezzo: ${biglietto.prezzo},
            dataAcquisto: '${biglietto.dataAcquisto}',
            dataConvalida: '${biglietto.dataConvalida}',
            dataFine: '${biglietto.dataFine}',
            stato: '${biglietto.stato}',
            codice: '${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : "OM".concat(String.format("%07d", biglietto.id))}',
            percorso: '${biglietto.nome}',
            quantita: 1,
            data: new Date().toISOString().split('T')[0],
            orario: new Date().toTimeString().split(' ')[0].substring(0, 5)
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    
    // Se non ci sono biglietti dal server, crea biglietti dai guestTicketIds
    if (tickets.length === 0 && ${not empty sessionScope.guestTicketIds}) {
        <c:forEach var="ticketId" items="${sessionScope.guestTicketIds}" varStatus="status">
        tickets.push({
            id: '${ticketId}',
            tipo: 'NORMALE',
            nome: 'Biglietto Guest',
            prezzo: 0,
            dataAcquisto: new Date().toISOString(),
            dataConvalida: null,
            dataFine: null,
            stato: 'ACQUISTATO',
            codice: 'OM${String.format("%07d", ticketId)}',
            percorso: 'Biglietto Guest',
            quantita: 1,
            data: new Date().toISOString().split('T')[0],
            orario: new Date().toTimeString().split(' ')[0].substring(0, 5)
        });
        </c:forEach>
    }
    
    if (tickets.length > 0) {
        try {
            // Recupera i biglietti esistenti
            let existingTickets = JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
            
            // Aggiunge i nuovi biglietti (evita duplicati)
            tickets.forEach(newTicket => {
                const exists = existingTickets.some(existing => existing.id === newTicket.id);
                if (!exists) {
                    existingTickets.push(newTicket);
                }
            });
            
            // Salva nel sessionStorage
            sessionStorage.setItem('guestTickets', JSON.stringify(existingTickets));
            
            // Aggiorna la visibilità del wallet nell'header
            if (window.GuestWallet) {
                window.GuestWallet.updateVisibility();
                // Mostra notifica di successo
                window.GuestWallet.showNotification('Biglietti salvati nel portafoglio temporaneo!');
            }
            
            // Mostra messaggio di successo
            const statusDiv = document.getElementById('ticketSaveStatus');
            if (statusDiv) {
                statusDiv.style.background = '#d4edda';
                statusDiv.style.border = '1px solid #c3e6cb';
                statusDiv.style.color = '#155724';
                statusDiv.innerHTML = '✅ I tuoi biglietti sono stati salvati nel portafoglio temporaneo!';
                statusDiv.style.display = 'block';
            }
            
            console.log('Biglietti salvati con successo:', existingTickets);
            
        } catch (error) {
            console.error('Errore nel salvataggio dei biglietti:', error);
            
            // Mostra messaggio di errore
            const statusDiv = document.getElementById('ticketSaveStatus');
            if (statusDiv) {
                statusDiv.style.background = '#f8d7da';
                statusDiv.style.border = '1px solid #f5c6cb';
                statusDiv.style.color = '#721c24';
                statusDiv.innerHTML = '⚠️ Errore nel salvataggio dei biglietti. I tuoi biglietti sono comunque validi.';
                statusDiv.style.display = 'block';
            }
        }
        
        // Auto-hide status message after 5 seconds
        setTimeout(function() {
            const statusDiv = document.getElementById('ticketSaveStatus');
            if (statusDiv) {
                statusDiv.style.display = 'none';
            }
        }, 8000);
    }
});
</script>

</body>
</html>
