<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Acquista Biglietto - OmniRide</title>
    <jsp:include page="/import/metadata.jsp"/>
    <style>
        /* Main container */
        .purchase-page {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .purchase-layout {
            display: grid;
            grid-template-columns: 1fr 400px;
            gap: 2rem;
            align-items: start;
        }

        /* Left column - Journey details */
        .journey-details {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 2rem;
        }

        .journey-header {
            border-bottom: 2px solid #32cd32;
            padding-bottom: 1rem;
            margin-bottom: 1.5rem;
        }

        .journey-header h2 {
            color: #2d3436;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .route-info {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .route-stations {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1rem;
        }

        .station {
            text-align: center;
        }

        .station-name {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2d3436;
            margin-bottom: 0.5rem;
        }

        .station-time {
            color: #636e72;
            font-size: 0.9rem;
        }

        .route-arrow {
            flex: 1;
            text-align: center;
            color: #32cd32;
            font-size: 2rem;
        }

        .journey-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .info-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
            display: flex;
            align-items: center;
            gap: 0.8rem;
        }

        .info-icon {
            font-size: 1.5rem;
        }

        .info-content {
            flex: 1;
        }

        .info-label {
            font-size: 0.85rem;
            color: #636e72;
            margin-bottom: 0.2rem;
        }

        .info-value {
            font-weight: 600;
            color: #2d3436;
        }

        /* Ticket options */
        .ticket-options {
            margin-bottom: 1.5rem;
        }

        .ticket-options h3 {
            margin-bottom: 1rem;
            color: #2d3436;
        }

        .ticket-type {
            border: 2px solid #ddd;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .ticket-type:hover {
            border-color: #32cd32;
        }

        .ticket-type.selected {
            border-color: #32cd32;
            background: #f0fff0;
        }

        .ticket-type input[type="radio"] {
            display: none;
        }

        .ticket-info {
            flex: 1;
        }

        .ticket-name {
            font-weight: 600;
            margin-bottom: 0.3rem;
        }

        .ticket-description {
            font-size: 0.85rem;
            color: #636e72;
        }

        .ticket-price {
            font-size: 1.3rem;
            font-weight: 700;
            color: #32cd32;
        }

        /* Right column - Payment */
        .payment-section {
            position: sticky;
            top: 2rem;
        }

        .payment-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 2rem;
        }

        .payment-header {
            border-bottom: 1px solid #ddd;
            padding-bottom: 1rem;
            margin-bottom: 1.5rem;
        }

        .payment-header h3 {
            margin: 0;
            color: #2d3436;
        }

        .price-breakdown {
            margin-bottom: 1.5rem;
        }

        .price-line {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            color: #636e72;
        }

        .price-line.total {
            border-top: 2px solid #ddd;
            padding-top: 0.5rem;
            margin-top: 1rem;
            font-weight: 700;
            color: #2d3436;
            font-size: 1.2rem;
        }

        .payment-methods {
            margin-bottom: 1.5rem;
        }

        .payment-method {
            border: 1px solid #ddd;
            border-radius: 6px;
            padding: 0.8rem;
            margin-bottom: 0.5rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.2s ease;
        }

        .payment-method:hover {
            border-color: #32cd32;
        }

        .payment-method.selected {
            border-color: #32cd32;
            background: #f0fff0;
        }

        .payment-method input[type="radio"] {
            accent-color: #32cd32;
        }

        .btn-purchase {
            width: 100%;
            padding: 1rem;
            background: #32cd32;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .btn-purchase:hover {
            background: #28a428;
            transform: translateY(-2px);
        }

        .btn-purchase:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
        }

        /* Success state */
        .success-container {
            text-align: center;
            padding: 3rem;
        }

        .success-icon {
            font-size: 4rem;
            color: #32cd32;
            margin-bottom: 1rem;
        }

        .success-message {
            font-size: 1.5rem;
            font-weight: 600;
            color: #2d3436;
            margin-bottom: 1rem;
        }

        .ticket-number {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 8px;
            font-family: monospace;
            font-size: 1.2rem;
            margin-bottom: 2rem;
        }

        .success-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
        }

        .btn-secondary {
            padding: 0.8rem 1.5rem;
            background: #f8f9fa;
            color: #2d3436;
            border: 1px solid #ddd;
            border-radius: 6px;
            text-decoration: none;
            transition: all 0.2s ease;
        }

        .btn-secondary:hover {
            background: #e9ecef;
        }

        /* Responsive */
        @media (max-width: 968px) {
            .purchase-layout {
                grid-template-columns: 1fr;
            }
            
            .payment-section {
                position: relative;
                top: 0;
            }
        }

        /* Loading state */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255,255,255,0.9);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            display: none;
        }

        .loading-spinner {
            font-size: 3rem;
            color: #32cd32;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
<jsp:include page="/import/header.jsp"/>

<main class="purchase-page">
    <div class="purchase-layout">
        <!-- Left column - Journey details -->
        <div class="journey-details">
            <div class="journey-header">
                <h2>🎫 Dettagli del Viaggio</h2>
            </div>

            <!-- Route information -->
            <div class="route-info">
                <div class="route-stations">
                    <div class="station">
                        <div class="station-name">${param.partenza != null ? param.partenza : 'Milano Centrale'}</div>
                        <div class="station-time">${param.oraPartenza != null ? param.oraPartenza : '08:30'}</div>
                    </div>
                    <div class="route-arrow">→</div>
                    <div class="station">
                        <div class="station-name">${param.arrivo != null ? param.arrivo : 'Roma Termini'}</div>
                        <div class="station-time">${param.oraArrivo != null ? param.oraArrivo : '11:45'}</div>
                    </div>
                </div>
            </div>

            <!-- Journey information cards -->
            <div class="journey-info">
                <div class="info-card">
                    <div class="info-icon">📅</div>
                    <div class="info-content">
                        <div class="info-label">Data viaggio</div>
                        <div class="info-value">${param.data != null ? param.data : '25/12/2023'}</div>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-icon">⏱️</div>
                    <div class="info-content">
                        <div class="info-label">Durata</div>
                        <div class="info-value">${param.durata != null ? param.durata : '3h 15min'}</div>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-icon">🚌</div>
                    <div class="info-content">
                        <div class="info-label">Tratta</div>
                        <div class="info-value">${param.nomeTratta != null ? param.nomeTratta : 'Express 500'}</div>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-icon">🪑</div>
                    <div class="info-content">
                        <div class="info-label">Posti disponibili</div>
                        <div class="info-value">${param.postiDisponibili != null ? param.postiDisponibili : '45'}</div>
                    </div>
                </div>
            </div>

            <!-- Ticket type selection -->
            <div class="ticket-options">
                <h3>Scegli il tipo di biglietto</h3>
                
                <label class="ticket-type selected">
                    <input type="radio" name="ticketType" value="single" checked>
                    <div class="ticket-info">
                        <div class="ticket-name">Biglietto Singolo</div>
                        <div class="ticket-description">Valido per una sola corsa nella data selezionata</div>
                    </div>
                    <div class="ticket-price">€${param.prezzo != null ? param.prezzo : '15.50'}</div>
                </label>

                <label class="ticket-type">
                    <input type="radio" name="ticketType" value="daily">
                    <div class="ticket-info">
                        <div class="ticket-name">Biglietto Giornaliero</div>
                        <div class="ticket-description">Viaggi illimitati per l'intera giornata</div>
                    </div>
                    <div class="ticket-price">€${param.prezzoGiornaliero != null ? param.prezzoGiornaliero : '25.00'}</div>
                </label>

                <label class="ticket-type">
                    <input type="radio" name="ticketType" value="return">
                    <div class="ticket-info">
                        <div class="ticket-name">Andata e Ritorno</div>
                        <div class="ticket-description">Include viaggio di ritorno entro 30 giorni</div>
                    </div>
                    <div class="ticket-price">€${param.prezzoAR != null ? param.prezzoAR : '28.00'}</div>
                </label>
            </div>
        </div>

        <!-- Right column - Payment -->
        <div class="payment-section">
            <div class="payment-container" id="paymentContainer">
                <div class="payment-header">
                    <h3>Riepilogo Pagamento</h3>
                </div>

                <div class="price-breakdown">
                    <div class="price-line">
                        <span>Biglietto base</span>
                        <span id="basePrice">€15.50</span>
                    </div>
                    <div class="price-line">
                        <span>Tasse e commissioni</span>
                        <span>€2.00</span>
                    </div>
                    <div class="price-line total">
                        <span>Totale</span>
                        <span id="totalPrice">€17.50</span>
                    </div>
                </div>

                <div class="payment-methods">
                    <h4>Metodo di pagamento</h4>
                    <label class="payment-method selected">
                        <input type="radio" name="paymentMethod" value="card" checked>
                        <span>💳</span>
                        <span>Carta di credito/debito</span>
                    </label>
                    <label class="payment-method">
                        <input type="radio" name="paymentMethod" value="paypal">
                        <span>💰</span>
                        <span>PayPal</span>
                    </label>
                    <label class="payment-method">
                        <input type="radio" name="paymentMethod" value="applepay">
                        <span>📱</span>
                        <span>Apple Pay</span>
                    </label>
                </div>

                <button class="btn-purchase" onclick="processPurchase()">
                    <span>Procedi al pagamento</span>
                    <span>→</span>
                </button>
            </div>

            <!-- Success state -->
            <div class="success-container" id="successContainer" style="display: none;">
                <div class="success-icon">✅</div>
                <div class="success-message">Acquisto completato!</div>
                <div class="ticket-number">Codice biglietto: #${Math.random().toString(36).substr(2, 9).toUpperCase()}</div>
                <div class="success-actions">
                    <a href="${pageContext.request.contextPath}/biglietti.jsp" class="btn-secondary">Vai ai miei biglietti</a>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-secondary">Nuova ricerca</a>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Loading overlay -->
<div class="loading-overlay" id="loadingOverlay">
    <div class="loading-spinner">⌛</div>
</div>

<jsp:include page="/import/footer.jsp"/>

<script>
// Handle ticket type selection
document.querySelectorAll('.ticket-type').forEach(ticket => {
    ticket.addEventListener('click', function() {
        // Remove selected class from all
        document.querySelectorAll('.ticket-type').forEach(t => t.classList.remove('selected'));
        // Add selected class to clicked
        this.classList.add('selected');
        
        // Update price
        const price = this.querySelector('.ticket-price').textContent;
        document.getElementById('basePrice').textContent = price;
        const numericPrice = parseFloat(price.replace('€', ''));
        const total = numericPrice + 2.00;
        document.getElementById('totalPrice').textContent = '€' + total.toFixed(2);
    });
});

// Handle payment method selection
document.querySelectorAll('.payment-method').forEach(method => {
    method.addEventListener('click', function() {
        document.querySelectorAll('.payment-method').forEach(m => m.classList.remove('selected'));
        this.classList.add('selected');
    });
});

// Process purchase
function processPurchase() {
    // Show loading
    document.getElementById('loadingOverlay').style.display = 'flex';
    
    // Simulate processing
    setTimeout(() => {
        // Hide loading
        document.getElementById('loadingOverlay').style.display = 'none';
        
        // Show success
        document.getElementById('paymentContainer').style.display = 'none';
        document.getElementById('successContainer').style.display = 'block';
        
        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }, 2000);
}

// Get URL parameters
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('trattaId')) {
    // Load tratta details if ID is provided
    // This would normally make an AJAX call to get the details
}
</script>
</body>
</html>
