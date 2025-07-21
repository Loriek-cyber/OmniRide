document.addEventListener("DOMContentLoaded", function() {
    // Imposta l'orario di partenza attuale
    const setCurrentTime = () => {
        const now = new Date();
        const hours = now.getHours().toString().padStart(2, '0');
        const minutes = now.getMinutes().toString().padStart(2, '0');
        document.getElementById('orario').value = `${hours}:${minutes}`;

        const year = now.getFullYear().toString();
        const month = (now.getMonth() + 1).toString().padStart(2, '0');
        const day = now.getDate().toString().padStart(2, '0');
        document.getElementById('data').value = `${year}-${month}-${day}`;
    };
    setCurrentTime();

    // Event listener per la ricerca delle tratte
    document.getElementById('searchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        searchRoutes();
    });

    // Funzione per simulare la ricerca delle fermate
    const searchRoutes = () => {
        // Simulating a search operation
        const resultsContainer = document.getElementById('resultsContainer');
        const loadingSpinner = document.getElementById('loadingSpinner');
        const resultsContent = document.getElementById('resultsContent');

        // Mostra spinner di caricamento
        resultsContainer.style.display = 'block';
        loadingSpinner.style.display = 'block';
        resultsContent.innerHTML = '';

        // Simula una chiamata asincrona
        setTimeout(() => {
            loadingSpinner.style.display = 'none';
            
            // Ottieni i valori dal form
            const partenza = document.getElementById('partenza').value;
            const arrivo = document.getElementById('arrivo').value;
            const orario = document.getElementById('orario').value;
            const data = document.getElementById('data').value;
            
            // Genera risultati simulati
            const mockResults = generateMockResults(partenza, arrivo, orario, data);
            resultsContent.innerHTML = mockResults;
        }, 2000);
    };
});
