<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.dao.*" %>
<%@ page import="model.sdata.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<!DOCTYPE html>
<html>
<head>
    <title>Database Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .success { color: green; }
        .error { color: red; }
        .info { background: #f0f0f0; padding: 10px; margin: 10px 0; }
    </style>
</head>
<body>
    <h1>Database Connection Test</h1>
    
    <%
    try {
        // Test Fermate
        out.println("<h2>Testing Fermate (Stops):</h2>");
        List<Fermata> fermate = FermataDAO.getAll();
        out.println("<p class='success'>✓ Database connection successful</p>");
        out.println("<p class='info'>Number of Fermate: " + fermate.size() + "</p>");
        if (!fermate.isEmpty()) {
            out.println("<p>Sample fermate:</p><ul>");
            for (int i = 0; i < Math.min(5, fermate.size()); i++) {
                Fermata f = fermate.get(i);
                out.println("<li>" + f.getNome() + " - " + f.getIndirizzo() + "</li>");
            }
            out.println("</ul>");
        }
        
        // Test Tratte
        out.println("<h2>Testing Tratte (Routes):</h2>");
        List<Tratta> tratte = TrattaDAO.getAll();
        out.println("<p class='info'>Number of Tratte: " + tratte.size() + "</p>");
        if (!tratte.isEmpty()) {
            out.println("<p>Sample tratte:</p><ul>");
            for (int i = 0; i < Math.min(3, tratte.size()); i++) {
                Tratta t = tratte.get(i);
                out.println("<li>" + t.getNome() + " - Costo: €" + t.getCosto() + "</li>");
            }
            out.println("</ul>");
        }
        
        // Test context attributes
        out.println("<h2>Testing Application Context:</h2>");
        List<Fermata> contextFermate = (List<Fermata>) application.getAttribute("fermate");
        if (contextFermate != null) {
            out.println("<p class='success'>✓ Fermate loaded in application context: " + contextFermate.size() + "</p>");
        } else {
            out.println("<p class='error'>✗ Fermate NOT found in application context</p>");
        }
        
    } catch (SQLException e) {
        out.println("<p class='error'>Database Error: " + e.getMessage() + "</p>");
        out.println("<pre>");
        e.printStackTrace(new java.io.PrintWriter(out));
        out.println("</pre>");
    } catch (Exception e) {
        out.println("<p class='error'>General Error: " + e.getMessage() + "</p>");
        out.println("<pre>");
        e.printStackTrace(new java.io.PrintWriter(out));
        out.println("</pre>");
    }
    %>
    
    <h2>Quick Actions:</h2>
    <ul>
        <li><a href="${pageContext.request.contextPath}/">Go to Home</a></li>
        <li><a href="${pageContext.request.contextPath}/cercaTratte?partenza=Test&arrivo=Test&orario=10:00&data=2024-01-20">Test Search (will fail)</a></li>
    </ul>
</body>
</html>
