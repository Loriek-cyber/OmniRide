document.addEventListener('DOMContentLoaded', () => {
    const tratte = JSON.parse(document.getElementById('tratte-data').textContent);
    const listContainer = document.getElementById('tratte-list');
    const detailsContainer = document.getElementById('details-content');
    const placeholder = document.getElementById('details-placeholder');

    if (tratte.length === 0) {
        listContainer.innerHTML = '<p>Nessuna tratta trovata. <a href="addTratta.jsp">Aggiungine una</a>.</p>';
        return;
    }

    // Popola la lista
    tratte.forEach((tratta, index) => {
        const item = document.createElement('div');
        item.className = 'tratta-item';
        item.dataset.index = index;
        item.innerHTML = `
            <div>
                <span class="status-indicator ${tratta.attiva ? 'active' : 'inactive'}"></span>
                <strong>${tratta.nome}</strong>
            </div>
            <span>ID: ${tratta.id}</span>
        `;
        item.addEventListener('click', () => showDetails(tratta, item));
        listContainer.appendChild(item);
    });

    function showDetails(tratta, element) {
        document.querySelectorAll('.tratta-item').forEach(el => el.classList.remove('selected'));
        element.classList.add('selected');

        placeholder.style.display = 'none';
        
        let fermateHtml = '<ul>';
        tratta.fermataTrattaList.forEach(ft => {
            fermateHtml += `<li>${ft.fermata.nome}</li>`;
        });
        fermateHtml += '</ul>';

        let orariHtml = '';
        tratta.orari.forEach(orario => {
            orariHtml += `<span class="orario-item">${orario.oraPartenza.substring(0, 5)} (${orario.giorniSettimana})</span>`;
        });

        detailsContainer.innerHTML = `
            <div class="details-header">
                <h3>${tratta.nome}</h3>
                <div class="details-actions">
                    <button class="btn btn-secondary btn-sm" onclick="handleAction('toggle', ${tratta.id})">${tratta.attiva ? 'Disattiva' : 'Attiva'}</button>
                    <button class="btn btn-danger btn-sm" onclick="handleAction('delete', ${tratta.id})">Elimina</button>
                </div>
            </div>
            <div class="details-section">
                <strong>Stato:</strong> ${tratta.attiva ? 'Attiva' : 'Non Attiva'}
            </div>
            <div class="details-section">
                <strong>Costo:</strong> €${tratta.costo.toFixed(2)}
            </div>
            <div class="details-section">
                <h4>Fermate</h4>
                ${fermateHtml}
            </div>
            <div class="details-section">
                <h4>Orari</h4>
                <div class="orari-grid">${orariHtml}</div>
            </div>
        `;
        detailsContainer.style.display = 'block';
    }

    window.handleAction = async function(action, trattaId) {
        if (action === 'delete' && !confirm('Sei sicuro di voler eliminare questa tratta? L\'azione è irreversibile.')) {
            return;
        }

        try {
            const response = await fetch('gestisciTratte', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=${action}&trattaId=${trattaId}`
            });

            const result = await response.json();

            if (result.success) {
                location.reload(); // Ricarica la pagina per vedere le modifiche
            } else {
                alert('Errore: ' + result.error);
            }
        } catch (error) {
            console.error('Errore:', error);
            alert('Si è verificato un errore di comunicazione con il server.');
        }
    }
});