<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="model.dao.*" %>
<%@page import="model.sdata.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.HashMap" %>
<html>
<head>
    <%
        List<Avvisi> avvisi;
        try {
            avvisi = AvvisiDAO.getAllAvvisi();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    %>
    <title>Avvisi</title>
    <%@ include file="import/metadata.jsp"%>
</head>
<body>
    <%@ include file="import/header.jsp"%>
    <main class="content">
        <div class = "avvisi-result">
            <div>
                <h1>Avvisi</h1>
            </div>

            <table border="1" cellspacing="0" cellpadding="8">
                <thead>
                <tr>
                    <th>Descrizione</th>
                    <th>Tratte Coinvolte</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (Avvisi avv : avvisi) {
                %>
                <tr>
                    <td><%= avv.getDescrizione() %></td>
                    <td>
                        <%
                            for (Long id : avv.getId_tratte_coinvolte()) {
                                String nomeTratta = TrattaDAO.getTrattaNameByID(id);
                        %>
                        <%= nomeTratta %><br>
                        <%
                            }
                        %>
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </main>
    <%@include file="import/footer.jsp"%>
</body>
</html>
