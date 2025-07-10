<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%
    // Gestione del codice di errore
    Integer errorCode = (Integer) request.getAttribute("errorCode");
    if (errorCode == null) {
        errorCode = response.getStatus();
        if (errorCode == 200) {
            errorCode = 500; // Default se non specificato
        }
    }
    
    // Gestione del messaggio di errore
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        if (errorMessage == null) {
            switch (errorCode) {
                case 400:
                    errorMessage = "Richiesta non valida";
                    break;
                case 401:
                    errorMessage = "Non autorizzato";
                    break;
                case 403:
                    errorMessage = "Accesso negato";
                    break;
                case 404:
                    errorMessage = "Pagina non trovata";
                    break;
                case 500:
                    errorMessage = "Errore interno del server";
                    break;
                case 503:
                    errorMessage = "Servizio non disponibile";
                    break;
                default:
                    errorMessage = "Si è verificato un errore imprevisto";
            }
        }
    }
    
    // Icona e colore basati sul codice di errore
    String iconClass = "fas fa-exclamation-triangle";
    String colorClass = "error-warning";
    
    if (errorCode == 404) {
        iconClass = "fas fa-search";
        colorClass = "error-not-found";
    } else if (errorCode == 403 || errorCode == 401) {
        iconClass = "fas fa-ban";
        colorClass = "error-forbidden";
    } else if (errorCode >= 500) {
        iconClass = "fas fa-server";
        colorClass = "error-server";
    }
%>
<html>
<head>
    <%@ include file="import/metadata.jsp" %>
    <title>Errore <%= errorCode %> - OmniRide</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .error-container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .error-card {
            background: white;
            border-radius: 20px;
            padding: 50px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 600px;
            width: 100%;
            animation: fadeIn 0.5s ease-in;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .error-icon {
            font-size: 80px;
            margin-bottom: 20px;
        }
        
        .error-warning { color: #f39c12; }
        .error-not-found { color: #3498db; }
        .error-forbidden { color: #e74c3c; }
        .error-server { color: #9b59b6; }
        
        .error-code {
            font-size: 72px;
            font-weight: bold;
            margin: 0;
            background: linear-gradient(45deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .error-message {
            font-size: 24px;
            color: #555;
            margin: 20px 0;
            font-weight: 300;
        }
        
        .error-description {
            font-size: 16px;
            color: #777;
            margin: 30px 0;
            line-height: 1.6;
        }
        
        .error-actions {
            margin-top: 40px;
        }
        
        .btn {
            display: inline-block;
            padding: 15px 30px;
            margin: 0 10px;
            text-decoration: none;
            border-radius: 50px;
            font-weight: 500;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
            font-size: 16px;
        }
        
        .btn-primary {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }
        
        .btn-secondary {
            background: #f8f9fa;
            color: #666;
            border: 2px solid #dee2e6;
        }
        
        .btn-secondary:hover {
            background: #e9ecef;
            border-color: #adb5bd;
        }
        
        .error-details {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
            border-left: 4px solid #667eea;
        }
        
        .error-details h4 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .error-details p {
            color: #666;
            margin: 5px 0;
            font-size: 14px;
        }
        
        @media (max-width: 768px) {
            .error-card {
                padding: 30px 20px;
            }
            
            .error-code {
                font-size: 48px;
            }
            
            .error-message {
                font-size: 20px;
            }
            
            .btn {
                display: block;
                margin: 10px 0;
            }
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <div class="error-icon <%= colorClass %>">
                <i class="<%= iconClass %>"></i>
            </div>
            
            <h1 class="error-code"><%= errorCode %></h1>
            <p class="error-message"><%= errorMessage %></p>
            
            <div class="error-description">
                <% if (errorCode == 404) { %>
                    La pagina che stai cercando non è stata trovata. Potrebbe essere stata spostata, eliminata o l'indirizzo potrebbe essere errato.
                <% } else if (errorCode == 403) { %>
                    Non hai i permessi necessari per accedere a questa risorsa. Effettua il login o contatta l'amministratore.
                <% } else if (errorCode == 500) { %>
                    Si è verificato un errore interno del server. Il nostro team è stato notificato e sta lavorando per risolvere il problema.
                <% } else if (errorCode == 503) { %>
                    Il servizio è temporaneamente non disponibile. Riprova tra qualche minuto.
                <% } else { %>
                    Si è verificato un errore imprevisto. Se il problema persiste, contatta il supporto tecnico.
                <% } %>
            </div>
            
            <div class="error-actions">
                <a href="<%= request.getContextPath() %>/" class="btn btn-primary">
                    <i class="fas fa-home"></i> Torna alla Home
                </a>
                <a href="javascript:history.back()" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Torna Indietro
                </a>
            </div>
            
            <% if (request.getParameter("debug") != null) { %>
            <div class="error-details">
                <h4><i class="fas fa-bug"></i> Dettagli Tecnici</h4>
                <p><strong>Codice:</strong> <%= errorCode %></p>
                <p><strong>URI:</strong> <%= request.getAttribute("javax.servlet.error.request_uri") %></p>
                <p><strong>Servlet:</strong> <%= request.getAttribute("javax.servlet.error.servlet_name") %></p>
                <p><strong>Timestamp:</strong> <%= new java.util.Date() %></p>
                <% 
                    Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
                    if (exception != null) {
                %>
                <p><strong>Eccezione:</strong> <%= exception.getClass().getSimpleName() %></p>
                <% } %>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
