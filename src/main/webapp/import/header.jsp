<!-- Header principale del sito -->
<header class="main-header">
    <!-- Sezione sinistra: Logo e navigazione principale -->
    <div class="header-left">
        <!-- Logo del sito che funge da link alla homepage -->
        <div class="logo">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/Images/logo.png" alt="Omniride Logo">
            </a>
        </div>
        <!-- Navigazione principale -->
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/tratte.jsp">Tratte</a>
            <a href="${pageContext.request.contextPath}/biglietti.jsp">Biglietti</a>
            <a href="#">Abbonamenti</a>
            <a href="#">Servizi</a>
        </nav>
    </div>

    <!-- Sezione destra: Azioni utente -->
    <div class="header-right">
        <!-- Pulsante Portafoglio - Visibile solo per utenti loggati -->
        <c:if test="${not empty sessionScope.utente}">
            <a href="${pageContext.request.contextPath}/portafoglio" class="header-button wallet-button">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 12V8H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h12v4"/>
                    <path d="M4 6v12h12a2 2 0 0 0 2-2V8"/>
                    <path d="M18 12a2 2 0 0 0-2 2h-4a2 2 0 0 0-2-2v-2a2 2 0 0 0 2-2h4a2 2 0 0 0 2 2z"/>
                </svg>
                <span>Portafoglio</span>
            </a>
        </c:if>

        <!-- Pulsante Carrello - Visibile solo per utenti loggati -->
        <c:if test="${not empty sessionScope.utente}">
            <a href="${pageContext.request.contextPath}/carrello" class="header-button icon-button">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="9" cy="21" r="1"/>
                    <circle cx="20" cy="21" r="1"/>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                </svg>
                <!-- Badge per il numero di elementi nel carrello (opzionale) -->
                <c:if test="${not empty sessionScope.carrello and sessionScope.carrello.numeroElementi > 0}">
                    <span class="cart-badge">${sessionScope.carrello.numeroElementi}</span>
                </c:if>
            </a>
        </c:if>
    </div>
</header>