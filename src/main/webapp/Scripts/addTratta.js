/**
 * JavaScript per il form di aggiunta tratta con ricerca interattiva
 * Gestisce la ricerca delle fermate, l'aggiunta iterativa e la validazione
 */

// Variabili globali
let selectedFermate = [];
let selectedFermataForAdd = null;
let searchTimeout = null;
let orariInizio = []; // Array per gestire più orari
let formValidator = {
    nome: false,
    costo: false,
    fermate: false,
    orari: false,
    giorni: false
};

// Inizializzazione quando il DOM è caricato
document.addEventListener('DOMContentLoaded', function() {
    initializeForm();
    setupEventListeners();
    initializeOrari(); // Inizializza la gestione degli orari
});

/**
 * Inizializza il form e i suoi componenti
 */
function initializeForm() {
    console.log('Inizializzazione form addTratta con ricerca interattiva...');
    
    // Reset dello stato iniziale
    selectedFermate = [];
    selectedFermataForAdd = null;
    
    // Verifica che i dati delle fermate siano disponibili
    if (!window.fermateData || window.fermateData.length === 0) {
        console.warn('Nessuna fermata disponibile per la ricerca');
        showNoFermateMessage();
        return;
    }
    
    console.log('Fermate disponibili:', window.fermateData.length);
    updateFormValidation();
}

/**
 * Configura tutti gli event listeners
 */
