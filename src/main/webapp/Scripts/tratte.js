document.addEventListener("DOMContentLoaded", () => {
    console.log("tratte.js caricato correttamente.");

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

                // Usa i tempi reali di percorrenza dalla base dati
                // L'ultimo elemento non ha tempo alla prossima fermata
                if (index < tratta.tempiPercorrenza.length) {
                    const tempoReale = tratta.tempiPercorrenza[index] || 0;
                    tempoAccumulato += tempoReale;
                }
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
                    <div class="section-title">Orari e Fermate</div>
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
                                                <span class="stop-icon"><img src="${window.contextPath}/icons/pin.svg"></span>
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
                        <div class="info-icon"><img src="${window.contextPath}/icons/pin.svg"></div>
                        <div class="info-content">
                            <div class="info-label">Fermate Totali</div>
                            <div class="info-value">${tratta.fermate.length}</div>
                        </div>
                    </div>
                    <div class="info-card">
                        <div class="info-icon"><img src="${window.contextPath}/icons/clock.svg"></div>
                        <div class="info-content">
                            <div class="info-label">Orari Disponibili</div>
                            <div class="info-value">${tratta.orari.length}</div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        detailsContainer.innerHTML = detailsHTML;
        // Aggiungi pulsante "Aggiungi al Carrello"
        detailsContainer.insertAdjacentHTML('beforeend', `
    <div class="add-to-cart-button">
        <button class="btn-cart-custom" onclick="addToCart(${tratta.id})">Aggiungi al Carrello</button>
    </div>`);


        // Funzione per aggiungere la tratta al carrello
        window.addToCart = function(trattaId) {
            // Costruisci URL per selectTicketType con i parametri della tratta
            const today = new Date().toISOString().split('T')[0];
            const currentTime = new Date().toTimeString().split(' ')[0].substring(0, 5);

            // Trova la tratta selezionata
            const tratta = tratteData.find(t => t.id == trattaId);
            if (!tratta) {
                alert('Errore: Tratta non trovata');
                return;
            }

            // Parametri per la selezione del biglietto
            const params = new URLSearchParams({
                percorso: tratta.nome,
                data: today,
                orario: tratta.orari[0] || currentTime, // Primo orario disponibile
                prezzo: tratta.prezzo || '10.00' // Usa il prezzo reale della tratta dal database
            });

            // Reindirizza a selectTicketType
            // Ottieni il context path dall'URL corrente
            const contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            window.location.href = contextPath + '/selectTicketType?' + params.toString();
        };
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