<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="model.udata.Biglietto" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Portafoglio Ospite - OmniRide</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="Styles/waller.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
<jsp:include page="import/header.jsp"/>

<main>
    <div class="wallet-container">
        <div class="wallet-header">
            <h1><i class="fas fa-wallet"></i> Portafoglio Ospite</h1>
            <p class="wallet-subtitle">I tuoi biglietti temporanei</p>
        </div>
        
        <div class="wallet-notice">
            <div class="notice-content">
                <i class="fas fa-info-circle"></i>
                <div>
                    <h3>Portafoglio Temporaneo</h3>
                    <p>Questo è un portafoglio temporaneo per ospiti. I tuoi biglietti saranno disponibili solo per questa sessione.</p>
                    <p>Per conservare i tuoi biglietti in modo permanente, <a href="${pageContext.request.contextPath}/register">registrati</a> o <a href="${pageContext.request.contextPath}/login">accedi</a>.</p>
                </div>
            </div>
        </div>

        <div class="tickets-section">
            <h2>I tuoi biglietti</h2>
            
            <%
                @SuppressWarnings("unchecked")
                Map<String, Biglietto> guestTickets = (Map<String, Biglietto>) request.getAttribute("guestTickets");
                
                if (guestTickets == null || guestTickets.isEmpty()) {
            %>
                <div class="empty-wallet">
                    <i class="fas fa-ticket-alt"></i>
                    <h3>Nessun biglietto</h3>
                    <p>Non hai ancora acquistato biglietti.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-primary">Cerca tratte</a>
                </div>
            <%
                } else {
                    for (Map.Entry<String, Biglietto> entry : guestTickets.entrySet()) {
                        String ticketId = entry.getKey();
                        Biglietto biglietto = entry.getValue();
            %>
                <div class="ticket-card">
                    <div class="ticket-header">
                        <div class="ticket-id">
                            <i class="fas fa-ticket-alt"></i>
                            <span>Biglietto #<%= ticketId.substring(0, 8) %>...</span>
                        </div>
                        <div class="ticket-status status-<%= biglietto.getStato().toString().toLowerCase() %>">
                            <%= biglietto.getStato().toString() %>
                        </div>
                    </div>
                    
                    <div class="ticket-details">
                        <div class="detail-row">
                            <i class="fas fa-calendar-alt"></i>
                            <span>Data acquisto: <%= biglietto.getDataAcquisto() %></span>
                        </div>
                        <div class="detail-row">
                            <i class="fas fa-euro-sign"></i>
                            <span>Prezzo: €<%= String.format("%.2f", biglietto.getPrezzo()) %></span>
                        </div>
                        <div class="detail-row">
                            <i class="fas fa-route"></i>
                            <span>Tratte: <%= biglietto.getMap().size() %></span>
                        </div>
                    </div>
                    
                    <div class="ticket-actions">
                        <button class="btn-secondary" onclick="showTicketDetails('<%= ticketId %>')">
                            <i class="fas fa-info-circle"></i> Dettagli
                        </button>
                        <% if (biglietto.getStato() == Biglietto.StatoBiglietto.ACQUISTATO) { %>
                            <button class="btn-primary" onclick="validateTicket('<%= ticketId %>')">
                                <i class="fas fa-check"></i> Convalida
                            </button>
                        <% } %>
                    </div>
                </div>
            <%
                    }
                }
            %>
        </div>
        
        <div class="wallet-actions">
            <a href="${pageContext.request.contextPath}/" class="btn-primary">
                <i class="fas fa-search"></i> Cerca altre tratte
            </a>
            <a href="${pageContext.request.contextPath}/register" class="btn-secondary">
                <i class="fas fa-user-plus"></i> Registrati per salvare i biglietti
            </a>
        </div>
    </div>
</main>

<!-- Modal per i dettagli del biglietto -->
<div id="ticketModal" class="modal" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Dettagli biglietto</h3>
            <button class="close-btn" onclick="closeModal()">×</button>
        </div>
        <div class="modal-body" id="ticketModalBody">
            <!-- Contenuto dinamico -->
        </div>
    </div>
</div>

<jsp:include page="import/footer.jsp"/>

