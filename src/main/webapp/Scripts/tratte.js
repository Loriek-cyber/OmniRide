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
        
        if (!tratta.fermate || tratta.fermate.length === 0) {
            detailsContainer.innerHTML = `
                <div class="no-data">
                    <p>Nessuna fermata disponibile per questa tratta.</p>
                </div>
            `;
            return;
        }
        
        // Calcola i tempi di arrivo per ogni fermata e ogni orario
        const orariConTempi = tratta.orari.map(orario => {
            const partenza = parseTime(orario);
            if (!partenza) return null;
            
            const tempiArrivo = [];
            let tempoAccumulato = 0;
            
            tratta.fermate.forEach((fermata, index) => {
                const oraArrivo = new Date(partenza);
                oraArrivo.setMinutes(oraArrivo.getMinutes() + tempoAccumulato);
                tempiArrivo.push(formatTime(oraArrivo));
                
                // Simula 5 minuti tra le fermate (puoi modificare questo)
                tempoAccumulato += 5;
            });
            
            return {
                orarioOriginale: orario,
                tempiArrivo: tempiArrivo
            };
        }).filter(o => o !== null);
        
        const detailsHTML = `
            <div class="tratta-details">
                <div class="tratta-title">${tratta.nome}</div>
                
                <div class="schedule-section">
                    <div class="section-title">🗓️ Orari e Fermate</div>
                    <div class="schedule-table-container">
                        <table class="schedule-table">
                            <thead>
                                <tr>
                                    <th class="stop-header">Fermata</th>
                                    ${orariConTempi.map(o => `
                                        <th class="time-header">${o.orarioOriginale}</th>
                                    `).join('')}
                                </tr>
                            </thead>
                            <tbody>
                                ${tratta.fermate.map((fermata, fermataIndex) => `
                                    <tr class="${fermataIndex % 2 === 0 ? 'even-row' : 'odd-row'}">
                                        <td class="stop-cell">
                                            <div class="stop-info">
                                                <span class="stop-icon">🚏</span>
                                                <span class="stop-name">${fermata}</span>
                                            </div>
                                        </td>
                                        ${orariConTempi.map(o => `
                                            <td class="time-cell">${o.tempiArrivo[fermataIndex]}</td>
                                        `).join('')}
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <div class="info-section">
                    <div class="info-card">
                        <div class="info-icon">📍</div>
                        <div class="info-content">
                            <div class="info-label">Fermate Totali</div>
                            <div class="info-value">${tratta.fermate.length}</div>
                        </div>
                    </div>
                    <div class="info-card">
                        <div class="info-icon">🕒</div>
                        <div class="info-content">
                            <div class="info-label">Orari Disponibili</div>
                            <div class="info-value">${tratta.orari.length}</div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        detailsContainer.innerHTML = detailsHTML;
    }
    
    // Funzione helper per parsare l'orario
    function parseTime(timeString) {
        const timeMatch = timeString.match(/^(\d{1,2}):(\d{2})/);
        if (!timeMatch) return null;
        
        const date = new Date();
        date.setHours(parseInt(timeMatch[1]), parseInt(timeMatch[2]), 0, 0);
        return date;
    }
    
    // Funzione helper per formattare l'orario
    function formatTime(date) {
        return date.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' });
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
