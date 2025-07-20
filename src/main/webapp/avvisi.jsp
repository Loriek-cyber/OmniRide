<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="model.dao.*" %>
<%@page import="model.sdata.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.HashMap" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Avvisi - Omniride</title>
    <jsp:include page="import/metadata.jsp"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .avvisi-container {
            max-width: 1000px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            min-height: calc(100vh - 200px);
        }

        .avvisi-header {
            text-align: center;
            margin-bottom: 3rem;
        }

        .avvisi-header h1 {
            color: #1f2937;
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            font-weight: 700;
        }

        .avvisi-header p {
            color: #6b7280;
            font-size: 1.1rem;
        }

        .avviso-box {
            background: #f8f9fa;
            border: 2px solid #e5e7eb;
            border-left: 4px solid #dc3545;
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            transition: all 0.3s ease;
        }

        .avviso-box:hover {
            border-color: #dc3545;
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.1);
        }

        .avviso-box.info {
            border-left-color: #0dcaf0;
        }

        .avviso-box.info:hover {
            border-color: #0dcaf0;
            box-shadow: 0 4px 12px rgba(13, 202, 240, 0.1);
        }

        .avviso-box.warning {
            border-left-color: #ffc107;
        }

        .avviso-box.warning:hover {
            border-color: #ffc107;
            box-shadow: 0 4px 12px rgba(255, 193, 7, 0.1);
        }

        .avviso-icon {
            display: inline-flex;
            align-items: center;
            margin-bottom: 1rem;
            font-size: 1.1rem;
            font-weight: 600;
            color: #dc3545;
        }

        .avviso-icon i {
            margin-right: 0.5rem;
        }

        .descrizione {
            color: #1f2937;
            font-size: 1rem;
            line-height: 1.6;
            margin-bottom: 1rem;
        }

        .coinvolte {
            color: #6b7280;
            font-size: 0.9rem;
            background: #e5e7eb;
            padding: 0.75rem;
            border-radius: 8px;
        }

        .coinvolte strong {
            color: #1f2937;
        }

        .no-avvisi {
            text-align: center;
            padding: 3rem;
            color: #6b7280;
        }

        .no-avvisi i {
            font-size: 3rem;
            color: #d1d5db;
            margin-bottom: 1rem;
            display: block;
        }

        @media (max-width: 768px) {
            .avvisi-container {
                margin: 1rem;
                padding: 1rem;
            }
            
            .avvisi-header h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="import/header.jsp"/>
    
    <main>
        <div class="avvisi-container">
            <div class="avvisi-header">
                <h1><i class="fas fa-exclamation-triangle"></i> Avvisi e Comunicazioni</h1>
                <p>Informazioni importanti sui nostri servizi</p>
            </div>
            
            <c:choose>
                <c:when test="${empty avvisi}">
                    <div class="no-avvisi">
                        <i class="fas fa-info-circle"></i>
                        <h3>Nessun avviso al momento</h3>
                        <p>Non ci sono avvisi o comunicazioni da mostrare.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="avviso" items="${avvisi}">
                        <div class="avviso-box">
                            <div class="avviso-icon">
                                <i class="fas fa-exclamation-triangle"></i>
                                Comunicazione Importante
                            </div>
                            
                            <p class="descrizione">${avviso.descrizione}</p>
        
                            <c:if test="${not empty avviso.id_tratte_coinvolte}">
                                <p class="coinvolte">
                                    <strong>Tratte coinvolte:</strong>
                                    <c:forEach var="id" items="${avviso.id_tratte_coinvolte}" varStatus="loop">
                                        ${id}<c:if test="${!loop.last}">, </c:if>
                                    </c:forEach>
                                </p>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
    
    <jsp:include page="import/footer.jsp"/>
</body>
</html>
