<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Portafoglio Ospite - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/wallet.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="wallet-container">
        <div class="wallet-section">
            <h3><i class="fas fa-ticket-alt"></i> I Tuoi Biglietti</h3>
            
            <div class="wallet-section-content">
                <div class="warning-message">
                    <i class="fas fa-exclamation-triangle"></i>
                    <div>
                        <strong>Attenzione!</strong> Stai navigando come ospite. I tuoi biglietti sono salvati temporaneamente nel browser.
                        <a href="${pageContext.request.contextPath}/register">Registrati ora</a> per non perderli!
                    </div>
                </div>
                
                <div id="tickets-container">
                    <div class="empty-state" id="empty-state">
                        <i class="fas fa-ticket-alt"></i>
                        <p>Nessun biglietto nel tuo portafoglio temporaneo.</p>
                        <p><em>I biglietti acquistati come ospite appariranno qui.</em></p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                            <i class="fas fa-search"></i> Cerca Biglietti
                        </a>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="wallet-section">
            <h3><i class="fas fa-info-circle"></i> Informazioni Importanti</h3>
            <div class="wallet-section-content">
                <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #32cd32;">
                    <h4 style="margin-top: 0; color: #32cd32;">Come funziona il portafoglio ospite?</h4>
                    <ul style="margin-bottom: 0; padding-left: 20px;">
                        <li><strong>Temporaneo:</strong> I biglietti sono salvati solo nel tuo browser</li>
                        <li><strong>Durata:</strong> I biglietti rimangono fino alla chiusura del browser</li>
                        <li><strong>Dispositivo:</strong> Disponibili solo su questo dispositivo</li>
                        <li><strong>Backup:</strong> Salva il QR code per sicurezza</li>
                    </ul>
                </div>
                
                <div style="text-align: center; margin-top: 20px;">
                    <h4>Vuoi conservare permanentemente i tuoi biglietti?</h4>
                    <div class="action-buttons" style="gap: 10px; justify-content: center;">
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
</main>

<jsp:include page="import/footer.jsp"/>

