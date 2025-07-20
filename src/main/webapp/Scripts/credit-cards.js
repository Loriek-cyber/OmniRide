/**
 * Credit Cards Management JavaScript
 * Gestisce la funzionalità delle carte di credito
 */

let cardToDelete = null;

// Inizializzazione
document.addEventListener('DOMContentLoaded', function() {
    console.log('Credit Cards JS loaded');
    initializeCardValidation();
    setupEventListeners();
});

/**
 * Mostra il form per aggiungere una nuova carta
 */
function showAddCardForm() {
    console.log('Showing add card form');
    
    // Reset form
    document.getElementById('cardForm').reset();
    document.getElementById('formAction').value = 'add';
    document.getElementById('cardId').value = '';
    document.getElementById('formTitle').textContent = 'Aggiungi Nuova Carta';
    document.getElementById('submitBtnText').textContent = 'Salva Carta';
    
    // Mostra overlay
    document.getElementById('cardFormOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden';
    
    // Focus sul primo campo
    setTimeout(() => {
        document.getElementById('cardNumber').focus();
    }, 100);
}

/**
 * Nascondi il form della carta
 */
function hideCardForm() {
    console.log('Hiding card form');
    
    document.getElementById('cardFormOverlay').style.display = 'none';
    document.body.style.overflow = '';
    
    // Reset errori
    clearValidationErrors();
}

/**
 * Modifica una carta esistente
 */
function editCard(cardId) {
    console.log('Editing card:', cardId);
    
    // TODO: Implementare il caricamento dei dati della carta
    document.getElementById('formAction').value = 'edit';
    document.getElementById('cardId').value = cardId;
    document.getElementById('formTitle').textContent = 'Modifica Carta';
    document.getElementById('submitBtnText').textContent = 'Aggiorna Carta';
    
    // Mostra form
    showAddCardForm();
}

/**
 * Conferma eliminazione carta
 */
function confirmDeleteCard(cardId) {
    console.log('Confirm delete card:', cardId);
    
    cardToDelete = cardId;
    document.getElementById('deleteConfirmModal').style.display = 'flex';
    document.body.style.overflow = 'hidden';
    
    // Setup conferma
    document.getElementById('confirmDeleteBtn').onclick = function() {
        deleteCard(cardToDelete);
    };
}

/**
 * Nascondi conferma eliminazione
 */
function hideDeleteConfirm() {
    document.getElementById('deleteConfirmModal').style.display = 'none';
    document.body.style.overflow = '';
    cardToDelete = null;
}

/**
 * Elimina carta
 */
function deleteCard(cardId) {
    console.log('Deleting card:', cardId);
    
    // Mostra loading
    const deleteBtn = document.getElementById('confirmDeleteBtn');
    deleteBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Eliminando...';
    deleteBtn.disabled = true;
    
    // Crea form per eliminazione
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/deleteCard';
    
    const cardIdInput = document.createElement('input');
    cardIdInput.type = 'hidden';
    cardIdInput.name = 'cardId';
    cardIdInput.value = cardId;
    form.appendChild(cardIdInput);
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'delete';
    form.appendChild(actionInput);
    
    document.body.appendChild(form);
    form.submit();
}

/**
 * Inizializza la validazione dei campi carta
 */
function initializeCardValidation() {
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
 * Formatta il numero della carta
 */
function formatCardNumber(input) {
    let value = input.value.replace(/\D/g, '');
    value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
    input.value = value;
}

/**
 * Formatta la scadenza
 */
function formatExpiry(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    input.value = value;
}

/**
 * Formatta il CVV
 */
function formatCVV(input) {
    input.value = input.value.replace(/\D/g, '');
}

/**
 * Rileva il tipo di carta
 */
function detectCardType(cardNumber) {
    const cleanNumber = cardNumber.replace(/\s/g, '');
    const indicator = document.getElementById('cardTypeIndicator');
    
    if (!indicator) return;
    
    // Reset
    indicator.innerHTML = '';
    
    if (cleanNumber.length < 4) return;
    
    // Visa
    if (/^4/.test(cleanNumber)) {
        indicator.innerHTML = '<i class="fab fa-cc-visa" style="color: #1434A4;"></i>';
    }
    // Mastercard
    else if (/^5[1-5]/.test(cleanNumber) || /^2[2-7]/.test(cleanNumber)) {
        indicator.innerHTML = '<i class="fab fa-cc-mastercard" style="color: #eb001b;"></i>';
    }
    // American Express
    else if (/^3[47]/.test(cleanNumber)) {
        indicator.innerHTML = '<i class="fab fa-cc-amex" style="color: #006fcf;"></i>';
    }
    // Generic
    else {
        indicator.innerHTML = '<i class="fas fa-credit-card" style="color: #32cd32;"></i>';
    }
}

/**
 * Validazione numero carta
 */
function validateCardNumber(input) {
    const value = input.value.replace(/\s/g, '');
    const isValid = value.length >= 13 && value.length <= 19 && /^\d+$/.test(value);
    
    if (!isValid && value.length > 0) {
        showFieldError(input, 'Numero carta non valido');
        return false;
    } else {
        clearFieldError(input);
        return true;
    }
}

/**
 * Validazione scadenza
 */
function validateExpiry(input) {
    const value = input.value;
    const regex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    
    if (!regex.test(value) && value.length > 0) {
        showFieldError(input, 'Formato scadenza non valido (MM/AA)');
        return false;
    }
    
    if (value.length === 5) {
        const [month, year] = value.split('/');
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear() % 100;
        const currentMonth = currentDate.getMonth() + 1;
        
        if (parseInt(year) < currentYear || (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
            showFieldError(input, 'La carta è scaduta');
            return false;
        }
    }
    
    clearFieldError(input);
    return true;
}

/**
 * Validazione CVV
 */
function validateCVV(input) {
    const value = input.value;
    const isValid = /^[0-9]{3,4}$/.test(value);
    
    if (!isValid && value.length > 0) {
        showFieldError(input, 'CVV deve essere di 3 o 4 cifre');
        return false;
    } else {
        clearFieldError(input);
        return true;
    }
}

/**
 * Validazione intestatario
 */
function validateCardHolder(input) {
    const value = input.value.trim();
    const isValid = value.length >= 2 && /^[a-zA-ZÀ-ÿ\s]+$/.test(value);
    
    if (!isValid && value.length > 0) {
        showFieldError(input, 'Nome intestatario non valido');
        return false;
    } else {
        clearFieldError(input);
        return true;
    }
}

/**
 * Mostra errore campo
 */
function showFieldError(input, message) {
    const formGroup = input.closest('.form-group');
    const errorDiv = formGroup.querySelector('.error-message');
    
    formGroup.classList.add('error');
    formGroup.classList.remove('success');
    
    if (errorDiv) {
        errorDiv.textContent = message;
    }
}

/**
 * Pulisci errore campo
 */
function clearFieldError(input) {
    const formGroup = input.closest('.form-group');
    const errorDiv = formGroup.querySelector('.error-message');
    
    formGroup.classList.remove('error');
    formGroup.classList.add('success');
    
    if (errorDiv) {
        errorDiv.textContent = '';
    }
}

/**
 * Pulisci tutti gli errori di validazione
 */
function clearValidationErrors() {
    document.querySelectorAll('.form-group.error, .form-group.success').forEach(group => {
        group.classList.remove('error', 'success');
    });
    
    document.querySelectorAll('.error-message').forEach(error => {
        error.textContent = '';
    });
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Submit form validation
    const cardForm = document.getElementById('cardForm');
    if (cardForm) {
        cardForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (validateForm()) {
                submitForm();
            }
        });
    }
    
    // Close modals on overlay click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', function(e) {
            if (e.target === this) {
                if (this.id === 'cardFormOverlay') {
                    hideCardForm();
                } else if (this.id === 'deleteConfirmModal') {
                    hideDeleteConfirm();
                }
            }
        });
    });
    
    // Escape key to close modals
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const openOverlay = document.querySelector('.modal-overlay[style*="flex"]');
            if (openOverlay) {
                if (openOverlay.id === 'cardFormOverlay') {
                    hideCardForm();
                } else if (openOverlay.id === 'deleteConfirmModal') {
                    hideDeleteConfirm();
                }
            }
        }
    });
}

