/**
 * Checkout JavaScript
 * Gestisce la funzionalità della pagina di checkout
 */

// Inizializzazione
document.addEventListener('DOMContentLoaded', function() {
    console.log('Checkout JS loaded');
    initializeCheckout();
});

/**
 * Inizializza il checkout
 */
function initializeCheckout() {
    setupCardValidation();
    setupPaymentMethods();
    setupFormSubmission();
    loadSavedCards();
}

/**
 * Setup validazione carta
 */
function setupCardValidation() {
    const cardNumberInput = document.getElementById('cardNumber');
    const cardExpiryInput = document.getElementById('cardExpiry');
    const cardCVVInput = document.getElementById('cardCVV');
    const cardHolderInput = document.getElementById('cardHolder');
    
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function(e) {
            formatCardNumber(e.target);
            detectCardType(e.target.value);
        });
        
        cardNumberInput.addEventListener('blur', function(e) {
            validateCardNumber(e.target);
        });
    }
    
    if (cardExpiryInput) {
        cardExpiryInput.addEventListener('input', function(e) {
            formatExpiry(e.target);
        });
        
        cardExpiryInput.addEventListener('blur', function(e) {
            validateExpiry(e.target);
        });
    }
    
    if (cardCVVInput) {
        cardCVVInput.addEventListener('input', function(e) {
            formatCVV(e.target);
        });
        
        cardCVVInput.addEventListener('blur', function(e) {
            validateCVV(e.target);
        });
    }
    
    if (cardHolderInput) {
        cardHolderInput.addEventListener('blur', function(e) {
            validateCardHolder(e.target);
        });
    }
}

/**
 * Setup metodi di pagamento
 */
function setupPaymentMethods() {
    // Gestione selezione carte salvate vs nuova carta
    const savedCardRadios = document.querySelectorAll('input[name="savedCard"]');
    const newCardRadio = document.getElementById('newCard');
    const cardForm = document.getElementById('cardForm');
    
    savedCardRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.checked) {
                toggleCardForm(false);
                selectSavedCard(this.value);
            }
        });
    });
    
    if (newCardRadio) {
        newCardRadio.addEventListener('change', function() {
            if (this.checked) {
                toggleCardForm(true);
                clearSavedCardSelection();
            }
        });
    }
}

/**
 * Setup submit form
 */
function setupFormSubmission() {
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        paymentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            processPayment();
        });
    }
}

/**
 * Carica le carte salvate
 */
function loadSavedCards() {
    // Se non ci sono carte salvate, forza l'uso della nuova carta
    const savedCards = document.querySelectorAll('input[name="savedCard"]');
    if (savedCards.length === 0) {
        const newCardRadio = document.getElementById('newCard');
        if (newCardRadio) {
            newCardRadio.checked = true;
            toggleCardForm(true);
        }
    }
}

/**
 * Toggle visibilità form carta
 */
function toggleCardForm(show) {
    const cardForm = document.querySelector('.card-form');
    if (cardForm) {
        cardForm.style.display = show ? 'block' : 'none';
        
        // Disabilita/abilita i campi
        const inputs = cardForm.querySelectorAll('input[required]');
        inputs.forEach(input => {
            input.disabled = !show;
        });
    }
}

/**
 * Seleziona carta salvata
 */
