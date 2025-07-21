<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="/import/metadata.jsp"/>
<html>
<head>
    <title>Storico Acquisti - I Miei Biglietti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/userSidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/storicoBiglietti.css">
</head>
<body class="user-dashboard-layout" data-page="storico">
    <c:if test="${not empty sessionScope.utente}">
        <jsp:include page="/import/userSidebar.jsp"/>
        <div class="user-main-content">
            <jsp:include page="/import/header.jsp"/>
            <main>
                <div class="content-header">
                    <h1>Storico Acquisti</h1>
                    <div class="breadcrumb">Area Utente / Storico Acquisti</div>
                </div>
                <div class="user-content">
                    <c:choose>
                        <c:when test="${not empty biglietti}">
                            <div class="tickets-list">
                                <c:forEach var="ticket" items="${biglietti}">
                                    <div class="ticket-card">
                                        <div class="ticket-info">
                                            <div class="ticket-route">
                                                <i class="fas fa-route"></i>
                                                <span>${ticket.nome}</span>
                                            </div>
                                            <div class="ticket-meta">
                                                <span class="ticket-type ${ticket.tipo}">${ticket.tipo}</span>
                                                <span class="ticket-date"><i class="fas fa-calendar"></i> <fmt:formatDate value="${ticket.dataAcquisto}" pattern="dd/MM/yyyy"/></span>
                                                <span class="ticket-status status-${ticket.stato}">${ticket.stato}</span>
                                            </div>
                                        </div>
                                        <div class="ticket-actions">
                                            <span class="ticket-price"> <fmt:formatNumber value="${ticket.prezzo}" type="currency" currencySymbol=" "/>
                                            </span>
                                            <button class="btn btn-primary" onclick="showTicketQR('${ticket.id}')">
                                                <i class="fas fa-qrcode"></i> QR
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-history">
                                <i class="fas fa-ticket-alt"></i>
                                <h3>Nessun biglietto acquistato</h3>
                                <p>Acquista un biglietto per vederlo qui!</p>
                                <a href="${pageContext.request.contextPath}/visualizzaTratte" class="btn btn-primary">Cerca Tratte</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
            <jsp:include page="/import/footer.jsp"/>
        </div>
    </c:if>
    <c:if test="${empty sessionScope.utente}">
        <jsp:include page="/import/header.jsp"/>
        <main>
            <div class="content">
                <p>Per accedere a questa pagina, devi prima effettuare il <a href="${pageContext.request.contextPath}/login">login</a>.</p>
            </div>
        </main>
    </c:if>
    <div id="qrModal" class="qr-modal" style="display:none;">
        <div class="qr-modal-content">
            <span class="close" onclick="closeQRModal()">&times;</span>
            <h2>QR Biglietto</h2>
            <div id="qrCodeContainer"></div>
            <button class="btn btn-secondary" onclick="downloadQR()"><i class="fas fa-download"></i> Scarica QR</button>
        </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
    <script>
    function showTicketQR(ticketId) {
        document.getElementById('qrCodeContainer').innerHTML = '';
        new QRCode(document.getElementById('qrCodeContainer'), {
            text: ticketId,
            width: 220,
            height: 220,
            colorDark: "#228b22",
            colorLight: "#fff",
            correctLevel: QRCode.CorrectLevel.H
        });
        document.getElementById('qrModal').style.display = 'flex';
    }
    function closeQRModal() {
        document.getElementById('qrModal').style.display = 'none';
    }
    function downloadQR() {
        var qrCanvas = document.querySelector('#qrCodeContainer canvas');
        if (qrCanvas) {
            var link = document.createElement('a');
            link.download = 'biglietto-qr.png';
            link.href = qrCanvas.toDataURL();
            link.click();
        }
    }
    </script>
</body>
</html> 