<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Images/logo.png" alt="Omniride Logo">
        </a>
    </div>
    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/biglietti.jsp">Biglietti</a>
        <a href="${pageContext.request.contextPath}/tratte.jsp">Tratte</a>
        <a href="${pageContext.request.contextPath}/about.jsp">Chi Siamo</a>
    </nav>
    <div class="user-actions">
        <c:choose>
            <%-- Caso 1: L'utente è loggato --%>
            <c:when test="${not empty sessionScope.utente}">
                <span>
                <a href="${pageContext.request.contextPath}/prvUser/dashboard.jsp" class="user-avatar-link">
                    <c:choose>
                        <c:when test="${not empty sessionScope.utente.avatar}">
                            <img src="${pageContext.request.contextPath}/AvatarServlet?userId=${sessionScope.utente.id}" alt="User Avatar" class="user-avatar">
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/Images/default_avatar.png" alt="Default Avatar" class="user-avatar">
                        </c:otherwise>
                    </c:choose>
                </a>


                <%-- Mostra il link Admin solo se l'utente ha il ruolo 'admin' --%>
                <c:if test="${sessionScope.utente.ruolo == 'admin'}">
                    <a href="${pageContext.request.contextPath}/prvAdmin/admin.jsp" class="btn btn-danger">Area Admin</a>
                </c:if>

                <a href="${pageContext.request.contextPath}/logout" class="btnHeader">Logout</a>
            </span>
            </c:when>


            <%-- Caso 2: L'utente non è loggato --%>
            <c:otherwise>
                <span>
                <a href="${pageContext.request.contextPath}/login" class="btnHeader">Login</a>
                <a href="${pageContext.request.contextPath}/register" class="btnHeader">Registrati</a>
                </span>
            </c:otherwise>
        </c:choose>
    </div>
</header>
