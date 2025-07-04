<%@ page import="model.sdata.Tratta" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="model.dao.TrattaDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    List<Tratta> tratte = null;
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
    <div class="tratte-container">
        <table class="tratte-table">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Azienda</th>
                    <th>Costo</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="tratta" items="${tratte}">
                <tr>
                    <td>${tratta.nome}</td>
                    <td>${tratta.azienda.getNome()}</td>
                    <td>${tratta.costo}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>

<jsp:include page="import/footer.jsp" />
<script>
    function toggleAddForm() {
        const form = document.getElementById('form-add-tratta');
        form.classList.toggle('active');
    }
</script>
</body>
</html>
