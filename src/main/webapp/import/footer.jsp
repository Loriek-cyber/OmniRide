<%@ page contentType="text/html;charset=UTF-8" %>

<footer class="footer">
    <div class="footer-bottom">
        <p id="info_owner">Proprieta privata di Arjel Buzi e Daniele Capentieri</p>
        <c:when test="${empty sessionScope.utente}">
            <a href="${pageContext.request.contextPath}/register/registerAzienda.jsp">Registra la tua Azienda</a>
        </c:when>
    </div>
</footer>
