package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.dao.ProductDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductCatalogServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String category = request.getParameter("category");
        String search = request.getParameter("search");
        
        try {
            ProductDAO productDAO = new ProductDAO();
            List<Product> products;
            
            if (search != null && !search.trim().isEmpty()) {
                // Search products
                products = productDAO.searchProducts(search.trim());
                request.setAttribute("searchTerm", search.trim());
            } else if (category != null && !category.trim().isEmpty()) {
                // Filter by category
                products = productDAO.getProductsByCategory(category);
                request.setAttribute("selectedCategory", category);
            } else {
                // Show all active products
                products = productDAO.getActiveProducts();
            }
            
            // Get all categories for filter dropdown
            List<String> categories = productDAO.getCategories();
            
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("products.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Errore nel caricamento dei prodotti: " + e.getMessage());
            request.getRequestDispatcher("products.jsp").forward(request, response);
        }
    }
}