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
        
        <main>
            <form action="Research" method="GET" class="search-form">
                <div class="form-group">
                    <label for="keyword">Search:</label>
                    <input type="text" id="keyword" name="keyword" placeholder="Enter a keyword" required>
                </div>
                
                <div class="form-group">
                    <label for="category">Category:</label>
                    <select id="category" name="category">
                        <option value="">-- Select a category --</option>
                        <option value="technology">Technology</option>
                        <option value="science">Science</option>
                        <option value="art">Art</option>
                        <option value="sport">Sport</option>
                        <option value="music">Music</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Sort Order:</label>
                    <div class="radio-group">
                        <input type="radio" id="sortAsc" name="sort" value="asc" checked>
                        <label for="sortAsc">Ascending</label>
                        
                        <input type="radio" id="sortDesc" name="sort" value="desc">
                        <label for="sortDesc">Descending</label>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="limit">Results Limit:</label>
                    <input type="number" id="limit" name="limit" min="1" max="100" value="10">
                </div>
                
                <div class="button-group">
                    <button type="submit" class="btn primary">Search</button>
                    <button type="reset" class="btn secondary">Reset</button>
                </div>
            </form>
        </main>
        
        <footer>
            <p>&copy; 2025 Omniride. All rights reserved.</p>
        </footer>
    </div>
</body>
</html>