document.addEventListener('DOMContentLoaded', function() {
    // Inizializza il sistema di cookie del carrello se disponibile
    if (window.CartCookieManager) {
        window.CartCookieManager.updateBadge();
    }
    
    const quantityButtons = document.querySelectorAll('.quantity-btn');
    const removeButtons = document.querySelectorAll('.remove-btn');
    
    quantityButtons.forEach(button => {
        button.addEventListener('click', function() {
            const index = this.dataset.index;
            const delta = this.dataset.delta;
            updateQuantity(index, parseInt(delta));
        });
    });
    
    removeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const index = this.dataset.index;
            removeItem(index);
        });
    });
});

function updateQuantity(index, delta) {
    // Retrieve the current quantity from the display
    const quantityDisplay = document.querySelector(`.quantity-display[data-index='${index}']`);
    const currentQuantity = parseInt(quantityDisplay.innerText);
    const newQuantity = currentQuantity + delta;

    // Ensure new quantity bounds
    if (newQuantity < 1 || newQuantity > 10) return;

    // Aggiorna anche nei cookie se disponibile
    if (window.CartCookieManager) {
        window.CartCookieManager.updateQuantity(index, newQuantity);
    }

    // Create and submit a form to update server-side session data
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/carrello';

    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'aggiorna';
    form.appendChild(actionInput);

    const indexInput = document.createElement('input');
    indexInput.type = 'hidden';
    indexInput.name = 'indice';
    indexInput.value = index;
    form.appendChild(indexInput);

    const quantityInput = document.createElement('input');
    quantityInput.type = 'hidden';
    quantityInput.name = 'quantita';
    quantityInput.value = newQuantity;
    form.appendChild(quantityInput);

    document.body.appendChild(form);
    form.submit();
}

function removeItem(index) {
    // Confirmation dialog
    if (!confirm('Sei sicuro di voler rimuovere questo articolo dal carrello?')) return;

    // Rimuovi anche dai cookie se disponibile
    if (window.CartCookieManager) {
        window.CartCookieManager.removeFromCart(index);
    }

    // Create and submit a form to update server-side session data
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/carrello';

    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'rimuovi';
    form.appendChild(actionInput);

    const indexInput = document.createElement('input');
    indexInput.type = 'hidden';
    indexInput.name = 'indice';
    indexInput.value = index;
    form.appendChild(indexInput);

    document.body.appendChild(form);
    form.submit();
}
