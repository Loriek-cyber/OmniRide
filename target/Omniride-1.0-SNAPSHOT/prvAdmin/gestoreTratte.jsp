<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Gestione Tratte</title>
    <jsp:include page="/import/metadata.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/Styles/form.css">
</head>
<body>
    <jsp:include page="/import/header.jsp"/>

    <main>
        <div id="content" class="content">
            <h2>Gestione Tratte</h2>
            <p>In questa sezione puoi visualizzare, aggiungere, modificare o eliminare le tratte del sistema.</p>

            <a href="#" class="btn btn-primary mb-3">Aggiungi Nuova Tratta</a>

            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome Tratta</th>
                        <th>Azienda</th>
                        <th>Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="tratta" items="${tratte}">
                        <tr>
                            <td>${tratta.id}</td>
                            <td>${tratta.nome}</td>
                            <td>${tratta.azienda.nome}</td>
                            <td>
                                <a href="#" class="btn btn-secondary btn-sm">Modifica</a>
                                <a href="#" class="btn btn-danger btn-sm">Elimina</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </main>

    <jsp:include page="/import/footer.jsp"/>
</body>
</html>
