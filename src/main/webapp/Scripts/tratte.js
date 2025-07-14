document.addEventListener("DOMContentLoaded", () => {
    console.log("📦 tratte.js caricato correttamente.");

    // Gestione selezione tratte
    window.selectTratta = function(index) {
        // Rimuovi selezione precedente
        document.querySelectorAll('.tratta-item').forEach(item => {
            item.classList.remove('selected');
        });
        
        // Aggiungi selezione alla tratta corrente
        const selectedItem = document.querySelector(`[data-tratta-id="${index}"]`);
        if (selectedItem) {
            selectedItem.classList.add('selected');
        }
        
        // Mostra dettagli tratta
        if (typeof tratteData !== 'undefined' && tratteData[index]) {
            showTrattaDetails(tratteData[index]);
        }
    };
    
    // Funzione per mostrare i dettagli della tratta
    function showTrattaDetails(tratta) {
        const detailsContainer = document.getElementById('tratta-details');
        
        const detailsHTML = `
            <div class="tratta-details">
                <div class="tratta-title">${tratta.nome}</div>
                
                <div class="fermate-section">
                    <div class="section-title">📍 Fermate</div>
                    <div class="fermate-list">
                        ${tratta.fermate.map(fermata => `
                            <div class="fermata-item">
                                <span class="icon">🚏</span>
                                <span>${fermata}</span>
                            </div>
                        `).join('')}
                    </div>
                </div>
                
                <div class="orari-section">
                    <div class="section-title">🕒 Orari</div>
                    <div class="orari-grid">
                        ${tratta.orari.map(orario => `
                            <div class="orario-item">${orario}</div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
        
        detailsContainer.innerHTML = detailsHTML;
    }
    
    // Gestione hover effects
    document.querySelectorAll('.tratta-item').forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(5px)';
        });
        
        item.addEventListener('mouseleave', function() {
            if (!this.classList.contains('selected')) {
                this.style.transform = 'translateX(0)';
            }
        });
    });
    
    // Seleziona automaticamente la prima tratta se disponibile
    if (typeof tratteData !== 'undefined' && tratteData.length > 0) {
        selectTratta(0);
    }
});