function setupEventListeners() {
    // Event listener per la ricerca fermate
    const searchInput = document.getElementById('fermataSearch');
    if (searchInput) {
        searchInput.addEventListener('input', handleSearchInput);
        searchInput.addEventListener('focus', handleSearchFocus);
        searchInput.addEventListener('blur', handleSearchBlur);
    }
    
    // Event listener per il pulsante aggiungi fermata
    const addBtn = document.getElementById('addFermataBtn');
    if (addBtn) {
        addBtn.addEventListener('click', handleAddFermata);
    }
    
    // Event listener per il form submit
    const form = document.getElementById('addTrattaForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
    
    // Event listener per l'aggiunta di orari
    const addOrarioBtn = document.getElementById('addOrarioBtn');
    if (addOrarioBtn) {
        addOrarioBtn.addEventListener('click', addOrario);
    }
    
    // Event listener per i campi di input (validazione real-time)
    const nomeInput = document.getElementById('nome');
    const costoInput = document.getElementById('costo');
    const orarioInput = document.getElementById('orarioInizio');
    
    if (nomeInput) {
        nomeInput.addEventListener('blur', () => validateField(nomeInput));
        nomeInput.addEventListener('input', () => clearFieldError(nomeInput));
    }
    
    if (costoInput) {
        costoInput.addEventListener('blur', () => validateField(costoInput));
        costoInput.addEventListener('input', () => clearFieldError(costoInput));
    }
    
    if (orarioInput) {
        orarioInput.addEventListener('change', () => validateField(orarioInput));
    }
    
    // Event listener per i giorni
    const giornoCheckboxes = document.querySelectorAll('input[name="giorni"]');
    giornoCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', validateGiorni);
    });
    
    // Event listener per chiudere i risultati di ricerca quando si clicca fuori
    document.addEventListener('click', function(e) {
        const searchContainer = document.querySelector('.search-box');
        if (searchContainer && !searchContainer.contains(e.target)) {
            hideSearchResults();
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
    
        showAlert('Per favore, correggi tutti i campi evidenziati in rosso e assicurati che i tempi stimati siano inseriti correttamente.');

    // Rimuovi la classe 
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

/**
 * Gestisce l'input nella ricerca fermate
 */
function handleSearchInput(event) {
    const query = event.target.value.trim();
    
    // Cancella il timeout precedente
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    
    // Debounce la ricerca
    searchTimeout = setTimeout(() => {
        if (query.length >= 2) {
            performSearch(query);
        } else {
            hideSearchResults();
        }
    }, 300);
}

/**
 * Gestisce il focus sul campo di ricerca
 */
function handleSearchFocus(event) {
    const query = event.target.value.trim();
    if (query.length >= 2) {
        performSearch(query);
    }
}

/**
 * Gestisce la perdita di focus dal campo di ricerca
 */
function handleSearchBlur(event) {
    // Ritarda la chiusura per permettere i click sui risultati
    setTimeout(() => {
        hideSearchResults();
    }, 200);
}

/**
 * Esegue la ricerca delle fermate
 */
function performSearch(query) {
    if (!window.fermateData || window.fermateData.length === 0) {
        showNoFermateMessage();
        return;
    }
    
    const results = window.fermateData.filter(fermata => {
        const nomeMatch = fermata.nome.toLowerCase().includes(query.toLowerCase());
        const indirizzoMatch = fermata.indirizzo && fermata.indirizzo.toLowerCase().includes(query.toLowerCase());
        const isAlreadySelected = selectedFermate.some(selected => selected.id === fermata.id);
        
        return (nomeMatch || indirizzoMatch) && !isAlreadySelected;
    });
    
    displaySearchResults(results);
}

/**
 * Mostra i risultati della ricerca
 */
function displaySearchResults(results) {
    const searchResults = document.getElementById('searchResults');
    if (!searchResults) return;
    
    searchResults.innerHTML = '';
    
    if (results.length === 0) {
        searchResults.innerHTML = '<div class="no-results">Nessuna fermata trovata</div>';
        searchResults.style.display = 'block';
        return;
    }
    
    results.forEach(fermata => {
        const resultItem = document.createElement('div');
        resultItem.className = 'search-result-item';
        resultItem.innerHTML = `
            <div class="fermata-info">
                <div class="fermata-name">${fermata.nome}</div>
                <div class="fermata-address">${fermata.indirizzo || 'Indirizzo non specificato'}</div>
            </div>
        `;
        
        resultItem.addEventListener('click', () => {
            selectFermataForAdd(fermata);
        });
        
        searchResults.appendChild(resultItem);
    });
    
    searchResults.style.display = 'block';
}

/**
 * Nasconde i risultati della ricerca
 */
function hideSearchResults() {
    const searchResults = document.getElementById('searchResults');
    if (searchResults) {
        searchResults.style.display = 'none';
    }
}

/**
 * Seleziona una fermata per l'aggiunta
 */
function selectFermataForAdd(fermata) {
    selectedFermataForAdd = fermata;
    
    // Aggiorna il campo di ricerca
    const searchInput = document.getElementById('fermataSearch');
    if (searchInput) {
        searchInput.value = `${fermata.nome} - ${fermata.indirizzo || 'Indirizzo non specificato'}`;
    }
    
    // Abilita il pulsante aggiungi
    const addBtn = document.getElementById('addFermataBtn');
    if (addBtn) {
        addBtn.disabled = false;
    }
    
    hideSearchResults();
}

/**
 * Gestisce l'aggiunta di una fermata
 */
function handleAddFermata() {
    if (!selectedFermataForAdd) return;
    
    // Aggiungi la fermata alla lista
    selectedFermate.push(selectedFermataForAdd);
    
    // Aggiorna la visualizzazione
    updateSelectedFermateDisplay();
    
    // Reset del form di ricerca
    resetSearchForm();
    
    // Aggiorna la validazione
    updateFormValidation();
    
    console.log('Fermata aggiunta:', selectedFermataForAdd);
}

/**
 * Aggiorna la visualizzazione delle fermate selezionate
 */
function updateSelectedFermateDisplay() {
    const selectedContainer = document.getElementById('selectedFermate');
    const emptyState = document.getElementById('emptyState');
    
    if (!selectedContainer) return;
    
    // Nascondi lo stato vuoto se ci sono fermate
    if (emptyState) {
        emptyState.style.display = selectedFermate.length === 0 ? 'block' : 'none';
    }
    
    // Rimuovi le fermate esistenti (eccetto lo stato vuoto)
    const existingItems = selectedContainer.querySelectorAll('.selected-fermata-item');
    existingItems.forEach(item => item.remove());
    
    // Aggiungi le fermate selezionate
    selectedFermate.forEach((fermata, index) => {
        const fermataItem = createSelectedFermataItem(fermata, index);
        selectedContainer.appendChild(fermataItem);
    });
}

/**
 * Crea un elemento per una fermata selezionata
 */
function createSelectedFermataItem(fermata, index) {
    const item = document.createElement('div');
    item.className = 'selected-fermata-item';
    
    const isLast = index === selectedFermate.length - 1;
    const showTempoInput = !isLast;
    
    item.innerHTML = `
        <div class="fermata-order">${index + 1}</div>
        <div class="fermata-details">
            <div class="fermata-name">${fermata.nome}</div>
            <div class="fermata-address">${fermata.indirizzo || 'Indirizzo non specificato'}</div>
        </div>
        ${showTempoInput ? `
            <div class="tempo-input-container">
                <label>Tempo alla prossima (min):</label>
                <input type="number" 
                       class="tempo-input" 
                       placeholder="5" 
                       min="1" 
                       step="1" 
                       data-fermata-id="${fermata.id}">
            </div>
        ` : ''}
        <button type="button" class="remove-fermata-btn" data-fermata-id="${fermata.id}">
            <span class="icon">×</span>
        </button>
    `;
    
    // Aggiungi event listener per il pulsante rimuovi
    const removeBtn = item.querySelector('.remove-fermata-btn');
    if (removeBtn) {
        removeBtn.addEventListener('click', () => {
            removeFermata(fermata.id);
        });
    }
    
    // Aggiungi event listener per il campo tempo
    const tempoInput = item.querySelector('.tempo-input');
    if (tempoInput) {
        tempoInput.addEventListener('input', function(event) {
            validateTempoInput(event.target);
            updateTempoTotaleDisplay(); // Aggiorna il tempo totale
        });
    }
    
    return item;
}

/**
 * Rimuove una fermata dalla selezione
 */
function removeFermata(fermataId) {
    selectedFermate = selectedFermate.filter(fermata => fermata.id !== fermataId);
    updateSelectedFermateDisplay();
    updateFormValidation();
}

/**
 * Reset del form di ricerca
 */
function resetSearchForm() {
    const searchInput = document.getElementById('fermataSearch');
    const addBtn = document.getElementById('addFermataBtn');
    
    if (searchInput) {
        searchInput.value = '';
    }
    
    if (addBtn) {
        addBtn.disabled = true;
    }
    
    selectedFermataForAdd = null;
    hideSearchResults();
}

/**
 * Valida un campo tempo
 */
function validateTempoInput(input) {
    const value = parseInt(input.value);
    const isValid = !isNaN(value) && value > 0;
    
    input.classList.toggle('valid', isValid && input.value !== '');
    input.classList.toggle('invalid', !isValid && input.value !== '');
    
    return isValid;
}

/**
 * Valida i giorni selezionati
 */
function validateGiorni() {
    const selectedGiorni = document.querySelectorAll('input[name="giorni"]:checked');
    const isValid = selectedGiorni.length > 0;
    
    formValidator.giorni = isValid;
    
    const daysContainer = document.querySelector('.days-container');
    if (daysContainer) {
        daysContainer.classList.toggle('invalid', !isValid);
    }
    
    return isValid;
}

/**
 * Valida il campo orario
 */
function validateOrario() {
    const orarioInput = document.getElementById('orarioInizio');
    const isValid = orarioInput && orarioInput.value !== '';
    
    formValidator.orario = isValid;
    
    if (orarioInput) {
        orarioInput.classList.toggle('valid', isValid);
        orarioInput.classList.toggle('invalid', !isValid);
    }
    
    return isValid;
}

/**
 * Aggiorna la validazione del form
 */
function updateFormValidation() {
    // Valida le fermate
    formValidator.fermate = selectedFermate.length >= 2;
    
    // Valida i giorni
    validateGiorni();
    
    // Valida l'orario
    validateOrario();
    
    // Aggiorna il pulsante submit
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        const isFormValid = formValidator.fermate && formValidator.orario && formValidator.giorni;
        submitBtn.disabled = !isFormValid;
    }
}

/**
 * Mostra il messaggio quando non ci sono fermate
 */
function showNoFermateMessage() {
    const selectedContainer = document.getElementById('selectedFermate');
    if (selectedContainer) {
        selectedContainer.innerHTML = `
            <div class="no-fermate-message">
                <p>Nessuna fermata disponibile per la ricerca.</p>
                <p><a href="../addFermata">Aggiungi prima alcune fermate</a>.</p>
            </div>
        `;
    }
}

/**
 * Prepara i dati per il submit del form
 */
function prepareFormData() {
    // Prepara i dati delle fermate
    const fermateIds = selectedFermate.map(fermata => fermata.id).join(',');
    const fermateInput = document.getElementById('fermateSelezionate');
    if (fermateInput) {
        fermateInput.value = fermateIds;
    }
    
    // Prepara i dati dei tempi
    const tempoInputs = document.querySelectorAll('.tempo-input');
    const tempi = [];
    tempoInputs.forEach(input => {
        const value = parseInt(input.value);
        if (!isNaN(value) && value > 0) {
            tempi.push(value);
        } else {
            tempi.push(0); // Valore di default
        }
    });
    
    const tempiInput = document.getElementById('tempiTraFermate');
    if (tempiInput) {
        tempiInput.value = tempi.join(',');
    }
}

/**
 * Override del handleFormSubmit per includere la nuova logica
 */
function handleFormSubmit(event) {
    console.log('Submit del form...');
    
    // Prepara i dati prima della validazione
    prepareFormData();
    prepareOrariData();
    
    // Valida tutti i campi
    const nomeInput = document.getElementById('nome');
    const costoInput = document.getElementById('costo');
    
    if (nomeInput) validateField(nomeInput);
    if (costoInput) validateField(costoInput);
    
    updateFormValidation();
    
    // Controlla se il form è valido
    const isFormValid = formValidator.nome && formValidator.costo && 
                       formValidator.fermate && formValidator.orari && 
                       formValidator.giorni;
    
    if (!isFormValid) {
        event.preventDefault();
        showGeneralError();
        return false;
    }
    
    // Validazioni specifiche aggiuntive
    if (selectedFermate.length < 2) {
        event.preventDefault();
        showAlert('Seleziona almeno 2 fermate per creare una tratta.');
        return false;
    }
    
    // Mostra indicatore di caricamento
    showSubmitLoading();
    
    return true;
}


/**
 * Calcola il tempo totale di percorrenza della tratta
 */
function calcolaTempotTotaleTratta() {
    const tempoInputs = document.querySelectorAll('.tempo-input');
    let tempoTotale = 0;
    
    tempoInputs.forEach(input => {
        const tempo = parseInt(input.value);
        if (!isNaN(tempo) && tempo > 0) {
            tempoTotale += tempo;
        }
    });
    
    return tempoTotale;
}

/**
 * Mostra il tempo totale della tratta nell'interfaccia
 */
function updateTempoTotaleDisplay() {
    const tempoTotale = calcolaTempotTotaleTratta();
    
    // Cerca o crea l'elemento per mostrare il tempo totale
    let tempoTotaleDiv = document.querySelector('.tempo-totale-tratta');
    if (!tempoTotaleDiv && tempoTotale > 0) {
        tempoTotaleDiv = document.createElement('div');
        tempoTotaleDiv.className = 'tempo-totale-tratta';
        tempoTotaleDiv.innerHTML = `
            <div class="tempo-totale-label">Tempo totale stimato:</div>
            <div class="tempo-totale-value">${tempoTotale} minuti</div>
        `;
        
        // Inserisci prima del pulsante submit
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.parentNode.insertBefore(tempoTotaleDiv, submitBtn);
        }
    } else if (tempoTotaleDiv) {
        const valueElement = tempoTotaleDiv.querySelector('.tempo-totale-value');
        if (valueElement) {
            valueElement.textContent = `${tempoTotale} minuti`;
        }
    }
}

/**
 * Aggiunge un nuovo orario di inizio
 */
function addOrario() {
    const orarioContainer = document.getElementById('orariContainer');
    if (!orarioContainer) return;
    
    const orarioIndex = orariInizio.length;
    
    // Crea il nuovo elemento orario
    const orarioItem = document.createElement('div');
    orarioItem.className = 'orario-item';
    orarioItem.innerHTML = `
        <input type="time" 
               class="orario-input" 
               name="orariInizio" 
               data-index="${orarioIndex}"
               required>
        <button type="button" 
                class="remove-orario-btn" 
                data-index="${orarioIndex}">
            <span class="icon">×</span>
        </button>
    `;
    
    // Aggiungi event listeners
    const orarioInput = orarioItem.querySelector('.orario-input');
    const removeBtn = orarioItem.querySelector('.remove-orario-btn');
    
    orarioInput.addEventListener('change', function() {
        updateOrario(orarioIndex, this.value);
        validateOrari();
    });
    
    removeBtn.addEventListener('click', function() {
        removeOrario(orarioIndex);
    });
    
    // Aggiungi al container
    orarioContainer.appendChild(orarioItem);
    
    // Aggiungi all'array
    orariInizio.push('');
    
    // Aggiorna la validazione
    validateOrari();
    
    // Focus sul nuovo campo
    orarioInput.focus();
    
    console.log('Orario aggiunto. Totale orari:', orariInizio.length);
}

/**
 * Rimuove un orario specifico
 */
function removeOrario(index) {
    const orarioItems = document.querySelectorAll('.orario-item');
    
    // Trova e rimuovi l'elemento con animazione
    orarioItems.forEach(item => {
        const input = item.querySelector('.orario-input');
        if (input && parseInt(input.dataset.index) === index) {
            item.classList.add('removing');
            setTimeout(() => {
                item.remove();
            }, 300);
        }
    });
    
    // Rimuovi dall'array
    orariInizio.splice(index, 1);
    
    // Aggiorna gli indici
    updateOrariIndices();
    
    // Aggiorna la validazione
    validateOrari();
    
    console.log('Orario rimosso. Totale orari:', orariInizio.length);
}

/**
 * Aggiorna un orario specifico
 */
function updateOrario(index, value) {
    if (index >= 0 && index < orariInizio.length) {
        orariInizio[index] = value;
        updateOrariSummary();
    }
}

/**
 * Aggiorna gli indici degli orari dopo una rimozione
 */
function updateOrariIndices() {
    const orarioItems = document.querySelectorAll('.orario-item');
    
    orarioItems.forEach((item, newIndex) => {
        const input = item.querySelector('.orario-input');
        const removeBtn = item.querySelector('.remove-orario-btn');
        
        if (input) input.dataset.index = newIndex;
        if (removeBtn) removeBtn.dataset.index = newIndex;
    });
}

/**
 * Valida gli orari inseriti
 */
function validateOrari() {
    const orariValidi = orariInizio.filter(orario => orario && orario.trim() !== '');
    const isValid = orariValidi.length > 0;
    
    formValidator.orari = isValid;
    
    // Aggiorna gli stili degli input
    const orarioInputs = document.querySelectorAll('.orario-input');
    orarioInputs.forEach(input => {
        const hasValue = input.value && input.value.trim() !== '';
        input.classList.toggle('valid', hasValue);
        input.classList.toggle('invalid', !hasValue);
    });
    
    // Aggiorna il summary
    updateOrariSummary();
    
    // Aggiorna la validazione generale del form
    updateFormValidation();
    
    return isValid;
}

/**
 * Aggiorna il summary degli orari
 */
function updateOrariSummary() {
    const orariContainer = document.getElementById('orariContainer');
    if (!orariContainer) return;
    
    // Rimuovi il summary esistente
    const existingSummary = orariContainer.querySelector('.orari-summary');
    if (existingSummary) {
        existingSummary.remove();
    }
    
    // Filtra solo gli orari validi
    const orariValidi = orariInizio.filter(orario => orario && orario.trim() !== '');
    
    if (orariValidi.length === 0) return;
    
    // Crea il nuovo summary
    const summary = document.createElement('div');
    summary.className = 'orari-summary';
    
    const orariOrdinati = [...orariValidi].sort();
    
    summary.innerHTML = `
        <div class="orari-summary-title">Orari configurati (${orariOrdinati.length}):</div>
        <div class="orari-list">
            ${orariOrdinati.map(orario => `<span class="orario-tag">${orario}</span>`).join('')}
        </div>
    `;
    
    orariContainer.appendChild(summary);
}

/**
 * Inizializza la gestione degli orari al caricamento della pagina
 */
function initializeOrari() {
    // Aggiungi automaticamente il primo orario
    addOrario();
}

/**
 * Prepara i dati degli orari per il submit
 */
function prepareOrariData() {
    const orariValidi = orariInizio.filter(orario => orario && orario.trim() !== '');
    
    // Crea un campo hidden per gli orari
    let orariInput = document.getElementById('orariInizioData');
    if (!orariInput) {
        orariInput = document.createElement('input');
        orariInput.type = 'hidden';
        orariInput.id = 'orariInizioData';
        orariInput.name = 'orariInizio';
        document.getElementById('addTrattaForm').appendChild(orariInput);
    }
    
    orariInput.value = orariValidi.join(',');
    
    console.log('Dati orari preparati:', orariValidi);
}

// Esponi alcune funzioni per debugging (solo in development)
if (typeof window !== 'undefined') {
    window.addTrattaDebug = {
        debugForm,
        selectedFermate,
        formValidator,
        performSearch,
        updateSelectedFermateDisplay,
        calcolaTempotTotaleTratta,
        addOrario,
        removeOrario,
        orariInizio
    };
}
