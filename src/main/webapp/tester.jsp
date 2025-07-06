<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Tratta" %>
<%@ page import="model.dao.TrattaDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="error.ErrorPage" %><%--
  Created by IntelliJ IDEA.
  User: Arjel
  Date: 06/07/2025
  Time: 14:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    List<Tratta> tratte;
    try {
        tratte = TrattaDAO.getAllTratte();
    } catch (SQLException e) {
        request.setAttribute("error", new ErrorPage(500,"Errore nel databse"));
        request.getRequestDispatcher("/error").forward(request,response);
    }
%>


<head>
    <title>Tester</title>
</head>
<body>

    <p>

        <%
            for (Tratta tratta : tratte) {
                out.println(tratta.MatrixHour());
            }
        %>

    </p>

</body>
</html>
