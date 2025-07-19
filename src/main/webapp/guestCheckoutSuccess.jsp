<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Acquisto Completato - OmniRide</title>
    <style>
        /* Success page styles */
        .success-page {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .success-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2rem;
            margin: 2rem 0;
            text-align: center;
        }

        .success-icon {
            font-size: 5rem;
            color: #32cd32;
            margin-bottom: 1rem;
        }

        .success-title {
            font-size: 2.5rem;
            font-weight: 600;
            color: #2d3436;
            margin-bottom: 1rem;
        }

        .success-message {
            font-size: 1.2rem;
            color: #636e72;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .ticket-info {
            background: #f0fff0;
            border: 2px solid #32cd32;
            border-radius: 8px;
            padding: 2rem;
            margin: 2rem 0;
            text-align: left;
        }

        .ticket-info h3 {
            color: #32cd32;
            margin-top: 0;
            margin-bottom: 1rem;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin: 1rem 0;
        }

        .info-item {
            background: white;
            padding: 1rem;
            border-radius: 6px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .info-label {
            font-size: 0.85rem;
            color: #636e72;
            margin-bottom: 0.3rem;
        }

        .info-value {
            font-weight: 600;
            color: #2d3436;
            font-size: 1.1rem;
        }

        .guest-notice {
            background: #fff3cd;
            border: 2px solid #ffc107;
            border-radius: 8px;
            padding: 1.5rem;
            margin: 2rem 0;
            text-align: left;
        }

        .guest-notice h4 {
            color: #856404;
            margin-top: 0;
            margin-bottom: 1rem;
        }

        .guest-notice ul {
            margin: 0;
            padding-left: 1.5rem;
        }

        .guest-notice li {
            margin-bottom: 0.5rem;
            color: #856404;
        }

        .actions-section {
            margin-top: 2rem;
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 1rem 2rem;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-primary {
            background: #32cd32;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background: #28a428;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background: #f8f9fa;
            color: #2d3436;
            border: 1px solid #ddd;
        }

        .btn-secondary:hover {
            background: #e9ecef;
        }

        .tickets-list {
            margin-top: 2rem;
        }

        .ticket-item {
            background: #f8f9fa;
            border-radius: 6px;
            padding: 1rem;
            margin-bottom: 1rem;
            text-align: left;
        }

        .ticket-id {
            font-family: monospace;
            font-size: 1.2rem;
            font-weight: 600;
            color: #32cd32;
            margin-bottom: 0.5rem;
        }

        /* Print styles */
        @media print {
            .actions-section {
                display: none;
            }
        }

        /* Responsive */
        @media (max-width: 768px) {
            .success-container {
                padding: 2rem 1rem;
            }
            
            .success-title {
                font-size: 2rem;
            }
            
            .actions-section {
                flex-direction: column;
            }
            
            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/import/header.jsp"/>

<main class="success-page">
    <div class="success-container">
        <div class="success-icon">✅</div>
        
        <h1 class="success-title">Acquisto Completato!</h1>
        
        <div class="success-message">
            Il tuo acquisto è stato elaborato con successo. I tuoi biglietti sono pronti per l'uso.
        </div>

        <div class="ticket-info">
            <h3>📋 Riepilogo Acquisto</h3>
            
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Data Acquisto</div>
                    <div class="info-value">
                        <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy HH:mm"/>
                    </div>
                </div>
                
                <div class="info-item">
                    <div class="info-label">Numero Biglietti</div>
                    <div class="info-value">
                        <c:choose>
                            <c:when test="${not empty sessionScope.guestTicketIds}">
                                ${sessionScope.guestTicketIds.size()}
                            </c:when>
                            <c:otherwise>
                                1
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="info-item">
                    <div class="info-label">Tipo Acquirente</div>
                    <div class="info-value">Ospite (Guest)</div>
                </div>
            </div>

            <c:if test="${not empty sessionScope.guestTicketIds}">
                <div class="tickets-list">
                    <h4>I tuoi biglietti:</h4>
                    <c:forEach var="ticketId" items="${sessionScope.guestTicketIds}" varStatus="status">
                        <div class="ticket-item">
                            <div class="ticket-id">Biglietto #${ticketId}</div>
                            <p style="margin: 0; color: #636e72; font-size: 0.9rem;">
                                Codice: OMR${String.format("%08d", ticketId)}
                            </p>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <div class="guest-notice">
            <h4>⚠️ Importante per gli Ospiti</h4>
            <ul>
                <li><strong>Conserva questa pagina:</strong> Stampa o fai uno screenshot di questa pagina per i tuoi archivi</li>
                <li><strong>Nessun account richiesto:</strong> I tuoi biglietti sono validi anche senza registrazione</li>
                <li><strong>Validazione:</strong> Dovrai convalidare i biglietti prima dell'uso</li>
                <li><strong>Supporto:</strong> Per assistenza, contatta il nostro servizio clienti</li>
            </ul>
        </div>

        <div class="actions-section">
            <button onclick="window.print()" class="btn btn-secondary">
                🖨️ Stampa Biglietti
            </button>
            
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                🏠 Torna alla Home
            </a>
            
            <a href="${pageContext.request.contextPath}/visualizzaTratte" class="btn btn-secondary">
                🔍 Cerca Altri Viaggi
            </a>
        </div>
    </div>

    <!-- Additional information section -->
    <div style="background: #f8f9fa; border-radius: 8px; padding: 2rem; margin-top: 2rem;">
        <h3>📞 Hai bisogno di aiuto?</h3>
        <p style="margin-bottom: 1rem; color: #636e72;">
            Se hai domande sui tuoi biglietti o necessiti di assistenza, contattaci:
        </p>
        <div style="display: flex; gap: 2rem; justify-content: center; flex-wrap: wrap;">
            <div style="text-align: center;">
                <strong>📞 Telefono</strong><br>
                <span style="color: #32cd32; font-size: 1.1rem;">+39 02 1234 5678</span>
            </div>
            <div style="text-align: center;">
                <strong>📧 Email</strong><br>
                <span style="color: #32cd32; font-size: 1.1rem;">supporto@omniride.it</span>
            </div>
            <div style="text-align: center;">
                <strong>🕒 Orari</strong><br>
                <span style="color: #636e72;">Lun-Ven: 8:00-20:00</span>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/import/footer.jsp"/>

<script>
// Auto-scroll to top on page load
window.addEventListener('load', function() {
    window.scrollTo(0, 0);
});

// Optional: Clear guest ticket IDs after a certain time to avoid session bloat
setTimeout(function() {
    // This would need to be implemented server-side for actual cleanup
    console.log('Guest checkout completed');
}, 5000);
</script>

</body>
</html>
