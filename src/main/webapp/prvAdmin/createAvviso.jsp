<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Crea Nuovo Avviso - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/avvisi.css">
</head>
<body>
    <div class="admin-container">
        <h1>📝 Crea Nuovo Avviso</h1>
        
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <div class="form-container">
            <form method="post" action="${pageContext.request.contextPath}/prvAdmin/avvisi">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="descrizione">🔔 Descrizione Avviso *</label>
                    <textarea id="descrizione" name="descrizione" class="form-control" 
                              placeholder="Inserisci qui il testo dell'avviso..." required>${param.descrizione}</textarea>
                    <small>💡 Scrivi un messaggio chiaro e informativo per gli utenti</small>
                </div>
                
                <div class="form-group">
                    <label>🚌 Tratte Coinvolte</label>
                    <div class="tratte-selection">
                        <c:choose>
                            <c:when test="${not empty tratte}">
                                <c:forEach var="tratta" items="${tratte}">
                                    <div class="tratta-item">
                                        <input type="checkbox" 
                                               name="tratte" 
                                               value="${tratta.id}" 
                                               id="tratta_${tratta.id}">
                                        <label for="tratta_${tratta.id}" class="tratta-nome">
                                            ${tratta.nome}
                                            <c:if test="${not empty tratta.azienda}">
                                                <span class="tratta-azienda">(${tratta.azienda.nome})</span>
                                            </c:if>
                                        </label>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <p class="tratte-empty">⚠️ Nessuna tratta disponibile nel sistema</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="selection-actions">
                        <button type="button" class="btn btn-sm btn-secondary" onclick="selectAll()">
                            ✅ Seleziona Tutte
                        </button>
                        <button type="button" class="btn btn-sm btn-secondary" onclick="deselectAll()">
                            ❌ Deseleziona Tutte
                        </button>
                    </div>
                    <small>💡 Seleziona le tratte interessate dall'avviso. Se non selezioni nessuna tratta, l'avviso sarà generale</small>
                </div>
                
                <div class="form-group text-center mt-30">
                    <button type="submit" class="btn btn-primary">📢 Pubblica Avviso</button>
                    <a href="${pageContext.request.contextPath}/prvAdmin/avvisi" class="btn btn-secondary">❌ Annulla</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Funzioni per selezione multiple
        function selectAll() {
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => cb.checked = true);
        }
        
        function deselectAll() {
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => cb.checked = false);
        }
        
        // Validazione client-side
        document.querySelector('form').addEventListener('submit', function(e) {
            const descrizione = document.getElementById('descrizione').value.trim();
            
            if (descrizione.length < 10) {
                alert('La descrizione deve contenere almeno 10 caratteri');
                e.preventDefault();
                return false;
            }
            
            if (descrizione.length > 500) {
                alert('La descrizione non può superare i 500 caratteri');
                e.preventDefault();
                return false;
            }
        });
        
        // Contatore caratteri
        document.getElementById('descrizione').addEventListener('input', function() {
            const maxLength = 500;
            const currentLength = this.value.length;
            const remaining = maxLength - currentLength;
            
            // Trova o crea elemento contatore
            let counter = document.getElementById('char-counter');
            if (!counter) {
                counter = document.createElement('div');
                counter.id = 'char-counter';
                counter.className = 'char-counter';
                this.parentNode.appendChild(counter);
            }
            
            counter.textContent = `${currentLength}/${maxLength} caratteri`;
            counter.className = 'char-counter' + (remaining < 50 ? ' warning' : '');
        });
    </script>
</body>
</html>
