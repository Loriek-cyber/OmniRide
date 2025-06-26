<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<%
    String error = (String) request.getAttribute("error");
    String searchTerm = (String) request.getAttribute("searchTerm");
    String selectedCategory = (String) request.getAttribute("selectedCategory");
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<String> categories = (List<String>) request.getAttribute("categories");
    
    if (searchTerm == null) searchTerm = "";
    if (selectedCategory == null) selectedCategory = "";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Prodotti - Omniride</title>
    <%@ include file="import/metadata.jsp" %>
</head>
<body>
<%@ include file="import/header.jsp" %>

<main>
    <div class="products-container">
        <h1>Catalogo Prodotti</h1>
        
        <% if (error != null) { %>
            <div class="error-message">
                <%= error %>
            </div>
        <% } %>
        
        <!-- Search and Filter Section -->
        <div class="search-filter-section">
            <form action="products" method="get" class="search-form">
                <div class="search-group">
                    <input type="text" name="search" placeholder="Cerca prodotti..." 
                           value="<%= searchTerm %>" class="search-input">
                    <button type="submit" class="btn btn-search">Cerca</button>
                </div>
            </form>
            
            <form action="products" method="get" class="filter-form">
                <div class="filter-group">
                    <label for="category">Categoria:</label>
                    <select name="category" id="category" onchange="this.form.submit()">
                        <option value="">Tutte le categorie</option>
                        <% if (categories != null) {
                            for (String category : categories) { %>
                                <option value="<%= category %>" 
                                    <%= category.equals(selectedCategory) ? "selected" : "" %>>
                                    <%= category %>
                                </option>
                        <%  }
                        } %>
                    </select>
                </div>
            </form>
            
            <% if (!searchTerm.isEmpty() || !selectedCategory.isEmpty()) { %>
                <div class="active-filters">
                    <% if (!searchTerm.isEmpty()) { %>
                        <span class="filter-tag">Ricerca: "<%= searchTerm %>"</span>
                    <% } %>
                    <% if (!selectedCategory.isEmpty()) { %>
                        <span class="filter-tag">Categoria: <%= selectedCategory %></span>
                    <% } %>
                    <a href="products" class="clear-filters">Rimuovi filtri</a>
                </div>
            <% } %>
        </div>
        
        <!-- Products Grid -->
        <div class="products-grid">
            <% if (products != null && !products.isEmpty()) {
                for (Product product : products) { %>
                    <div class="product-card">
                        <% if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) { %>
                            <img src="<%= product.getImageUrl() %>" alt="<%= product.getName() %>" class="product-image">
                        <% } else { %>
                            <div class="product-image-placeholder">
                                <span>Nessuna immagine</span>
                            </div>
                        <% } %>
                        
                        <div class="product-info">
                            <h3 class="product-name"><%= product.getName() %></h3>
                            <p class="product-description"><%= product.getDescription() %></p>
                            <p class="product-category">Categoria: <%= product.getCategory() %></p>
                            <p class="product-price"><%= product.getFormattedPrice() %></p>
                            
                            <% if (product.isAvailable()) { %>
                                <p class="product-stock">Disponibili: <%= product.getStockQuantity() %></p>
                                <form action="cart" method="post" class="add-to-cart-form">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="<%= product.getId() %>">
                                    <div class="quantity-group">
                                        <label for="quantity_<%= product.getId() %>">Quantità:</label>
                                        <input type="number" name="quantity" id="quantity_<%= product.getId() %>" 
                                               value="1" min="1" max="<%= product.getStockQuantity() %>" class="quantity-input">
                                    </div>
                                    <button type="submit" class="btn btn-primary">Aggiungi al carrello</button>
                                </form>
                            <% } else { %>
                                <p class="product-unavailable">Non disponibile</p>
                                <button class="btn btn-disabled" disabled>Non disponibile</button>
                            <% } %>
                        </div>
                    </div>
            <%  }
            } else { %>
                <div class="no-products">
                    <h3>Nessun prodotto trovato</h3>
                    <p>Non ci sono prodotti che corrispondono ai criteri di ricerca.</p>
                    <a href="products" class="btn btn-secondary">Visualizza tutti i prodotti</a>
                </div>
            <% } %>
        </div>
    </div>
</main>

<%@ include file="import/footer.jsp" %>

<script>
// Add to cart with AJAX (optional enhancement)
document.querySelectorAll('.add-to-cart-form').forEach(form => {
    form.addEventListener('submit', function(e) {
        const quantity = this.querySelector('input[name="quantity"]').value;
        const maxQuantity = this.querySelector('input[name="quantity"]').max;
        
        if (parseInt(quantity) > parseInt(maxQuantity)) {
            e.preventDefault();
            alert('Quantità non disponibile in magazzino');
        }
    });
});

// Search form validation
document.querySelector('.search-form').addEventListener('submit', function(e) {
    const searchInput = this.querySelector('input[name="search"]');
    if (searchInput.value.trim().length < 2) {
        e.preventDefault();
        alert('Inserisci almeno 2 caratteri per la ricerca');
    }
});
</script>

</body>
</html>