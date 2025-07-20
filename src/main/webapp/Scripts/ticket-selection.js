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
    
    // Aggiungi al sistema di cookie se disponibile
    if (window.CartCookieManager) {
        const itemData = {
            percorsoJson: formData.percorso,
            nome: extractRouteNameFromJson(formData.percorso),
            data: formData.data,
            orario: formData.orario,
            prezzo: formData.prezzo,
            quantita: formData.quantita,
            tipo: formData.tipo
        };
        
        window.CartCookieManager.addToCart(itemData);
    }
    
    // Invia al server
    submitForm('selectTicketType', formData, function(success) {
        if (success) {
            showSuccessMessage('Biglietto aggiunto al carrello!');
            // Ottieni il context path dall'URL corrente
            const contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            setTimeout(() => {
                window.location.href = contextPath + '/carrello';
            }, 1000);
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
    showNotification(message, 'error');
}

/**
 * Show success message
 * @param {string} message - Success message
 */
function showSuccessMessage(message) {
    showNotification(message, 'success');
}

/**
 * Show notification message
 * @param {string} message - Message text
 * @param {string} type - Type of notification (success, error, info)
 */
function showNotification(message, type = 'info') {
    // Rimuovi eventuali notifiche precedenti
    const existingNotification = document.querySelector('.ticket-notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // Crea la notifica
    const notification = document.createElement('div');
    notification.className = `ticket-notification notification-${type}`;
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

    // Rimuovi automaticamente la notifica dopo 4 secondi
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 4000);
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

/**
 * Extract route name from JSON string
 * @param {string} percorsoJson - JSON string of the route
 * @returns {string} - Readable route name
 */
function extractRouteNameFromJson(percorsoJson) {
    try {
        const percorso = JSON.parse(percorsoJson);
        if (percorso && percorso.segmenti && percorso.segmenti.length > 0) {
            const primo = percorso.segmenti[0].fermataIn.nome;
            const ultimo = percorso.segmenti[percorso.segmenti.length - 1].fermataOu.nome;
            return `${primo} - ${ultimo}`;
        }
    } catch (e) {
        console.warn('Errore nel parsing del percorso JSON:', e);
    }
    return 'Percorso Personalizzato';
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
    
    .ticket-notification .notification-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .ticket-notification button {
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        cursor: pointer;
        padding: 0;
        margin-left: auto;
    }
    
    .ticket-notification button:hover {
        opacity: 0.8;
    }
    
    .ticket-selection-container.loading {
        pointer-events: none;
        opacity: 0.7;
    }
    
    .ticket-card {
        transition: all 0.3s ease;
        cursor: pointer;
    }
    
    .ticket-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    
    .ticket-card.selected {
        border-color: #007bff;
        background-color: #f8f9ff;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,123,255,0.2);
    }
`;
document.head.appendChild(style);

// Export functions for use in HTML
window.selectTicket = selectTicket;
window.changeQuantity = changeQuantity;
