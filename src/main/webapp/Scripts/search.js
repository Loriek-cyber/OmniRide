/**
 * Script per la gestione della ricerca delle tratte e l'acquisto dei biglietti
 */

// Inizializza il form di ricerca
document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.getElementById('searchForm');
    const resultsContainer = document.getElementById('resultsContainer');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const resultsContent = document.getElementById('resultsContent');
    
    // Imposta la data di oggi come minimo
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('data').min = today;
    
    // Imposta la data di oggi come default
    document.getElementById('data').value = today;
    
    // Imposta l'orario corrente come default
    const now = new Date();
    const currentTime = now.toTimeString().slice(0, 5);
    document.getElementById('orario').value = currentTime;
    
    // Gestione del form di ricerca
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        performSearch();
    });
});

/**
 * Mostra/nasconde i filtri avanzati
 */
function toggleAdvancedFilters() {
    const advancedFilters = document.querySelector('.advanced-filters');
    const filterBtn = document.querySelector('.btn-filters');
    
    if (advancedFilters.style.display === 'none' || advancedFilters.style.display === '') {
        advancedFilters.style.display = 'flex';
        advancedFilters.classList.add('show');
        filterBtn.classList.add('active');
        filterBtn.innerHTML = '<i class="fas fa-filter"></i> Nascondi filtri';
    } else {
        advancedFilters.style.display = 'none';
        advancedFilters.classList.remove('show');
        filterBtn.classList.remove('active');
        filterBtn.innerHTML = '<i class="fas fa-filter"></i> Filtri avanzati';
    }
}

/**
 * Esegue la ricerca delle tratte
 */
async function performSearch() {
    const searchForm = document.getElementById('searchForm');
    const resultsContainer = document.getElementById('resultsContainer');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const resultsContent = document.getElementById('resultsContent');
    
    // Raccoglie i dati del form
    const formData = new FormData(searchForm);
    
    // Mostra il container dei risultati e il loading
    resultsContainer.style.display = 'block';
    loadingSpinner.style.display = 'block';
    resultsContent.innerHTML = '';
    
    try {
        // Invia la richiesta di ricerca
        const response = await fetch('/search', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        // Nasconde il loading
        loadingSpinner.style.display = 'none';
        
        if (data.success) {
            displayResults(data);
        } else {
            displayError(data.error || 'Errore durante la ricerca');
        }
        
    } catch (error) {
        console.error('Errore nella ricerca:', error);
        loadingSpinner.style.display = 'none';
        displayError('Errore di connessione. Riprova più tardi.');
    }
}

/**
 * Visualizza i risultati della ricerca
 */
function displayResults(data) {
    const resultsContent = document.getElementById('resultsContent');
    
    if (!data.percorsi || data.percorsi.length === 0) {
        resultsContent.innerHTML = `
            <div class="no-results">
                <i class="fas fa-search"></i>
                <h3>Nessun percorso trovato</h3>
                <p>Non sono stati trovati percorsi per la tratta selezionata.</p>
                <p>Prova a modificare le tue preferenze di ricerca.</p>
            </div>
        `;
        return;
    }
    
    // Genera l'HTML per i risultati
    let html = '';
    
    data.percorsi.forEach((percorso, index) => {
        html += createRouteCard(percorso, data.fermataPartenza, data.fermataArrivo, data.data, data.orario);
    });
    
    resultsContent.innerHTML = html;
}

/**
 * Crea la card HTML per un singolo percorso
 */
function createRouteCard(percorso, fermataPartenza, fermataArrivo, data, orario) {
    const price = percorso.costo.toFixed(2);
    const duration = Math.ceil(percorso.costo * 60); // Approssimazione: costo come proxy per durata in minuti
    const segments = percorso.segmenti || [];
    
    let segmentsHtml = '';
    if (segments.length > 0) {
        segmentsHtml = '<div class="route-segments">';
        segments.forEach(segment => {
            segmentsHtml += `
                <div class="segment">
                    <i class="fas fa-bus segment-icon"></i>
                    <span>Tratta ${segment.id_tratta}: ${segment.fermataIn.nome} → ${segment.fermataOu.nome}</span>
                </div>
            `;
        });
        segmentsHtml += '</div>';
    }
    
    return `
        <div class="route-card">
            <div class="route-header">
                <div class="route-info">
                    <div class="route-path">
                        <span class="route-point">${fermataPartenza.nome}</span>
                        <i class="fas fa-arrow-right route-arrow"></i>
                        <span class="route-point">${fermataArrivo.nome}</span>
                    </div>
                    <div class="route-duration">
                        <i class="fas fa-clock"></i>
                        Durata stimata: ${duration} minuti
                    </div>
                </div>
                <div class="route-price">€${price}</div>
            </div>
            ${segmentsHtml}
            <div class="route-actions">
                <button class="buy-ticket-btn" onclick="buyTicket(${JSON.stringify(percorso).replace(/"/g, '&quot;')}, '${data}', '${orario}')">
                    <i class="fas fa-ticket-alt"></i>
                    Acquista biglietto
                </button>
            </div>
        </div>
    `;
}

/**
 * Visualizza un messaggio di errore
 */
function displayError(message) {
    const resultsContent = document.getElementById('resultsContent');
    resultsContent.innerHTML = `
        <div class="error-message">
            <i class="fas fa-exclamation-triangle"></i>
            <strong>Errore:</strong> ${message}
        </div>
    `;
}

/**
 * Gestisce l'acquisto di un biglietto
 */
async function buyTicket(percorso, data, orario) {
    console.log('Acquisto biglietto per percorso:', percorso);
    
    try {
        // Verifica se l'utente è loggato
        const isLoggedIn = await checkLoginStatus();
        
        if (!isLoggedIn) {
            // Se non è loggato, reindirizza al processo di acquisto per ospiti
            showGuestPurchaseDialog(percorso, data, orario);
        } else {
            // Se è loggato, procedi con l'acquisto normale
            proceedToPurchase(percorso, data, orario);
        }
        
    } catch (error) {
        console.error('Errore durante l\'acquisto:', error);
        alert('Errore durante l\'acquisto. Riprova più tardi.');
    }
}

/**
 * Verifica se l'utente è loggato
 */
async function checkLoginStatus() {
    try {
        const response = await fetch('/api/auth/status');
        const data = await response.json();
        return data.isLoggedIn;
    } catch (error) {
        console.error('Errore nel controllo login:', error);
        return false;
    }
}

/**
 * Mostra il dialog per l'acquisto ospite
 */
function showGuestPurchaseDialog(percorso, data, orario) {
    const modal = document.createElement('div');
    modal.className = 'purchase-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>Acquisto biglietto</h3>
                <button class="close-btn" onclick="this.closest('.purchase-modal').remove()">×</button>
            </div>
            <div class="modal-body">
                <p>Puoi acquistare il biglietto in due modi:</p>
                <div class="purchase-options">
                    <button class="option-btn login-btn" onclick="redirectToLogin()">
                        <i class="fas fa-user"></i>
                        Accedi al tuo account
                    </button>
                    <button class="option-btn guest-btn" onclick="proceedAsGuest(${JSON.stringify(percorso).replace(/"/g, '&quot;')}, '${data}', '${orario}')">
                        <i class="fas fa-shopping-cart"></i>
                        Continua come ospite
                    </button>
                </div>
                <p class="note">
                    <i class="fas fa-info-circle"></i>
                    Come ospite, potrai pagare e ricevere il biglietto nel tuo portafoglio temporaneo.
                </p>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

