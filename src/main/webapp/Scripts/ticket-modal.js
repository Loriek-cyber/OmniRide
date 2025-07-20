/**
 * Compact Ticket Selection Modal
 * Handles ticket type selection in a modal instead of full page
 */

let currentTicketData = null;

/**
 * Show compact ticket selection modal
 * @param {Object} routeData - Route information
 * @param {string} basePrice - Base price for the route
 */
function showTicketSelectionModal(routeData, basePrice) {
    currentTicketData = {
        percorso: routeData.percorso,
        data: routeData.data,
        orario: routeData.orario,
        prezzoBase: parseFloat(basePrice)
    };
    
    const modal = document.createElement('div');
    modal.className = 'ticket-selection-modal';
    modal.innerHTML = `
        <div class="modal-content ticket-modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-ticket-alt"></i> Seleziona Tipo Biglietto</h3>
                <button class="close-btn" onclick="closeTicketModal()">×</button>
            </div>
            <div class="modal-body">
                <div class="route-summary">
                    <div class="route-info">
                        <span class="route-path">${routeData.percorso}</span>
                        <div class="route-details">
                            <span><i class="fas fa-calendar"></i> ${routeData.data}</span>
                            <span><i class="fas fa-clock"></i> ${routeData.orario}</span>
                        </div>
                    </div>
                </div>
                
                <div class="ticket-types-compact">
                    <div class="ticket-option" data-type="NORMALE" data-multiplier="1">
                        <input type="radio" name="ticketTypeModal" id="normale-modal" value="NORMALE">
                        <label for="normale-modal" class="ticket-label">
                            <div class="ticket-info">
                                <span class="ticket-name">Normale</span>
                                <span class="ticket-duration">4 ore</span>
                            </div>
                            <div class="ticket-price">€${(currentTicketData.prezzoBase * 1).toFixed(2)}</div>
                        </label>
                    </div>
                    
                    <div class="ticket-option" data-type="GIORNALIERO" data-multiplier="2.5">
                        <input type="radio" name="ticketTypeModal" id="giornaliero-modal" value="GIORNALIERO">
                        <label for="giornaliero-modal" class="ticket-label">
                            <div class="ticket-info">
                                <span class="ticket-name">Giornaliero</span>
                                <span class="ticket-duration">24 ore</span>
                            </div>
                            <div class="ticket-price">€${(currentTicketData.prezzoBase * 2.5).toFixed(2)}</div>
                        </label>
                    </div>
                    
                    <div class="ticket-option" data-type="ANNUALE" data-multiplier="120">
                        <input type="radio" name="ticketTypeModal" id="annuale-modal" value="ANNUALE">
                        <label for="annuale-modal" class="ticket-label">
                            <div class="ticket-info">
                                <span class="ticket-name">Annuale</span>
                                <span class="ticket-duration">365 giorni</span>
                            </div>
                            <div class="ticket-price">€${(currentTicketData.prezzoBase * 120).toFixed(2)}</div>
                        </label>
                    </div>
                </div>
                
                <div class="quantity-section-modal">
                    <label for="quantita-modal"><strong>Quantità:</strong></label>
                    <div class="quantity-controls">
                        <button type="button" onclick="changeQuantityModal(-1)">-</button>
                        <input type="number" id="quantita-modal" value="1" min="1" max="10">
                        <button type="button" onclick="changeQuantityModal(1)">+</button>
                    </div>
                </div>
                
                <div class="total-price-modal">
                    <span>Totale: <strong id="totalPriceModal">€0.00</strong></span>
                </div>
                
                <div class="modal-actions">
                    <button class="btn-secondary" onclick="closeTicketModal()">Annulla</button>
                    <button class="btn-primary" id="addToCartModalBtn" onclick="addToCartFromModal()" disabled>
                        <i class="fas fa-shopping-cart"></i> Aggiungi al Carrello
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Add event listeners
    setupModalEventListeners();
}

/**
 * Setup event listeners for the modal
 */
function setupModalEventListeners() {
    const ticketOptions = document.querySelectorAll('.ticket-option');
    const quantityInput = document.getElementById('quantita-modal');
    
    ticketOptions.forEach(option => {
        option.addEventListener('click', function() {
            const radio = this.querySelector('input[type="radio"]');
            radio.checked = true;
            selectTicketInModal();
        });
    });
    
    quantityInput.addEventListener('change', updateTotalPriceModal);
    
    // Select first option by default
    if (ticketOptions.length > 0) {
        ticketOptions[0].querySelector('input[type="radio"]').checked = true;
        selectTicketInModal();
    }
}

/**
 * Handle ticket selection in modal
 */
function selectTicketInModal() {
    const selectedOption = document.querySelector('input[name="ticketTypeModal"]:checked');
    if (!selectedOption) return;
    
    const ticketOption = selectedOption.closest('.ticket-option');
    const multiplier = parseFloat(ticketOption.dataset.multiplier);
    
    // Update visual selection
    document.querySelectorAll('.ticket-option').forEach(option => {
        option.classList.remove('selected');
    });
    ticketOption.classList.add('selected');
    
    // Update total price
    updateTotalPriceModal();
    
    // Enable add to cart button
    document.getElementById('addToCartModalBtn').disabled = false;
}

/**
 * Change quantity in modal
 */
function changeQuantityModal(delta) {
    const quantityInput = document.getElementById('quantita-modal');
    const currentValue = parseInt(quantityInput.value) || 1;
    const newValue = Math.max(1, Math.min(10, currentValue + delta));
    
    quantityInput.value = newValue;
    updateTotalPriceModal();
}

/**
 * Update total price in modal
 */
function updateTotalPriceModal() {
    const selectedOption = document.querySelector('input[name="ticketTypeModal"]:checked');
    if (!selectedOption) return;
    
    const ticketOption = selectedOption.closest('.ticket-option');
    const multiplier = parseFloat(ticketOption.dataset.multiplier);
    const quantity = parseInt(document.getElementById('quantita-modal').value) || 1;
    
    const unitPrice = currentTicketData.prezzoBase * multiplier;
    const totalPrice = unitPrice * quantity;
    
    document.getElementById('totalPriceModal').textContent = `€${totalPrice.toFixed(2)}`;
    
    // Update button text
    const addToCartBtn = document.getElementById('addToCartModalBtn');
    addToCartBtn.innerHTML = `<i class="fas fa-shopping-cart"></i> Aggiungi al Carrello - €${totalPrice.toFixed(2)}`;
}

/**
 * Add ticket to cart from modal
 */
function addToCartFromModal() {
    const selectedOption = document.querySelector('input[name="ticketTypeModal"]:checked');
    if (!selectedOption) return;
    
    const ticketOption = selectedOption.closest('.ticket-option');
    const ticketType = selectedOption.value;
    const multiplier = parseFloat(ticketOption.dataset.multiplier);
    const quantity = parseInt(document.getElementById('quantita-modal').value) || 1;
    const unitPrice = currentTicketData.prezzoBase * multiplier;
    
    // Create form data
    const formData = new FormData();
    formData.append('action', 'aggiungi');
    formData.append('percorso', JSON.stringify(currentTicketData.percorso));
    formData.append('data', currentTicketData.data);
    formData.append('orario', currentTicketData.orario);
    formData.append('tipo', ticketType);
    formData.append('prezzo', unitPrice);
    formData.append('quantita', quantity);
    
    // Show loading state
    const addToCartBtn = document.getElementById('addToCartModalBtn');
    const originalText = addToCartBtn.innerHTML;
    addToCartBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Aggiungendo...';
    addToCartBtn.disabled = true;
    
    // Submit to cart
    fetch('./carrello', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            closeTicketModal();
            showCartMessage('Biglietto aggiunto al carrello!', 'success');
            updateCartCounter();
        } else {
            throw new Error('Failed to add to cart');
        }
    })
    .catch(error => {
        console.error('Error adding to cart:', error);
        showCartMessage('Errore nell\'aggiunta al carrello', 'error');
        addToCartBtn.innerHTML = originalText;
        addToCartBtn.disabled = false;
    });
}

/**
 * Close ticket selection modal
 */
function closeTicketModal() {
    const modal = document.querySelector('.ticket-selection-modal');
    if (modal) {
        modal.remove();
    }
    currentTicketData = null;
}

/**
 * Show cart message
 */
function showCartMessage(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `cart-message ${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 10px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 1001;
        background-color: ${type === 'success' ? '#059669' : '#dc2626'};
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}

/**
 * Update cart counter (placeholder - implement based on your cart logic)
 */
function updateCartCounter() {
    // This should be implemented based on your existing cart counter logic
    console.log('Cart counter should be updated');
}

// Export functions for global access
window.showTicketSelectionModal = showTicketSelectionModal;
window.closeTicketModal = closeTicketModal;
window.changeQuantityModal = changeQuantityModal;
window.addToCartFromModal = addToCartFromModal;
