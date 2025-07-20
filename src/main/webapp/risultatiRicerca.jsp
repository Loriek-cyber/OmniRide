

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.pathfinding.Percorso" %>
<%@ page import="model.pathfinding.SegmentoPercorso" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Risultati Ricerca - OmniRide</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/Styles/search-results.css">
</head>
<body>
    <jsp:include page="import/header.jsp"/>
    
    <!-- Layout a due colonne come tratte.jsp -->
    <div class="tratte-layout">
        
        <!-- Colonna sinistra - Lista risultati -->
        <div class="tratte-list-container">
            <%
                List<Percorso> percorsi = (List<Percorso>) request.getAttribute("percorsi");
                int numResultati = (percorsi != null) ? percorsi.size() : 0;
                String partenza = (String) request.getAttribute("partenza");
                String arrivo = (String) request.getAttribute("arrivo");
                String data = (String) request.getAttribute("data");
                String orario = (String) request.getAttribute("orario");
            %>
            
            <div class="tratte-list-header">
                <h2>🔍 Risultati Ricerca</h2>
                <span class="tratte-count"><%= numResultati %> risultat<%= numResultati == 1 ? "o" : "i" %></span>
            </div>
            
            <!-- Info ricerca -->
            <div class="search-info">
                <div class="route-info">
                    <strong><%= partenza != null ? partenza : "N/A" %></strong> → <strong><%= arrivo != null ? arrivo : "N/A" %></strong>
                </div>
                <div class="date-time-info">
                    📅 <%= data != null ? data : "Oggi" %> | 🕐 <%= orario != null ? orario : "Ora" %>
                </div>
            </div>
            
            <!-- Lista percorsi -->
            <div class="tratte-list">
                <%
                    if (percorsi != null && !percorsi.isEmpty()) {
                        int index = 0;
                        for (Percorso percorso : percorsi) {
                %>
                    <div class="tratta-item" data-percorso-id="<%= index %>" onclick="selectPercorso(<%= index %>)"
                         data-costo="<%= percorso.getCosto() %>"
                         data-segmenti='[
                            <% 
                            int segIndex = 0;
                            for (SegmentoPercorso seg : percorso.getSegmenti()) { 
                                if (segIndex > 0) out.print(",");
                            %>
                            {
                                "id_tratta": <%= seg.getId_tratta() %>,
                                "fermataIn": "<%= seg.getFermataIn().getNome().replace("\"", "\\\"") %>",
                                "fermataOu": "<%= seg.getFermataOu().getNome().replace("\"", "\\\"") %>",
                                "numero_fermate": <%= seg.getNumero_fermate() != null ? seg.getNumero_fermate() : 0 %>,
                                "tempo_partenza": "<%= seg.getTempo_partenza() != null ? seg.getTempo_partenza().format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--" %>",
                                "tempo_arrivo": "<%= seg.getTempo_arrivo() != null ? seg.getTempo_arrivo().format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--" %>"
                            }
                            <% 
                                segIndex++;
                            } 
                            %>
                         ]'>
                        <div class="tratta-header">
                            <div class="tratta-name">
                                <%= percorso.getSegmenti().get(0).getFermataIn().getNome() %> → 
                                <%= percorso.getSegmenti().get(percorso.getSegmenti().size()-1).getFermataOu().getNome() %>
                            </div>
                            <div class="price-badge">€<%= String.format("%.2f", percorso.getCosto()) %></div>
                        </div>
                        
                        <div class="tratta-info">
                            <div class="tratta-stat">
                                <span class="stat-icon">🚌</span>
                                <span class="stat-value">
                                    <% if (percorso.getSegmenti().size() == 1) { %>
                                        Diretto - <%= percorso.getSegmenti().get(0).getNumero_fermate() %> fermate
                                    <% } else { %>
                                        <%= percorso.getSegmenti().size() %> cambi
                                    <% } %>
                                </span>
                            </div>
                            
                            <% if (percorso.getSegmenti().get(0).getTempo_partenza() != null) { %>
                                <div class="tratta-stat">
                                    <span class="stat-icon">🕐</span>
                                    <span class="stat-value">Partenza: <%= percorso.getSegmenti().get(0).getTempo_partenza().format(DateTimeFormatter.ofPattern("HH:mm")) %></span>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <%
                        index++;
                        }
                    } else {
                %>
                    <div class="no-data">
                        <div class="no-data-icon">🚌</div>
                        <p>Nessun percorso trovato per la ricerca effettuata.</p>
                        <a href="<%= request.getContextPath() %>/" class="btn btn-primary">Nuova Ricerca</a>
                    </div>
                <% } %>
            </div>
        </div>
        
        <!-- Colonna destra - Dettagli percorso selezionato -->
        <div class="tratta-details-container">
            <div class="tratta-details-header">
                <h3>📋 Dettagli Percorso</h3>
            </div>
            
            <div id="percorso-details" class="tratta-details-content">
                <div class="no-selection">
                    <div class="no-selection-icon">👈</div>
                    <p>Seleziona un percorso dalla lista per visualizzarne i dettagli</p>
                </div>
            </div>
            
            <!-- Pulsante per selezionare il ticket (nascosto inizialmente) -->
            <div class="booking-section" id="bookingSection" style="display: none;">
                <button type="button" class="btn-primary" onclick="selectTicketType()" id="selectTicketBtn">
                    🎫 Seleziona Tipo Biglietto
                </button>
            </div>
            
            <!-- Form nascosto per inviare i dati -->
            <form id="ticketForm" method="GET" action="<%= request.getContextPath() %>/selectTicketType" style="display: none;">
                <input type="hidden" name="percorso" id="percorsoData">
                <input type="hidden" name="data" value="<%= data %>">
                <input type="hidden" name="orario" value="<%= orario %>">
                <input type="hidden" name="prezzo" id="prezzoData">
                <input type="hidden" name="partenza" value="<%= partenza %>">
                <input type="hidden" name="arrivo" value="<%= arrivo %>">
            </form>
        </div>
    </div>
    
    <script>
        var selectedPercorso = null;
        
        function selectPercorso(index) {
            console.log('Selezione percorso:', index);
            
            // Rimuovi selezione precedente
            document.querySelectorAll('.tratta-item').forEach(item => {
                item.classList.remove('selected');
            });
            
            // Seleziona nuovo percorso
            const percorsoElement = document.querySelector('[data-percorso-id="' + index + '"]');
            if (percorsoElement) {
                percorsoElement.classList.add('selected');
                
                // Leggi i dati
                const costo = parseFloat(percorsoElement.dataset.costo);
                const segmenti = JSON.parse(percorsoElement.dataset.segmenti);
                
                selectedPercorso = {
                    index: index,
                    costo: costo,
                    segmenti: segmenti
                };
                
                // Mostra dettagli
                showPercorsoDetails(selectedPercorso);
                
                // Mostra pulsante booking
                document.getElementById('bookingSection').style.display = 'block';
                
                // Aggiorna form
                document.getElementById('percorsoData').value = JSON.stringify(selectedPercorso);
                document.getElementById('prezzoData').value = costo.toString();
            }
        }
        
        function showPercorsoDetails(percorso) {
            const detailsContainer = document.getElementById('percorso-details');
            
            let html = '<div class="percorso-details">';
            html += '<div class="percorso-title">Percorso Selezionato</div>';
            
            // Prezzo
            html += '<div class="price-section">';
            html += '<div class="price-display">€' + percorso.costo.toFixed(2) + '</div>';
            html += '<div class="price-label">Prezzo totale</div>';
            html += '</div>';
            
            // Segmenti
            html += '<div class="segments-section">';
            html += '<div class="section-title">Dettagli del viaggio:</div>';
            
            percorso.segmenti.forEach((seg, i) => {
                html += '<div class="segment-item">';
                html += '<div class="segment-header"><strong>Tratta ' + (i+1) + '</strong></div>';
                html += '<div class="segment-route">' + seg.fermataIn + ' → ' + seg.fermataOu + '</div>';
                html += '<div class="segment-times">Partenza: ' + seg.tempo_partenza + ' - Arrivo: ' + seg.tempo_arrivo + '</div>';
                if (seg.numero_fermate > 0) {
                    html += '<div class="segment-stops">Fermate intermedie: ' + seg.numero_fermate + '</div>';
                }
                html += '</div>';
            });
            
            html += '</div>';
            html += '</div>';
            
            detailsContainer.innerHTML = html;
        }
        
        function selectTicketType() {
            if (!selectedPercorso) {
                alert('Seleziona prima un percorso');
                return;
            }
            
            console.log('Invio a selectTicketType:', selectedPercorso);
            document.getElementById('ticketForm').submit();
        }
    </script>
    
    <jsp:include page="import/footer.jsp"/>
</body>
</html>
