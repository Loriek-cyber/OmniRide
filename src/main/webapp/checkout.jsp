<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="import/metadata.jsp"/>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Checkout - OmniRide</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/checkout.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="import/header.jsp"/>
    
    <main class="checkout-main">
        <div class="checkout-container">
            <!-- Header del checkout -->
            <div class="checkout-header">
                <h1><i class="fas fa-shopping-cart"></i> Checkout</h1>
                <div class="checkout-steps">
                    <div class="step active">
                        <span class="step-number">1</span>
                        <span class="step-text">Riepilogo</span>
                    </div>
                    <div class="step active">
                        <span class="step-number">2</span>
                        <span class="step-text">Pagamento</span>
                    </div>
                    <div class="step">
                        <span class="step-number">3</span>
                        <span class="step-text">Conferma</span>
                    </div>
                </div>
            </div>
            
            <div class="checkout-content">
                <!-- Colonna sinistra - Dettagli ordine -->
                <div class="order-summary">
                    <h2>Riepilogo Ordine</h2>
                    
                    <div class="order-item">
                        <div class="item-details">
                            <h3>${percorso.nomePercorso != null ? percorso.nomePercorso : 'Biglietto Trasporto'}</h3>
                            <p class="route-info">
                                <i class="fas fa-route"></i>
                                ${partenza} → ${arrivo}
                            </p>
                            <div class="trip-details">
                                <span><i class="fas fa-calendar"></i> ${data}</span>
                                <span><i class="fas fa-clock"></i> ${orario}</span>
                            </div>
                        </div>
                        <div class="item-price">
                            €<span id="ticketPrice">${prezzo}</span>
                        </div>
                    </div>
                    
                    <div class="order-total">
                        <div class="total-row">
                            <span>Subtotale</span>
                            <span>€${prezzo}</span>
                        </div>
                        <div class="total-row">
                            <span>Commissioni</span>
                            <span>€0.50</span>
                        </div>
                        <div class="total-row final-total">
                            <strong>Totale</strong>
                            <strong>€<span id="finalTotal">${prezzo + 0.50}</span></strong>
                        </div>
                    </div>
                </div>
                
                <!-- Colonna destra - Form pagamento -->
                <div class="payment-section">
                    <h2>Metodo di Pagamento</h2>
                    
                    <!-- Se l'utente è loggato, mostra le sue carte -->
                    <c:if test="${not empty sessionScope.utente}">
                        <div class="user-cards">
                            <h3>Le tue carte salvate</h3>
                            <c:choose>
                                <c:when test="${not empty sessionScope.utente.carteCredito}">
                                    <div class="saved-cards">
                                        <c:forEach var="card" items="${sessionScope.utente.carteCredito}" varStatus="status">
                                            <div class="saved-card" onclick="selectSavedCard(${card.id})">
                                                <input type="radio" name="savedCard" id="card_${card.id}" value="${card.id}">
                                                <label for="card_${card.id}">
                                                    <div class="card-info">
                                                        <i class="fab fa-${fn:toLowerCase(card.tipo)}"></i>
                                                        <span class="card-number">•••• •••• •••• ${card.ultimeQuattroCifre}</span>
                                                        <span class="card-expiry">${card.scadenza}</span>
                                                    </div>
                                                </label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-saved-cards">
                                        <p>Non hai carte salvate. Inserisci una nuova carta di credito:</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            
                            <div class="payment-option">
                                <input type="radio" name="paymentMethod" id="newCard" value="new" checked>
                                <label for="newCard">Usa una nuova carta</label>
                            </div>
                        </div>
                    </c:if>
                    
                    <!-- Form carta di credito -->
                    <div class="card-form" id="cardForm">
                        <form id="paymentForm" method="POST" action="${pageContext.request.contextPath}/processPayment">
                            <!-- Dati nascosti -->
                            <input type="hidden" name="percorso" value="${percorso}">
                            <input type="hidden" name="partenza" value="${partenza}">
                            <input type="hidden" name="arrivo" value="${arrivo}">
                            <input type="hidden" name="data" value="${data}">
                            <input type="hidden" name="orario" value="${orario}">
                            <input type="hidden" name="prezzo" value="${prezzo}">
                            <input type="hidden" name="selectedSavedCard" id="selectedSavedCard" value="">
                            
                            <div class="form-group">
                                <label for="cardNumber">Numero Carta *</label>
                                <input type="text" id="cardNumber" name="numeroCart" 
                                       placeholder="1234 5678 9012 3456" required>
                            </div>
                            
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="cardExpiry">Scadenza *</label>
                                    <input type="text" id="cardExpiry" name="scadenza" 
                                           placeholder="MM/YY" maxlength="5" required>
                                </div>
                                <div class="form-group">
                                    <label for="cardCVV">CVV *</label>
                                    <input type="text" id="cardCVV" name="cvv" 
                                           placeholder="123" maxlength="4" required>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="cardHolder">Nome Intestatario *</label>
                                <input type="text" id="cardHolder" name="intestatario" 
                                       placeholder="Nome Cognome" required>
                            </div>
                            
                            <!-- Se l'utente è loggato, opzione per salvare la carta -->
                            <c:if test="${not empty sessionScope.utente}">
                                <div class="form-group checkbox-group">
                                    <input type="checkbox" id="saveCard" name="saveCard" value="true">
                                    <label for="saveCard">Salva questa carta per acquisti futuri</label>
                                </div>
                            </c:if>
                            
                            <div class="form-group checkbox-group">
                                <input type="checkbox" id="termsAccept" required>
                                <label for="termsAccept">
                                    Accetto i <a href="#" target="_blank">termini e condizioni</a> 
                                    e la <a href="#" target="_blank">privacy policy</a> *
                                </label>
                            </div>
                            
                            <button type="submit" class="btn-pay" id="payButton">
                                <i class="fas fa-lock"></i>
                                <span>Paga €<span id="payButtonAmount">${prezzo + 0.50}</span></span>
                                <div class="btn-loading" style="display: none;">
                                    <i class="fas fa-spinner fa-spin"></i>
                                </div>
                            </button>
                        </form>
                    </div>
                    
                    <!-- Info sicurezza -->
                    <div class="security-info">
                        <div class="security-badges">
                            <div class="security-item">
                                <i class="fas fa-shield-alt"></i>
                                <span>Pagamento sicuro SSL</span>
                            </div>
                            <div class="security-item">
                                <i class="fas fa-credit-card"></i>
                                <span>Tutte le carte accettate</span>
                            </div>
                            <div class="security-item">
                                <i class="fas fa-clock"></i>
                                <span>Conferma immediata</span>
                            </div>
                        </div>
                        <p class="security-text">
                            I tuoi dati di pagamento sono protetti con crittografia SSL a 256-bit. 
                            Non conserviamo i dati completi della tua carta di credito.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <jsp:include page="import/footer.jsp"/>
    
</body>
</html>
