package control.error;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "error", value = "/error")
public class ErrorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String errorMessage = "Errore sconosciuto.";
        Integer errorCode = 500;
        if(request.getAttribute("errorCode") != null){
            errorCode = (Integer) request.getAttribute("errorCode");
        }
        if(request.getAttribute("errorMessage") != null){
            errorMessage = (String) request.getAttribute("errorMessage");
        }
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("errorCode", errorCode);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
