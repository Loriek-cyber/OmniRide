<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="../import/metadata.jsp"/>
    <title>Il Mio Portafoglio - Omniride</title>
    <style>
        .wallet-container {
            max-width: 1000px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }
        
        .wallet-section {
            margin-bottom: 2rem;
        }
        
        .wallet-section h3 {
            color: #1f2937;
            border-bottom: 2px solid #32cd32;
            padding-bottom: 0.5rem;
            margin-bottom: 1rem;
        }
        
        .ticket-card {
            background: #f8f9fa;
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            transition: all 0.3s ease;
        }
        
        .ticket-card:hover {
            border-color: #32cd32;
            box-shadow: 0 4px 12px rgba(50, 205, 50, 0.1);
        }
        
        .ticket-info p {
            margin: 0.5rem 0;
        }
        
        .ticket-status {
            padding: 0.25rem 0.75rem;
            border-radius: 12px;
            font-weight: 600;
            font-size: 0.875rem;
        }
        
        .ticket-status.active {
            background: #10b981;
            color: white;
        }
        
        .ticket-status.inactive {
            background: #f59e0b;
            color: white;
        }
        
        .ticket-status.expired {
            background: #ef4444;
            color: white;
        }
        
        .btn-activate {
            background: linear-gradient(135deg, #32cd32 0%, #228b22 100%);
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .btn-activate:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(50, 205, 50, 0.3);
        }
        
        .empty-state {
            text-align: center;
            padding: 2rem;
            color: #6b7280;
        }
        
        .alert {
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
        }
        
        .alert.alert-danger {
            background: #fee2e2;
            border: 1px solid #fecaca;
            color: #dc2626;
        }
        
        .alert.alert-success {
            background: #d1fae5;
            border: 1px solid #a7f3d0;
            color: #059669;
        }
    </style>
</head>
<body>
<jsp:include page="../import/header.jsp"/>

<main>
    <div class="wallet-container">
        <h2>Il Mio Portafoglio</h2>

        <!-- Messaggi di errore/successo -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            </div>
        </c:if>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${successMessage}
            </div>
        </c:if>

        <div class="wallet-section">
            <h3>I Miei Biglietti</h3>
            <c:choose>
                <c:when test="${not empty biglietti}">
                    <c:forEach var="biglietto" items="${biglietti}">
                        <div class="ticket-card">
                            <div class="ticket-info">
                                <p><strong>Tratta:</strong> ${tratteNomi[biglietto.id_tratta]}</p>
                                <p><strong>Acquistato il:</strong> <fmt:formatDate value="${biglietto.dataAquisto}" pattern="dd/MM/yyyy HH:mm"/></p>
                                <p><strong>Prezzo:</strong> €<fmt:formatNumber value="${biglietto.prezzo}" pattern="#0.00"/></p>
                                <p><strong>Stato:</strong> <span class="ticket-status ${biglietto.stato eq 'ACQUISTATO' ? 'inactive' : (biglietto.stato eq 'CONVALIDATO' ? 'active' : 'expired')}">${biglietto.stato}</span></p>
                                <c:if test="${not empty biglietto.dataConvalida}">
                                    <p><strong>Convalidato il:</strong> <fmt:formatDate value="${biglietto.dataConvalida}" pattern="dd/MM/yyyy HH:mm"/></p>
                                </c:if>
                                <c:if test="${not empty biglietto.dataScadenza}">
                                    <p><strong>Scade il:</strong> <fmt:formatDate value="${biglietto.dataScadenza}" pattern="dd/MM/yyyy HH:mm"/></p>
                                </c:if>
                            </div>
                            <div class="ticket-actions">
                                <c:if test="${biglietto.stato eq 'ACQUISTATO'}">
                                    <form action="${pageContext.request.contextPath}/wallet" method="post">
                                        <input type="hidden" name="action" value="activateTicket">
                                        <input type="hidden" name="idBiglietto" value="${biglietto.id}">
                                        <button type="submit" class="btn-activate">Attiva Biglietto</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>Nessun biglietto nel tuo portafoglio.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="wallet-section">
            <h3>I Miei Abbonamenti</h3>
            <div class="empty-state">
                <p>Nessun abbonamento attivo al momento.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

        <div class="wallet-section">
            <h3>Carte di Credito Salvate</h3>
            <div class="empty-state">
                <p>Nessuna carta di credito salvata.</p>
                <p><em>Funzionalità in sviluppo.</em></p>
            </div>
        </div>

    </div>
</main>

<jsp:include page="../import/footer.jsp"/>
</body>
</html>