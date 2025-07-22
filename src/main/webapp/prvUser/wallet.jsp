<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../import/metadata.jsp"/>
    <title>Il Mio Portafoglio - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/wallet.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body>
<jsp:include page="../import/header.jsp"/>

<main>
    <div class="wallet-container">
        <div class="wallet-section">
            <h3><i class="fas fa-ticket-alt"></i> I Miei Biglietti</h3>
            <div class="wallet-section-content">
                <c:if test="${not empty error}">
                    <div class="error-message" style="background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; margin-bottom: 20px; border-radius: 5px;">
                        <i class="fas fa-exclamation-triangle"></i> <strong>Errore:</strong> ${error}
                    </div>
                </c:if>
                <div class="tickets-grid">
                    <c:choose>
                        <c:when test="${not empty biglietti}">
                            <c:forEach var="biglietto" items="${biglietti}">
                                <div class="ticket-card">
                                    <div class="ticket-info">
                                        <p><strong>Percorso:</strong> ${not empty biglietto.nome ? biglietto.nome : 'Percorso Non Definito'}</p>
                                        <p><strong>Tipo:</strong> ${biglietto.tipo}</p>
                                        <p><strong>Prezzo:</strong> <fmt:formatNumber value="${biglietto.prezzo}" type="currency" currencySymbol="€"/></p>
                                        <p><strong>Acquistato il:</strong> ${biglietto.dataAcquisto}</p>
                                        <c:if test="${biglietto.dataConvalida != null}">
                                            <p><strong>Convalidato il:</strong> ${biglietto.dataConvalida}</p>
                                        </c:if>
                                        <c:if test="${biglietto.dataFine != null}">
                                            <p><strong>Valido fino:</strong> ${biglietto.dataFine}</p>
                                        </c:if>
                                        <p><strong>Stato:</strong> <span class="ticket-status ${biglietto.stato eq 'ACQUISTATO' ? 'inactive' : (biglietto.stato eq 'CONVALIDATO' ? 'active' : 'expired')}">${biglietto.stato}</span></p>
                                        <p><strong>Codice:</strong> <code style="background: #f8f9fa; padding: 2px 6px; border-radius: 4px; font-family: monospace;">${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : 'OM'.concat(String.format("%07d", biglietto.id))}</code></p>
                                    </div>
                                    
                                    <!-- QR Code Container -->
                                    <div class="qr-container" style="text-align: center; margin: 15px 0;">
                                        <div class="qr-code" id="qr-${biglietto.id}" style="width:100px;height:100px;margin:0 auto 10px;"></div>
                                        <div class="qr-code-text" style="font-family: monospace; font-size: 12px; color: #666;">${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : 'OM'.concat(String.format("%07d", biglietto.id))}</div>
                                    </div>
                                    
                                    <div class="ticket-actions">
                                        <c:choose>
                                            <c:when test="${biglietto.stato eq 'ACQUISTATO'}">
                                                <!-- Biglietto inattivo - Mostra pulsante per attivare -->
                                                <form action="${pageContext.request.contextPath}/wallet" method="post" style="display: inline;" class="activate-form">
                                                    <input type="hidden" name="action" value="activateTicket">
                                                    <input type="hidden" name="idBiglietto" value="${biglietto.id}">
                                                    <button type="submit" class="btn-activate" onclick="return confirm('Sei sicuro di voler attivare questo biglietto? Una volta attivato non può più essere disattivato.');">
                                                        <i class="fas fa-play"></i> Attiva Biglietto
                                                    </button>
                                                </form>
                                                <small class="activation-note">Il biglietto verrà attivato e inizierà il countdown</small>
                                            </c:when>
                                            <c:when test="${biglietto.stato eq 'CONVALIDATO'}">
                                                <!-- Biglietto attivo - Mostra informazioni di validità -->
                                                <div class="active-ticket-info">
                                                    <span class="active-badge"><i class="fas fa-check-circle"></i> Biglietto Attivo</span>
                                                    <c:if test="${biglietto.dataFine != null}">
                                                        <small class="validity-info">Valido fino alle ${biglietto.dataFine}</small>
                                                    </c:if>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Biglietto scaduto o annullato -->
                                                <div class="expired-ticket-info">
                                                    <span class="expired-badge"><i class="fas fa-times-circle"></i> ${biglietto.stato}</span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <!-- Pulsante QR sempre disponibile -->
                                        <button class="btn-qr" onclick="downloadQR('${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : 'OM'.concat(String.format("%07d", biglietto.id))}', 'qr-${biglietto.id}')" title="Scarica QR Code">
                                            <i class="fas fa-download"></i> Scarica QR
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-ticket-alt"></i>
                                <p>Non hai ancora acquistato biglietti.</p>
                                <p><em>I tuoi biglietti appariranno qui dopo l'acquisto.</em></p>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                                    <i class="fas fa-search"></i> Cerca Biglietti
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
        </div>
        
        <!-- Sezione Associazione Biglietti Ospite -->
        <div class="wallet-section">
            <h3><i class="fas fa-link"></i> Associa Biglietti Ospite</h3>
            <div class="wallet-section-content">
                <div class="guest-tickets-associate">
                    <p>Se hai acquistato biglietti come ospite, puoi associarli al tuo account inserendo l'ID del biglietto.</p>
                    
                    <form id="associateTicketForm" class="associate-form">
                        <div class="form-group">
                            <label for="guestTicketId">ID Biglietto Ospite:</label>
                            <input type="text" id="guestTicketId" name="guestTicketId" 
                                   placeholder="Es: OMR1234567" required>
                            <small class="help-text">L'ID del biglietto si trova nella email di conferma o sul QR code</small>
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-link"></i> Associa Biglietto
                        </button>
                    </form>
                    
                    <div id="associateResult" class="associate-result" style="display: none;"></div>
                </div>
            </div>
        </div>
    </div>
