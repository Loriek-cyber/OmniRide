<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.sdata.Tratta" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Aggiungi Avviso - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/form.css">
</head>
<body>
    <div class="dashboard-layout">
        <main class="main-content">
            <div class="content-header">
                <h1>Aggiungi Avviso</h1>
                <div class="breadcrumb">Dashboard Azienda / Aggiungi Avviso</div>
            </div>
            <div class="form-container">
                <form action="${pageContext.request.contextPath}/add/addAvvisi" method="post">
                    <div class="form-group">
                        <label for="descrizione">Descrizione Avviso</label>
                        <textarea id="descrizione" name="descrizione" rows="4" required></textarea>
                    </div>
                    <div class="form-group">
                        <label for="tratte">Seleziona Tratte Coinvolte</label>
                        <select id="tratte" name="id_tratte" multiple required>
                            <c:forEach var="tratta" items="${tratte}">
                                <option value="${tratta.id}">${tratta.nome}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn">Aggiungi Avviso</button>
                </form>
            </div>
        </main>
    </div>
</body>
</html>