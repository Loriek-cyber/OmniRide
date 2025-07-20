/**
 * Admin Dashboard JavaScript
 * Gestisce le funzionalità interattive della dashboard amministratore
 */

document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
});

function initializeDashboard() {
    // Animazioni per le statistiche
    animateStats();
    
    // Auto-refresh dei dati ogni 5 minuti
    setupAutoRefresh();
    
    // Gestione notifiche
    setupNotifications();
    
    // Tooltip per le statistiche
    setupTooltips();
    
    // Gestione responsive della navigazione
    setupResponsiveNav();
}

/**
 * Anima le statistiche con un effetto di conteggio
 */
function animateStats() {
    const statNumbers = document.querySelectorAll('.stat-number');
    
    statNumbers.forEach(stat => {
        const target = parseInt(stat.textContent.replace(/[^\d]/g, ''));
        if (isNaN(target)) return;
        
        let current = 0;
        const increment = target / 50; // Animazione in 50 step
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                stat.textContent = formatNumber(target);
                clearInterval(timer);
            } else {
                stat.textContent = formatNumber(Math.floor(current));
            }
        }, 30);
    });
}

/**
 * Formatta i numeri per la visualizzazione
 */
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    }
    if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

/**
 * Setup auto-refresh dei dati della dashboard
 */
function setupAutoRefresh() {
    // Refresh automatico ogni 5 minuti
    setInterval(() => {
        refreshDashboardData();
    }, 5 * 60 * 1000);
    
    // Mostra indicatore di ultimo aggiornamento
    updateLastRefreshTime();
}

/**
 * Aggiorna i dati della dashboard via AJAX
 */
function refreshDashboardData() {
    // Mostra loader
    showLoadingIndicator();
    
    fetch(window.location.pathname, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.text())
    .then(html => {
        // Parser per estrarre solo le statistiche
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        
        // Aggiorna le statistiche
        updateStats(doc);
        
        // Aggiorna timestamp
        updateLastRefreshTime();
        
        hideLoadingIndicator();
    })
    .catch(error => {
        console.error('Errore nell\'aggiornamento dei dati:', error);
        hideLoadingIndicator();
        showNotification('Errore nell\'aggiornamento dei dati', 'error');
    });
}

/**
 * Aggiorna le statistiche nella pagina
 */
function updateStats(newDoc) {
    const statCards = document.querySelectorAll('.stat-number');
    const newStatCards = newDoc.querySelectorAll('.stat-number');
    
    statCards.forEach((stat, index) => {
        if (newStatCards[index]) {
            const oldValue = stat.textContent;
            const newValue = newStatCards[index].textContent;
            
            if (oldValue !== newValue) {
                // Anima il cambiamento
                stat.style.transform = 'scale(1.1)';
                stat.style.color = '#28a745';
                
                setTimeout(() => {
                    stat.textContent = newValue;
                    stat.style.transform = 'scale(1)';
                    stat.style.color = '';
                }, 200);
            }
        }
    });
}

/**
 * Mostra/nasconde indicatore di caricamento
 */
function showLoadingIndicator() {
    let loader = document.getElementById('dashboard-loader');
    if (!loader) {
        loader = document.createElement('div');
        loader.id = 'dashboard-loader';
        loader.className = 'dashboard-loader';
        loader.innerHTML = '<div class="spinner"></div><span>Aggiornamento dati...</span>';
        document.body.appendChild(loader);
    }
    loader.style.display = 'flex';
}

function hideLoadingIndicator() {
    const loader = document.getElementById('dashboard-loader');
    if (loader) {
        loader.style.display = 'none';
    }
}

/**
 * Aggiorna il timestamp dell'ultimo refresh
 */
function updateLastRefreshTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('it-IT');
    
    let refreshIndicator = document.getElementById('last-refresh');
    if (!refreshIndicator) {
        refreshIndicator = document.createElement('div');
        refreshIndicator.id = 'last-refresh';
        refreshIndicator.className = 'last-refresh';
        document.querySelector('.admin-header').appendChild(refreshIndicator);
    }
    
    refreshIndicator.innerHTML = `<small>⏱️ Ultimo aggiornamento: ${timeString}</small>`;
}

/**
 * Setup sistema notifiche
 */
function setupNotifications() {
    // Container per le notifiche
    if (!document.getElementById('notifications-container')) {
        const container = document.createElement('div');
        container.id = 'notifications-container';
        container.className = 'notifications-container';
        document.body.appendChild(container);
    }
}

/**
 * Mostra una notifica
 */
