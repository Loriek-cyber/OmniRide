<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.sdata.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<html>
<head>
    <%@include file="import/metadata.jsp"%>
    <%
        boolean ison = false;
        List<Avvisi> avvisi = (List<Avvisi>) request.getAttribute("avvisi");
        Map<Long,Tratta> trattaMap = (Map<Long, Tratta>) request.getAttribute("tratteMap");
        if(avvisi!=null){
            ison = true;
            if(avvisi.isEmpty()){
                ison= false;
            }
        }
        int i = 0;
    %>
    <title>Avvisi - Omniride</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/avvisi.css">
</head>
<body>
    <%@include file="import/header.jsp"%>
    <main>
        <div class="avvisi-container">
            <h1>Avvisi e Comunicazioni</h1>
            
            <% if (avvisi == null || trattaMap == null) { %>
                <div class="error-message">
                    <strong>Errore:</strong> Impossibile caricare i dati degli avvisi. Riprova più tardi.
                </div>
            <% } else if (!ison || avvisi.isEmpty()) { %>
                <div class="no-avvisi">
                    <h2>Nessun avviso al momento</h2>
                    <p>Non ci sono avvisi o comunicazioni da visualizzare.</p>
                </div>
            <% } else { %>
                <% for (Avvisi avviso : avvisi) { %>
                <div class="avviso-card">
                    <div class="avviso-header">
                        <span class="avviso-numero">Avviso #<%= ++i %></span>
                    </div>
                    
                    <div class="avviso-descrizione">
                        <%= avviso.getDescrizione() != null ? avviso.getDescrizione() : "Descrizione non disponibile" %>
                    </div>
                    
                    <% if (avviso.getId_tratte_coinvolte() != null && !avviso.getId_tratte_coinvolte().isEmpty()) { %>
                    <div class="tratte-coinvolte">
                        <h4>Tratte coinvolte:</h4>
                        <% for (Long id : avviso.getId_tratte_coinvolte()) { %>
                            <% 
                                Tratta tratta = trattaMap.get(id);
                                if (tratta != null) {
                            %>
                                <div class="tratta-body">
                                    <%= tratta.getNome() %>
                                </div>
                            <% 
                                } else {
                            %>
                                <div class="tratta-body unavailable">
                                    Tratta non disponibile (ID: <%= id %>)
                                </div>
                            <% 
                                }
                            %>
                        <% } %>
                    </div>
                    <% } else { %>
                        <div class="tratte-coinvolte">
                            <p><em>Nessuna tratta specificata per questo avviso.</em></p>
                        </div>
                    <% } %>
                </div>
                <% } %>
            <% } %>
        </div>
    </main>
    <%@include file="import/footer.jsp"%>
</body>
</html>