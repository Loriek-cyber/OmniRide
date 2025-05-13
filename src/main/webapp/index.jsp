<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>OmniRide</title>
</head>
<body>

<!-- Simple search bar for the application -->
<form action="Tester" method="get">
    <input type="text" name="query" placeholder="Search...">
    <input type="submit" value="Search">
</form>

<!-- Display the result of the database connection check -->
<% 
    String connectionMessage = (String) request.getAttribute("connectionMessage");
    if (connectionMessage != null) {
        out.println("<h2>" + connectionMessage + "</h2>");
    }
%>

</body>
</html>
