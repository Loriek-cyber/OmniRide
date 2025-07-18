<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.pathfinding.Percorso" %>
<%@ page import="model.pathfinding.SegmentoPercorso" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Risultati Ricerca - OmniRide</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/search-results.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }
        
        .header {
            background-color: #2c3e50;
            color: white;
            padding: 15px 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .header h1 {
            font-size: 24px;
        }
        
        .container {
            display: flex;
            min-height: calc(100vh - 60px);
        }
        
        .results-section {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
        }
        
        .details-section {
            width: 400px;
            background-color: white;
            padding: 20px;
            border-left: 1px solid #ddd;
            overflow-y: auto;
        }
        
        .search-info {
            background-color: #ecf0f1;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        
        .percorso-card {
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 15px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .percorso-card:hover {
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            border-color: #3498db;
        }
        
        .percorso-card.selected {
            border-color: #3498db;
            border-width: 2px;
        }
        
        .percorso-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .percorso-route {
            font-size: 18px;
            font-weight: bold;
            color: #2c3e50;
        }
        
        .percorso-price {
            font-size: 24px;
            color: #27ae60;
            font-weight: bold;
        }
        
        .percorso-info {
            color: #7f8c8d;
            font-size: 14px;
        }
        
        .segmento {
            margin: 10px 0;
            padding: 10px;
            background-color: #ecf0f1;
            border-radius: 3px;
        }
        
        .no-results {
            text-align: center;
            padding: 50px;
            color: #7f8c8d;
        }
        
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            border: none;
            cursor: pointer;
            font-size: 16px;
            margin: 10px 0;
        }
        
        .btn:hover {
            background-color: #2980b9;
        }
        
        .btn-primary {
            background-color: #27ae60;
        }
        
        .btn-primary:hover {
            background-color: #229954;
        }
        
        #detailsContent {
            display: none;
        }
        
        #detailsContent.show {
            display: block;
        }
        
        .placeholder {
            text-align: center;
            color: #7f8c8d;
            padding: 50px 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="import/header.jsp"/>
    
    <main>
        <div class="page-title">
            <h1>Risultati della Ricerca</h1>
        </div>
        
        <div class="container">
        <div class="results-section">
            <div class="search-info">
                <strong>Percorso:</strong> <%= request.getAttribute("partenza") %> → <%= request.getAttribute("arrivo") %><br>
                <strong>Data:</strong> <%= request.getAttribute("data") %> | 
                <strong>Orario:</strong> <%= request.getAttribute("orario") %>
            </div>
            
            <%
                List<Percorso> percorsi = (List<Percorso>) request.getAttribute("percorsi");
                if (percorsi != null && !percorsi.isEmpty()) {
                    int index = 0;
                    for (Percorso percorso : percorsi) {
            %>
                <div class="percorso-card" onclick="showDetails(<%= index %>)" id="percorso-<%= index %>">
                    <div class="percorso-header">
                        <div class="percorso-route">
                            <%= percorso.getSegmenti().get(0).getFermataIn().getNome() %> → 
                            <%= percorso.getSegmenti().get(percorso.getSegmenti().size()-1).getFermataOu().getNome() %>
                        </div>
                        <div class="percorso-price">€<%= String.format("%.2f", percorso.getCosto()) %></div>
                    </div>
                    <div class="percorso-info">
                        <% if (percorso.getSegmenti().size() == 1) { %>
                            Tratta diretta - <%= percorso.getSegmenti().get(0).getNumero_fermate() %> fermate
                        <% } else { %>
                            <%= percorso.getSegmenti().size() %> cambi - Percorso complesso
                        <% } %>
                        <% if (percorso.getSegmenti().get(0).getTempo_partenza() != null) { %>
                            | Partenza: <%= percorso.getSegmenti().get(0).getTempo_partenza().format(DateTimeFormatter.ofPattern("HH:mm")) %>
                        <% } %>
                    </div>
                </div>
                
                <!-- Dati nascosti per i dettagli -->
                <script>
                    if (!window.percorsiData) window.percorsiData = [];
                    window.percorsiData[<%= index %>] = {
                        index: <%= index %>,
                        costo: <%= percorso.getCosto() %>,
                        segmenti: [
                            <% for (SegmentoPercorso seg : percorso.getSegmenti()) { %>
                            {
                                idTratta: <%= seg.getId_tratta() %>,
                                fermataIn: '<%= seg.getFermataIn().getNome() %>',
                                fermataOu: '<%= seg.getFermataOu().getNome() %>',
                                numeroFermate: <%= seg.getNumero_fermate() != null ? seg.getNumero_fermate() : 0 %>,
                                tempoPartenza: '<%= seg.getTempo_partenza() != null ? seg.getTempo_partenza().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/D" %>',
                                tempoArrivo: '<%= seg.getTempo_arrivo() != null ? seg.getTempo_arrivo().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/D" %>'
                            },
                            <% } %>
                        ]
                    };
                </script>
            <%
                    index++;
                    }
                } else {
            %>
                <div class="no-results">
                    <h2>Nessun percorso trovato</h2>
                    <p>Non sono stati trovati percorsi per la tratta selezionata.</p>
                    <a href="<%= request.getContextPath() %>/index.jsp" class="btn">Nuova ricerca</a>
                </div>
            <% } %>
        </div>
        
        <div class="details-section">
            <div class="placeholder" id="placeholder">
                <p>Seleziona un percorso per vedere i dettagli</p>
            </div>
            
            <div id="detailsContent">
                <h2>Dettagli Percorso</h2>
                <div id="detailsBody">
                    <!-- I dettagli verranno inseriti qui via JavaScript -->
                </div>
                
                <form action="<%= request.getContextPath() %>/acquistaBiglietto" method="get" id="buyForm">
                    <input type="hidden" name="percorsoId" id="percorsoId">
                    <input type="hidden" name="partenza" value="<%= request.getAttribute("partenza") %>">
                    <input type="hidden" name="arrivo" value="<%= request.getAttribute("arrivo") %>">
                    <input type="hidden" name="data" value="<%= request.getAttribute("data") %>">
                    <input type="hidden" name="orario" value="<%= request.getAttribute("orario") %>">
                    <input type="hidden" name="prezzo" id="prezzoHidden">
                    
                    <button type="submit" class="btn btn-primary" style="width: 100%;">
                        Acquista Biglietto
                    </button>
                </form>
                
                <button type="button" class="btn" style="width: 100%;" onclick="addToCart()">
                    Aggiungi al Carrello
                </button>
            </div>
        </div>
    </div>
    
    <script>
        let selectedPercorso = null;
        
        function showDetails(index) {
            // Rimuovi selezione precedente
            document.querySelectorAll('.percorso-card').forEach(card => {
                card.classList.remove('selected');
            });
            
            // Seleziona nuovo percorso
            document.getElementById('percorso-' + index).classList.add('selected');
            selectedPercorso = window.percorsiData[index];
            
            // Mostra dettagli
            document.getElementById('placeholder').style.display = 'none';
            document.getElementById('detailsContent').classList.add('show');
            
            // Costruisci HTML dettagli
            let detailsHtml = '<div style="margin: 20px 0;">';
            detailsHtml += '<p><strong>Prezzo totale:</strong> €' + selectedPercorso.costo.toFixed(2) + '</p>';
            detailsHtml += '<p><strong>Numero di tratte:</strong> ' + selectedPercorso.segmenti.length + '</p>';
            detailsHtml += '</div>';
            
            detailsHtml += '<h3>Segmenti del percorso:</h3>';
            selectedPercorso.segmenti.forEach((seg, i) => {
                detailsHtml += '<div class="segmento">';
                detailsHtml += '<strong>Tratta ' + (i+1) + ':</strong><br>';
                detailsHtml += seg.fermataIn + ' → ' + seg.fermataOu + '<br>';
                detailsHtml += 'Partenza: ' + seg.tempoPartenza + ' - Arrivo: ' + seg.tempoArrivo + '<br>';
                if (seg.numeroFermate > 0) {
                    detailsHtml += 'Fermate: ' + seg.numeroFermate;
                }
                detailsHtml += '</div>';
            });
            
            document.getElementById('detailsBody').innerHTML = detailsHtml;
            
            // Aggiorna form acquisto
            document.getElementById('percorsoId').value = index;
            document.getElementById('prezzoHidden').value = selectedPercorso.costo;
        }
        
        function addToCart() {
            if (!selectedPercorso) {
                alert('Seleziona prima un percorso');
                return;
            }
            
            // Qui implementerai la logica per aggiungere al carrello
            alert('Funzionalità carrello in sviluppo');
        }
    </script>
    </main>
    
    <jsp:include page="import/footer.jsp"/>
</body>
</html>
