<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to OmniRide</title>
    <jsp:include page="import/meta.jsp" />
</head>
<body>
    <jsp:include page="import/header.jsp" />
    <main>
    
    
    <div class="search-options">
    
    <form action="/q" method="get" id="searchForm">
        <label for="partenza">Partenza:</label>
    	<input type="text" name="partenza" placeholder="Patenza" required>
    	<label for="destinazione">Destinazione:</label>
    	<input type="text" name="destinazione" placeholder="Destinazione" required>
    	<button type="submit">Cerca</button>
    </form>
    
    </div>
    </main>
    <jsp:include page="import/footer.jsp" />
</body>
</html>