/**
 * Reindirizza alla pagina di login
 */
function redirectToLogin() {
    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
}

/**
 * Procede con l'acquisto come ospite
 */
function proceedAsGuest(percorso, data, orario) {
    // Rimuove il modal
    document.querySelector('.purchase-modal').remove();
    
    // Crea il form di pagamento
    showPaymentForm(percorso, data, orario, true);
}

/**
 * Procede con l'acquisto normale (utente loggato)
 */
function proceedToPurchase(percorso, data, orario) {
    showPaymentForm(percorso, data, orario, false);
}

/**
 * Mostra il form di pagamento
 */
function showPaymentForm(percorso, data, orario, isGuest) {
    const modal = document.createElement('div');
    modal.className = 'payment-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>Pagamento biglietto</h3>
                <button class="close-btn" onclick="this.closest('.payment-modal').remove()">×</button>
            </div>
            <div class="modal-body">
                <div class="ticket-summary">
                    <h4>Riepilogo biglietto</h4>
                    <p><strong>Percorso:</strong> ${percorso.segmenti[0].fermataIn.nome} → ${percorso.segmenti[percorso.segmenti.length-1].fermataOu.nome}</p>
                    <p><strong>Data:</strong> ${data}</p>
                    <p><strong>Orario:</strong> ${orario}</p>
                    <p><strong>Prezzo:</strong> €${percorso.costo.toFixed(2)}</p>
                </div>
                
                <form id="paymentForm" class="payment-form">
                    <div class="form-group">
                        <label for="cardNumber">Numero carta</label>
                        <input type="text" id="cardNumber" placeholder="1234 5678 9012 3456" required>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="expiryDate">Scadenza</label>
                            <input type="text" id="expiryDate" placeholder="MM/AA" required>
                        </div>
                        <div class="form-group">
                            <label for="cvv">CVV</label>
                            <input type="text" id="cvv" placeholder="123" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="cardHolder">Titolare carta</label>
                        <input type="text" id="cardHolder" placeholder="Nome Cognome" required>
                    </div>
                    
                    <button type="submit" class="pay-btn">
                        <i class="fas fa-credit-card"></i>
                        Paga €${percorso.costo.toFixed(2)}
                    </button>
                </form>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Gestisce il form di pagamento
    const paymentForm = document.getElementById('paymentForm');
    paymentForm.addEventListener('submit', (e) => {
        e.preventDefault();
        processPayment(percorso, data, orario, isGuest);
    });
}

