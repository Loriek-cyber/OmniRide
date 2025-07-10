<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="avvisi-container">
  <h2>Avvisi</h2>

  <c:choose>
    <c:when test="${empty avvisi}">
      <p class="no-avvisi">Nessun avviso disponibile.</p>
    </c:when>
    <c:otherwise>
      <c:forEach var="avviso" items="${avvisi}">
        <div class="avviso-box">
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