</main>
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
                        <p><strong>Tipo:</strong> \${ticket.tipo}</p>
                        <p><strong>Percorso:</strong> \${ticket.percorso}</p>
                        <p><strong>Prezzo:</strong> \${ticket.prezzo}€</p>
                        <p><strong>Data viaggio:</strong> \${ticket.data}</p>
                        <p><strong>Orario:</strong> \${ticket.orario}</p>
                        <p><strong>Acquistato il:</strong> \${new Date(ticket.dataAcquisto).toLocaleDateString('it-IT')}</p>
                        <p><strong>Stato:</strong> <span class="ticket-status inactive">\${ticket.stato}</span></p>
                    </div>
                    <div class="ticket-actions">
                        <button class="btn-qr" onclick="generateQR('\${ticket.id}')" title="Genera QR Code">📱 Salva QR</button>
                        <button class="btn-remove" onclick="removeGuestTicket('\${ticket.id}')" title="Rimuovi Biglietto">🗑️</button>
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
    
    // Genera i QR codes per i biglietti degli utenti registrati
    setTimeout(() => {
        const qrElements = document.querySelectorAll('[id^="qr-"]');
        qrElements.forEach(qrElement => {
            if (qrElement.innerHTML === '' || qrElement.children.length === 0) {
                const ticketCode = qrElement.parentElement.querySelector('.qr-code-text').textContent;
                try {
                    // Pulisci il contenitore prima di creare un nuovo QR
                    qrElement.innerHTML = '';
                    
                    new QRCode(qrElement, {
                        text: ticketCode,
                        width: 120,
                        height: 120,
                        colorDark: '#2d5016',
                        colorLight: '#ffffff',
                        correctLevel: QRCode.CorrectLevel.M
                    });
                    
                    console.log('QR generato per:', ticketCode);
                } catch (error) {
                    console.error('Errore generazione QR:', error);
                    qrElement.innerHTML = '<div class="qr-error">QR non disponibile</div>';
                }
            }
        });
    }, 500);
});

// Funzione per scaricare QR code (utenti registrati)
function downloadQR(ticketCode, qrElementId) {
    try {
        const qrElement = document.getElementById(qrElementId);
        const canvas = qrElement.querySelector('canvas');
        
        if (canvas) {
            const link = document.createElement('a');
            link.download = `biglietto-${ticketCode}.png`;
            link.href = canvas.toDataURL();
            link.click();
            
            showNotification('QR Code scaricato con successo!', 'success');
        } else {
            showNotification('Errore nel download del QR Code', 'error');
        }
    } catch (error) {
        console.error('Errore nel download:', error);
        showNotification('Errore nel download del QR Code', 'error');
    }
}

// Funzione per mostrare notifiche
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    
    const isSuccess = type === 'success';
    const bgColor = isSuccess ? '#d4edda' : '#d1ecf1';
    const borderColor = isSuccess ? '#c3e6cb' : '#bee5eb';
    const textColor = isSuccess ? '#155724' : '#0c5460';
    
    notification.style.cssText = 'position: fixed; top: 90px; right: 20px; padding: 15px 20px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); z-index: 10000; max-width: 300px; font-size: 14px;';
    notification.style.background = bgColor;
    notification.style.border = '1px solid ' + borderColor;
    notification.style.color = textColor;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(function() {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 3000);
}

// Gestione associazione biglietti ospite
document.addEventListener('DOMContentLoaded', function() {
    const associateForm = document.getElementById('associateTicketForm');
    const resultDiv = document.getElementById('associateResult');
    
    if (associateForm) {
        associateForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const guestTicketId = document.getElementById('guestTicketId').value.trim();
            
            if (!guestTicketId) {
                showAssociateResult('Per favore inserisci un ID biglietto valido.', 'error');
                return;
            }
            
            // Mostra loading
            showAssociateResult('Ricerca del biglietto in corso...', 'info');
            
            // Invio richiesta al server
            fetch('${pageContext.request.contextPath}/wallet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=associateGuestTicket&guestTicketId=`+encodeURIComponent(guestTicketId)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showAssociateResult('Biglietto associato con successo! Ricaricando la pagina...', 'success');
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                } else {
                    showAssociateResult(data.message || 'Errore nell\'associazione del biglietto.', 'error');
                }
            })
            .catch(error => {
                console.error('Errore:', error);
                showAssociateResult('Errore di comunicazione con il server.', 'error');
            });
        });
    }
    
    function showAssociateResult(message, type) {
        if (resultDiv) {
            resultDiv.style.display = 'block';
            resultDiv.className = `associate-result ${type}`;
            resultDiv.textContent = message;
            
            if (type === 'success') {
                resultDiv.style.backgroundColor = '#d4edda';
                resultDiv.style.color = '#155724';
                resultDiv.style.border = '1px solid #c3e6cb';
            } else if (type === 'error') {
                resultDiv.style.backgroundColor = '#f8d7da';
                resultDiv.style.color = '#721c24';
                resultDiv.style.border = '1px solid #f5c6cb';
            } else {
                resultDiv.style.backgroundColor = '#d1ecf1';
                resultDiv.style.color = '#0c5460';
                resultDiv.style.border = '1px solid #bee5eb';
            }
            
            resultDiv.style.padding = '15px';
            resultDiv.style.borderRadius = '5px';
            resultDiv.style.marginTop = '15px';
        }
    }
});
</script>

</body>
</html>
