/**
 * Script per gestire il reclamo dei biglietti guest quando l'utente fa login
 */

(function() {
    'use strict';

    // Funzione per reclamare i biglietti guest
    function claimGuestTickets() {
        const claimButton = document.getElementById('claimGuestTicketsBtn');
        if (claimButton) {
            claimButton.disabled = true;
            claimButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Reclamando...';
        }

        fetch('/guest-tickets', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                action: 'claim'
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification(data.message, 'success');
                
                // Rimuovi i biglietti guest dal sessionStorage
                sessionStorage.removeItem('guestTickets');
                
                // Ricarica la pagina per mostrare i biglietti reclamati
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
                
            } else {
                showNotification('Errore: ' + data.error, 'error');
                if (claimButton) {
                    claimButton.disabled = false;
                    claimButton.innerHTML = '<i class="fas fa-download"></i> Reclama Biglietti';
                }
            }
        })
        .catch(error => {
            console.error('Errore nel reclamare i biglietti:', error);
            showNotification('Errore di connessione. Riprova più tardi.', 'error');
            
            if (claimButton) {
                claimButton.disabled = false;
                claimButton.innerHTML = '<i class="fas fa-download"></i> Reclama Biglietti';
            }
        });
    }

    // Funzione per mostrare notifiche
    function showNotification(message, type = 'info') {
        // Rimuovi eventuali notifiche precedenti
        const existingNotification = document.querySelector('.claim-notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // Crea la notifica
        const notification = document.createElement('div');
        notification.className = `claim-notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="${getIconForType(type)}"></i>
                <span>${message}</span>
                <button onclick="this.parentElement.parentElement.remove()" title="Chiudi">×</button>
            </div>
        `;
        
        // Stile della notifica
        notification.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            background: ${getColorForType(type)};
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            z-index: 10000;
            max-width: 400px;
            font-size: 14px;
            animation: slideInRight 0.3s ease-out;
        `;

        document.body.appendChild(notification);

        // Rimuovi automaticamente la notifica dopo 5 secondi
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    function getIconForType(type) {
        switch (type) {
            case 'success':
                return 'fas fa-check-circle';
            case 'error':
                return 'fas fa-exclamation-circle';
            case 'warning':
                return 'fas fa-exclamation-triangle';
            default:
                return 'fas fa-info-circle';
        }
    }

    function getColorForType(type) {
        switch (type) {
            case 'success':
                return 'linear-gradient(135deg, #32cd32 0%, #228b22 100%)';
            case 'error':
                return 'linear-gradient(135deg, #dc3545 0%, #b02a37 100%)';
            case 'warning':
                return 'linear-gradient(135deg, #ffc107 0%, #d39e00 100%)';
            default:
                return 'linear-gradient(135deg, #007bff 0%, #0056b3 100%)';
        }
    }

    // Funzione per controllare e mostrare i biglietti guest disponibili
    function checkGuestTickets() {
        try {
            const guestTickets = JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
            const guestTicketsContainer = document.getElementById('guestTicketsContainer');
            const claimButton = document.getElementById('claimGuestTicketsBtn');

            if (guestTickets.length > 0) {
                // Mostra la sezione dei biglietti guest
                if (guestTicketsContainer) {
                    guestTicketsContainer.style.display = 'block';
                }

                // Aggiorna il contatore
                const ticketCount = document.getElementById('guestTicketCount');
                if (ticketCount) {
                    ticketCount.textContent = guestTickets.length;
                }

                // Mostra il pulsante di reclamo
                if (claimButton) {
                    claimButton.style.display = 'inline-block';
                }

                // Crea la lista dei biglietti guest
                const guestTicketsList = document.getElementById('guestTicketsList');
                if (guestTicketsList) {
                    guestTicketsList.innerHTML = '';
                    
                    guestTickets.forEach((ticket, index) => {
                        const ticketElement = document.createElement('div');
                        ticketElement.className = 'guest-ticket-item';
                        ticketElement.innerHTML = `
                            <div class="ticket-info">
                                <h4>${ticket.percorso || 'Percorso Non Definito'}</h4>
                                <p><strong>Tipo:</strong> ${ticket.tipo || 'NORMALE'}</p>
                                <p><strong>Prezzo:</strong> €${(ticket.prezzo || 0).toFixed(2)}</p>
                                <p><strong>Data:</strong> ${ticket.data || 'N/D'}</p>
                                <p><strong>Orario:</strong> ${ticket.orario || 'N/D'}</p>
                                <p><strong>Codice:</strong> ${ticket.codice || 'N/D'}</p>
                            </div>
                        `;
                        guestTicketsList.appendChild(ticketElement);
                    });
                }
            } else {
                // Nascondi la sezione dei biglietti guest
                if (guestTicketsContainer) {
                    guestTicketsContainer.style.display = 'none';
                }
            }
        } catch (e) {
            console.warn('Errore nel controllo dei biglietti guest:', e);
        }
    }

    // Inizializzazione
    document.addEventListener('DOMContentLoaded', function() {
        // Controlla i biglietti guest al caricamento
        checkGuestTickets();

        // Aggiungi event listener al pulsante di reclamo
        const claimButton = document.getElementById('claimGuestTicketsBtn');
        if (claimButton) {
            claimButton.addEventListener('click', function(e) {
                e.preventDefault();
                
                if (confirm('Sei sicuro di voler reclamare tutti i tuoi biglietti guest? Saranno associati al tuo account.')) {
                    claimGuestTickets();
                }
            });
        }

        // Controlla periodicamente per nuovi biglietti guest
        setInterval(checkGuestTickets, 2000);
    });

    // Aggiungi stili CSS dinamicamente
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        .claim-notification .notification-content {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .claim-notification button {
            background: none;
            border: none;
            color: white;
            font-size: 18px;
            cursor: pointer;
            padding: 0;
            margin-left: auto;
        }
        
        .claim-notification button:hover {
            opacity: 0.8;
        }
        
        .guest-ticket-item {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
        }
        
        .guest-ticket-item h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
        
        .guest-ticket-item p {
            margin: 5px 0;
            font-size: 14px;
            color: #6c757d;
        }
        
        #guestTicketsContainer {
            display: none;
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        #claimGuestTicketsBtn {
            background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            transition: all 0.3s ease;
        }
        
        #claimGuestTicketsBtn:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
        }
        
        #claimGuestTicketsBtn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
    `;
    document.head.appendChild(style);

    // Esporta funzioni per uso esterno
    window.ClaimTickets = {
        claim: claimGuestTickets,
        checkGuestTickets: checkGuestTickets,
        showNotification: showNotification
    };

})();
