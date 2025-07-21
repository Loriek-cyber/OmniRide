/**
 * Search Results JavaScript
 * Gestisce l'interazione con i risultati di ricerca percorsi
 */

// Stato globale dell'applicazione
const SearchResults = {
    selectedRoute: null,
    routes: [],
    
    // Inizializza l'applicazione
    init() {
        console.log('🚀 Inizializzazione Search Results');
        this.loadRouteData();
        this.bindEvents();
        this.setupAnimations();
    },
    
    // Carica i dati delle rotte dalle card HTML
    loadRouteData() {
        const cards = document.querySelectorAll('.result-card');
        this.routes = [];
        
        cards.forEach((card, index) => {
            const routeData = {
                index: parseInt(card.dataset.index),
                cost: parseFloat(card.dataset.cost),
                segments: JSON.parse(card.dataset.segments || '[]'),
                element: card
            };
            this.routes.push(routeData);
        });
        
        console.log(`📊 Caricate ${this.routes.length} rotte`);
    },
    
    // Bind degli eventi
    bindEvents() {
        // Event delegation per le card
        document.addEventListener('click', (e) => {
            const card = e.target.closest('.result-card');
            if (card) {
                const index = parseInt(card.dataset.index);
                this.selectRoute(index);
            }
        });
        
        // Gestione responsive
        window.addEventListener('resize', this.handleResize.bind(this));
        
        // Keyboard navigation
        document.addEventListener('keydown', this.handleKeyboard.bind(this));
    },
    
    // Setup animazioni
    setupAnimations() {
        // Animate cards on load
        const cards = document.querySelectorAll('.result-card');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.3s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });
    },
    
    // Gestione responsive
    handleResize() {
        if (window.innerWidth <= 768 && this.selectedRoute !== null) {
            // Su mobile, scroll ai dettagli quando selezionato
            const detailsElement = document.getElementById('detailsContent');
            if (detailsElement && detailsElement.style.display !== 'none') {
                detailsElement.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        }
    },
    
    // Gestione tastiera
    handleKeyboard(e) {
        if (!this.routes.length) return;
        
        switch (e.key) {
            case 'ArrowDown':
                e.preventDefault();
                this.navigateRoute(1);
                break;
            case 'ArrowUp':
                e.preventDefault();
                this.navigateRoute(-1);
                break;
            case 'Enter':
                if (this.selectedRoute !== null) {
                    proceedToBooking();
                }
                break;
            case 'Escape':
                clearSelection();
                break;
        }
    },
    
    // Navigazione con tastiera
    navigateRoute(direction) {
        let newIndex;
        if (this.selectedRoute === null) {
            newIndex = direction > 0 ? 0 : this.routes.length - 1;
        } else {
            newIndex = this.selectedRoute.index + direction;
            if (newIndex < 0) newIndex = this.routes.length - 1;
            if (newIndex >= this.routes.length) newIndex = 0;
        }
        
        this.selectRoute(newIndex);
        
        // Scroll al percorso selezionato
        const selectedCard = document.getElementById(`result-${newIndex}`);
        if (selectedCard) {
            selectedCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
    }
};

/**
 * Seleziona una rotta e mostra i dettagli
 */
function selectRoute(index) {
    console.log(`🎯 Selezione rotta ${index}`);
    
    // Trova la rotta
    const route = SearchResults.routes.find(r => r.index === index);
    if (!route) {
        console.error(`❌ Rotta ${index} non trovata`);
        return;
    }
    
    // Aggiorna UI - rimuovi selezioni precedenti
    document.querySelectorAll('.result-card').forEach(card => {
        card.classList.remove('selected');
        card.setAttribute('aria-selected', 'false');
    });
    
    // Seleziona nuova card
    const selectedCard = document.getElementById(`result-${index}`);
    if (selectedCard) {
        selectedCard.classList.add('selected');
        selectedCard.setAttribute('aria-selected', 'true');
        
        // Animazione di selezione
        selectedCard.style.transform = 'scale(1.02)';
        setTimeout(() => {
            selectedCard.style.transform = '';
        }, 200);
    }
    
    // Aggiorna stato
    SearchResults.selectedRoute = route;
    
    // Mostra dettagli
    showRouteDetails(route);
    
    // Accessibilità
    announceSelection(route);
}

/**
 * Mostra i dettagli della rotta selezionata
 */
function showRouteDetails(route) {
    console.log('📋 Mostra dettagli rotta:', route);
    
    const placeholder = document.getElementById('detailsPlaceholder');
    const content = document.getElementById('detailsContent');
    
    if (!content) return;
    
    // Nascondi placeholder, mostra content
    if (placeholder) placeholder.style.display = 'none';
    content.style.display = 'block';
    
    // Anima l'entrata
    content.style.opacity = '0';
    content.style.transform = 'translateY(10px)';
    
    requestAnimationFrame(() => {
        content.style.transition = 'all 0.3s ease';
        content.style.opacity = '1';
        content.style.transform = 'translateY(0)';
    });
    
    // Genera riepilogo
    generateRouteSummary(route);
    
    // Genera segmenti
    generateRouteSegments(route);
    
    // Aggiorna form
    updateBookingForm(route);
}

/**
 * Genera il riepilogo della rotta
 */
function generateRouteSummary(route) {
    const container = document.getElementById('routeSummary');
    if (!container) return;
    
    const firstSegment = route.segments[0];
    const lastSegment = route.segments[route.segments.length - 1];
    
    const isDirect = route.segments.length === 1;
    const totalStops = route.segments.reduce((sum, seg) => sum + seg.stopsCount, 0);
    
    container.innerHTML = `
        <div class="summary-header">
            <div class="route-title">
                <i class="fas fa-route"></i>
                <span>${firstSegment.departureStop} → ${lastSegment.arrivalStop}</span>
            </div>
            <div class="route-price">
                <span class="price-amount">€${route.cost.toFixed(2)}</span>
                <span class="price-label">Prezzo totale</span>
            </div>
        </div>
        
        <div class="summary-details">
            <div class="detail-item">
                <i class="fas ${isDirect ? 'fa-arrow-right' : 'fa-exchange-alt'}"></i>
                <span>${isDirect ? 'Percorso diretto' : `${route.segments.length} cambi`}</span>
            </div>
            
            ${firstSegment.departureTime && firstSegment.departureTime !== '--:--' ? `
            <div class="detail-item">
                <i class="fas fa-clock"></i>
                <span>Partenza: ${firstSegment.departureTime}</span>
            </div>
            ` : ''}
            
            ${lastSegment.arrivalTime && lastSegment.arrivalTime !== '--:--' ? `
            <div class="detail-item">
                <i class="fas fa-clock"></i>
                <span>Arrivo: ${lastSegment.arrivalTime}</span>
            </div>
            ` : ''}
            
            <div class="detail-item">
                <i class="fas fa-map-marker-alt"></i>
                <span>${totalStops} fermate totali</span>
            </div>
        </div>
    `;
}

/**
 * Genera i segmenti della rotta
 */
function generateRouteSegments(route) {
    const container = document.getElementById('segmentsContainer');
    if (!container) return;
    
    let segmentsHtml = '<div class="segments-header"><h3>Dettagli del Viaggio</h3></div>';
    
    route.segments.forEach((segment, index) => {
        const segmentNumber = index + 1;
        const isLast = index === route.segments.length - 1;
        
        segmentsHtml += `
            <div class="segment-item" data-segment="${index}">
                <div class="segment-header">
                    <div class="segment-number">
                        <span>${segmentNumber}</span>
                    </div>
                    <div class="segment-title">
                        <strong>Tratta ${segmentNumber}</strong>
                        ${route.segments.length > 1 ? `<span class="route-id">ID: ${segment.routeId}</span>` : ''}
                    </div>
                </div>
                
                <div class="segment-details">
                    <div class="segment-route">
                        <div class="route-stops">
                            <div class="stop-point departure">
                                <i class="fas fa-circle-dot"></i>
                                <span>${segment.departureStop}</span>
                                ${segment.departureTime && segment.departureTime !== '--:--' ? 
                                    `<time>${segment.departureTime}</time>` : ''}
                            </div>
                            
                            <div class="route-line">
                                <div class="line-decoration"></div>
                                ${segment.stopsCount > 0 ? 
                                    `<div class="stops-indicator">${segment.stopsCount} fermate</div>` : ''}
                            </div>
                            
                            <div class="stop-point arrival">
                                <i class="fas fa-location-dot"></i>
                                <span>${segment.arrivalStop}</span>
                                ${segment.arrivalTime && segment.arrivalTime !== '--:--' ? 
                                    `<time>${segment.arrivalTime}</time>` : ''}
                            </div>
                        </div>
                    </div>
                </div>
                
                ${!isLast ? '<div class="segment-separator"><i class="fas fa-exchange-alt"></i><span>Cambio</span></div>' : ''}
            </div>
        `;
    });
    
    container.innerHTML = segmentsHtml;
}

/**
 * Aggiorna il form di booking
 */
function updateBookingForm(route) {
    const routeDataInput = document.getElementById('routeDataInput');
    const priceInput = document.getElementById('priceInput');
    
    if (routeDataInput) {
        routeDataInput.value = JSON.stringify({
            index: route.index,
            costo: route.cost,
            segmenti: route.segments
        });
    }
    
    if (priceInput) {
        priceInput.value = route.cost.toString();
    }
}

/**
 * Procede al booking
 */
function proceedToBooking() {
    console.log('🎫 Procede al booking');
    
    if (!SearchResults.selectedRoute) {
        showNotification('Seleziona prima un percorso', 'warning');
        return;
    }
    
    const btn = document.getElementById('bookTicketBtn');
    const btnText = btn.querySelector('span');
    const btnLoading = btn.querySelector('.btn-loading');
    const btnIcon = btn.querySelector('i:not(.btn-loading i)');
    
    // Stato loading
    btn.disabled = true;
    btnText.style.display = 'none';
    btnIcon.style.display = 'none';
    btnLoading.style.display = 'inline-flex';
    
    // Simula un breve delay per UX
    setTimeout(() => {
        const form = document.getElementById('bookingForm');
        if (form) {
            form.submit();
        } else {
            console.error('❌ Form di booking non trovato');
            resetBookingButton();
            showNotification('Errore nel caricamento del form', 'error');
        }
    }, 500);
}

/**
 * Reset del pulsante booking
 */
function resetBookingButton() {
    const btn = document.getElementById('bookTicketBtn');
    const btnText = btn.querySelector('span');
    const btnLoading = btn.querySelector('.btn-loading');
    const btnIcon = btn.querySelector('i:not(.btn-loading i)');
    
    btn.disabled = false;
    btnText.style.display = 'inline';
    btnIcon.style.display = 'inline';
    btnLoading.style.display = 'none';
}

/**
 * Pulisce la selezione
 */
function clearSelection() {
    console.log('🧹 Pulisce selezione');
    
    // Rimuovi selezioni visive
    document.querySelectorAll('.result-card').forEach(card => {
        card.classList.remove('selected');
        card.setAttribute('aria-selected', 'false');
    });
    
    // Reset stato
    SearchResults.selectedRoute = null;
    
    // Mostra placeholder, nascondi dettagli
    const placeholder = document.getElementById('detailsPlaceholder');
    const content = document.getElementById('detailsContent');
    
    if (placeholder) placeholder.style.display = 'block';
    if (content) content.style.display = 'none';
    
    showNotification('Selezione rimossa', 'info');
}

/**
 * Mostra notifiche all'utente
 */
function showNotification(message, type = 'info', duration = 4000) {
    // Rimuovi notifica esistente
    const existing = document.querySelector('.notification-toast');
    if (existing) existing.remove();
    
    const notification = document.createElement('div');
    notification.className = `notification-toast notification-${type}`;
    
    const iconMap = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle', 
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
    };
    
    notification.innerHTML = `
        <div class="notification-content">
            <i class="${iconMap[type] || iconMap.info}"></i>
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    // Stili inline per posizionamento
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        min-width: 300px;
        max-width: 500px;
        background: var(--bg-primary, white);
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        animation: slideInRight 0.3s ease-out;
        border-left: 4px solid var(--${type}-color, #32cd32);
    `;
    
    document.body.appendChild(notification);
    
    // Auto-remove
    if (duration > 0) {
        setTimeout(() => {
            if (notification.parentElement) {
                notification.style.animation = 'slideOutRight 0.3s ease-in forwards';
                setTimeout(() => notification.remove(), 300);
            }
        }, duration);
    }
}

