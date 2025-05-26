<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Benvenuto in Omniride</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Benvenuto in Omniride</h1>
        </header>
        
        <main class="main-content">
            <form action="Research" class="search-form" method="get">
                <input type="text" name="search" placeholder="Cerca corse..." required>
                <button type="submit">Cerca</button>
            </form>
        </main>
        
        <footer>
            <p>&copy; 2025 Omniride. Tutti i diritti riservati.</p>
        </footer>
    </div>
</body>
</html>