/**
 * Valida tutto il form
 */
function validateForm() {
    const cardNumberInput = document.getElementById('cardNumber');
    const cardExpiryInput = document.getElementById('cardExpiry');
    const cardCVVInput = document.getElementById('cardCVV');
    const cardHolderInput = document.getElementById('cardHolder');
    
    let isValid = true;
    
    if (!validateCardNumber(cardNumberInput)) isValid = false;
    if (!validateExpiry(cardExpiryInput)) isValid = false;
    if (!validateCVV(cardCVVInput)) isValid = false;
    if (!validateCardHolder(cardHolderInput)) isValid = false;
    
    return isValid;
}

/**
 * Invia il form
 */
function submitForm() {
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.innerHTML;
    
    // Mostra loading
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvando...';
    submitBtn.disabled = true;
    
    // Submit form
    document.getElementById('cardForm').submit();
}

/**
 * Ottieni il context path
 */
function getContextPath() {
    return window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
}

/**
 * Mostra notifica
 */
function showNotification(message, type = 'info') {
    // Rimuovi notifica esistente
    const existing = document.querySelector('.notification-toast');
    if (existing) existing.remove();
    
    const notification = document.createElement('div');
    notification.className = `notification-toast notification-${type}`;
    
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
            <button onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;
    
    // Stili inline
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        border-left: 4px solid var(--${type}-color, #32cd32);
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
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
    }, 4000);
}

// CSS dinamico per animazioni
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
    }
    
    .notification-content button {
        background: none;
        border: none;
        font-size: 18px;
        cursor: pointer;
        margin-left: auto;
        opacity: 0.6;
    }
    
    .notification-content button:hover {
        opacity: 1;
    }
`;
document.head.appendChild(style);
