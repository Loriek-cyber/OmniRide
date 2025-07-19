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
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="container">
        <h2>Seleziona il Tipo di Biglietto</h2>
        
        <div class="route-info">
            <h4>Dettagli del Viaggio</h4>
            <p><strong>Data:</strong> ${data}</p>
            <p><strong>Orario:</strong> ${orario}</p>
            <p><strong>Prezzo base tratta:</strong> <fmt:formatNumber value="${prezzoBase}" type="currency" currencySymbol="€"/></p>
        </div>
        
        <form action="${pageContext.request.contextPath}/selectTicketType" method="post" id="ticketForm">
            <input type="hidden" name="percorso" value="${percorso}">
            <input type="hidden" name="data" value="${data}">
            <input type="hidden" name="orario" value="${orario}">
            <input type="hidden" name="prezzo" value="" id="selectedPrice">
            <input type="hidden" name="tipo" value="" id="selectedType">
            
            <div class="ticket-options">
                <!-- Biglietto Normale -->
                <div class="ticket-card" onclick="selectTicket('NORMALE', ${prezzoNormale})">
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
                <div class="ticket-card" onclick="selectTicket('GIORNALIERO', ${prezzoGiornaliero})">
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
                <div class="ticket-card" onclick="selectTicket('ANNUALE', ${prezzoAnnuale})">
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
                <a href="${pageContext.request.contextPath}/tratte" class="btn btn-secondary">
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
function selectTicket(type, price) {
    // Remove selected class from all cards
    document.querySelectorAll('.ticket-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    // Add selected class to clicked card
    event.currentTarget.classList.add('selected');
    
    // Set hidden form values
    document.getElementById('selectedType').value = type;
    document.getElementById('selectedPrice').value = price;
    
    // Check the corresponding radio button
    document.getElementById(type.toLowerCase()).checked = true;
    
    // Enable the submit button
    document.getElementById('addToCartBtn').disabled = false;
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
