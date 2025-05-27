<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to OmniRide</title>
    <jsp:include page="import/meta.jsp" />
</head>
<body>
    <jsp:include page="import/header.jsp" />
    <h1>Welcome to OmniRide</h1>
    
    <form action="/q" method="get" id="searchForm">
        
    	<input type="text" name="search" placeholder="Search for rides, vehicles, or users..." required>
    </form>

    <jsp:include page="import/footer.jsp" />
</body>
</html>