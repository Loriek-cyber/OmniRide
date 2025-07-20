<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Il Tuo Carrello - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/carrello.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main style="background: #32cd32; min-height: calc(100vh - 200px);">
    <div class="cart-container">
        <div class="cart-header">
            <h2><i class="fas fa-shopping-cart"></i> Il Tuo Carrello</h2>
        </div>

        <c:choose>
            <c:when test="${not empty carrello}">
                <div class="cart-items">
                    <c:forEach var="item" items="${carrello}" varStatus="status">
                        <div class="cart-item">
                            <div class="item-header">
                                <div class="item-route">
                                    <i class="fas fa-route"></i>
                                    <c:choose>
                                        <c:when test="${item != null and item.percorso != null and not empty item.percorso}">
                                            ${item.percorso}
                                        </c:when>
                                        <c:when test="${item != null and item.percorsoJson != null and not empty item.percorsoJson}">
                                            Percorso Personalizzato
                                        </c:when>
                                        <c:otherwise>
                                            Tratta Diretta
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="ticket-type-badge">${item.tipo}</span>
                                </div>
                                <div class="item-price">
                                    <fmt:formatNumber value="${item.prezzo * item.quantita}" type="currency" currencySymbol="€"/>
                                </div>
                            </div>
                            
                            <div class="item-details">
                                <div class="item-detail">
                                    <i class="fas fa-calendar"></i>
                                    <span>${item.data}</span>
                                </div>
                                <div class="item-detail">
                                    <i class="fas fa-clock"></i>
                                    <span>${item.orario}</span>
                                </div>
                                <div class="item-detail">
                                    <i class="fas fa-ticket-alt"></i>
                                    <span>Biglietto ${item.tipo}</span>
                                </div>
                                <div class="item-detail">
                                    <i class="fas fa-euro-sign"></i>
                                    <span>Prezzo unitario: <fmt:formatNumber value="${item.prezzo}" type="currency" currencySymbol="€"/></span>
                                </div>
                            </div>
                            
                            <div class="item-actions">
                                <div class="quantity-controls">
                                    <button type="button" class="quantity-btn" onclick="updateQuantity(${status.index}, -1)" ${item.quantita <= 1 ? 'disabled' : ''}>
                                        <img src="${pageContext.request.contextPath}/icons/minus.svg">
                                    </button>
                                    <span class="quantity-display">${item.quantita}</span>
                                    <button type="button" class="quantity-btn" onclick="updateQuantity(${status.index}, 1)" ${item.quantita >= 10 ? 'disabled' : ''}>
                                        <img src="${pageContext.request.contextPath}/icons/plus.svg">
                                    </button>
                                </div>
                                
                                <button type="button" class="remove-btn" onclick="removeItem(${status.index})">
                                    <i class="fas fa-trash"></i> Rimuovi
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="cart-summary">
                    <div class="summary-row">
                        <span>Articoli nel carrello:</span>
                        <span>${carrello.size()}</span>
                    </div>
                    <div class="summary-row">
                        <span>Totale biglietti:</span>
                        <span><c:set var="totalQuantity" value="0"/>
                              <c:forEach var="item" items="${carrello}">
                                  <c:set var="totalQuantity" value="${totalQuantity + item.quantita}"/>
                              </c:forEach>
                              ${totalQuantity}
                        </span>
                    </div>
                    <div class="summary-row">
                        <span><strong>Totale da pagare:</strong></span>
                        <span><strong><fmt:formatNumber value="${totale}" type="currency" currencySymbol="€"/></strong></span>
                    </div>
                </div>

                <!-- Layout con pagamento a sinistra e riepilogo a destra -->
                <div class="checkout-layout">
                    <!-- Sezione Pagamento (Sinistra) -->
                    <div class="payment-sidebar">
                    <div class="payment-section">
                        <h3><i class="fas fa-credit-card"></i> Metodo di Pagamento</h3>
                        
                        <!-- Selezione carte salvate (solo per utenti loggati) -->
                        <c:if test="${not empty sessionScope.utente}">
                            <c:choose>
                                <c:when test="${not empty carteCredito}">
                                    <div class="saved-cards-section">
                                        <h4>Carte Salvate</h4>
                                        <div class="cards-grid">
                                            <c:forEach var="carta" items="${carteCredito}" varStatus="status">
                                                <div class="saved-card-option">
                                                    <input type="radio" id="saved_card_${status.index}" 
                                                           name="payment_method" value="saved_card" 
                                                           data-card-id="${carta.id_utente}" 
                                                           data-card-number="${carta.numeroCarta}" 
                                                           data-card-holder="${carta.nome_intestatario}" 
                                                           data-card-expiry="${carta.data_scadenza}">
                                                    <label for="saved_card_${status.index}" class="card-label">
                                                        <div class="card-display">
                                                            <div class="card-icon">
                                                                <i class="fas fa-credit-card"></i>
                                                            </div>
                                                            <div class="card-info">
                                                                <div class="card-number">**** **** **** ${carta.numeroCarta.substring(carta.numeroCarta.length() - 4)}</div>
                                                                <div class="card-details">
                                                                    <span class="card-holder">${carta.nome_intestatario}</span>
                                                                    <span class="card-expiry">${carta.data_scadenza}</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </label>
                                                </div>
                                            </c:forEach>
                                        </div>
                                        
                                        <div class="payment-option">
                                            <input type="radio" id="new_card" name="payment_method" value="new_card">
                                            <label for="new_card">Usa una nuova carta</label>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-saved-cards">
                                        <p><i class="fas fa-info-circle"></i> Non hai carte salvate. Inserisci i dati di una nuova carta.</p>
                                        <input type="hidden" id="new_card_hidden" name="payment_method" value="new_card">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        
                        <!-- Form nuova carta (sempre visibile per guest) -->
                        <div id="new-card-form" class="new-card-form" 
                             style="${(not empty carteCredito) ? 'display: none;' : 'display: block;'}">
                            <h4>Dati Carta di Credito</h4>
                            
                            <div class="form-group">
                                <label for="numero_carta">Numero Carta:</label>
                                <input type="text" id="numero_carta" name="numero_carta" 
                                       placeholder="1234 5678 9012 3456" maxlength="19" required>
                            </div>
                            
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="data_scadenza">Scadenza (MM/YY):</label>
                                    <input type="text" id="data_scadenza" name="data_scadenza" 
                                           placeholder="12/25" maxlength="5" required>
                                </div>
                                
                                <div class="form-group">
                                    <label for="cvv">CVV:</label>
                                    <input type="text" id="cvv" name="cvv" 
                                           placeholder="123" maxlength="4" required>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="nome_intestatario">Nome Intestatario:</label>
                                <input type="text" id="nome_intestatario" name="nome_intestatario" 
                                       placeholder="Mario Rossi" required>
                            </div>
                            
                            <!-- Opzione per salvare la carta (solo per utenti loggati) -->
                            <c:if test="${not empty sessionScope.utente}">
                                <div class="form-group checkbox-group">
                                    <label class="checkbox-label">
                                        <input type="checkbox" id="save_card" name="save_card" value="true" checked>
                                        <span class="checkbox-custom"></span>
                                        Salva questa carta per acquisti futuri
                                    </label>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    </div>
                    
                    <!-- Sezione Azioni (Destra) -->
                    <div class="actions-sidebar">
                        <div class="cart-actions-section">
                            <h3><i class="fas fa-cog"></i> Azioni Carrello</h3>
                            
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                                    <i class="fas fa-plus"></i> Aggiungi Altri Biglietti
                                </a>
                                <button type="button" class="btn btn-secondary" onclick="clearCart()">
                                    <i class="fas fa-trash"></i> Svuota Carrello
                                </button>
                            </div>
                        </div>
                        
                        <!-- Pulsante Checkout -->
                        <form id="checkout-form" action="${pageContext.request.contextPath}/checkout" method="post">
                            <input type="hidden" id="selected_payment_method" name="selected_payment_method" value="">
                            <input type="hidden" id="selected_card_data" name="selected_card_data" value="">
                            <button type="submit" class="btn checkout-btn" 
                                    style="background-color: white; border: 2px solid lime; color: black; 
                                           transition: all 0.3s ease; font-weight: bold;"
                                    onmouseover="this.style.transform='scale(1.05)'; this.style.borderColor='#32ff32'; this.style.backgroundColor='#f9fff9';"
                                    onmouseout="this.style.transform='scale(1)'; this.style.borderColor='lime'; this.style.backgroundColor='white';">
                                <i class="fas fa-shopping-cart" style="color: lime;"></i> Procedi all'Acquisto
                            </button>
                        </form>
                    </div>
                </div>

            </c:when>
            <c:otherwise>
                <div class="empty-cart">
                    <i class="fas fa-shopping-cart"></i>
                    <h3>Il tuo carrello è vuoto</h3>
                    <p>Aggiungi delle tratte al carrello per poterle acquistare.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Cerca Biglietti</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>
