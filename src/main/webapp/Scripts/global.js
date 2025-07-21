/**
 * OMNIRIDE GLOBAL JAVASCRIPT
 * Funzioni comuni utilizzate in tutto il progetto
 */

// Funzioni globali per dropdown e menu
function toggleDropdown(event) {
    event.preventDefault();
    event.stopPropagation();
    
    const dropdown = document.getElementById('userDropdown');
    if (dropdown) {
        const isVisible = dropdown.classList.contains('show');
        
        // Chiudi tutti i dropdown aperti
        document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
            menu.classList.remove('show');
        });
        
        // Toggle del dropdown corrente
        if (!isVisible) {
            dropdown.classList.add('show');
        }
    }
}

// Funzione per mostrare/nascondere filtri avanzati
function toggleAdvancedFilters() {
    const filters = document.querySelector('.advanced-filters');
    if (filters) {
        const isVisible = filters.style.display !== 'none';
        filters.style.display = isVisible ? 'none' : 'block';
        
        // Animazione smooth
        if (filters.style.display === 'block') {
            filters.style.opacity = '0';
            filters.style.transform = 'translateY(-10px)';
            setTimeout(() => {
                filters.style.opacity = '1';
                filters.style.transform = 'translateY(0)';
                filters.style.transition = 'all 0.3s ease';
            }, 10);
        }
    }
}

// Funzione per confermare eliminazione
function confirmDelete(message = "Sei sicuro di voler eliminare questo elemento?") {
    return confirm(message);
}

// Funzione per mostrare toast notifications
function showToast(message, type = 'info', duration = 3000) {
    // Rimuovi toast esistenti
    document.querySelectorAll('.toast').forEach(toast => toast.remove());
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    // Stili per il toast
    Object.assign(toast.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '12px 24px',
        borderRadius: '8px',
        color: 'white',
        fontWeight: '500',
        zIndex: '9999',
        opacity: '0',
        transform: 'translateX(100%)',
        transition: 'all 0.3s ease'
    });
    
    // Colori per tipo
    const colors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#ffc107',
        info: '#17a2b8'
    };
    
    toast.style.backgroundColor = colors[type] || colors.info;
    
    document.body.appendChild(toast);
    
    // Animazione di entrata
    setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateX(0)';
    }, 10);
    
    // Auto-remove dopo duration
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// Funzione per validare form
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return true;
    
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('error');
            isValid = false;
        } else {
            input.classList.remove('error');
        }
    });
    
    return isValid;
}

// Funzione per formattare numeri come valuta
function formatCurrency(amount, currency = '€') {
    return new Intl.NumberFormat('it-IT', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
    }).format(amount);
}

// Funzione per formattare date
function formatDate(dateString, options = {}) {
    const defaultOptions = {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    };
    
    return new Date(dateString).toLocaleDateString('it-IT', {...defaultOptions, ...options});
}

// Gestione globale dei click fuori dai dropdown
document.addEventListener('click', function(event) {
    const dropdowns = document.querySelectorAll('.dropdown-menu.show');
    dropdowns.forEach(dropdown => {
        const parent = dropdown.closest('.user-dropdown');
        if (parent && !parent.contains(event.target)) {
            dropdown.classList.remove('show');
        }
    });
});

// Gestione escape key per chiudere modali e dropdown
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        // Chiudi dropdown
        document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
            menu.classList.remove('show');
        });
        
        // Chiudi modali
        document.querySelectorAll('.modal.show').forEach(modal => {
            modal.classList.remove('show');
        });
    }
});

// Utility per gestire loading states
const LoadingManager = {
    show: function(element, text = 'Caricamento...') {
        if (typeof element === 'string') {
            element = document.getElementById(element);
        }
        
        if (element) {
            element.disabled = true;
            element.dataset.originalText = element.textContent;
            element.textContent = text;
            element.classList.add('loading');
        }
    },
    
    hide: function(element) {
        if (typeof element === 'string') {
            element = document.getElementById(element);
        }
        
        if (element) {
            element.disabled = false;
            element.textContent = element.dataset.originalText || element.textContent;
            element.classList.remove('loading');
        }
    }
};

// Funzione per animazioni scroll smooth
function scrollToElement(elementId, offset = 0) {
    const element = document.getElementById(elementId);
    if (element) {
        const position = element.offsetTop - offset;
        window.scrollTo({
            top: position,
            behavior: 'smooth'
        });
    }
}

// Debug helper per sviluppo
const Debug = {
    log: function(message, data = null) {
        if (console && console.log) {
            console.log(`[OMNIRIDE DEBUG] ${message}`, data || '');
        }
    },
    
    error: function(message, error = null) {
        if (console && console.error) {
            console.error(`[OMNIRIDE ERROR] ${message}`, error || '');
        }
    }
};

// Export per uso globale
window.OmniRide = {
    toggleDropdown,
    toggleAdvancedFilters,
    confirmDelete,
    showToast,
    validateForm,
    formatCurrency,
    formatDate,
    LoadingManager,
    scrollToElement,
    Debug
};
