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

                <div class="cart-actions">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                        <i class="fas fa-plus"></i> Aggiungi Altri Biglietti
                    </a>
                    <button type="button" class="btn btn-secondary" onclick="clearCart()">
                        <i class="fas fa-trash"></i> Svuota Carrello
                    </button>
                    <form action="${pageContext.request.contextPath}/checkout" method="post" style="display: inline;">
                        <button type="submit" class="btn btn-primary">
                            Procedi all'Acquisto
                        </button>
                    </form>
                </div>

            </c:when>
            <c:otherwise>
                <div class="empty-cart">
                    <i class="fas fa-shopping-cart"></i>
                    <h3>Il tuo carrello è vuoto</h3>
                    <p>Aggiungi delle tratte al carrello per poterle acquistare.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                        <i class="fas fa-search"></i> Cerca Biglietti
                    </a>
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
</script>

</body>
</html>
