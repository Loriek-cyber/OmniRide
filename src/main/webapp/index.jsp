<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ricerca Informazioni</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Sistema di Ricerca</h1>
            <p class="subtitle">Inserisci i parametri per la tua ricerca</p>
        </header>
        
        <main>
            <form action="Research" method="GET" class="search-form">
                <div class="form-group">
                    <label for="keyword">Parola Chiave:</label>
                    <input type="text" id="keyword" name="keyword" placeholder="Inserisci una parola chiave" required>
                </div>
                
                <div class="form-group">
                    <label for="category">Categoria:</label>
                    <select id="category" name="category">
                        <option value="">-- Seleziona una categoria --</option>
                        <option value="tecnologia">Tecnologia</option>
                        <option value="scienza">Scienza</option>
                        <option value="arte">Arte</option>
                        <option value="sport">Sport</option>
                        <option value="musica">Musica</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Ordinamento:</label>
                    <div class="radio-group">
                        <input type="radio" id="sortAsc" name="sort" value="asc" checked>
                        <label for="sortAsc">Crescente</label>
                        
                        <input type="radio" id="sortDesc" name="sort" value="desc">
                        <label for="sortDesc">Decrescente</label>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="limit">Numero risultati:</label>
                    <input type="number" id="limit" name="limit" min="1" max="100" value="10">
                </div>
                
                <div class="button-group">
                    <button type="submit" class="btn primary">Cerca</button>
                    <button type="reset" class="btn secondary">Cancella</button>
                </div>
            </form>
            
            <% if(request.getParameter("keyword") != null) { %>
            <div class="results">
                <h2>Risultati della ricerca</h2>
                <p>Hai cercato: <%= request.getParameter("keyword") %></p>
                
                <% if(request.getAttribute("results") != null) { %>
                    <!-- I risultati verranno inseriti dalla servlet -->
                    <%= request.getAttribute("results") %>
                <% } else { %>
                    <p class="no-results">Nessun risultato trovato. Prova a modificare i parametri di ricerca.</p>
                <% } %>
            </div>
            <% } %>
        </main>
        
        <footer>
            <p>&copy; 2025 Sistema di Ricerca - Tutti i diritti riservati</p>
        </footer>
    </div>
</body>
</html>