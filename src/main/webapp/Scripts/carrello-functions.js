/**
 * CARRELLO JAVASCRIPT
 * Funzioni per la gestione del carrello acquisti
 */

/**
 * Update item quantity in cart
 * @param {number} index - Item index in cart
 * @param {number} delta - Change amount (+1 or -1)
 */
function updateQuantity(index, delta, contextPath = '') {
    // Calculate new quantity by getting current quantity and applying delta
    const quantityDisplay = document.querySelectorAll('.quantity-display')[index];
    const currentQuantity = parseInt(quantityDisplay.textContent);
    const newQuantity = currentQuantity + delta;
    
    // Validate new quantity
    if (newQuantity < 1 || newQuantity > 10) {
        OmniRide.showToast(
            newQuantity < 1 ? 'Quantità minima: 1' : 'Quantità massima: 10', 
            'warning'
        );
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = contextPath + '/carrello';
    
    // Add hidden fields
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'aggiorna';
    form.appendChild(actionInput);
    
    const indexInput = document.createElement('input');
    indexInput.type = 'hidden';
    indexInput.name = 'indice';
    indexInput.value = index;
    form.appendChild(indexInput);
    
    const quantitaInput = document.createElement('input');
    quantitaInput.type = 'hidden';
    quantitaInput.name = 'quantita';
    quantitaInput.value = newQuantity;
    form.appendChild(quantitaInput);
    
    // Submit form
    document.body.appendChild(form);
    form.submit();
}

/**
 * Remove item from cart
 * @param {number} index - Item index in cart
 */
function removeItem(index, contextPath = '') {
    if (!OmniRide.confirmDelete('Sei sicuro di voler rimuovere questo biglietto dal carrello?')) {
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = contextPath + '/carrello';
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'rimuovi';
    form.appendChild(actionInput);
    
    const indexInput = document.createElement('input');
    indexInput.type = 'hidden';
    indexInput.name = 'indice';
    indexInput.value = index;
    form.appendChild(indexInput);
    
    document.body.appendChild(form);
    form.submit();
}

/**
 * Clear entire cart
 */
function clearCart(contextPath = '') {
    if (!OmniRide.confirmDelete('Sei sicuro di voler svuotare completamente il carrello?')) {
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = contextPath + '/carrello';
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'svuota';
    form.appendChild(actionInput);
    
    document.body.appendChild(form);
    form.submit();
}

// Payment form utilities
const PaymentManager = {
    
    // Auto-format credit card number
    formatCardNumber: function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
            let matches = value.match(/.{1,4}/g);
            let match = matches && matches.join(' ');
            if (match) {
                e.target.value = match.substring(0, 19);
            } else {
                e.target.value = value;
            }
        });
    },
    
    // Auto-format expiry date
    formatExpiryDate: function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            e.target.value = value;
        });
    },
    
    // CVV validation (numbers only)
    formatCVV: function(input) {
        input.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4);
        });
    },
    
    // Initialize payment method selection
    initializePaymentMethods: function() {
        const paymentMethods = document.querySelectorAll('input[name="payment_method"]');
        const newCardForm = document.getElementById('new-card-form');
        
        paymentMethods.forEach(method => {
            method.addEventListener('change', function() {
                if (this.value === 'saved_card') {
                    newCardForm.style.display = 'none';
                    // Clear new card form validation requirements
                    const newCardInputs = newCardForm.querySelectorAll('input[required]');
                    newCardInputs.forEach(input => input.removeAttribute('required'));
                } else if (this.value === 'new_card') {
                    newCardForm.style.display = 'block';
                    // Restore validation requirements for new card form
                    document.getElementById('numero_carta').setAttribute('required', '');
                    document.getElementById('data_scadenza').setAttribute('required', '');
                    document.getElementById('cvv').setAttribute('required', '');
                    document.getElementById('nome_intestatario').setAttribute('required', '');
                }
            });
        });
        
        // Initialize first selection
        const firstSavedCard = document.querySelector('input[name="payment_method"][value="saved_card"]');
        if (firstSavedCard) {
            firstSavedCard.checked = true;
            newCardForm.style.display = 'none';
        } else if (document.getElementById('new_card_hidden')) {
            // For users without saved cards
            newCardForm.style.display = 'block';
        }
    }
};