<script>
function showTicketDetails(ticketId) {
    // Implementa la logica per mostrare i dettagli del biglietto
    const modal = document.getElementById('ticketModal');
    const modalBody = document.getElementById('ticketModalBody');
    
    // Qui dovresti fare una chiamata AJAX per ottenere i dettagli completi
    modalBody.innerHTML = `
        <div class="ticket-detail-info">
            <h4>Biglietto #${ticketId.substring(0, 8)}...</h4>
            <p>I dettagli completi del biglietto saranno implementati qui.</p>
        </div>
    `;
    
    modal.style.display = 'flex';
}

function validateTicket(ticketId) {
    if (confirm('Sei sicuro di voler convalidare questo biglietto?')) {
        // Implementa la logica per convalidare il biglietto
        fetch('/api/tickets/validate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ticketId: ticketId,
                isGuest: true
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Biglietto convalidato con successo!');
                location.reload();
            } else {
                alert('Errore nella convalida: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Errore:', error);
            alert('Errore durante la convalida.');
        });
    }
}

function closeModal() {
    document.getElementById('ticketModal').style.display = 'none';
}

// Chiudi il modal cliccando fuori
window.onclick = function(event) {
    const modal = document.getElementById('ticketModal');
    if (event.target == modal) {
        closeModal();
    }
}
</script>

<style>
.wallet-container {
    max-width: 800px;
    margin: 2rem auto;
    padding: 0 1rem;
}

.wallet-header {
    text-align: center;
    margin-bottom: 2rem;
}

.wallet-header h1 {
    color: #1f2937;
    margin-bottom: 0.5rem;
}

.wallet-subtitle {
    color: #6b7280;
    font-size: 1.1rem;
}

.wallet-notice {
    background-color: #fef3c7;
    border: 1px solid #f59e0b;
    border-radius: 8px;
    padding: 1rem;
    margin-bottom: 2rem;
}

.notice-content {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
}

.notice-content i {
    color: #f59e0b;
    font-size: 1.5rem;
    margin-top: 0.2rem;
}

.notice-content h3 {
    margin: 0 0 0.5rem 0;
    color: #92400e;
}

.notice-content p {
    margin: 0.5rem 0;
    color: #92400e;
}

.notice-content a {
    color: #1d4ed8;
    text-decoration: none;
}

.notice-content a:hover {
    text-decoration: underline;
}

.tickets-section h2 {
    color: #1f2937;
    margin-bottom: 1rem;
}

.empty-wallet {
    text-align: center;
    padding: 3rem;
    color: #6b7280;
}

.empty-wallet i {
    font-size: 4rem;
    margin-bottom: 1rem;
    color: #d1d5db;
}

.ticket-card {
    background-color: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.ticket-id {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-weight: 600;
    color: #1f2937;
}

.ticket-status {
    padding: 0.25rem 0.75rem;
    border-radius: 999px;
    font-size: 0.875rem;
    font-weight: 500;
    text-transform: uppercase;
}

.status-acquistato {
    background-color: #dcfce7;
    color: #166534;
}

.status-convalidato {
    background-color: #dbeafe;
    color: #1e40af;
}

.status-scaduto {
    background-color: #fef2f2;
    color: #dc2626;
}

.ticket-details {
    margin-bottom: 1rem;
}

.detail-row {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
    color: #6b7280;
}

.detail-row i {
    color: #3b82f6;
    width: 16px;
}

.ticket-actions {
    display: flex;
    gap: 0.5rem;
}

.wallet-actions {
    display: flex;
    gap: 1rem;
    justify-content: center;
    margin-top: 2rem;
}

.btn-primary, .btn-secondary {
    padding: 0.75rem 1.5rem;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 1rem;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.3s ease;
}

.btn-primary {
    background-color: #3b82f6;
    color: white;
}

.btn-primary:hover {
    background-color: #2563eb;
}

.btn-secondary {
    background-color: #6b7280;
    color: white;
}

.btn-secondary:hover {
    background-color: #4b5563;
}

.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    border-radius: 8px;
    max-width: 500px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-header {
    padding: 1rem;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    margin: 0;
    color: #1f2937;
}

.close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #6b7280;
}

.modal-body {
    padding: 1rem;
}

@media (max-width: 768px) {
    .wallet-actions {
        flex-direction: column;
        align-items: center;
    }
    
    .btn-primary, .btn-secondary {
        width: 100%;
        justify-content: center;
    }
    
    .ticket-actions {
        flex-direction: column;
    }
}
</style>

</body>
</html>
