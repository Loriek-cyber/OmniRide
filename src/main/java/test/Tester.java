package test;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import utils.DBConnector;  // Import the DBConnector class

@WebServlet("/Tester")
public class Tester extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Tester() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        Connection connection = null;
        try {
            // Use the DBConnector to get a connection
            connection = DBConnector.getConnection();
            
            // Check if the connection is successful
            if (connection != null) {
                out.println("<h1>Database connection successful!</h1>");
            } else {
                out.println("<h1>Failed to connect to the database!</h1>");
            }
        } catch (SQLException e) {
            out.println("<h1>Error: Unable to connect to the database!</h1>");
            e.printStackTrace();
        } finally {
            // Close the connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
