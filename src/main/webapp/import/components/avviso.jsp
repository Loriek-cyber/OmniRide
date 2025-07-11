<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty avvisi}">
    <div class="avvisi-container">
        <h3>Avvisi Recenti</h3>
        <c:forEach var="avviso" items="${avvisi}">
            <div class="avviso-item">
                <p>${avviso.descrizione}</p>
            </div>
        </c:forEach>
    </div>
</c:if>
