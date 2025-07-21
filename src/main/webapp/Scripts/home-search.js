/**
 * HOME SEARCH JAVASCRIPT
 * Gestione della ricerca nella home page
 */

$(document).ready(function() {
    // Inizializzazione
    initializeHomePage();
    
    // Imposta la data di oggi come minimo
    const today = new Date().toISOString().split('T')[0];
    $('#data').attr('min', today);
    $('#data').val(today);
    
    // Imposta l'ora corrente
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    $('#orario').val(`${hours}:${minutes}`);
});

function initializeHomePage() {
    // Prepara l'array delle fermate per l'autocomplete
    const fermate = [];
    
    // Se le fermate sono disponibili dal context, le usa
    if (typeof window.fermateData !== 'undefined') {
        fermate.push(...window.fermateData);
    } else {
        // Fermate di default se non caricate dal server
        fermate.push("Milano", "Roma", "Napoli", "Torino", "Firenze", "Bologna", "Venezia", "Genova", "Bari", "Palermo");
    }
    
    // Configura autocomplete per i campi partenza e arrivo
    $("#partenza, #arrivo").autocomplete({
        source: fermate,
        minLength: 2,
        delay: 300,
        select: function(event, ui) {
            $(this).val(ui.item.value);
            return false;
        },
        open: function() {
            $(this).autocomplete('widget').css('z-index', 1000);
        }
    });
    
    // Gestione form submission
    $('#searchForm').on('submit', function(e) {
        if (!validateSearchForm()) {
            e.preventDefault();
            return false;
        }
        
        // Mostra loading state
        const submitButton = $(this).find('button[type="submit"]');
        OmniRide.LoadingManager.show(submitButton[0], 'Cerco...');
    });
}

function validateSearchForm() {
    let isValid = true;
    const errors = [];
    
    // Validazione partenza
    const partenza = $('#partenza').val().trim();
    if (!partenza) {
        errors.push('Seleziona una città di partenza');
        $('#partenza').addClass('error');
        isValid = false;
    } else {
        $('#partenza').removeClass('error');
    }
    
    // Validazione arrivo
    const arrivo = $('#arrivo').val().trim();
    if (!arrivo) {
        errors.push('Seleziona una città di arrivo');
        $('#arrivo').addClass('error');
        isValid = false;
    } else {
        $('#arrivo').removeClass('error');
    }
    
    // Controlla che partenza e arrivo siano diverse
    if (partenza && arrivo && partenza.toLowerCase() === arrivo.toLowerCase()) {
        errors.push('La città di partenza e arrivo devono essere diverse');
        $('#partenza, #arrivo').addClass('error');
        isValid = false;
    }
    
    // Validazione data
    const data = $('#data').val();
    const selectedDate = new Date(data);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (!data) {
        errors.push('Seleziona una data');
        $('#data').addClass('error');
        isValid = false;
    } else if (selectedDate < today) {
        errors.push('La data non può essere nel passato');
        $('#data').addClass('error');
        isValid = false;
    } else {
        $('#data').removeClass('error');
    }
    
    // Validazione orario
    const orario = $('#orario').val();
    if (!orario) {
        errors.push('Seleziona un orario');
        $('#orario').addClass('error');
        isValid = false;
    } else {
        $('#orario').removeClass('error');
    }
    
    // Mostra errori se presenti
    if (!isValid) {
        let errorMessage = 'Correggi i seguenti errori:\n';
        errors.forEach(error => {
            errorMessage += '• ' + error + '\n';
        });
        
        OmniRide.showToast(errorMessage.replace(/\n/g, '<br>'), 'error', 5000);
    }
    
    return isValid;
}

// Funzione per scambiare partenza e arrivo
function swapLocations() {
    const partenza = $('#partenza').val();
    const arrivo = $('#arrivo').val();
    
    $('#partenza').val(arrivo);
    $('#arrivo').val(partenza);
    
    // Animazione visiva
    $('#partenza, #arrivo').addClass('highlight');
    setTimeout(() => {
        $('#partenza, #arrivo').removeClass('highlight');
    }, 500);
}

// Funzione per popolare campi con ricerche popolari
function usePopularRoute(from, to) {
    $('#partenza').val(from);
    $('#arrivo').val(to);
    
    // Focus sul prossimo campo (data)
    $('#data').focus();
}

// Aggiungi listener per il bottone di scambio se presente
$(document).ready(function() {
    if ($('.swap-locations').length) {
        $('.swap-locations').on('click', swapLocations);
    }

});

// Export funzioni per uso globale
window.HomeSearch = {
    swapLocations,
    usePopularRoute,
    validateSearchForm
};
