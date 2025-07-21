<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
                    <c:choose>
                        <c:when test="${not empty requestScope.biglietti}">
                            <!-- Biglietti dal database per guest -->
                            <c:forEach var="biglietto" items="${requestScope.biglietti}">
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
                                    
                    <!-- QR Code Container Migliorato -->
                    <div class="qr-container modern-qr">
                        <div class="qr-wrapper">
                            <div class="qr-code" id="qr-${biglietto.id}"></div>
                            <div class="qr-overlay">
                                <div class="qr-status ${fn:toLowerCase(biglietto.stato)}">
                                    <c:choose>
                                        <c:when test="${biglietto.stato eq 'CONVALIDATO'}">✓</c:when>
                                        <c:when test="${biglietto.stato eq 'SCADUTO'}">✕</c:when>
                                        <c:otherwise>●</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                        <div class="qr-code-info">
                            <div class="qr-code-text">${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : 'OM'.concat(String.format("%07d", biglietto.id))}</div>
                            <div class="qr-scan-hint">Scansiona per convalidare</div>
                        </div>
                    </div>
                                    
                                    <div class="ticket-actions">
                                        <button class="btn-qr" onclick="downloadQRFromDB('${not empty biglietto.codiceBiglietto ? biglietto.codiceBiglietto : 'OM'.concat(String.format("%07d", biglietto.id))}', 'qr-${biglietto.id}')" title="Scarica QR Code">
                                            <i class="fas fa-download"></i> Scarica QR
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" id="empty-state">
                                <i class="fas fa-ticket-alt"></i>
                                <p>Nessun biglietto nel tuo portafoglio temporaneo.</p>
                                <p><em>I biglietti acquistati come ospite appariranno qui.</em></p>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                                    <i class="fas fa-search"></i> Cerca Biglietti
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
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
    
    // Controlla se siamo in modalità guest e abbiamo biglietti dal server
    const isGuest = ${not empty requestScope.isGuest ? 'true' : 'false'};
    const serverBiglietti = ${not empty requestScope.biglietti ? requestScope.biglietti.size() : 0};
    
    console.log('[WALLET DEBUG] isGuest:', isGuest, 'serverBiglietti:', serverBiglietti);
    
    // Se siamo in modalità guest e abbiamo biglietti dal server, non usare sessionStorage
    if (isGuest && serverBiglietti > 0) {
        console.log('[WALLET DEBUG] Usando biglietti dal database per guest');
        return; // I biglietti sono già renderizzati dal server
    }
    
    // Funzione per ottenere i biglietti guest dal sessionStorage (fallback)
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
                        
                                        <!-- QR Code Container Migliorato -->
                        <div class="qr-container modern-qr">
                            <div class="qr-wrapper">
                                <div class="qr-code" id="qr-guest-${index}"></div>
                                <div class="qr-overlay">
                                    <div class="qr-status " id="status-${index}">
                                        <span class="status-icon">●</span>
                                    </div>
                                </div>
                            </div>
                            <div class="qr-code-info">
                                <div class="qr-code-text">${codice}</div>
                                <div class="qr-scan-hint">Scansiona per convalidare</div>
                            </div>
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
                    const statusElement = document.getElementById(`status-${index}`);
                    
                    // Genera QR code
                    if (qrElement && qrElement.innerHTML === '') {
                        new QRCode(qrElement, {
                            text: ticket.codice || 'OMR' + ticket.id,
                            width: 120,
                            height: 120,
                            colorDark: '#228b22',
                            colorLight: '#ffffff',
                            correctLevel: QRCode.CorrectLevel.H
                        });
                    }
                    
                    // Aggiorna stato del QR
                    if (statusElement) {
                        const stato = ticket.stato || 'ACQUISTATO';
                        const statusIcon = statusElement.querySelector('.status-icon');
                        const statusClass = getStatusClass(stato);
                        
                        statusElement.className = `qr-status ${statusClass}`;
                        
                        if (statusIcon) {
                            switch(stato) {
                                case 'CONVALIDATO':
                                    statusIcon.textContent = '✓';
                                    break;
                                case 'SCADUTO':
                                    statusIcon.textContent = '✕';
                                    break;
                                default:
                                    statusIcon.textContent = '●';
                            }
                        }
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
    
    // Espone la funzione globalmente per uso in altri script
    window.getStatusClass = getStatusClass;
    
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
    
    // Funzione per scaricare QR da biglietti del database
    window.downloadQRFromDB = function(ticketCode, qrElementId) {
        try {
            const qrElement = document.getElementById(qrElementId);
            const canvas = qrElement ? qrElement.querySelector('canvas') : null;
            
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
    };
    
    // Genera i QR codes per i biglietti dal database
    setTimeout(() => {
        const qrElements = document.querySelectorAll('[id^="qr-"]');
        qrElements.forEach(qrElement => {
            if (qrElement.innerHTML === '' && !qrElement.classList.contains('qr-generated')) {
                // Trova il codice del biglietto
                let ticketCode = '';
                const qrContainer = qrElement.closest('.modern-qr');
                if (qrContainer) {
                    const codeElement = qrContainer.querySelector('.qr-code-text');
                    if (codeElement) {
                        ticketCode = codeElement.textContent.trim();
                    }
                }
                
                if (ticketCode) {
                    console.log('Generando QR per:', ticketCode);
                    new QRCode(qrElement, {
                        text: ticketCode,
                        width: 120,
                        height: 120,
                        colorDark: '#228b22',
                        colorLight: '#ffffff',
                        correctLevel: QRCode.CorrectLevel.H
                    });
                    qrElement.classList.add('qr-generated');
                } else {
                    console.warn('Codice biglietto non trovato per elemento:', qrElement.id);
                }
            }
        });
    }, 500);
    
    // Carica e visualizza i biglietti al caricamento della pagina (solo se non ci sono biglietti dal server)
    if (!(isGuest && serverBiglietti > 0)) {
        displayGuestTickets();
    }
    
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
