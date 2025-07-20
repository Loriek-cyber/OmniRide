/**
 * Script globale per la gestione del wallet degli utenti guest
 * Controlla se ci sono biglietti nella sessione e mostra/nasconde l'icona del wallet nell'header
 */

(function() {
    'use strict';

    // Funzione per ottenere i biglietti guest dal sessionStorage
    function getGuestTickets() {
        try {
            return JSON.parse(sessionStorage.getItem('guestTickets') || '[]');
        } catch (e) {
            console.warn('Errore nel parsing dei biglietti guest:', e);
            return [];
        }
    }

    // Funzione per aggiornare la visibilità del wallet nell'header
    function updateGuestWalletVisibility() {
        const guestWalletIcon = document.getElementById('guestWalletIcon');
        const guestWalletBadge = document.getElementById('guestWalletBadge');
        
        if (!guestWalletIcon || !guestWalletBadge) {
            // Se gli elementi non esistono, l'utente è probabilmente loggato
            return;
        }

        const guestTickets = getGuestTickets();
        const ticketCount = guestTickets.length;

        if (ticketCount > 0) {
            // Mostra l'icona del wallet
            guestWalletIcon.style.display = 'inline-flex';
            guestWalletBadge.textContent = ticketCount;
            guestWalletBadge.style.display = 'flex';
            
            // Aggiunge un effetto di pulsazione per attirare l'attenzione
            guestWalletIcon.classList.add('has-tickets');
        } else {
            // Nasconde l'icona del wallet
            guestWalletIcon.style.display = 'none';
            guestWalletBadge.style.display = 'none';
            guestWalletIcon.classList.remove('has-tickets');
        }
    }

    // Funzione per salvare un nuovo biglietto nella sessione guest
    function saveGuestTicket(ticketData) {
        const tickets = getGuestTickets();
        
        const newTicket = {
            id: Date.now() + Math.random(), // ID unico
            tipo: ticketData.tipo || ticketData.ticketType || 'NORMALE',
            prezzo: parseFloat(ticketData.prezzo || ticketData.price || 0),
            data: ticketData.data || new Date().toISOString().split('T')[0],
            orario: ticketData.orario || new Date().toTimeString().split(' ')[0].substring(0, 5),
            percorso: ticketData.percorso || ticketData.nome || 'Percorso Non Definito',
            quantita: parseInt(ticketData.quantita || 1),
            dataAcquisto: new Date().toISOString(),
            stato: 'ACQUISTATO',
            codice: 'OMR' + Math.random().toString(36).substr(2, 9).toUpperCase()
        };
        
        tickets.push(newTicket);
        
        try {
            sessionStorage.setItem('guestTickets', JSON.stringify(tickets));
            updateGuestWalletVisibility();
            
            // Mostra una notifica di successo
            showTicketNotification('Biglietto salvato nel tuo portafoglio temporaneo!');
            
            return newTicket;
        } catch (e) {
            console.error('Errore nel salvataggio del biglietto guest:', e);
            return null;
        }
    }

    // Funzione per rimuovere un biglietto guest
    function removeGuestTicket(ticketId) {
        const tickets = getGuestTickets();
        const filteredTickets = tickets.filter(ticket => ticket.id !== ticketId);
        
        try {
            sessionStorage.setItem('guestTickets', JSON.stringify(filteredTickets));
            updateGuestWalletVisibility();
            return true;
        } catch (e) {
            console.error('Errore nella rimozione del biglietto guest:', e);
            return false;
        }
    }

    // Funzione per mostrare una notifica temporanea
    function showTicketNotification(message) {
        // Rimuove eventuali notifiche precedenti
        const existingNotification = document.querySelector('.guest-ticket-notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // Crea la notifica
        const notification = document.createElement('div');
        notification.className = 'guest-ticket-notification';
        notification.innerHTML = `
            <i class="fas fa-check-circle"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.remove()" title="Chiudi">×</button>
        `;
        
        // Stile inline per la notifica
        notification.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            background: linear-gradient(135deg, #32cd32 0%, #228b22 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(50, 205, 50, 0.3);
            z-index: 10000;
            display: flex;
            align-items: center;
            gap: 10px;
            max-width: 300px;
            font-size: 14px;
            animation: slideInRight 0.3s ease-out;
        `;

        // Aggiunge l'animazione CSS se non esiste già
        if (!document.querySelector('#guest-notification-styles')) {
            const style = document.createElement('style');
            style.id = 'guest-notification-styles';
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
                .guest-ticket-notification button {
                    background: none;
                    border: none;
                    color: white;
                    font-size: 18px;
                    cursor: pointer;
                    padding: 0;
                    margin-left: auto;
                }
                .guest-ticket-notification button:hover {
                    opacity: 0.8;
                }
                .btnHeader-icon.has-tickets {
                    animation: pulse 2s infinite;
                }
                @keyframes pulse {
                    0% { transform: scale(1); }
                    50% { transform: scale(1.05); }
                    100% { transform: scale(1); }
                }
            `;
            document.head.appendChild(style);
        }

        document.body.appendChild(notification);

        // Rimuove automaticamente la notifica dopo 5 secondi
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    // Funzione per monitorare i cambiamenti nel sessionStorage (per più schede/finestre)
    function monitorSessionStorage() {
        window.addEventListener('storage', function(e) {
            if (e.key === 'guestTickets') {
                updateGuestWalletVisibility();
            }
        });

        // Controlla periodicamente per cambiamenti nel sessionStorage della stessa scheda
        let lastTicketCount = getGuestTickets().length;
        setInterval(function() {
            const currentTicketCount = getGuestTickets().length;
            if (currentTicketCount !== lastTicketCount) {
                updateGuestWalletVisibility();
                lastTicketCount = currentTicketCount;
            }
        }, 1000);
    }

    // Inizializzazione al caricamento della pagina
    document.addEventListener('DOMContentLoaded', function() {
        updateGuestWalletVisibility();
        monitorSessionStorage();
    });

    // Esporta le funzioni globalmente per l'uso in altre parti del sito
    window.GuestWallet = {
        updateVisibility: updateGuestWalletVisibility,
        saveTicket: saveGuestTicket,
        removeTicket: removeGuestTicket,
        getTickets: getGuestTickets,
        showNotification: showTicketNotification
    };

})();