function showNotification(message, type = 'info', duration = 5000) {
    const container = document.getElementById('notifications-container');
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    const icon = getNotificationIcon(type);
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-icon">${icon}</span>
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="closeNotification(this)">×</button>
        </div>
    `;
    
    container.appendChild(notification);
    
    // Mostra con animazione
    setTimeout(() => notification.classList.add('show'), 100);
    
    // Auto-remove dopo il duration
    if (duration > 0) {
        setTimeout(() => {
            closeNotification(notification.querySelector('.notification-close'));
        }, duration);
    }
}

function getNotificationIcon(type) {
    const icons = {
        'success': '✅',
        'error': '❌',
        'warning': '⚠️',
        'info': 'ℹ️'
    };
    return icons[type] || icons.info;
}

function closeNotification(button) {
    const notification = button.closest('.notification');
    notification.classList.remove('show');
    setTimeout(() => notification.remove(), 300);
}

/**
 * Setup tooltips per le statistiche
 */
function setupTooltips() {
    const statCards = document.querySelectorAll('.stat-card');
    
    statCards.forEach(card => {
        const tooltip = createTooltip(card);
        
        card.addEventListener('mouseenter', (e) => {
            showTooltip(e, tooltip);
        });
        
        card.addEventListener('mouseleave', () => {
            hideTooltip(tooltip);
        });
    });
}

function createTooltip(element) {
    const tooltip = document.createElement('div');
    tooltip.className = 'admin-tooltip';
    tooltip.textContent = getTooltipText(element);
    document.body.appendChild(tooltip);
    return tooltip;
}

function getTooltipText(statCard) {
    const title = statCard.querySelector('h3')?.textContent || '';
    const tooltips = {
        'UTENTI TOTALI': 'Numero totale di utenti registrati nel sistema',
        'BIGLIETTI VENDUTI': 'Totale biglietti venduti dall\'inizio',
        'FATTURATO TOTALE': 'Ricavi totali dalle vendite dei biglietti',
        'TRATTE ATTIVE': 'Numero di tratte attualmente operative',
        'AZIENDE PARTNER': 'Compagnie di trasporto registrate',
        'DIPENDENTI': 'Personale attivo nelle aziende partner'
    };
    return tooltips[title.toUpperCase()] || 'Statistica del sistema';
}

function showTooltip(event, tooltip) {
    tooltip.style.display = 'block';
    tooltip.style.left = event.pageX + 10 + 'px';
    tooltip.style.top = event.pageY - 10 + 'px';
}

function hideTooltip(tooltip) {
    tooltip.style.display = 'none';
}

/**
 * Setup navigazione responsive
 */
function setupResponsiveNav() {
    const nav = document.querySelector('.admin-nav');
    const links = nav.querySelectorAll('.nav-link');
    
    // Nascondi/mostra links in base alla larghezza schermo
    function checkNavOverflow() {
        if (window.innerWidth < 768) {
            // Mostra solo dashboard e il link attivo su mobile
            links.forEach(link => {
                if (!link.classList.contains('active') && !link.textContent.includes('Dashboard')) {
                    link.style.display = 'none';
                }
            });
            
            // Aggiungi menu hamburger se necessario
            addMobileMenu();
        } else {
            // Mostra tutti i link su desktop
            links.forEach(link => link.style.display = '');
            removeMobileMenu();
        }
    }
    
    checkNavOverflow();
    window.addEventListener('resize', checkNavOverflow);
}

function addMobileMenu() {
    if (document.getElementById('mobile-menu-toggle')) return;
    
    const nav = document.querySelector('.admin-nav');
    const toggle = document.createElement('button');
    toggle.id = 'mobile-menu-toggle';
    toggle.className = 'mobile-menu-toggle';
    toggle.innerHTML = '☰ Menu';
    
    toggle.addEventListener('click', () => {
        nav.classList.toggle('mobile-open');
        const links = nav.querySelectorAll('.nav-link');
        links.forEach(link => {
            link.style.display = nav.classList.contains('mobile-open') ? 'block' : 'none';
        });
        // Mantieni sempre visibile dashboard
        nav.querySelector('.nav-link[href*="dashboard"]').style.display = 'block';
    });
    
    nav.parentElement.insertBefore(toggle, nav);
}

function removeMobileMenu() {
    const toggle = document.getElementById('mobile-menu-toggle');
    if (toggle) toggle.remove();
    
    const nav = document.querySelector('.admin-nav');
    nav.classList.remove('mobile-open');
}

/**
 * Utilità per il debugging
 */
function debugInfo() {
    console.log('Dashboard Admin caricata');
    console.log('Statistiche animate:', document.querySelectorAll('.stat-number').length);
    console.log('Auto-refresh attivo: ogni 5 minuti');
}

// CSS dinamico per componenti JavaScript
const dashboardCSS = `
.dashboard-loader {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.7);
    display: none;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    color: white;
    font-size: 18px;
    gap: 15px;
}

.spinner {
    border: 4px solid rgba(255,255,255,0.3);
    border-top: 4px solid white;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.last-refresh {
    position: absolute;
    top: 10px;
    right: 10px;
    opacity: 0.7;
}

.notifications-container {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 10000;
    max-width: 400px;
}

.notification {
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    margin-bottom: 10px;
    transform: translateX(400px);
    transition: transform 0.3s ease;
    border-left: 4px solid #007bff;
}

.notification.show {
    transform: translateX(0);
}

.notification-success { border-left-color: #28a745; }
.notification-error { border-left-color: #dc3545; }
.notification-warning { border-left-color: #ffc107; }

.notification-content {
    display: flex;
    align-items: center;
    padding: 15px;
    gap: 10px;
}

.notification-close {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    margin-left: auto;
    opacity: 0.7;
}

.admin-tooltip {
    position: absolute;
    background: #333;
    color: white;
    padding: 8px 12px;
    border-radius: 4px;
    font-size: 12px;
    display: none;
    z-index: 1000;
    max-width: 200px;
    word-wrap: break-word;
}

.mobile-menu-toggle {
    display: none;
    background: #007bff;
    color: white;
    border: none;
    padding: 10px 15px;
    border-radius: 5px;
    cursor: pointer;
    margin-bottom: 10px;
}

@media (max-width: 768px) {
    .mobile-menu-toggle {
        display: block;
    }
}
`;

// Inietta il CSS
const style = document.createElement('style');
style.textContent = dashboardCSS;
document.head.appendChild(style);
