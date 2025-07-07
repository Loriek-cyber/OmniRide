/**
 * JavaScript per il form di aggiunta tratta
 * Gestisce la selezione delle fermate e la validazione del form
 */

// Variabili globali
let selectedFermate = [];
let formValidator = null;

// Inizializzazione quando il DOM è caricato
document.addEventListener('DOMContentLoaded', function() {
    initializeForm();
    setupEventListeners();
    setupFormValidation();
});

/**
 * Inizializza il form e i suoi componenti
 */
function initializeForm() {
    console.log('Inizializzazione form addTratta...');
    
    // Reset dello stato iniziale
    selectedFermate = [];
    updateTempiInputs();
    
    // Aggiungi indicatori di validazione ai campi
    const requiredInputs = document.querySelectorAll('input[required]');
    requiredInputs.forEach(input => {
        input.classList.add('form-input');
    });
}

/**
 * Configura tutti gli event listeners
 */
function setupEventListeners() {
    // Event listener per le checkbox delle fermate
    const fermateCheckboxes = document.querySelectorAll('input[name="fermateSelezionate"]');
    fermateCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', handleFermataSelection);
    });
    
    // Event listener per il form submit
    const form = document.getElementById('addTrattaForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
    
    // Event listener per i campi di input (validazione real-time)
    const nomeInput = document.getElementById('nome');
    const costoInput = document.getElementById('costo');
    
    if (nomeInput) {
        nomeInput.addEventListener('blur', () => validateField(nomeInput));
        nomeInput.addEventListener('input', () => clearFieldError(nomeInput));
    }
    
    if (costoInput) {
        costoInput.addEventListener('blur', () => validateField(costoInput));
        costoInput.addEventListener('input', () => clearFieldError(costoInput));
    }
    
    // Event listener per i tempi di percorrenza
    document.addEventListener('input', function(e) {
        if (e.target.classList.contains('tempo-input')) {
            validateTempoInput(e.target);
        }
    });
}

/**
 * Configura la validazione del form
 */
function setupFormValidation() {
    formValidator = {
        nome: false,
        costo: false,
        fermate: false,
        tempi: false
    };
}

/**
 * Gestisce la selezione/deselezione delle fermate
 */
function handleFermataSelection(event) {
    const checkbox = event.target;
    const fermataId = checkbox.value;
    
    if (checkbox.checked) {
        // Aggiungi fermata alla selezione
        if (!selectedFermate.includes(fermataId)) {
            selectedFermate.push(fermataId);
        }
    } else {
        // Rimuovi fermata dalla selezione
        const index = selectedFermate.indexOf(fermataId);
        if (index > -1) {
            selectedFermate.splice(index, 1);
        }
    }
    
    // Aggiorna i campi tempo
    updateTempiInputs();
    
    // Valida la selezione delle fermate
    validateFermateSelection();
    
    console.log('Fermate selezionate:', selectedFermate);
}

/**
 * Aggiorna la visualizzazione dei campi tempo
 */
function updateTempiInputs() {
    const allCheckboxes = document.querySelectorAll('input[name="fermateSelezionate"]');
    const checkedCheckboxes = document.querySelectorAll('input[name="fermateSelezionate"]:checked');
    
    // Nascondi tutti i campi tempo
    allCheckboxes.forEach(checkbox => {
        const tempoContainer = document.getElementById('tempo_' + checkbox.value);
        const tempoInput = document.getElementById('tempo_' + checkbox.value + '_input');
        
        if (tempoContainer && tempoInput) {
            tempoContainer.style.display = 'none';
            tempoInput.required = false;
            tempoInput.value = '';
            clearFieldError(tempoInput);
        }
    });
    
    // Mostra i campi tempo solo per le fermate selezionate (eccetto l'ultima)
    for (let i = 0; i < checkedCheckboxes.length - 1; i++) {
        const checkbox = checkedCheckboxes[i];
        const tempoContainer = document.getElementById('tempo_' + checkbox.value);
        const tempoInput = document.getElementById('tempo_' + checkbox.value + '_input');
        
        if (tempoContainer && tempoInput) {
            tempoContainer.style.display = 'block';
            tempoInput.required = true;
            
            // Aggiungi placeholder dinamico
            const fermataCorrente = checkbox.parentElement.querySelector('.fermata-name').textContent;
            const prossimaFermata = checkedCheckboxes[i + 1] ? 
                checkedCheckboxes[i + 1].parentElement.querySelector('.fermata-name').textContent : '';
            
            if (prossimaFermata) {
                tempoInput.placeholder = `Da ${fermataCorrente} a ${prossimaFermata}`;
                tempoInput.title = `Tempo di percorrenza da ${fermataCorrente} a ${prossimaFermata}`;
            }
        }
    }
    
    // Aggiorna la validazione dei tempi
    validateTempiInputs();
}

/**
 * Valida la selezione delle fermate
 */
function validateFermateSelection() {
    const checkedBoxes = document.querySelectorAll('input[name="fermateSelezionate"]:checked');
    const isValid = checkedBoxes.length >= 2;
    
    formValidator.fermate = isValid;
    
    const fermateContainer = document.querySelector('.fermate-container');
    if (fermateContainer) {
        fermateContainer.classList.toggle('invalid', !isValid);
        
        // Rimuovi messaggi di errore precedenti
        const existingError = fermateContainer.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
        
        // Aggiungi messaggio di errore se necessario
        if (!isValid && checkedBoxes.length > 0) {
            const errorMsg = document.createElement('div');
            errorMsg.className = 'error-message';
            errorMsg.textContent = 'Seleziona almeno 2 fermate per creare una tratta.';
            fermateContainer.appendChild(errorMsg);
        }
    }
    
    return isValid;
}