function selectSavedCard(cardId) {
    console.log('Selected saved card:', cardId);
    
    // Rimuovi selezione precedente
    document.querySelectorAll('.saved-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    // Seleziona la carta
    const selectedCard = document.querySelector(`#card_${cardId}`).closest('.saved-card');
    if (selectedCard) {
        selectedCard.classList.add('selected');
    }
    
    // Aggiorna il campo nascosto
    const selectedSavedCardInput = document.getElementById('selectedSavedCard');
    if (selectedSavedCardInput) {
        selectedSavedCardInput.value = cardId;
    }
}

/**
 * Pulisce selezione carte salvate
 */
function clearSavedCardSelection() {
    document.querySelectorAll('.saved-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    const selectedSavedCardInput = document.getElementById('selectedSavedCard');
    if (selectedSavedCardInput) {
        selectedSavedCardInput.value = '';
    }
}

/**
 * Formatta numero carta
 */
function formatCardNumber(input) {
    let value = input.value.replace(/\D/g, '');
    value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
    input.value = value;
}

/**
 * Formatta scadenza
 */
function formatExpiry(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    input.value = value;
}

/**
 * Formatta CVV
 */
function formatCVV(input) {
    input.value = input.value.replace(/\D/g, '');
}

/**
 * Rileva tipo carta e aggiorna icone
 */
function detectCardType(cardNumber) {
    const cleanNumber = cardNumber.replace(/\s/g, '');
    const icons = document.querySelectorAll('.card-type-icons i');
    
    // Reset tutte le icone
    icons.forEach(icon => icon.classList.remove('active'));
    
    if (cleanNumber.length < 4) return;
    
    // Visa
    if (/^4/.test(cleanNumber)) {
        document.getElementById('visa-icon')?.classList.add('active');
    }
    // Mastercard
    else if (/^5[1-5]/.test(cleanNumber) || /^2[2-7]/.test(cleanNumber)) {
        document.getElementById('mastercard-icon')?.classList.add('active');
    }
    // American Express
    else if (/^3[47]/.test(cleanNumber)) {
        document.getElementById('amex-icon')?.classList.add('active');
    }
}

/**
 * Validazione numero carta
 */
function validateCardNumber(input) {
    const value = input.value.replace(/\s/g, '');
    const isValid = value.length >= 13 && value.length <= 19 && /^\d+$/.test(value);
    
    showFieldValidation(input, isValid, 'Numero carta non valido');
    return isValid;
}

/**
 * Validazione scadenza
 */
function validateExpiry(input) {
    const value = input.value;
    const regex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    
    if (!regex.test(value)) {
        showFieldValidation(input, false, 'Formato scadenza non valido (MM/YY)');
        return false;
    }
    
    // Controllo se la carta è scaduta
    const [month, year] = value.split('/');
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear() % 100;
    const currentMonth = currentDate.getMonth() + 1;
    
    if (parseInt(year) < currentYear || (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
        showFieldValidation(input, false, 'La carta è scaduta');
        return false;
    }
    
    showFieldValidation(input, true);
    return true;
}

/**
 * Validazione CVV
 */
function validateCVV(input) {
    const value = input.value;
    const isValid = /^[0-9]{3,4}$/.test(value);
    
    showFieldValidation(input, isValid, 'CVV deve essere di 3 o 4 cifre');
    return isValid;
}

/**
 * Validazione intestatario
 */
function validateCardHolder(input) {
    const value = input.value.trim();
    const isValid = value.length >= 2 && /^[a-zA-ZÀ-ÿ\s]+$/.test(value);
    
    showFieldValidation(input, isValid, 'Nome intestatario non valido');
    return isValid;
}

/**
 * Mostra validazione campo
 */
function showFieldValidation(input, isValid, errorMessage = '') {
    const formGroup = input.closest('.form-group');
    
    if (isValid) {
        formGroup.classList.add('success');
        formGroup.classList.remove('error');
    } else {
        formGroup.classList.add('error');
        formGroup.classList.remove('success');
        
        // Mostra messaggio di errore se presente
        const errorDiv = formGroup.querySelector('.error-message');
        if (errorDiv && errorMessage) {
            errorDiv.textContent = errorMessage;
        }
    }
}

/**
 * Valida tutto il form
 */
function validateForm() {
    // Se è selezionata una carta salvata, non validare i campi nuova carta
    const selectedSavedCard = document.getElementById('selectedSavedCard');
    if (selectedSavedCard && selectedSavedCard.value) {
        return true; // Carta salvata selezionata
    }
    
    // Valida i campi della nuova carta
    const cardNumberInput = document.getElementById('cardNumber');
    const cardExpiryInput = document.getElementById('cardExpiry');
    const cardCVVInput = document.getElementById('cardCVV');
    const cardHolderInput = document.getElementById('cardHolder');
    const termsAccept = document.getElementById('termsAccept');
    
    let isValid = true;
    
    if (cardNumberInput && !cardNumberInput.disabled) {
        if (!validateCardNumber(cardNumberInput)) isValid = false;
    }
    
    if (cardExpiryInput && !cardExpiryInput.disabled) {
        if (!validateExpiry(cardExpiryInput)) isValid = false;
    }
    
    if (cardCVVInput && !cardCVVInput.disabled) {
        if (!validateCVV(cardCVVInput)) isValid = false;
    }
    
    if (cardHolderInput && !cardHolderInput.disabled) {
        if (!validateCardHolder(cardHolderInput)) isValid = false;
    }
    
    // Controllo accettazione termini
    if (termsAccept && !termsAccept.checked) {
        showNotification('Devi accettare i termini e condizioni', 'error');
        isValid = false;
    }
    
    return isValid;
}

/**
 * Processa il pagamento
 */
function processPayment() {
    console.log('Processing payment...');
    
    if (!validateForm()) {
        showNotification('Controlla i dati inseriti', 'error');
        return;
    }
    
    const payButton = document.getElementById('payButton');
    const originalContent = payButton.innerHTML;
    
    // Mostra loading
    payButton.classList.add('loading');
    payButton.disabled = true;
    payButton.querySelector('.btn-loading').style.display = 'inline-flex';
    payButton.querySelector('span').style.display = 'none';
    
    // Mostra overlay di caricamento
    showLoadingOverlay('Elaborazione pagamento in corso...');
    
    // Simula elaborazione
    setTimeout(() => {
        // In un'applicazione reale, qui ci sarebbe la chiamata AJAX al server
        document.getElementById('paymentForm').submit();
    }, 2000);
}

/**
 * Mostra overlay di caricamento
 */
function showLoadingOverlay(message = 'Caricamento...') {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.innerHTML = `
        <div class="loading-content">
            <div class="loading-spinner"></div>
            <h3>${message}</h3>
            <p>Non chiudere la finestra</p>
        </div>
    `;
    
    document.body.appendChild(overlay);
}

/**
 * Mostra notifica
 */
function showNotification(message, type = 'info') {
    // Rimuovi notifica esistente
    const existing = document.querySelector('.checkout-notification');
    if (existing) existing.remove();
    
    const notification = document.createElement('div');
    notification.className = `checkout-notification notification-${type}`;
    
    const iconMap = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
    };
    
    notification.innerHTML = `
        <div class="notification-content">
            <i class="${iconMap[type] || iconMap.info}"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" aria-label="Chiudi">×</button>
        </div>
    `;
    
    // Stili inline
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        border-left: 4px solid ${getColorForType(type)};
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        z-index: 10001;
        min-width: 300px;
        animation: slideInRight 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // Auto-remove
    setTimeout(() => {
        if (notification.parentElement) {
            notification.style.animation = 'slideOutRight 0.3s ease forwards';
            setTimeout(() => notification.remove(), 300);
        }
    }, 5000);
}

/**
 * Ottieni colore per tipo notifica
 */
function getColorForType(type) {
    switch (type) {
        case 'success': return '#28a745';
        case 'error': return '#dc3545';
        case 'warning': return '#ffc107';
        default: return '#007bff';
    }
}

// CSS dinamico per animazioni e stili
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOutRight {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 20px;
        color: #333;
        font-family: 'Inter', sans-serif;
    }
    
    .notification-content button {
        background: none;
        border: none;
        font-size: 18px;
        cursor: pointer;
        margin-left: auto;
        opacity: 0.6;
        padding: 4px;
    }
    
    .notification-content button:hover {
        opacity: 1;
    }
    
    .form-group.error input {
        border-color: #dc3545;
        box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.1);
    }
    
    .form-group.success input {
        border-color: #28a745;
        box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.1);
    }
    
    .error-message {
        color: #dc3545;
        font-size: 0.85rem;
        margin-top: 4px;
    }
    
    .card-type-icons i.active {
        color: #32cd32 !important;
        transform: scale(1.2);
    }
`;
document.head.appendChild(style);
