<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Le Mie Carte - OmniRide</title>
    <jsp:include page="/import/metadata.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/userSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/credit-cards.css">
</head>

<body class="user-dashboard-layout" data-page="credit-cards">
<c:if test="${not empty sessionScope.utente}">
    <!-- Include sidebar -->
    <jsp:include page="/import/userSidebar.jsp"/>

    <!-- Contenuto principale -->
    <div class="user-main-content">
        <jsp:include page="/import/header.jsp"/>

        <main>
            <div class="content-header">
                <h1><i class="fas fa-credit-card"></i> Gestione Carte di Credito</h1>
                <div class="breadcrumb">Area Utente / Carte di Credito</div>
            </div>

            <!-- Messaggi di feedback -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    <c:choose>
                        <c:when test="${param.success == 'add'}">Carta aggiunta con successo!</c:when>
                        <c:when test="${param.success == 'update'}">Carta modificata con successo!</c:when>
                        <c:when test="${param.success == 'delete'}">Carta eliminata con successo!</c:when>
                        <c:otherwise>Operazione completata con successo!</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${not empty param.error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    <c:choose>
                        <c:when test="${param.error == 'invalid_card'}">Dati della carta non validi!</c:when>
                        <c:when test="${param.error == 'duplicate_card'}">Questa carta è già registrata!</c:when>
                        <c:when test="${param.error == 'card_not_found'}">Carta non trovata!</c:when>
                        <c:when test="${param.error == 'database_error'}">Errore del sistema. Riprova più tardi.</c:when>
                        <c:otherwise>Si è verificato un errore. Riprova.</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <div class="user-content">
                <!-- Lista Carte Esistenti -->
                <div class="cards-section">
                    <h2>Le Tue Carte</h2>
                    
                    <c:choose>
                        <c:when test="${not empty carteCredito}">
                            <div class="cards-list">
                                <c:forEach var="card" items="${carteCredito}">
                                    <div class="card-item">
                                        <div class="card-icon">
                                            <i class="fas fa-credit-card"></i>
                                        </div>
                                        <div class="card-info">
                                            <p>${card.numeroCarta}</p>
                                            <small>Intestatario: ${card.nome_intestatario}</small>
                                            <small>Scadenza: ${card.data_scadenza}</small>
                                        </div>
                                        <div class="card-actions">
                                            <%--
                                            <form method="POST" action="${pageContext.request.contextPath}/deleteCard" style="display: inline;">
                                                <input type="hidden" name="cardId" value="${card.id}">
                                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Sei sicuro di voler eliminare questa carta?')">
                                                    Elimina
                                                </button>
                                            </form>
                                            --%>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="no-cards">
                                <p>Non hai ancora aggiunto nessuna carta di credito.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <!-- Form per Aggiungere Nuova Carta - Sempre Visibile -->
                <div class="add-card-section">
                    <h3>Aggiungi Nuova Carta</h3>
                    
                    <form method="POST" action="${pageContext.request.contextPath}/addCard">
                        <div class="form-group">
                            <label for="numero_carta">Numero Carta:</label>
                            <input type="text" id="numero_carta" name="numero_carta" placeholder="1234 5678 9012 3456" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="data">Scadenza (MM/YY):</label>
                                <input type="text" id="data" name="data" placeholder="12/25" maxlength="5" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="cvv">CVV:</label>
                                <input type="text" id="cvv" name="cvv" placeholder="123" maxlength="4" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="intestatario">Nome Intestatario:</label>
                            <input type="text" id="intestatario" name="intestatario" placeholder="Mario Rossi" required>
                        </div>
                        
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Aggiungi Carta
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </main>

        <jsp:include page="/import/footer.jsp"/>
    </div>
</c:if>

<c:if test="${empty sessionScope.utente}">
    <jsp:include page="/import/header.jsp"/>
    <main>
        <div class="content">
            <div class="login-required">
                <i class="fas fa-lock"></i>
                <h2>Accesso Richiesto</h2>
                <p>Per accedere alla gestione delle carte di credito, devi prima effettuare il login.</p>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                    <i class="fas fa-sign-in-alt"></i> Accedi
                </a>
            </div>
        </div>
    </main>
</c:if>


<script>
// Formattazione automatica del numero di carta
document.getElementById('numero_carta').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    let matches = value.match(/.{1,4}/g);
    let match = matches && matches.join(' ');
    if (match) {
        e.target.value = match.substring(0, 19); // Massimo 16 digits + 3 spazi
    } else {
        e.target.value = value;
    }
});

// Formattazione automatica della scadenza
document.getElementById('data').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    e.target.value = value;
});

// Validazione CVV (solo numeri)
document.getElementById('cvv').addEventListener('input', function(e) {
    e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4);
});

// Validazione form prima dell'invio
document.querySelector('form[action*="addCard"]').addEventListener('submit', function(e) {
    const numeroCarta = document.getElementById('numero_carta').value.replace(/\s/g, '');
    const data = document.getElementById('data').value;
    const cvv = document.getElementById('cvv').value;
    
    // Verifica lunghezza numero carta
    if (numeroCarta.length < 13 || numeroCarta.length > 19) {
        alert('Il numero di carta deve essere compreso tra 13 e 19 cifre.');
        e.preventDefault();
        return;
    }
    
    // Verifica formato data MM/YY
    if (!data.match(/^(0[1-9]|1[0-2])\/\d{2}$/)) {
        alert('Inserisci la data di scadenza nel formato MM/YY (es. 12/25)');
        e.preventDefault();
        return;
    }
    
    // Verifica CVV
    if (cvv.length < 3 || cvv.length > 4) {
        alert('Il CVV deve essere di 3 o 4 cifre.');
        e.preventDefault();
        return;
    }
});
</script>

</body>
</html>