/**
 * Valida tutti i campi tempo
 */
function validateTempiInputs() {
    const requiredTempiInputs = document.querySelectorAll('.tempo-input[required]');
    let allValid = true;
    
    requiredTempiInputs.forEach(input => {
        if (!validateTempoInput(input)) {
            allValid = false;
        }
    });
    
    formValidator.tempi = allValid;
    return allValid;
}

/**
 * Valida un singolo campo tempo
 */
function validateTempoInput(input) {
    const value = parseInt(input.value);
    const isValid = !isNaN(value) && value > 0;
    
    input.classList.toggle('valid', isValid && input.value !== '');
    input.classList.toggle('invalid', !isValid && input.value !== '');
    
    return isValid;
}

/**
 * Valida un campo generico
 */
function validateField(field) {
    let isValid = false;
    const value = field.value.trim();
    
    switch (field.id) {
        case 'nome':
            isValid = value.length >= 3;
            formValidator.nome = isValid;
            break;
            
        case 'costo':
            const cost = parseFloat(value);
            isValid = !isNaN(cost) && cost > 0;
            formValidator.costo = isValid;
            break;
    }
    
    // Aggiorna le classi CSS
    field.classList.toggle('valid', isValid);
    field.classList.toggle('invalid', !isValid);
    
    // Gestisci messaggi di errore
    if (!isValid && value !== '') {
        showFieldError(field, getErrorMessage(field.id));
    } else {
        clearFieldError(field);
    }
    
    return isValid;
}

/**
 * Mostra un messaggio di errore per un campo
 */
function showFieldError(field, message) {
    clearFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
}

/**
 * Rimuove il messaggio di errore da un campo
 */
function clearFieldError(field) {
    const errorMsg = field.parentNode.querySelector('.error-message');
    if (errorMsg) {
        errorMsg.remove();
    }
    
    field.classList.remove('invalid');
}

/**
 * Restituisce il messaggio di errore appropriato per un campo
 */
function getErrorMessage(fieldId) {
    const messages = {
        'nome': 'Il nome della tratta deve contenere almeno 3 caratteri',
        'costo': 'Inserisci un costo valido maggiore di 0'
    };
    
    return messages[fieldId] || 'Campo non valido';
}

/**
 * Gestisce il submit del form
 */
function handleFormSubmit(event) {
    console.log('Submit del form...');
    
    // Valida tutti i campi
    const nomeInput = document.getElementById('nome');
    const costoInput = document.getElementById('costo');
    
    validateField(nomeInput);
    validateField(costoInput);
    validateFermateSelection();
    validateTempiInputs();
    
    // Controlla se il form è valido
    const isFormValid = Object.values(formValidator).every(valid => valid);
    
    if (!isFormValid) {
        event.preventDefault();
        showGeneralError();
        return false;
    }
    
    // Controlla validazioni specifiche aggiuntive
    if (!performFinalValidation()) {
        event.preventDefault();
        return false;
    }
    
    // Mostra indicatore di caricamento
    showSubmitLoading();
    
    return true;
}

/**
 * Esegue validazioni finali prima del submit
 */
function performFinalValidation() {
    const checkedBoxes = document.querySelectorAll('input[name="fermateSelezionate"]:checked');
    
    if (checkedBoxes.length < 2) {
        showAlert('Seleziona almeno 2 fermate per creare una tratta.');
        return false;
    }
    
    // Verifica che tutti i tempi richiesti siano compilati
    for (let i = 0; i < checkedBoxes.length - 1; i++) {
        const checkbox = checkedBoxes[i];
        const tempoInput = document.getElementById('tempo_' + checkbox.value + '_input');
        
        if (!tempoInput || !tempoInput.value || parseInt(tempoInput.value) <= 0) {
            showAlert('Inserisci tutti i tempi di percorrenza tra le fermate.');
            tempoInput?.focus();
            return false;
        }
    }
    
    return true;
}

/**
 * Mostra un errore generale del form
 */
function showGeneralError() {
    const firstInvalidField = document.querySelector('.form-input.invalid');
    if (firstInvalidField) {
        firstInvalidField.focus();
        firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
    
    showAlert('Per favore, correggi tutti i campi evidenziati in rosso.');
}

/**
 * Mostra l'indicatore di caricamento durante il submit
 */
function showSubmitLoading() {
    const submitBtn = document.querySelector('.submit-btn');
    if (submitBtn) {
        submitBtn.textContent = 'Creazione in corso...';
        submitBtn.disabled = true;
        submitBtn.style.opacity = '0.7';
    }
}

/**
 * Mostra un alert personalizzato
 */
function showAlert(message) {
    // Puoi personalizzare questo per usare modal o notifiche più eleganti
    alert(message);
}

/**
 * Utility: debug del form
 */
function debugForm() {
    console.log('Stato del form:', {
        selectedFermate: selectedFermate,
        validator: formValidator,
        checkedBoxes: document.querySelectorAll('input[name="fermateSelezionate"]:checked').length,
        requiredTempi: document.querySelectorAll('.tempo-input[required]').length
    });
}

// Esponi alcune funzioni per debugging (solo in development)
if (typeof window !== 'undefined') {
    window.addTrattaDebug = {
        debugForm,
        validateFermateSelection,
        validateTempiInputs,
        formValidator
    };
}
