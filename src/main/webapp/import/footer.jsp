<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="footer">
    <div class="footer-bottom">
        <p id="info-owner">
            Proprietà privata di <strong>Arjel Buzi</strong> e <strong>Daniele Capentieri</strong>
        </p>

        <!-- Mostra il link solo se l'utente è loggato -->
        <c:if test="${empty sessionScope.utente}">
            <a class="register-link" href="${pageContext.request.contextPath}/registerAzienda">
                Registra azienda
            </a>
        </c:if>
    </div>
</footer>