<script>
document.addEventListener('DOMContentLoaded', function() {
    
    // Funzione per ottenere i biglietti guest dal sessionStorage
    function getGuestTickets() {
        try {
            return JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
        } catch (e) {
            console.warn('Errore nel parsing dei biglietti guest:', e);
            return [];
        }
    }
    
    // Funzione per visualizzare i biglietti guest
    function displayGuestTickets() {
        const tickets = getGuestTickets();
        const ticketsContainer = document.getElementById('tickets-container');
        const emptyState = document.getElementById('empty-state');
        
        if (tickets.length > 0) {
            // Nasconde lo stato vuoto
            if (emptyState) {
                emptyState.style.display = 'none';
            }
            
            // Crea HTML per i biglietti ospite
            const guestTicketsHTML = tickets.map((ticket, index) => {
                const percorso = ticket.percorso || ticket.nome || 'Percorso Non Definito';
                const tipo = ticket.tipo || 'NORMALE';
                const prezzo = ticket.prezzo ? ticket.prezzo.toFixed(2) : '0.00';
                const data = ticket.data || 'Non specificata';
                const orario = ticket.orario || 'Non specificato';
                const dataAcquisto = ticket.dataAcquisto ? new Date(ticket.dataAcquisto).toLocaleDateString('it-IT') : 'Oggi';
                const stato = ticket.stato || 'ACQUISTATO';
                const codice = ticket.codice || 'OMR' + ticket.id;
                const statusClass = getStatusClass(stato);
                
                return `
                    <div class="ticket-card guest-ticket" data-ticket-index="${index}">
                        <div class="ticket-info">
                            <p><strong>Percorso:</strong> ${percorso}</p>
                            <p><strong>Tipo:</strong> ${tipo}</p>
                            <p><strong>Prezzo:</strong> €${prezzo}</p>
                            <p><strong>Data viaggio:</strong> ${data}</p>
                            <p><strong>Orario:</strong> ${orario}</p>
                            <p><strong>Acquistato il:</strong> ${dataAcquisto}</p>
                            <p><strong>Stato:</strong> <span class="ticket-status ${statusClass}">${stato}</span></p>
                            <p><strong>Codice:</strong> <code style="background: #f8f9fa; padding: 2px 6px; border-radius: 4px; font-family: monospace;">${codice}</code></p>
                        </div>
                        
                        <!-- QR Code Container -->
                        <div class="qr-container" style="text-align: center; margin: 15px 0;">
                            <div class="qr-code" id="qr-guest-${index}" style="width:100px;height:100px;margin:0 auto 10px;"></div>
                            <div class="qr-code-text" style="font-family: monospace; font-size: 12px; color: #666;">${codice}</div>
                        </div>
                        
                        <div class="ticket-actions">
                            <button class="btn-qr" onclick="downloadQR('${codice}')" title="Scarica QR Code">
                                <i class="fas fa-download"></i> Scarica QR
                            </button>
                            <button class="btn-remove" onclick="removeGuestTicket(${index})" title="Rimuovi Biglietto">
                                <i class="fas fa-trash"></i> Rimuovi
                            </button>
                        </div>
                    </div>
                `;
            }).join('');
            
            // Inserisce i biglietti ospite nel container
            ticketsContainer.innerHTML = guestTicketsHTML;
            
            // Genera i QR codes dopo l'inserimento
            setTimeout(() => {
                tickets.forEach((ticket, index) => {
                    const qrElement = document.getElementById(`qr-guest-${index}`);
                    if (qrElement && qrElement.innerHTML === '') {
                        new QRCode(qrElement, {
                            text: ticket.codice || 'OMR' + ticket.id,
                            width: 100,
                            height: 100
                        });
                    }
                });
            }, 100);
            
        } else {
            // Mostra lo stato vuoto
            if (emptyState) {
                emptyState.style.display = 'block';
            }
        }
    }
    
    // Funzione per determinare la classe CSS dello stato del biglietto
    function getStatusClass(stato) {
        switch(stato) {
            case 'CONVALIDATO': return 'active';
            case 'SCADUTO': return 'expired';
            case 'ACQUISTATO':
            default: return 'inactive';
        }
    }
    
    // Funzione per rimuovere un biglietto ospite
    window.removeGuestTicket = function(ticketIndex) {
        if (!confirm('Sei sicuro di voler rimuovere questo biglietto?')) {
            return;
        }
        
        let tickets = getGuestTickets();
        if (ticketIndex >= 0 && ticketIndex < tickets.length) {
            tickets.splice(ticketIndex, 1);
            sessionStorage.setItem('guestTickets', JSON.stringify(tickets));
            
            // Aggiorna la visualizzazione
            displayGuestTickets();
            
            // Aggiorna il badge nell'header
            if (window.GuestWallet) {
                window.GuestWallet.updateVisibility();
            }
            
            // Mostra notifica
            showNotification('Biglietto rimosso dal portafoglio temporaneo.', 'success');
        }
    };
    
    // Funzione per scaricare QR code
    window.downloadQR = function(ticketCode) {
        try {
            // Trova il QR code corrispondente
            const qrElements = document.querySelectorAll('[id^="qr-guest-"]');
            let targetQR = null;
            
            for (let qrEl of qrElements) {
                const canvas = qrEl.querySelector('canvas');
                if (canvas) {
                    targetQR = canvas;
                    break;
                }
            }
            
            if (targetQR) {
                // Crea un link per il download
                const link = document.createElement('a');
                link.download = `biglietto-${ticketCode}.png`;
                link.href = targetQR.toDataURL();
                link.click();
                
                showNotification('QR Code scaricato con successo!', 'success');
            } else {
                showNotification('Errore nel download del QR Code', 'error');
            }
        } catch (error) {
            console.error('Errore nel download:', error);
            showNotification('Errore nel download del QR Code', 'error');
        }
    };
    
    // Funzione per mostrare notifiche
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        
        // Imposta gli stili in base al tipo
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
        
        // Rimuove automaticamente dopo 3 secondi
        setTimeout(function() {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 3000);
    }
    
    // Carica e visualizza i biglietti al caricamento della pagina
    displayGuestTickets();
    
    // Monitora i cambiamenti nel sessionStorage
    const originalSetItem = sessionStorage.setItem;
    sessionStorage.setItem = function(key, value) {
        originalSetItem.apply(this, arguments);
        if (key === 'guestTickets') {
            displayGuestTickets();
        }
    };
});
</script>

</body>
</html>
