<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Fermata" %>
<!DOCTYPE html>
<html>
<head>
    <title>Aggiungi Tratta - Omniride</title>
    <%@ include file="../import/metadata.jsp" %>
</head>
<body>
    <div class="add-tratta-container">
        <a href="../dashboard" class="back-link">← Torna alla Dashboard</a>
        <h1 class="add-tratta-title">Aggiungi Nuova Tratta</h1>
        
        <form action="addTratta" method="post" id="addTrattaForm">
            <div class="form-group">
                <label for="nome">Nome Tratta *:</label>
                <input type="text" id="nome" name="nome" class="form-input" required 
                       placeholder="Es: Linea 1 - Centro-Periferia">
                <div class="help-text">Inserisci un nome descrittivo per la tratta</div>
            </div>

            <div class="form-group">
                <label for="costo">Costo (€) *:</label>
                <input type="number" id="costo" name="costo" class="form-input" 
                       step="0.01" min="0.01" required placeholder="Es: 2.50">
                <div class="help-text">Inserisci il costo del biglietto per l'intera tratta</div>
            </div>

            <div class="form-group">
                <label>Seleziona Fermate e Tempi di Percorrenza *:</label>
                <div class="help-text">Seleziona almeno 2 fermate e specifica i tempi di percorrenza tra fermate consecutive</div>
                <div class="fermate-container" id="fermateContainer">
                    <%
                        List<Fermata> fermate = (List<Fermata>) request.getAttribute("fermate");
                        if (fermate != null && !fermate.isEmpty()) {
                    %>
                        <% for (int i = 0; i < fermate.size(); i++) { 
                            Fermata fermata = fermate.get(i);
                        %>
                        <div class="fermata-item">
                            <input type="checkbox" class="fermata-checkbox" 
                                   name="fermateSelezionate" 
                                   value="<%= fermata.getId() %>" 
                                   id="fermata_<%= fermata.getId() %>">
                            <label for="fermata_<%= fermata.getId() %>" class="fermata-name">
                                <%= fermata.getNome() %>
                            </label>
                            <div class="tempo-container" id="tempo_<%= fermata.getId() %>" style="display: none;">
                                <label for="tempo_<%= fermata.getId() %>_input">Tempo alla prossima (min):</label>
                                <input type="number" 
                                       class="tempo-input" 
                                       name="tempiTraFermate" 
                                       id="tempo_<%= fermata.getId() %>_input"
                                       min="1" 
                                       placeholder="15">
                            </div>
                        </div>
                        <% } %>
                    <%
                        } else {
                    %>
                        <div class="no-fermate-message">
                            <p>Nessuna fermata disponibile.</p>
                            <p><a href="../addFermata">Aggiungi prima alcune fermate</a>.</p>
                        </div>
                    <%
                        }
                    %>
                </div>
            </div>

            <% if (fermate != null && !fermate.isEmpty()) { %>
            <button type="submit" class="submit-btn">Crea Tratta</button>
            <% } %>
        </form>
    </div>
</body>
</html>