// Initialize carrello functionality
document.addEventListener('DOMContentLoaded', function() {
    
    // Add loading states to quantity and remove buttons
    const actionButtons = document.querySelectorAll('.quantity-btn, .remove-btn');
    actionButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.disabled) return;
            
            OmniRide.LoadingManager.show(this);
            
            // Re-enable after 5 seconds as fallback
            setTimeout(() => {
                OmniRide.LoadingManager.hide(this);
            }, 5000);
        });
    });
    
    // Special handling for form submit buttons
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Add a small delay to allow form submission
            setTimeout(() => {
                OmniRide.LoadingManager.show(this, 'Elaborazione...');
            }, 100);
        });
    });
    
    // Add loading effect to secondary buttons (not submit buttons)
    const secondaryButtons = document.querySelectorAll('.btn-secondary');
    secondaryButtons.forEach(button => {
        // Skip if it's a submit button
        if (button.type === 'submit') return;
        
        button.addEventListener('click', function() {
            if (this.disabled) return;
            
            OmniRide.LoadingManager.show(this);
            
            // Re-enable after 3 seconds as fallback
            setTimeout(() => {
                OmniRide.LoadingManager.hide(this);
            }, 3000);
        });
    });
    
    // Initialize payment form formatting
    const cardNumberInput = document.getElementById('numero_carta');
    if (cardNumberInput) {
        PaymentManager.formatCardNumber(cardNumberInput);
    }
    
    const expiryInput = document.getElementById('data_scadenza');
    if (expiryInput) {
        PaymentManager.formatExpiryDate(expiryInput);
    }
    
    const cvvInput = document.getElementById('cvv');
    if (cvvInput) {
        PaymentManager.formatCVV(cvvInput);
    }
    
    // Initialize payment methods
    PaymentManager.initializePaymentMethods();
    
    // Form validation on submit
    const checkoutForm = document.getElementById('checkout-form');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function(e) {
            const selectedMethod = document.querySelector('input[name="payment_method"]:checked');
            
            if (!selectedMethod) {
                e.preventDefault();
                OmniRide.showToast('Seleziona un metodo di pagamento', 'error');
                return;
            }
            
            if (selectedMethod.value === 'new_card') {
                // Validate new card form
                const requiredFields = ['numero_carta', 'data_scadenza', 'cvv', 'nome_intestatario'];
                let hasErrors = false;
                
                requiredFields.forEach(fieldId => {
                    const field = document.getElementById(fieldId);
                    if (!field.value.trim()) {
                        field.classList.add('error');
                        hasErrors = true;
                    } else {
                        field.classList.remove('error');
                    }
                });
                
                if (hasErrors) {
                    e.preventDefault();
                    OmniRide.showToast('Compila tutti i campi obbligatori della carta', 'error');
                    return;
                }
                
                // Validate card number format (basic check)
                const cardNumber = document.getElementById('numero_carta').value.replace(/\s/g, '');
                if (cardNumber.length < 13 || cardNumber.length > 19) {
                    e.preventDefault();
                    document.getElementById('numero_carta').classList.add('error');
                    OmniRide.showToast('Inserisci un numero di carta valido', 'error');
                    return;
                }
                
                // Validate expiry date format
                const expiry = document.getElementById('data_scadenza').value;
                if (!/^\d{2}\/\d{2}$/.test(expiry)) {
                    e.preventDefault();
                    document.getElementById('data_scadenza').classList.add('error');
                    OmniRide.showToast('Formato data scadenza: MM/YY', 'error');
                    return;
                }
            }
            
            // Form is valid, allow submission
            OmniRide.showToast('Reindirizzamento al checkout...', 'info');
        });
    }
});

// Export functions for global access
window.CarrelloManager = {
    updateQuantity,
    removeItem,
    clearCart,
    PaymentManager
};
