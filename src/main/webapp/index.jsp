<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to Omniride</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Welcome to Omniride</h1>
            <p class="subtitle">Your gateway to seamless ride searches</p>
        </header>
        
        <main class="main-content">
            <form action="Research" class="search-form" method="get">
                <input type="text" name="search" placeholder="Search for rides..." required>
            </form>
        </main>
        
        <footer>
            <p>&copy; 2025 Omniride. All rights reserved.</p>
        </footer>
    </div>
</body>
</html>