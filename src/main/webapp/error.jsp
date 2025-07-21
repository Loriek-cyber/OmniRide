<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/import/metadata.jsp"/>
    <title>Errore - OmniRide</title>
    <style>
        /* Error page styles */
        .error-page {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 1rem;
            text-align: center;
        }

        .error-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2rem;
            margin: 2rem 0;
        }

        .error-icon {
            font-size: 4rem;
            color: #e74c3c;
            margin-bottom: 1rem;
        }

        .error-title {
            font-size: 2rem;
            font-weight: 600;
            color: #2d3436;
            margin-bottom: 1rem;
        }

        .error-message {
            font-size: 1.1rem;
            color: #636e72;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .error-details {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 2rem;
            text-align: left;
            border-left: 4px solid #e74c3c;
        }

        .error-details h4 {
            margin-top: 0;
            color: #e74c3c;
        }

        .error-details p {
            margin-bottom: 0.5rem;
            font-family: monospace;
            font-size: 0.9rem;
            color: #2d3436;
        }

        .error-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 0.8rem 1.5rem;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-primary {
            background: #32cd32;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background: #28a428;
            transform: translateY(-1px);
        }

        .btn-secondary {
            background: #f8f9fa;
            color: #2d3436;
            border: 1px solid #ddd;
        }

        .btn-secondary:hover {
            background: #e9ecef;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .error-container {
                padding: 2rem 1rem;
            }
            
            .error-title {
                font-size: 1.5rem;
            }
            
            .error-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/import/header.jsp"/>

<main class="error-page">
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        
        <h1 class="error-title">
            <c:choose>
                <c:when test="${not empty error}">
                    Errore nell'applicazione
                </c:when>
                <c:otherwise>
                    Si è verificato un errore
                </c:otherwise>
            </c:choose>
        </h1>
        
        <div class="error-message">
            <c:choose>
                <c:when test="${not empty error}">
                    Si è verificato un problema durante l'elaborazione della tua richiesta.
                </c:when>
                <c:otherwise>
                    La pagina che stai cercando non è disponibile o si è verificato un errore imprevisto.
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${not empty error}">
            <div class="error-details">
                <h4>Dettagli dell'errore:</h4>
                <p>${error}</p>
            </div>
        </c:if>

        <c:if test="${not empty requestScope['javax.servlet.error.message']}">
            <div class="error-details">
                <h4>Messaggio di sistema:</h4>
                <p>${requestScope['javax.servlet.error.message']}</p>
            </div>
        </c:if>

        <div class="error-actions">
            <a href="javascript:history.back()" class="btn btn-secondary">
                ← Torna indietro
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                🏠 Torna alla Home
            </a>
        </div>
    </div>

    <div style="margin-top: 2rem; padding: 2rem; background: #f8f9fa; border-radius: 8px;">
        <h3>Cosa puoi fare:</h3>
        <ul style="text-align: left; max-width: 500px; margin: 1rem auto;">
            <li>Controlla che l'URL sia corretto</li>
            <li>Riprova più tardi se il problema persiste</li>
            <li>Contatta il supporto se necessario</li>
            <li>Torna alla pagina principale per continuare la navigazione</li>
        </ul>
    </div>
</main>

<jsp:include page="/import/footer.jsp"/>
</body>
</html>
