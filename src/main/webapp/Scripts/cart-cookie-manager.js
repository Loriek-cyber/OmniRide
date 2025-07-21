/**
 * Gestore dei cookie per il carrello
 * Salva i dati del carrello nei cookie e sincronizza con la sessione
 */

(function() {
    'use strict';

    const CART_COOKIE_NAME = 'omniride_cart';
    const COOKIE_MAX_AGE = 7 * 24 * 60 * 60 * 1000; // 7 giorni in millisecondi

    /**
     * Ottiene i dati del carrello dai cookie
     */
    function getCartFromCookies() {
        try {
            const cookieValue = getCookie(CART_COOKIE_NAME);
            if (cookieValue) {
                return JSON.parse(decodeURIComponent(cookieValue));
            }
        } catch (e) {
            console.warn('Errore nel parsing del carrello dai cookie:', e);
        }
        return [];
    }

    /**
     * Salva i dati del carrello nei cookie
     */
    function saveCartToCookies(cartData) {
        try {
            const cookieValue = encodeURIComponent(JSON.stringify(cartData));
            const expires = new Date(Date.now() + COOKIE_MAX_AGE);
            setCookie(CART_COOKIE_NAME, cookieValue, expires);
            return true;
        } catch (e) {
            console.error('Errore nel salvataggio del carrello nei cookie:', e);
            return false;
        }
    }

    /**
     * Pulisce il carrello dai cookie
     */
    function clearCartCookies() {
        setCookie(CART_COOKIE_NAME, '', new Date(0));
    }

    /**
     * Sincronizza il carrello con il server
     */
    function syncCartWithServer(cartData) {
        return new Promise((resolve, reject) => {
            fetch('/carrello', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    action: 'sync',
                    cartData: JSON.stringify(cartData)
                })
            })
            .then(response => {
                if (response.ok) {
                    resolve();
                } else {
                    reject(new Error('Errore nella sincronizzazione'));
                }
            })
            .catch(reject);
        });
    }

    /**
     * Aggiunge un item al carrello (sia cookie che server)
     */
    function addToCart(itemData) {
        // Ottieni il carrello attuale
        let cart = getCartFromCookies();
        
        // Verifica se l'item esiste già
        const existingIndex = cart.findIndex(item => 
            item.percorsoJson === itemData.percorsoJson &&
            item.data === itemData.data &&
            item.orario === itemData.orario &&
            item.tipo === itemData.tipo &&
            item.prezzo === itemData.prezzo
        );

        if (existingIndex !== -1) {
            // Aggiorna la quantità
            cart[existingIndex].quantita += parseInt(itemData.quantita || 1);
        } else {
            // Aggiungi nuovo item
            cart.push({
                percorsoJson: itemData.percorsoJson,
                nome: itemData.nome || 'Percorso Personalizzato',
                data: itemData.data,
                orario: itemData.orario,
                prezzo: parseFloat(itemData.prezzo),
                quantita: parseInt(itemData.quantita || 1),
                tipo: itemData.tipo || 'NORMALE'
            });
        }

        // Salva nei cookie
        saveCartToCookies(cart);

        // Aggiorna il badge del carrello
        updateCartBadge();

        // Sincronizza con il server se necessario
        syncCartWithServer(cart).catch(console.warn);

        return cart;
    }

    /**
     * Rimuove un item dal carrello
     */
    function removeFromCart(index) {
        let cart = getCartFromCookies();
        if (index >= 0 && index < cart.length) {
            cart.splice(index, 1);
            saveCartToCookies(cart);
            updateCartBadge();
            syncCartWithServer(cart).catch(console.warn);
        }
        return cart;
    }

    /**
     * Aggiorna la quantità di un item
     */
    function updateCartQuantity(index, quantity) {
        let cart = getCartFromCookies();
        if (index >= 0 && index < cart.length && quantity > 0) {
            cart[index].quantita = quantity;
            saveCartToCookies(cart);
            updateCartBadge();
            syncCartWithServer(cart).catch(console.warn);
        }
        return cart;
    }

    /**
     * Svuota il carrello
     */
    function clearCart() {
        clearCartCookies();
        updateCartBadge();
        syncCartWithServer([]).catch(console.warn);
    }

    /**
     * Aggiorna il badge del carrello nell'header
     */
    function updateCartBadge() {
        const cart = getCartFromCookies();
        const totalItems = cart.reduce((sum, item) => sum + item.quantita, 0);
        
        const cartBadge = document.querySelector('.cart-badge, #cartBadge');
        const cartIcon = document.querySelector('.cart-icon, #cartIcon');
        
        if (cartBadge) {
            if (totalItems > 0) {
                cartBadge.textContent = totalItems;
                cartBadge.style.display = 'flex';
                if (cartIcon) cartIcon.classList.add('has-items');
            } else {
                cartBadge.style.display = 'none';
                if (cartIcon) cartIcon.classList.remove('has-items');
            }
        }
    }

    /**
     * Carica il carrello dai cookie all'avvio
     */
    function loadCartOnStartup() {
        const cart = getCartFromCookies();
        if (cart.length > 0) {
            // Sincronizza con la sessione del server
            syncCartWithServer(cart).catch(console.warn);
        }
        updateCartBadge();
    }

    // Utility functions per i cookie
    function setCookie(name, value, expires) {
        let cookieString = `${name}=${value}; path=/`;
        if (expires) {
            cookieString += `; expires=${expires.toUTCString()}`;
        }
        document.cookie = cookieString;
    }

    function getCookie(name) {
        const nameEQ = name + "=";
        const ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    // Inizializzazione
    document.addEventListener('DOMContentLoaded', loadCartOnStartup);

    // Monitora i cambiamenti nella pagina corrente
    window.addEventListener('focus', updateCartBadge);

    // Espone le funzioni globalmente
    window.CartCookieManager = {
        getCart: getCartFromCookies,
        saveCart: saveCartToCookies,
        addToCart: addToCart,
        removeFromCart: removeFromCart,
        updateQuantity: updateCartQuantity,
        clearCart: clearCart,
        updateBadge: updateCartBadge,
        syncWithServer: syncCartWithServer
    };

})();
