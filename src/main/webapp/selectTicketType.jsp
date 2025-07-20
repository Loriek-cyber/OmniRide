<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp"/>
    <title>Seleziona Tipo Biglietto - Omniride</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/form.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/ticket-selection.css">
    <style>
        .trip-details-list {
            list-style: none;
            padding: 0;
            margin: 15px 0;
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
        }
        .trip-details-list li {
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .trip-details-list li:last-child {
            border-bottom: none;
        }
        .ticket-card {
            cursor: pointer;
            transition: all 0.3s ease;
            border: 2px solid #e9ecef;
            border-radius: 12px;
            padding: 20px;
            margin: 10px;
            background: white;
        }
        .ticket-card:hover {
            border-color: #007bff;
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,123,255,0.2);
        }
        .ticket-card.selected {
            border-color: #007bff;
            background: #f8f9ff;
        }
        .ticket-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #007bff;
            margin: 10px 0;
        }
    </style>
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="container">
        <h2>Seleziona il Tipo di Biglietto</h2>
        
        <div class="route-info">
            <h4>Dettagli del Viaggio</h4>
            <ul class="trip-details-list">
                <li><strong>Percorso:</strong> ${percorso}</li>
                <li><strong>Data:</strong> ${data}</li>
                <li><strong>Orario:</strong> ${orario}</li>
                <li><strong>Prezzo base tratta:</strong> <fmt:formatNumber value="${prezzoBase}" type="currency" currencySymbol="€"/></li>
            </ul>
        </div>
        
        <form action="${pageContext.request.contextPath}/selectTicketType" method="post" id="ticketForm">
            <input type="hidden" name="percorso" value="${percorso}">
            <input type="hidden" name="data" value="${data}">
            <input type="hidden" name="orario" value="${orario}">
            <input type="hidden" name="prezzo" value="" id="selectedPrice">
            <input type="hidden" name="tipo" value="" id="selectedType">
            
            <div class="ticket-options">
                <!-- Biglietto Normale -->
                <div class="ticket-card" data-type="NORMALE" data-price="${prezzoNormale}">
                    <input type="radio" name="ticketType" value="NORMALE" id="normale">
                    <div class="ticket-title">Biglietto Normale</div>
                    <div class="ticket-duration">Valido per 4 ore</div>
                    <div class="ticket-price">
                        <fmt:formatNumber value="${prezzoNormale}" type="currency" currencySymbol="€"/>
                    </div>
                    <ul class="ticket-features">
                        <li>✓ Valido per una singola corsa</li>
                        <li>✓ Attivazione richiesta</li>
                        <li>✓ Perfetto per viaggi occasionali</li>
                    </ul>
                </div>
                
                <!-- Biglietto Giornaliero -->
                <div class="ticket-card" data-type="GIORNALIERO" data-price="${prezzoGiornaliero}">
                    <input type="radio" name="ticketType" value="GIORNALIERO" id="giornaliero">
                    <div class="ticket-title">Biglietto Giornaliero</div>
                    <div class="ticket-duration">Valido per 24 ore</div>
                    <div class="ticket-price">
                        <fmt:formatNumber value="${prezzoGiornaliero}" type="currency" currencySymbol="€"/>
                    </div>
                    <ul class="ticket-features">
                        <li>✓ Viaggi illimitati per 24 ore</li>
                        <li>✓ Attivazione automatica</li>
                        <li>✓ Ideale per più viaggi nella giornata</li>
                        <li>✓ Risparmio del 50% rispetto a 3 biglietti normali</li>
                    </ul>
                </div>
                
                <!-- Abbonamento Annuale -->
                <div class="ticket-card" data-type="ANNUALE" data-price="${prezzoAnnuale}">
                    <input type="radio" name="ticketType" value="ANNUALE" id="annuale">
                    <div class="ticket-title">Abbonamento Annuale</div>
                    <div class="ticket-duration">Valido per 365 giorni</div>
                    <div class="ticket-price">
                        <fmt:formatNumber value="${prezzoAnnuale}" type="currency" currencySymbol="€"/>
                    </div>
                    <ul class="ticket-features">
                        <li>✓ Viaggi illimitati per un anno</li>
                        <li>✓ Attivazione automatica</li>
                        <li>✓ Massimo risparmio per viaggiatori frequenti</li>
                        <li>✓ Nessuna scadenza giornaliera</li>
                    </ul>
                </div>
            </div>
            
            <div class="quantity-section">
                <label for="quantita"><strong>Quantità:</strong></label>
                <input type="number" name="quantita" id="quantita" class="quantity-input" 
                       value="1" min="1" max="10" required>
            </div>
            
            <div class="btn-container">
                <a href="${pageContext.request.contextPath}/visualizzaTratte" class="btn btn-secondary">
                    ← Torna alle Tratte
                </a>
                <button type="submit" class="btn btn-primary btn-large" id="addToCartBtn" disabled style="flex: 1; max-width: 300px; margin-left: 15px;">
                    <i class="fas fa-shopping-cart"></i>
                    Aggiungi al Carrello
                </button>
            </div>
        </form>
    </div>
</main>

<jsp:include page="import/footer.jsp"/>

<script>
// Add event listeners when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Add click event to all ticket cards
    document.querySelectorAll('.ticket-card').forEach(card => {
        card.addEventListener('click', function() {
            const type = this.dataset.type;
            const price = parseFloat(this.dataset.price);
            selectTicket(type, price, this);
        });
    });
    
    // Update total price when quantity changes
    document.getElementById('quantita').addEventListener('change', updateTotalPrice);
});

function selectTicket(type, price, cardElement) {
    // Remove selected class from all cards
    document.querySelectorAll('.ticket-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    // Add selected class to clicked card
    cardElement.classList.add('selected');
    
    // Set hidden form values
    document.getElementById('selectedType').value = type;
    document.getElementById('selectedPrice').value = price;
    
    // Check the corresponding radio button
    document.getElementById(type.toLowerCase()).checked = true;
    
    // Enable the submit button and update text
    const addBtn = document.getElementById('addToCartBtn');
    addBtn.disabled = false;
    updateButtonText(price);
}

function updateTotalPrice() {
    const price = parseFloat(document.getElementById('selectedPrice').value || 0);
    if (price > 0) {
        updateButtonText(price);
    }
}

function updateButtonText(price) {
    const quantity = parseInt(document.getElementById('quantita').value || 1);
    const total = price * quantity;
    const addBtn = document.getElementById('addToCartBtn');
    
    addBtn.innerHTML = `<i class="fas fa-shopping-cart"></i> Aggiungi al Carrello - €${total.toFixed(2)}`;
}

// Form validation
document.getElementById('ticketForm').addEventListener('submit', function(e) {
    if (!document.getElementById('selectedType').value) {
        e.preventDefault();
        alert('Per favore seleziona un tipo di biglietto');
    }
});
</script>

</body>
</html>
