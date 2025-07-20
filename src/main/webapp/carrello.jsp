<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Il Tuo Carrello - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .cart-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .cart-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .cart-header h2 {
            margin: 0;
            font-size: 2rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
        }
        
        .cart-items {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-bottom: 30px;
        }
        
        .cart-item {
            border-bottom: 1px solid #e5e7eb;
            padding: 25px;
            transition: background-color 0.2s ease;
        }
        
        .cart-item:last-child {
            border-bottom: none;
        }
        
        .cart-item:hover {
            background-color: #f8fafc;
        }
        
        .item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .item-route {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1f2937;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .item-price {
            font-size: 1.5rem;
            font-weight: 700;
            color: #667eea;
        }
        
        .item-details {
            display: flex;
            gap: 30px;
            margin-bottom: 20px;
            color: #6b7280;
        }
        
        .item-detail {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .item-detail i {
            color: #667eea;
            width: 16px;
        }
        
        .ticket-type-badge {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
            margin-left: 10px;
        }
        
        .item-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .quantity-controls {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .quantity-btn {
            background: #667eea;
            color: white;
            border: none;
            width: 36px;
            height: 36px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: bold;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s ease;
        }
        
        .quantity-btn:hover {
            background: #5a67d8;
            transform: scale(1.05);
        }
        
        .quantity-btn:disabled {
            background: #d1d5db;
            cursor: not-allowed;
            transform: none;
        }
        
        .quantity-display {
            background: #f3f4f6;
            border: 1px solid #d1d5db;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 600;
            min-width: 60px;
            text-align: center;
        }
        
        .remove-btn {
            background: #ef4444;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.2s ease;
        }
        
        .remove-btn:hover {
            background: #dc2626;
            transform: translateY(-1px);
        }
        
        .cart-summary {
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 30px;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #cbd5e1;
        }
        
        .summary-row:last-child {
            border-bottom: none;
            font-size: 1.3rem;
            font-weight: 700;
            color: #667eea;
        }
        
        .cart-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 15px 30px;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 10px;
            transition: all 0.3s ease;
            font-size: 1rem;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #6b7280;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5b6470;
            transform: translateY(-1px);
        }
        
        .empty-cart {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }
        
        .empty-cart i {
            font-size: 4rem;
            color: #d1d5db;
            margin-bottom: 20px;
        }
        
        .empty-cart h3 {
            color: #6b7280;
            margin-bottom: 15px;
        }
        
        .empty-cart p {
            color: #9ca3af;
            margin-bottom: 30px;
        }
        
        @media (max-width: 768px) {
            .cart-container {
                padding: 15px;
            }
            
            .cart-header {
                padding: 20px;
            }
            
            .cart-header h2 {
                font-size: 1.5rem;
            }
            
            .cart-item {
                padding: 20px 15px;
            }
            
            .item-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
            }
            
            .item-details {
                flex-direction: column;
                gap: 10px;
            }
            
            .item-actions {
                flex-direction: column;
                gap: 15px;
                align-items: stretch;
            }
            
            .quantity-controls {
                justify-content: center;
            }
            
            .cart-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
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
                                        <c:when test="${not empty item.percorso}">
                                            ${item.percorso}
                                        </c:when>
                                        <c:when test="${item.percorsoJson != null}">
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
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <span class="quantity-display">${item.quantita}</span>
                                    <button type="button" class="quantity-btn" onclick="updateQuantity(${status.index}, 1)" ${item.quantita >= 10 ? 'disabled' : ''}>
                                        <i class="fas fa-plus"></i>
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
                            <i class="fas fa-credit-card"></i> Procedi all'Acquisto
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

// Add loading states to buttons
document.addEventListener('DOMContentLoaded', function() {
    // Add click handlers for loading states
    const buttons = document.querySelectorAll('.quantity-btn, .remove-btn, .btn');
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
});
</script>

</body>
</html>
