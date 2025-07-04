
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <%@ include file="import/metadata.jsp" %>
    <link rel="stylesheet" href="Styles/search-form.css">
    <link rel="stylesheet" href="Styles/home.css">
</head>
<body>
    <%@ include file="import/header.jsp" %>

    <!-- Contenuto principale della homepage -->
    <main class="homepage-main">

        <!-- Colonna sinistra (per l'immagine futura) -->
        <div class="left-column">
            <!-- Qui andrà l'immagine -->
        </div>

        <!-- Colonna destra (contenuto principale) -->
        <div class="right-column">
            <!-- Box bianco per il contenuto -->
            <div class="content-box">
                <!-- Form di ricerca tratta -->
                <div class="search-section">
                    <form method="get" action="TratteServlet" class="search-form">
                        <h2>Cerca la tua tratta</h2>
                        <div class="search-container">
                            <div class="input-group-wrapper">
                                <span class="input-label">Da</span>
                                <input type="text" id="start" name="start" placeholder="Milano Centrale" required>
                            </div>
                            <div class="separator">
                                <a href="#" id="swap-button" title="Inverti partenza e destinazione">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="swap-icon"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>
                                </a>
                            </div>
                            <div class="input-group-wrapper">
                                <span class="input-label">A</span>
                                <input type="text" id="end" name="end" placeholder="Roma Termini" required>
                            </div>
                        </div>
                        <button type="submit" class="btn search-btn">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                            <span>Cerca</span>
                        </button>
                    </form>
                </div>

                <!-- Sezione Registrazione -->
                <div class="register-prompt">
                    <p>Non sei ancora registrato? <a href="${pageContext.request.contextPath}/register">Registrati ora</a>!</p>
                </div>

                <!-- Pulsante Avvisi -->
                <div class="alerts-section">
                    <a href="#" class="btn alert-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                        <span>AVVISI</span>
                    </a>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="import/footer.jsp" %>

    <script>
        // Script per invertire partenza e destinazione
        document.getElementById('swap-button').addEventListener('click', function(event) {
            event.preventDefault();
            var startInput = document.getElementById('start');
            var endInput = document.getElementById('end');
            var temp = startInput.value;
            startInput.value = endInput.value;
            endInput.value = temp;
        });
    </script>
</body>
</html>