<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@include file="import/metadata.jsp" %>
    <title>Risultati</title>
</head>
<body>
<%@include file="import/header.jsp" %>

<main>
    <div id="results">
        <%
            //Sezione di reindirizzamenti in mancanza di dati
            String content = request.getParameter("content");
            if(content==null){
                RequestDispatcher ris = request.getRequestDispatcher("index.jsp");
                ris.forward(request,response);
            }

        %>
    </div>
</main>

<%@include file="import/footer.jsp" %>
</body>
</html>
