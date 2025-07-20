document.addEventListener('DOMContentLoaded', () => {
    const percorsi = JSON.parse(document.getElementById('percorsi-data').textContent);
    const listContainer = document.getElementById('results-list');
    const detailsContainer = document.getElementById('details-content');
    const placeholder = document.getElementById('details-placeholder');

    if (percorsi.length === 0) {
        listContainer.innerHTML = '<div class="no-results"><p>Nessun percorso trovato per questa ricerca.</p></div>';
        return;
    }

    // Popola la lista dei percorsi
    percorsi.forEach((percorso, index) => {
        const card = document.createElement('div');
        card.className = 'percorso-card';
        card.dataset.index = index;
        
        const cambi = percorso.segmenti.length - 1;
        let tipoPercorso = 'Diretto';
        if (cambi > 0) {
            tipoPercorso = `${cambi} cambio${cambi > 1 ? 'i' : ''}`;
        }

        card.innerHTML = `
            <div class="percorso-header">
                <span class="percorso-route">${percorso.segmenti[0].fermataIn.nome} → ${percorso.segmenti[percorso.segmenti.length - 1].fermataOu.nome}</span>
                <span class="percorso-price">€${percorso.costo.toFixed(2)}</span>
            </div>
            <div class="percorso-info">
                <span>${tipoPercorso}</span>
                <span>Durata: ${calculateDuration(percorso)}</span>
            </div>
        `;
        card.addEventListener('click', () => showDetails(percorso, card));
        listContainer.appendChild(card);
    });

    function showDetails(percorso, cardElement) {
        // Evidenzia la card selezionata
        document.querySelectorAll('.percorso-card').forEach(c => c.classList.remove('selected'));
        cardElement.classList.add('selected');

        placeholder.style.display = 'none';
        
        let detailsHtml = `<h3>Dettaglio Percorso</h3>`;
        percorso.segmenti.forEach((seg, i) => {
            detailsHtml += `
                <div class="segmento-detail">
                    <div class="segmento-header">
                        <img src="icons/route.svg" class="icon-sm" alt="Tratta"> 
                        <strong>Tratta ${i + 1}:</strong> ${seg.fermataIn.nome} → ${seg.fermataOu.nome}
                    </div>
                    <p>Partenza: <strong>${seg.tempo_partenza || 'N/D'}</strong> - Arrivo: <strong>${seg.tempo_arrivo || 'N/D'}</strong></p>
                    <p>${seg.numero_fermate} fermate intermedie</p>
                </div>`;
        });

        detailsHtml += `
            <div class="details-summary">
                <p>Costo totale: <strong class="prezzo">€${percorso.costo.toFixed(2)}</strong></p>
                <button id="addToCartBtn" class="btn btn-primary" style="width: 100%;">Aggiungi al Carrello</button>
            </div>`;
        
        detailsContainer.innerHTML = detailsHtml;
        detailsContainer.style.display = 'block';

        document.getElementById('addToCartBtn').addEventListener('click', () => addToCart(percorso));
    }

    function addToCart(percorso) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'carrello';
        
        const data = {
            action: 'aggiungi',
            percorsoJson: JSON.stringify(percorso),
            data: document.getElementById('data-viaggio').value,
            orario: document.getElementById('orario-viaggio').value,
            prezzo: percorso.costo,
            quantita: 1,
            tipo: 'Percorso'
        };

        for (const key in data) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = data[key];
            form.appendChild(input);
        }
        document.body.appendChild(form);
        form.submit();
    }

    function calculateDuration(percorso) {
        // Semplice calcolo basato su numero di segmenti e fermate
        // Una logica più precisa richiederebbe i tempi di percorrenza effettivi
        const minutiPerFermata = 3;
        const minutiPerCambio = 15;
        let durataMinuti = (percorso.segmenti.length - 1) * minutiPerCambio;
        percorso.segmenti.forEach(seg => {
            durataMinuti += seg.numero_fermate * minutiPerFermata;
        });

        const ore = Math.floor(durataMinuti / 60);
        const minuti = durataMinuti % 60;

        if (ore > 0) {
            return `${ore}h ${minuti}min`;
        }
        return `${minuti}min`;
    }
});