<script>
/**
 * Update item quantity in cart
 * @param {number} index - Item index in cart
 * @param {number} delta - Change amount (+1 or -1)
 */
function updateQuantity(index, delta) {
    // Calculate new quantity by getting current quantity and applying delta
    const quantityDisplay = document.querySelectorAll('.quantity-display')[index];
    const currentQuantity = parseInt(quantityDisplay.textContent);
    const newQuantity = currentQuantity + delta;
    
    // Validate new quantity
    if (newQuantity < 1 || newQuantity > 10) {
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/carrello';
    
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
function removeItem(index) {
    if (!confirm('Sei sicuro di voler rimuovere questo biglietto dal carrello?')) {
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/carrello';
    
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
function clearCart() {
    if (!confirm('Sei sicuro di voler svuotare completamente il carrello?')) {
        return;
    }
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/carrello';
    
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'svuota';
    form.appendChild(actionInput);
    
    document.body.appendChild(form);
    form.submit();
}

// Add loading states to buttons (excluding submit buttons)
document.addEventListener('DOMContentLoaded', function() {
    // Add click handlers for loading states to quantity and remove buttons only
    const buttons = document.querySelectorAll('.quantity-btn, .remove-btn');
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.disabled) return;
            
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            this.disabled = true;
            
            // Re-enable after 5 seconds as fallback
            setTimeout(() => {
                this.innerHTML = originalText;
                this.disabled = false;
            }, 5000);
        });
    });
    
    // Special handling for form submit buttons
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Add a small delay to allow form submission
            setTimeout(() => {
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Elaborazione...';
                this.disabled = true;
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
            
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            this.disabled = true;
            
            // Re-enable after 3 seconds as fallback
            setTimeout(() => {
                this.innerHTML = originalText;
                this.disabled = false;
            }, 3000);
        });
    });
});

