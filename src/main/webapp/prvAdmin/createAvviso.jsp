<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Crea Nuovo Avviso - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/admin.css">
    <style>
        .admin-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .form-container {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        .form-control {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }
        .form-control:focus {
            border-color: #007bff;
            outline: none;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
        }
        .form-control textarea {
            min-height: 120px;
            resize: vertical;
            font-family: inherit;
        }
        .btn {
            padding: 12px 20px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
        }
        .btn-primary { background-color: #007bff; color: white; }
        .btn-secondary { background-color: #6c757d; color: white; }
        .message {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
        }
        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .tratte-selection {
            max-height: 300px;
            overflow-y: auto;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 10px;
            background: white;
        }
        .tratta-item {
            display: flex;
            align-items: center;
            padding: 8px;
            margin: 2px 0;
            background: #f8f9fa;
            border-radius: 4px;
        }
        .tratta-item:hover {
            background: #e9ecef;
        }
        .tratta-item input[type="checkbox"] {
            margin-right: 10px;
        }
        .tratta-nome {
            font-weight: 500;
        }
        .tratta-azienda {
            font-size: 12px;
            color: #6c757d;
            margin-left: 10px;
        }
        .selection-actions {
            margin-top: 10px;
        }
        .btn-sm {
            padding: 6px 12px;
            font-size: 14px;
        }
    </style>
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
                                <p style="color: #6c757d; text-align: center; padding: 20px;">
                                    ⚠️ Nessuna tratta disponibile nel sistema
                                </p>
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
                
                <div class="form-group" style="text-align: center; margin-top: 30px;">
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
                counter.style.cssText = 'font-size: 12px; color: #6c757d; text-align: right; margin-top: 5px;';
                this.parentNode.appendChild(counter);
            }
            
            counter.textContent = `${currentLength}/${maxLength} caratteri`;
            counter.style.color = remaining < 50 ? '#dc3545' : '#6c757d';
        });
    </script>
</body>
</html>
