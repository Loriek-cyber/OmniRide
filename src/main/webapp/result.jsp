<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="net.data.Coordinate" %>
<%
    String pageTitle = (String) request.getAttribute("pageTitle");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String address = (String) request.getAttribute("address");
    Coordinate coordinates = (Coordinate) request.getAttribute("coordinates");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="style.css">
    <title><%= pageTitle != null ? pageTitle : "Geocoding Result" %></title>
</head>
<body>
    <h1><%= pageTitle != null ? pageTitle : "Geocoding Result" %></h1>

    <% if (errorMessage != null) { %>
        <p style="color:red;">Error: <%= errorMessage %></p>
    <% } else if (address != null && coordinates != null) { %>
        <p><strong>Address Searched:</strong> <%= address %></p>
        <p><strong>Coordinates:</strong></p>
        <ul>
            <li>Latitude: <%= coordinates.getLatitudine() %></li>
            <li>Longitude: <%= coordinates.getLongitudine() %></li>
        </ul>
    <% } else { %>
        <p>No address was processed, or no results found.</p>
    <% } %>

</body>
</html>