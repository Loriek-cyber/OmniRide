/**
 * SEARCH RESULTS JAVASCRIPT
 * Funzioni per la gestione dei risultati di ricerca
 */

var selectedPercorso = null;

/**
 * Seleziona un percorso dalla lista
 */
function selectPercorso(index) {
    console.log('Selezione percorso:', index);
    
    // Rimuovi selezione precedente
    document.querySelectorAll('.tratta-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Seleziona nuovo percorso
    const percorsoElement = document.querySelector(`[data-percorso-id="${index}"]`);
    if (percorsoElement) {
        percorsoElement.classList.add('selected');
        
        // Leggi i dati
        const costo = parseFloat(percorsoElement.dataset.costo);
        const segmenti = JSON.parse(percorsoElement.dataset.segmenti);
        
        selectedPercorso = {
            index: index,
            costo: costo,
            segmenti: segmenti
        };
        
        // Mostra dettagli
        showPercorsoDetails(selectedPercorso);
        
        // Mostra pulsante booking
        const bookingSection = document.getElementById('bookingSection');
        if (bookingSection) {
            bookingSection.style.display = 'block';
        }
        
        // Aggiorna prezzo nel pulsante con animazione
        updatePurchaseButtonPrice(costo);
        
        // Aggiorna form
        const percorsoDataField = document.getElementById('percorsoData');
        const prezzoDataField = document.getElementById('prezzoData');
        
        if (percorsoDataField) {
            percorsoDataField.value = JSON.stringify(selectedPercorso);
        }
        if (prezzoDataField) {
            prezzoDataField.value = costo.toString();
        }
        
        // Scroll smooth ai dettagli
        OmniRide.scrollToElement('percorso-details', 100);
    }
}

/**
 * Mostra i dettagli del percorso selezionato
 */
function showPercorsoDetails(percorso) {
    const detailsContainer = document.getElementById('percorso-details');
    if (!detailsContainer) return;
    
    let html = '<div class="percorso-details">';
    html += '<div class="percorso-title">Percorso Selezionato</div>';
    
    // Informazioni generali
    html += '<div class="percorso-summary">';
    html += `<div class="summary-item">`;
    html += `<span class="summary-label">💰 Costo Totale:</span>`;
    html += `<span class="summary-value">${OmniRide.formatCurrency(percorso.costo)}</span>`;
    html += `</div>`;
    html += `<div class="summary-item">`;
    html += `<span class="summary-label">🚌 Tratte:</span>`;
    html += `<span class="summary-value">${percorso.segmenti.length}</span>`;
    html += `</div>`;
    html += '</div>';
    
    // Dettagli segmenti
    html += '<div class="percorso-segments">';
    html += '<div class="segments-title">📍 Dettaglio Percorso</div>';
    
    percorso.segmenti.forEach((segmento, index) => {
        html += '<div class="segment-item">';
        html += `<div class="segment-header">`;
        html += `<span class="segment-number">${index + 1}</span>`;
        html += `<div class="segment-route">${segmento.fermataIn} → ${segmento.fermataOu}</div>`;
        html += `</div>`;
        
        html += `<div class="segment-details">`;
        if (segmento.tempo_partenza && segmento.tempo_arrivo) {
            html += `<div class="segment-time">`;
            html += `<span class="time-departure">🚀 ${segmento.tempo_partenza}</span>`;
            html += `<span class="time-arrival">🏁 ${segmento.tempo_arrivo}</span>`;
            html += `</div>`;
        }
        if (segmento.numero_fermate > 0) {
            html += `<div class="segment-stops">🛑 ${segmento.numero_fermate} fermate</div>`;
        }
        html += `</div>`;
        html += '</div>';
    });
    
    html += '</div>'; // percorso-segments
    
    // Informazioni aggiuntive
    html += '<div class="percorso-info">';
    html += '<div class="info-title">ℹ️ Informazioni Aggiuntive</div>';
    html += '<ul class="info-list">';
    
    if (percorso.segmenti.length === 1) {
        html += '<li>✅ Viaggio diretto senza cambi</li>';
    } else {
        html += `<li>🔄 Richiede ${percorso.segmenti.length - 1} cambio/i</li>`;
    }
    
    html += '<li>💳 Pagamento sicuro online</li>';
    html += '<li>📱 Biglietto digitale disponibile</li>';
    html += '<li>♿ Servizio accessibile</li>';
    html += '</ul>';
    html += '</div>';
    
    html += '</div>'; // percorso-details
    
    detailsContainer.innerHTML = html;
    
    // Aggiungi animazione di fade-in
    detailsContainer.style.opacity = '0';
    setTimeout(() => {
        detailsContainer.style.transition = 'opacity 0.3s ease';
        detailsContainer.style.opacity = '1';
    }, 50);
}

/**
 * Aggiorna il prezzo nel pulsante di acquisto
 */
function updatePurchaseButtonPrice(price) {
    const priceDisplay = document.getElementById('btnPrice');
    if (priceDisplay) {
        // Animazione del prezzo
        priceDisplay.style.transition = 'all 0.3s ease';
        priceDisplay.style.transform = 'scale(1.1)';
        priceDisplay.style.color = '#28a745';
        
        priceDisplay.textContent = OmniRide.formatCurrency(price);
        
        // Ripristina dopo l'animazione
        setTimeout(() => {
            priceDisplay.style.transform = 'scale(1)';
            priceDisplay.style.color = '';
        }, 300);
    }
}

/**
 * Gestisce la selezione del tipo di biglietto
 */
function selectTicketType() {
    if (!selectedPercorso) {
        OmniRide.showToast('Seleziona prima un percorso', 'warning');
        return;
    }
    
    const ticketForm = document.getElementById('ticketForm');
    if (ticketForm) {
        OmniRide.LoadingManager.show(document.getElementById('selectTicketBtn'), 'Caricamento...');
        ticketForm.submit();
    }
}

/**
 * Inizializza la pagina dei risultati di ricerca
 */
function initializeSearchResults() {
    // Aggiungi event listeners per hover effects sui percorsi
    document.querySelectorAll('.tratta-item').forEach(item => {
        item.addEventListener('mouseenter', function() {
            if (!this.classList.contains('selected')) {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
                this.style.transition = 'all 0.2s ease';
            }
        });
        
        item.addEventListener('mouseleave', function() {
            if (!this.classList.contains('selected')) {
                this.style.transform = '';
                this.style.boxShadow = '';
            }
        });
    });
    
    // Auto-seleziona il primo percorso se disponibile
    const firstPercorso = document.querySelector('.tratta-item');
    if (firstPercorso && document.querySelectorAll('.tratta-item').length === 1) {
        // Solo se c'è un solo risultato
        const index = firstPercorso.dataset.percorsoId;
        setTimeout(() => selectPercorso(index), 500);
    }
    
    // Mostra statistiche dei risultati
    const resultCount = document.querySelectorAll('.tratta-item').length;
    if (resultCount === 0) {
        // Suggerimenti per migliorare la ricerca
        const noDataContainer = document.querySelector('.no-data');
        if (noDataContainer) {
            const suggestions = document.createElement('div');
            suggestions.className = 'search-suggestions';
            suggestions.innerHTML = `
                <h4>💡 Suggerimenti:</h4>
                <ul>
                    <li>Prova con città vicine</li>
                    <li>Verifica l'ortografia delle città</li>
                    <li>Considera orari diversi</li>
                    <li>Prova con date alternative</li>
                </ul>
            `;
            noDataContainer.appendChild(suggestions);
        }
    } else {
        OmniRide.showToast(`${resultCount} percors${resultCount === 1 ? 'o trovato' : 'i trovati'}`, 'success');
    }
}

// Aggiungi stili CSS dinamici per migliorare l'esperienza
function addDynamicStyles() {
    const styles = `
        <style>
        .tratta-item.selected {
            border-left: 4px solid var(--primary-color);
            background: linear-gradient(135deg, rgba(0, 123, 255, 0.05) 0%, rgba(0, 123, 255, 0.1) 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
        }
        
        .search-suggestions {
            margin-top: 2rem;
            padding: 1.5rem;
            background: rgba(255, 255, 255, 0.9);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
        }
        
        .search-suggestions h4 {
            color: var(--primary-color);
            margin-bottom: 1rem;
        }
        
        .search-suggestions ul {
            list-style: none;
            padding: 0;
        }
        
        .search-suggestions li {
            padding: 0.5rem 0;
            border-bottom: 1px solid var(--gray-200);
        }
        
        .search-suggestions li:before {
            content: "→ ";
            color: var(--primary-color);
            font-weight: bold;
        }
        
        .percorso-details {
            animation: fadeInUp 0.4s ease;
        }
        
        .segment-item {
            border-left: 2px solid var(--gray-200);
            padding-left: 1rem;
            margin: 1rem 0;
        }
        
        .segment-item:hover {
            border-left-color: var(--primary-color);
            transition: border-color 0.2s ease;
        }
        
        .booking-section.modern-booking {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border-radius: var(--radius-lg);
            padding: 1.5rem;
            text-align: center;
            box-shadow: var(--shadow-lg);
        }
        
        .booking-section button {
            background: white;
            color: #28a745;
            border: none;
            padding: 1rem 2rem;
            border-radius: var(--radius-md);
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .booking-section button:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }
        </style>
    `;
    
    document.head.insertAdjacentHTML('beforeend', styles);
}

// Inizializzazione al caricamento della pagina
document.addEventListener('DOMContentLoaded', function() {
    addDynamicStyles();
    initializeSearchResults();
});

// Export per uso globale
window.SearchResults = {
    selectPercorso,
    showPercorsoDetails,
    updatePurchaseButtonPrice,
    selectTicketType,
    initializeSearchResults
};
