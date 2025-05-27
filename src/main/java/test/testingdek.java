package test;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import data.Fermata;
import data.Tratta;
import java.util.ArrayList;


@WebServlet("/testingdek")
public class testingdek extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public testingdek() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().append("<h1>Testing Servlet</h1>");
		
		
		Fermata f1 = new Fermata("Fermata 1", "001", "45.1234", "9.1234");
		Fermata f2 = new Fermata("Fermata 2", "002", "45.5678", "9.5678");
		Fermata f3 = new Fermata("Fermata 3", "003", "45.9101", "9.9101");
		
		Tratta tratta = new Tratta("TR001", "Tratta 1", new ArrayList<Fermata>());
		tratta.addFermata(f1);
		tratta.addFermata(f2);
		tratta.addFermata(f3);
		
		response.getWriter().append("<h2>Tratta Details</h2>");
		response.getWriter().append("<p>ID: ").append(tratta.toString()).append("</p>");
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
