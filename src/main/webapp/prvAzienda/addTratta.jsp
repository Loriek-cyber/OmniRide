<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Fermata" %>
<!DOCTYPE html>
<html>
<head>
    <title>Aggiungi Tratta - Omniride</title>
    <%@ include file="../import/metadata.jsp" %>
</head>
<%-- Mostra un messaggio di errore se esiste --%>
<%
    String errore = (String) request.getAttribute("errore");
    if (errore == null) {
        errore = (String) session.getAttribute("errore");
        session.removeAttribute("errore"); // rimuove dopo averlo mostrato
    }
%>

<% if (errore != null) { %>
<div style="color: red; background-color: #ffe6e6; border: 1px solid red; padding: 10px; margin: 10px 0; text-align: center;">
    <strong><%= errore %></strong>
</div>
<% } %>
<body>
    <div class="add-tratta-container">
        <a href="../dashboard" class="back-link">← Torna alla Dashboard</a>
        <h1 class="add-tratta-title">Aggiungi Nuova Tratta</h1>

        <form action="addTratta" method="post" id="addTrattaForm">
            <!-- Informazioni Base -->
            <div class="form-section">
                <h2>Informazioni Base</h2>
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
            </div>

            <!-- Percorso Tratta -->
            <div class="form-section">
                <h2>Percorso Tratta</h2>
                <div class="help-text">Aggiungi le fermate in ordine di percorrenza. Puoi cercare per nome o indirizzo.</div>

                <div class="fermate-selected-container">
                    <div class="fermata-search-container">
                        <div class="search-box">
                            <input type="text" id="fermataSearch" class="search-input"
                                   placeholder="Cerca fermata per nome o indirizzo...">
                            <div class="search-results" id="searchResults"></div>
                        </div>
                        <button type="button" class="add-fermata-btn" id="addFermataBtn" disabled>
                            <span class="icon">+</span> Aggiungi Fermata
                        </button>
                    </div>

                    <div class="selected-fermate" id="selectedFermate">
                        <div class="empty-state" id="emptyState">
                            <p>Nessuna fermata selezionata</p>
                            <p class="help-text">Cerca e aggiungi almeno 2 fermate per creare la tratta</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Orari e Giorni -->
            <div class="form-section">
                <h2>Orari e Giorni</h2>
            <div class="form-group">
                <label>Orari di Inizio *:</label>
                <div class="orari-container" id="orariContainer">
                    <!-- Orari verranno aggiunti qui dinamicamente -->
                </div>
                <button type="button" class="add-orario-btn" id="addOrarioBtn">+	Aggiungi Orario</button>
                <div class="help-text">Aggiungi uno o pi&ugrave; orari di partenza per la tratta.</div>
            </div>

                <div class="form-group">
                    <label>Giorni di Servizio *:</label>
                    <div class="days-container">
                        <div class="day-item">
                            <input type="checkbox" id="lunedi" name="giorni" value="LUN">
                            <label for="lunedi">Lunedì</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="martedi" name="giorni" value="MAR">
                            <label for="martedi">Martedì</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="mercoledi" name="giorni" value="MER">
                            <label for="mercoledi">Mercoledì</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="giovedi" name="giorni" value="GIO">
                            <label for="giovedi">Giovedì</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="venerdi" name="giorni" value="VEN">
                            <label for="venerdi">Venerdì</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="sabato" name="giorni" value="SAB">
                            <label for="sabato">Sabato</label>
                        </div>
                        <div class="day-item">
                            <input type="checkbox" id="domenica" name="giorni" value="DOM">
                            <label for="domenica">Domenica</label>
                        </div>
                    </div>
                    <div class="help-text">Seleziona i giorni in cui la tratta è attiva</div>
                </div>
            </div>

            <!-- Dati Hidden per il form -->
            <input type="hidden" id="fermateSelezionate" name="fermateSelezionate" value="">
            <input type="hidden" id="tempiTraFermate" name="tempiTraFermate" value="">

            <button type="submit" class="submit-btn">Crea Tratta</button>
        </form>
    </div>

    <!-- Dati fermate per JavaScript -->
    <script>

        window.fermateData = [
            //Allora questo è un piccolo parser di informazioni da java a javascript
            <%
                List<Fermata> fermate = (List<Fermata>) request.getAttribute("fermate");
                if (fermate != null && !fermate.isEmpty()) {
                    for (int i = 0; i < fermate.size(); i++) {
                        Fermata fermata = fermate.get(i);
                        if (i > 0) out.print(",");
                        out.print("{");
                        out.print("id: " + fermata.getId() + ",");
                        out.print("nome: \"" + fermata.getNome().replace("\"", "\\\"") + "\",");
                        out.print("indirizzo: \"" + (fermata.getIndirizzo() != null ? fermata.getIndirizzo().replace("\"", "\\\"") : "") + "\",");
                        out.print("latitudine: " + (fermata.getLatitudine() != null ? fermata.getLatitudine() : "null") + ",");
                        out.print("longitudine: " + (fermata.getLongitudine() != null ? fermata.getLongitudine() : "null"));
                        out.print("}");
                    }
                }
            %>
        ];
    </script>

    <!-- Script per la gestione del form -->
    <script src="../Scripts/addTratta.js"></script>
</body>
</html>
