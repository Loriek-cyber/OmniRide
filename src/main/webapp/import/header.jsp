<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/Images/logo.png" alt="Omniride Logo">
        </a>
    </div>

    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/ricercaPercorsi">Ricerca Percorsi</a>
        <a href="${pageContext.request.contextPath}/biglietti.jsp">Biglietti</a>
        <a href="${pageContext.request.contextPath}/visualizzaTratte">Tratte</a>
        <a href="${pageContext.request.contextPath}/trattePreferiti">Tratte Preferite</a>
        <a href="${pageContext.request.contextPath}/visualizzaAvvisi">Avvisi</a>
        <a href="${pageContext.request.contextPath}/faq">FAQ</a>
    </nav>

    <div class="user-actions">
        <c:choose>
            <%-- Caso 1: L'utente è loggato --%>
            <c:when test="${not empty sessionScope.utente}">
                <a href="${pageContext.request.contextPath}/wallet" class="btnHeader-icon" title="Portafoglio">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M21 8V18C21 19.1046 20.1046 20 19 20H5C3.89543 20 3 19.1046 3 18V6C3 4.89543 3.89543 4 5 4H17C18.1046 4 19 4.89543 19 6V8H21ZM19 8H17V6H5V18H19V8Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M17 10H15C13.8954 10 13 10.8954 13 12C13 13.1046 13.8954 14 15 14H17V10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </a>
                <a href="${pageContext.request.contextPath}/carrello" class="btnHeader-icon" title="Carrello">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V17C17 18.1 16.1 19 15 19H9C7.9 19 7 18.1 7 17V13M17 13H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span class="badge">${not empty sessionScope.carrello ? sessionScope.carrello.size() : 0}</span>
                </a>

                <!-- Dropdown utente -->
                <div class="user-dropdown">
                    <a href="#" class="user-dropdown-btn" onclick="toggleDropdown(event)">
                        <c:choose>
                            <c:when test="${not empty sessionScope.utente.avatar}">
                                <img src="${pageContext.request.contextPath}/AvatarServlet?userId=${sessionScope.utente.id}"
                                     alt="User Avatar" class="user-avatar">
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${not empty sessionScope.utente.nome and not empty sessionScope.utente.cognome}">
                                        <img src="https://ui-avatars.com/api/?name=${sessionScope.utente.nome}+${sessionScope.utente.cognome}&size=32&background=007bff&color=fff&font-size=0.33"
                                             alt="User Avatar" class="user-avatar">
                                    </c:when>
                                    <c:when test="${not empty sessionScope.utente.nome}">
                                        <img src="https://ui-avatars.com/api/?name=${sessionScope.utente.nome}&size=32&background=007bff&color=fff&font-size=0.33"
                                             alt="User Avatar" class="user-avatar">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://ui-avatars.com/api/?name=User&size=32&background=6c757d&color=fff&font-size=0.33"
                                             alt="Default Avatar" class="user-avatar">
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </a>

                    <div class="dropdown-menu" id="userDropdown">
                        <div class="dropdown-header">
                            Ciao, ${sessionScope.utente.nome}!
                        </div>
                        <a href="${pageContext.request.contextPath}/prvUser/dashboard.jsp" class="dropdown-item">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M20 21V19C20 17.9 19.1 17 18 17H6C4.9 17 4 17.9 4 19V21M16 7C16 9.2 14.2 11 12 11C9.8 11 8 9.2 8 7C8 4.8 9.8 3 12 3C14.2 3 16 4.8 16 7Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/wallet" class="dropdown-item">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M21 8V18C21 19.1046 20.1046 20 19 20H5C3.89543 20 3 19.1046 3 18V6C3 4.89543 3.89543 4 5 4H17C18.1046 4 19 4.89543 19 6V8H21ZM19 8H17V6H5V18H19V8Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M17 10H15C13.8954 10 13 10.8954 13 12C13 13.1046 13.8954 14 15 14H17V10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Portafoglio
                        </a>
                        <a href="${pageContext.request.contextPath}/carrello" class="dropdown-item">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V17C17 18.1 16.1 19 15 19H9C7.9 19 7 18.1 7 17V13M17 13H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Carrello (${not empty sessionScope.carrello ? sessionScope.carrello.size() : 0})
                        </a>
                        <c:if test="${sessionScope.utente.ruolo == 'admin'}">
                            <a href="${pageContext.request.contextPath}/prvAdmin/admin.jsp" class="dropdown-item admin">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
                                    <path d="M8 12L10.5 14.5L16 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                                Pannello Admin
                            </a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M9 21H5C4.44772 21 4 20.5523 4 20V4C4 3.44772 4.44772 3 5 3H9M16 17L21 12L16 7M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Logout
                        </a>
                    </div>
                </div>
            </c:when>

            <%-- Caso 2: L'utente non è loggato --%>
            <c:otherwise>
                <!-- Pulsante portafoglio per utenti non loggati -->
                <a href="${pageContext.request.contextPath}/wallet" class="btnHeader-icon" title="Portafoglio">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M21 8V18C21 19.1046 20.1046 20 19 20H5C3.89543 20 3 19.1046 3 18V6C3 4.89543 3.89543 4 5 4H17C18.1046 4 19 4.89543 19 6V8H21ZM19 8H17V6H5V18H19V8Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M17 10H15C13.8954 10 13 10.8954 13 12C13 13.1046 13.8954 14 15 14H17V10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </a>
                <a href="${pageContext.request.contextPath}/carrello" class="btnHeader-icon" title="Carrello">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V17C17 18.1 16.1 19 15 19H9C7.9 19 7 18.1 7 17V13M17 13H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span class="badge">${not empty sessionScope.carrello ? sessionScope.carrello.size() : 0}</span>
                </a>
                <a href="${pageContext.request.contextPath}/login" class="btnHeader">Login</a>
                <a href="${pageContext.request.contextPath}/register" class="btnHeader">Registrati</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>
