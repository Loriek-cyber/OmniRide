/**
 * Ticket Selection JavaScript Functions
 */

// Global variables
let selectedTicketType = null;
let selectedPrice = 0;
let isProcessing = false;

/**
 * Initialize ticket selection page
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    updateButtonStates();
});

/**
 * Initialize all event listeners
 */
function initializeEventListeners() {
    // Ticket card click handlers
    const ticketCards = document.querySelectorAll('.ticket-card');
    ticketCards.forEach(card => {
        card.addEventListener('click', function() {
            const type = this.dataset.type;
            const price = parseFloat(this.dataset.price);
            selectTicket(type, price);
        });
    });

    // Quantity input change handler
    const quantityInput = document.getElementById('quantita');
    if (quantityInput) {
        quantityInput.addEventListener('change', updateTotalPrice);
    }

    // Form submission handlers
    const addToCartBtn = document.getElementById('addToCartBtn');
    
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleAddToCart();
        });
    }
}

/**
 * Select a ticket type and update UI
 * @param {string} type - Ticket type (NORMALE, GIORNALIERO, ANNUALE)
 * @param {number} price - Price of the ticket
 */
function selectTicket(type, price) {
    if (isProcessing) return;

    // Remove selected class from all cards
    document.querySelectorAll('.ticket-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    // Add selected class to clicked card
    const selectedCard = document.querySelector(`[data-type="${type}"]`);
    if (selectedCard) {
        selectedCard.classList.add('selected');
    }
    
    // Update global variables
    selectedTicketType = type;
    selectedPrice = price;
    
    // Update hidden form fields
    const typeInput = document.getElementById('selectedType');
    const priceInput = document.getElementById('selectedPrice');
    
    if (typeInput) typeInput.value = type;
    if (priceInput) priceInput.value = price;
    
    // Check corresponding radio button
    const radioButton = document.getElementById(type.toLowerCase());
    if (radioButton) {
        radioButton.checked = true;
    }
    
    // Update button states and total price
    updateButtonStates();
    updateTotalPrice();
}

/**
 * Update button enabled/disabled states
 */
function updateButtonStates() {
    const addToCartBtn = document.getElementById('addToCartBtn');
    const hasSelection = selectedTicketType !== null;
    
    if (addToCartBtn) {
        addToCartBtn.disabled = !hasSelection || isProcessing;
        // Aggiorna il testo del pulsante con il prezzo
        if (hasSelection && selectedPrice > 0) {
            const quantity = parseInt(document.getElementById('quantita')?.value) || 1;
            const total = selectedPrice * quantity;
            addToCartBtn.innerHTML = `<i class="fas fa-shopping-cart"></i> Aggiungi al carrello - ${formatCurrency(total)}`;
        } else {
            addToCartBtn.innerHTML = `<i class="fas fa-shopping-cart"></i> Aggiungi al carrello`;
        }
    }
}

/**
 * Update total price display based on quantity
 */
function updateTotalPrice() {
    const quantityInput = document.getElementById('quantita');
    const totalPriceElement = document.getElementById('totalPrice');
    
    if (!quantityInput || !totalPriceElement || selectedPrice === 0) return;
    
    const quantity = parseInt(quantityInput.value) || 1;
    const total = selectedPrice * quantity;
    
    totalPriceElement.textContent = formatCurrency(total);
}

/**
 * Handle add to cart action
 */
function handleAddToCart() {
    if (!validateSelection()) return;
    
    setProcessingState(true);
    
    const formData = getFormData();
    formData.action = 'addToCart';
    
    submitForm('selectTicketType', formData, function(success) {
        if (success) {
            // Ottieni il context path dall'URL corrente
            const contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            window.location.href = contextPath + '/carrello';
        } else {
            showError('Errore nell\'aggiunta al carrello. Riprova.');
        }
        setProcessingState(false);
    });
}


/**
 * Validate ticket selection
 * @returns {boolean} - True if selection is valid
 */
function validateSelection() {
    if (!selectedTicketType) {
        showError('Per favore seleziona un tipo di biglietto');
        return false;
    }
    
    const quantityInput = document.getElementById('quantita');
    const quantity = parseInt(quantityInput?.value) || 0;
    
    if (quantity < 1 || quantity > 10) {
        showError('La quantità deve essere tra 1 e 10');
        return false;
    }
    
    return true;
}

/**
 * Get form data for submission
 * @returns {Object} - Form data object
 */
function getFormData() {
    const quantityInput = document.getElementById('quantita');
    
    return {
        percorso: document.querySelector('input[name="percorso"]')?.value || '',
        data: document.querySelector('input[name="data"]')?.value || '',
        orario: document.querySelector('input[name="orario"]')?.value || '',
        tipo: selectedTicketType,
        prezzo: selectedPrice,
        quantita: parseInt(quantityInput?.value) || 1
    };
}

/**
 * Submit form data
 * @param {string} url - Target URL
 * @param {Object} data - Form data
 * @param {Function} callback - Callback function
 */
function submitForm(url, data, callback) {
    const form = document.createElement('form');
    form.method = 'POST';
    // Aggiungi il context path se necessario
    const contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
    form.action = contextPath + '/' + url;
    
    // Add form fields
    Object.keys(data).forEach(key => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = data[key];
        form.appendChild(input);
    });
    
    // Submit form
    document.body.appendChild(form);
    form.submit();
    
    // Cleanup
    setTimeout(() => {
        document.body.removeChild(form);
        callback(true);
    }, 100);
}

/**
 * Set processing state
 * @param {boolean} processing - Whether processing is active
 */
function setProcessingState(processing) {
    isProcessing = processing;
    
    const container = document.querySelector('.ticket-selection-container');
    const buttons = document.querySelectorAll('.btn');
    
    if (processing) {
        container?.classList.add('loading');
        buttons.forEach(btn => btn.disabled = true);
    } else {
        container?.classList.remove('loading');
        updateButtonStates();
    }
}

/**
 * Show error message
 * @param {string} message - Error message
 */
function showError(message) {
    // You can customize this to use your preferred notification system
    alert(message);
}

/**
 * Format currency
 * @param {number} amount - Amount to format
 * @returns {string} - Formatted currency string
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('it-IT', {
        style: 'currency',
        currency: 'EUR'
    }).format(amount);
}

/**
 * Update quantity input value
 * @param {number} change - Change amount (+1 or -1)
 */
function changeQuantity(change) {
    const quantityInput = document.getElementById('quantita');
    if (!quantityInput) return;
    
    const currentValue = parseInt(quantityInput.value) || 1;
    const newValue = Math.max(1, Math.min(10, currentValue + change));
    
    quantityInput.value = newValue;
    updateTotalPrice();
}

// Export functions for use in HTML
window.selectTicket = selectTicket;
window.changeQuantity = changeQuantity;