/**
 * Annuncia la selezione per accessibilità
 */
function announceSelection(route) {
    const announcement = document.createElement('div');
    announcement.setAttribute('role', 'status');
    announcement.setAttribute('aria-live', 'polite');
    announcement.style.cssText = 'position: absolute; left: -10000px; width: 1px; height: 1px; overflow: hidden;';
    
    const firstSegment = route.segments[0];
    const lastSegment = route.segments[route.segments.length - 1];
    
    announcement.textContent = `Selezionato percorso da ${firstSegment.departureStop} a ${lastSegment.arrivalStop}, prezzo ${route.cost.toFixed(2)} euro`;
    
    document.body.appendChild(announcement);
    
    setTimeout(() => announcement.remove(), 1000);
}

// CSS per animazioni (aggiunto dinamicamente se non presente)
function addMissingStyles() {
    if (document.getElementById('search-results-dynamic-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'search-results-dynamic-styles';
    style.textContent = `
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOutRight {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(100%);
                opacity: 0;
            }
        }
        
        .notification-toast {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        }
        
        .notification-content {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px 20px;
            color: var(--text-primary, #333);
        }
        
        .notification-close {
            background: none;
            border: none;
            cursor: pointer;
            padding: 4px;
            margin-left: auto;
            opacity: 0.6;
            transition: opacity 0.2s;
        }
        
        .notification-close:hover {
            opacity: 1;
        }
        
        .result-card {
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            cursor: pointer;
        }
        
        .result-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(50, 205, 50, 0.15);
        }
        
        .result-card.selected {
            border-color: var(--primary-color, #32cd32) !important;
            background: linear-gradient(135deg, #32cd32 0%, #28a745 100%);
            color: white;
            transform: scale(1.02);
        }
        
        .result-card.selected .price-badge {
            background: rgba(255, 255, 255, 0.2);
            color: white;
        }
    `;
    
    document.head.appendChild(style);
}

// Inizializzazione quando il DOM è pronto
document.addEventListener('DOMContentLoaded', () => {
    console.log('📄 DOM Content Loaded - Inizializzazione Search Results');
    addMissingStyles();
    SearchResults.init();
});

// Backup per compatibilità
window.selectRoute = selectRoute;
window.proceedToBooking = proceedToBooking;
window.clearSelection = clearSelection;
window.showNotification = showNotification;

// Export per moduli ES6 se necessario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { SearchResults, selectRoute, proceedToBooking, clearSelection, showNotification };
}
