<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="model.dao.TrattaDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    List<Tratta> tratte;
    try{
        tratte = TrattaDAO.getAllTratte();
    }catch (SQLException e){
        e.printStackTrace();
    }

%>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="import/metadata.jsp" />
    <title>Gestione Tratte - Sistema di Trasporto Pubblico</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/tratte.css">
</head>
<body>
<jsp:include page="import/header.jsp" />

<main>
    <div class="container">
        <div class = "avvisi-result">
            <div>
                <h1>Tratte</h1>
            </div>

            <table border="1" cellspacing="0" cellpadding="8">
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Azienda</th>
                    <th>Costo</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (Tratta tratta : tratte) {
                %>
                <tr>
                    <td><%= tratta.getNome() %></td>
                    <td><%= tratta.getAzienda().getNome() %></td>
                    <td><%= tratta.getCosto()%>
                </tbody>
                <%}%>
            </table>
        </div>
    </div>
</main>

<jsp:include page="import/footer.jsp" />
</body>
</html>