// Payment method functionality
document.addEventListener('DOMContentLoaded', function() {
    const paymentMethods = document.querySelectorAll('input[name="payment_method"]');
    const newCardForm = document.getElementById('new-card-form');
    const checkoutForm = document.getElementById('checkout-form');
    
    // Handle payment method selection
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
    
    // Auto-format credit card number
    const cardNumberInput = document.getElementById('numero_carta');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
            let matches = value.match(/.{1,4}/g);
            let match = matches && matches.join(' ');
            if (match) {
                e.target.value = match.substring(0, 19);
            } else {
                e.target.value = value;
            }
        });
    }
    
    // Auto-format expiry date
    const expiryInput = document.getElementById('data_scadenza');
    if (expiryInput) {
        expiryInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            e.target.value = value;
        });
    }
    
    // CVV validation (numbers only)
    const cvvInput = document.getElementById('cvv');
    if (cvvInput) {
        cvvInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4);
        });
    }
    
    // Handle checkout form submission - simplified without validation
    checkoutForm.addEventListener('submit', function(e) {
        // Just proceed with the checkout - no payment validation needed
        // The form will submit normally to the checkout servlet
    });
    
    // Initialize payment method selection
    const firstSavedCard = document.querySelector('input[name="payment_method"][value="saved_card"]');
    if (firstSavedCard) {
        firstSavedCard.checked = true;
        newCardForm.style.display = 'none';
    } else if (document.getElementById('new_card_hidden')) {
        // For users without saved cards
        newCardForm.style.display = 'block';
    }
});
</script>

</body>
</html>
