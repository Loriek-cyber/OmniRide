document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('search-avvisi');
    if (searchInput) {
        searchInput.addEventListener('keyup', filterAvvisi);
    }
});

function filterAvvisi() {
    const filter = document.getElementById('search-avvisi').value.toLowerCase();
    const avvisiList = document.querySelectorAll('.avviso-item');

    avvisiList.forEach(avvisoElement => {
        const avvisoId = avvisoElement.dataset.avvisoId;
        const avviso = avvisiData[avvisoId];
        
        const descrizione = avviso.descrizione.toLowerCase();
        const nomiTratte = avviso.tratteCoinvolte.map(t => t.nome.toLowerCase()).join(' ');

        if (descrizione.includes(filter) || nomiTratte.includes(filter)) {
            avvisoElement.style.display = '';
        } else {
            avvisoElement.style.display = 'none';
        }
    });
}

function selectAvviso(element, avvisoId) {
    // Evidenzia l'elemento selezionato
    document.querySelectorAll('.avviso-item').forEach(item => {
        item.classList.remove('selected');
    });
    element.classList.add('selected');

    const avviso = avvisiData[avvisoId];
    const detailsContainer = document.getElementById('avviso-details-content');

    let tratteHtml = '<p>Nessuna tratta specificata.</p>';
    if (avviso.tratteCoinvolte.length > 0) {
        tratteHtml = '<ul class="tratte-coinvolte-list">';
        avviso.tratteCoinvolte.forEach(tratta => {
            tratteHtml += `<li><img src="icons/route.svg" class="icon-sm" alt="Tratta"> ${tratta.nome}</li>`;
        });
        tratteHtml += '</ul>';
    }

    detailsContainer.innerHTML = `
        <div class="avviso-details">
            <h3>Dettaglio Avviso</h3>
            <div class="details-section">
                <h4><img src="icons/info.svg" class="icon" alt="Info"> Descrizione</h4>
                <p>${avviso.descrizione}</p>
            </div>
            <div class="details-section">
                <h4><img src="icons/route.svg" class="icon" alt="Tratte"> Tratte Coinvolte</h4>
                ${tratteHtml}
            </div>
        </div>
    `;
}