/**
 * Processa il pagamento
 */
async function processPayment(percorso, data, orario, isGuest) {
    const payBtn = document.querySelector('.pay-btn');
    const originalText = payBtn.innerHTML;
    payBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Elaborazione...';
    payBtn.disabled = true;
    
    try {
        const response = await fetch('/api/payment/process', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                percorso: percorso,
                data: data,
                orario: orario,
                isGuest: isGuest,
                paymentData: {
                    cardNumber: document.getElementById('cardNumber').value,
                    expiryDate: document.getElementById('expiryDate').value,
                    cvv: document.getElementById('cvv').value,
                    cardHolder: document.getElementById('cardHolder').value
                }
            })
        });
        
        const result = await response.json();
        
        if (result.success) {
            // Pagamento riuscito
            document.querySelector('.payment-modal').remove();
            showSuccessMessage(result.ticketId, isGuest);
        } else {
            // Pagamento fallito
            payBtn.innerHTML = originalText;
            payBtn.disabled = false;
            alert('Pagamento fallito: ' + result.error);
        }
        
    } catch (error) {
        console.error('Errore durante il pagamento:', error);
        payBtn.innerHTML = originalText;
        payBtn.disabled = false;
        alert('Errore durante il pagamento. Riprova più tardi.');
    }
}

/**
 * Mostra il messaggio di successo
 */
function showSuccessMessage(ticketId, isGuest) {
    const modal = document.createElement('div');
    modal.className = 'success-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header success-header">
                <h3><i class="fas fa-check-circle"></i> Pagamento completato!</h3>
            </div>
            <div class="modal-body">
                <p>Il tuo biglietto è stato acquistato con successo.</p>
                <p><strong>ID Biglietto:</strong> ${ticketId}</p>
                
                <div class="success-actions">
                    ${isGuest ? 
                        '<button class="success-btn" onclick="openGuestWallet()">Visualizza portafoglio</button>' :
                        '<button class="success-btn" onclick="openWallet()">Visualizza i tuoi biglietti</button>'
                    }
                    <button class="success-btn secondary" onclick="this.closest(\'.success-modal\').remove()">Chiudi</button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

/**
 * Apre il portafoglio ospite
 */
function openGuestWallet() {
    window.location.href = '/guest-wallet';
}

/**
 * Apre il portafoglio utente
 */
function openWallet() {
    window.location.href = '/wallet';
}

// Stili per i modal (da aggiungere al CSS)
const modalStyles = `
<style>
.purchase-modal, .payment-modal, .success-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    border-radius: 8px;
    max-width: 500px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-header {
    padding: 20px;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    margin: 0;
    color: #1f2937;
}

.close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #6b7280;
}

.modal-body {
    padding: 20px;
}

.purchase-options {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin: 20px 0;
}

.option-btn {
    padding: 15px;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    background-color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    transition: all 0.3s ease;
}

.option-btn:hover {
    border-color: #3b82f6;
    background-color: #f0f9ff;
}

.payment-form .form-group {
    margin-bottom: 15px;
}

.payment-form label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
    color: #374151;
}

.payment-form input {
    width: 100%;
    padding: 10px;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    font-size: 14px;
}

.payment-form .form-row {
    display: flex;
    gap: 10px;
}

.pay-btn {
    width: 100%;
    padding: 15px;
    background-color: #059669;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    margin-top: 20px;
}

.pay-btn:hover:not(:disabled) {
    background-color: #047857;
}

.pay-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.ticket-summary {
    background-color: #f9fafb;
    padding: 15px;
    border-radius: 6px;
    margin-bottom: 20px;
}

.ticket-summary h4 {
    margin: 0 0 10px 0;
    color: #1f2937;
}

.success-header {
    background-color: #ecfdf5;
    color: #059669;
}

.success-actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
}

.success-btn {
    flex: 1;
    padding: 10px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    background-color: #059669;
    color: white;
}

.success-btn.secondary {
    background-color: #6b7280;
}

.note {
    font-size: 14px;
    color: #6b7280;
    margin-top: 15px;
}
</style>
`;

document.head.insertAdjacentHTML('beforeend', modalStyles);
