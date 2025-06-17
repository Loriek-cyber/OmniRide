package com.omniride;
import net.data.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FirstServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String adr = request.getParameter("address"); // Use getParameter for URL parameters
		Coordinate cod = null;
		String errorMessage = null;

		if (adr == null || adr.trim().isEmpty()) {
			errorMessage = "Address parameter is missing or empty. Please provide an address (e.g., /hello?address=your_address_here).";
		} else {
			try {
				cod = GeoLock.getCoordinatesFromAddress(adr); // Corrected: removed extra parenthesis
			} catch (IllegalArgumentException e) {
				errorMessage = "Invalid address provided: " + e.getMessage();
				e.printStackTrace(); // For server logs
			} catch (Exception e) {
				errorMessage = "Error retrieving coordinates: " + e.getMessage();
				e.printStackTrace(); // For server logs
			}
		}

		String pageTitle = "Geocoding Result";
		request.setAttribute("pageTitle", pageTitle);

		if (errorMessage != null) {
			request.setAttribute("errorMessage", errorMessage);
		} else if (cod != null) {
			request.setAttribute("address", adr);
			request.setAttribute("coordinates", cod); // Pass the Coordinate object directly
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
