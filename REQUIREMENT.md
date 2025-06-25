# Requisiti del Progetto E-commerce

## Requisiti Generali

- Progetto da realizzare in gruppo (2-3 componenti)
- Tutti i componenti devono conoscere l'intero progetto
- Sito di e-commerce per vendita di beni/servizi
- Ambiente di produzione richiesto per la demo

## Funzionalità Cliente

### Cliente non registrato

- Inserimento prodotti nel carrello
- Gestione quantità prodotti
- Rimozione prodotti
- Svuotamento carrello

### Cliente registrato

- Registrazione account
- Login e logout
- Finalizzazione ordini con:
    - Dati spedizione
    - Dati pagamento
- Visualizzazione storico ordini
- Carrello svuotato dopo conferma ordine

## Funzionalità Amministratore

- Login dedicato
- Gestione catalogo:
    - Inserimento prodotti
    - Modifica prodotti
    - Visualizzazione prodotti
    - Cancellazione prodotti
- Visualizzazione ordini filtrati per:
    - Intervallo date
    - Cliente specifico

## Requisiti Tecnici

### Architettura

- Pattern MVC
- Pattern DAO per persistenza dati
- Struttura package:
    - `control` per servlet
    - `model` per classi di modello

### Frontend

- Pagine dinamiche e responsive
- HTML generato solo da JSP
- Header/footer inclusi via direttiva JSP
- Risorse organizzate in cartelle:
    - `scripts` per JavaScript
    - `images` per immagini
    - `styles` per CSS
- Validazione form con:
    - JavaScript
    - Espressioni regolari
    - Gestione errori nel DOM
- AJAX per comunicazioni asincrone

### Backend

- DataSource/DriveManager per DB
- Sessioni per gestione carrello
- Token di sicurezza per controllo accessi
- Salvataggio ordini su DB

### Database

- Prezzi ordini mantenuti anche dopo modifiche catalogo
- Storico ordini preservato dopo cancellazione prodotti

### Sviluppo

- Uso corretto di Git con commit frequenti
- Website Design Document preliminare e coerente