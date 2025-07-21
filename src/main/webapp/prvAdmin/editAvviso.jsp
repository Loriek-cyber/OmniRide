<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Modifica Avviso - Admin</title>
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
        .btn-warning { background-color: #ffc107; color: black; }
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
        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .avviso-info {
            background: #e9ecef;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 25px;
        }
        .avviso-info h3 {
            margin: 0 0 10px 0;
            color: #495057;
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
        <h1>✏️ Modifica Avviso</h1>
        
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty editAvviso}">
                <!-- Informazioni correnti avviso -->
                <div class="avviso-info">
                    <h3>📋 Informazioni Correnti</h3>
                    <p><strong>ID Avviso:</strong> #${editAvviso.id}</p>
                    <p><strong>Descrizione Attuale:</strong></p>
                    <div style="background: white; padding: 10px; border-radius: 4px; font-style: italic;">
                        "${editAvviso.descrizione}"
                    </div>
                    <c:if test="${not empty editAvviso.id_tratte_coinvolte}">
                        <p><strong>Tratte Attualmente Associate:</strong> ${editAvviso.id_tratte_coinvolte.size()} tratte</p>
                    </c:if>
                </div>
                
                <div class="form-container">
                    <form method="post" action="${pageContext.request.contextPath}/prvAdmin/avvisi">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${editAvviso.id}">
                        
                        <div class="form-group">
                            <label for="descrizione">🔔 Descrizione Avviso *</label>
                            <textarea id="descrizione" name="descrizione" class="form-control" 
                                      placeholder="Modifica il testo dell'avviso..." required>${editAvviso.descrizione}</textarea>
                            <small>💡 Modifica il messaggio per renderlo più chiaro e informativo</small>
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
                                                       id="tratta_${tratta.id}"
                                                       <c:if test="${editAvviso.id_tratte_coinvolte.contains(tratta.id)}">checked</c:if>>
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
                                <button type="button" class="btn btn-sm btn-warning" onclick="resetToOriginal()">
                                    🔄 Ripristina Selezione Originale
                                </button>
                            </div>
                            <small>💡 Modifica la selezione delle tratte interessate dall'avviso</small>
                        </div>
                        
                        <div class="form-group" style="text-align: center; margin-top: 30px;">
                            <button type="submit" class="btn btn-primary">💾 Salva Modifiche</button>
                            <button type="button" class="btn btn-warning" onclick="resetForm()">🔄 Ripristina</button>
                            <a href="${pageContext.request.contextPath}/prvAdmin/avvisi" class="btn btn-secondary">❌ Annulla</a>
                        </div>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <div class="message error">
                    <h3>❌ Avviso non trovato</h3>
                    <p>L'avviso richiesto non esiste o non è accessibile.</p>
                    <a href="${pageContext.request.contextPath}/prvAdmin/avvisi" class="btn btn-primary">
                        ← Torna alla Lista Avvisi
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        // Valori originali per il ripristino
        const originalValues = {
            descrizione: '${editAvviso.descrizione}',
            tratte: [
                <c:forEach var="trattaId" items="${editAvviso.id_tratte_coinvolte}" varStatus="status">
                    ${trattaId}<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ]
        };
        
        // Funzioni per selezione multiple
        function selectAll() {
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => cb.checked = true);
        }
        
        function deselectAll() {
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => cb.checked = false);
        }
        
        function resetToOriginal() {
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => {
                const trattaId = parseInt(cb.value);
                cb.checked = originalValues.tratte.includes(trattaId);
            });
        }
        
        // Funzione per ripristinare tutto il form
        function resetForm() {
            if (confirm('Sei sicuro di voler ripristinare tutti i campi ai valori originali?')) {
                document.getElementById('descrizione').value = originalValues.descrizione;
                resetToOriginal();
            }
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
            
            // Conferma per i cambiamenti
            const originalDesc = originalValues.descrizione;
            if (descrizione !== originalDesc) {
                if (!confirm('Stai modificando la descrizione dell\'avviso. Continuare?')) {
                    e.preventDefault();
                    return false;
                }
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
        
        // Evidenzia i campi modificati
        document.addEventListener('DOMContentLoaded', function() {
            const descrizioneField = document.getElementById('descrizione');
            const originalDesc = originalValues.descrizione;
            
            descrizioneField.addEventListener('input', function() {
                if (this.value !== originalDesc) {
                    this.style.borderLeft = '4px solid #ffc107';
                } else {
                    this.style.borderLeft = '';
                }
            });
            
            // Monitora i checkbox delle tratte
            const checkboxes = document.querySelectorAll('input[name="tratte"]');
            checkboxes.forEach(cb => {
                cb.addEventListener('change', function() {
                    // Controlla se la selezione corrente è diversa da quella originale
                    const currentSelection = Array.from(document.querySelectorAll('input[name="tratte"]:checked')).map(cb => parseInt(cb.value));
                    const isChanged = !arraysEqual(currentSelection.sort(), originalValues.tratte.sort());
                    
                    const container = document.querySelector('.tratte-selection');
                    if (isChanged) {
                        container.style.borderLeft = '4px solid #ffc107';
                    } else {
                        container.style.borderLeft = '';
                    }
                });
            });
        });
        
        // Funzione di utilità per confrontare array
        function arraysEqual(a, b) {
            return a.length === b.length && a.every((val, i) => val === b[i]);
        }
    </script>
</body>
</html>
