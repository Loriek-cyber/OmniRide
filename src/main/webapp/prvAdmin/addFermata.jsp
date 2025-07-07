<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Aggiungi Fermata - Omniride</title>
    <%@ include file="../import/metadata.jsp" %>
</head>
<body>
    <div class="add-tratta-container">
        <a href="admin.jsp" class="back-link">← Torna alla Dashboard Admin</a>
        <h1 class="add-tratta-title">Aggiungi Nuova Fermata</h1>
        
        <form action="addFermata" method="post" id="addFermataForm">
            <div class="form-group">
                <label for="nome">Nome Fermata *:</label>
                <input type="text" id="nome" name="nome" class="form-input" required 
                       placeholder="Es: Stazione Centrale">
                <div class="help-text">Inserisci un nome identificativo per la fermata</div>
            </div>

            <div class="form-group">
                <label for="indirizzo">Indirizzo *:</label>
                <input type="text" id="indirizzo" name="indirizzo" class="form-input" required 
                       placeholder="Es: Via Roma 123, Milano">
                <div class="help-text">Inserisci l'indirizzo completo della fermata (sarà utilizzato per trovare automaticamente le coordinate)</div>
            </div>

            <div class="form-group">
                <label for="tipo">Tipo Fermata *:</label>
                <select id="tipo" name="tipo" class="form-input" required>
                    <option value="">Seleziona il tipo di fermata</option>
                    <option value="AUTOBUS">Fermata Autobus</option>
                    <option value="METRO">Stazione Metro</option>
                    <option value="TRENO">Stazione Treno</option>
                    <option value="TRAM">Fermata Tram</option>
                    <option value="AEROPORTO">Aeroporto</option>
                    <option value="PORTO">Porto</option>
                </select>
                <div class="help-text">Seleziona il tipo di trasporto per questa fermata</div>
            </div>

            <button type="submit" class="submit-btn">Crea Fermata</button>
        </form>
    </div>
    
    <script>
        // Validazione form semplificata
        document.getElementById('addFermataForm').addEventListener('submit', function(e) {
            const nome = document.getElementById('nome').value.trim();
            const indirizzo = document.getElementById('indirizzo').value.trim();
            const tipo = document.getElementById('tipo').value;
            
            if (nome.length < 3) {
                alert('Il nome della fermata deve contenere almeno 3 caratteri.');
                e.preventDefault();
                return false;
            }
            
            if (indirizzo.length < 10) {
                alert('Inserisci un indirizzo più specifico (almeno 10 caratteri).');
                e.preventDefault();
                return false;
            }
            
            if (!tipo) {
                alert('Seleziona il tipo di fermata.');
                e.preventDefault();
                return false;
            }
            
            // Mostra loading
            const submitBtn = document.querySelector('.submit-btn');
            submitBtn.textContent = 'Creazione in corso...';
            submitBtn.disabled = true;
            
            return true;
        });
    </script>
</body>
</html>
