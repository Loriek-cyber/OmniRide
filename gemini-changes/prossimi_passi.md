# Prossimi Passi per lo Sviluppo

Questo file elenca le funzionalità suggerite da implementare per completare e migliorare il sistema di autenticazione e l'area utente.

### 1. Creazione di un Filtro di Autenticazione
- **Obiettivo:** Proteggere le aree riservate (es. `/prvUser/*`, `/prvAdmin/*`) in modo centralizzato e sicuro.
- **Logica:** Creare un `AuthenticationFilter` che intercetta le richieste a URL protetti. Se l'utente non è in sessione (`session.getAttribute("utente") == null`), il filtro deve reindirizzarlo automaticamente alla pagina di login. Questo evita di dover ripetere il controllo di sicurezza in ogni servlet protetta.

### 2. Implementazione del Logout
- **Obiettivo:** Permettere agli utenti di terminare la loro sessione in modo sicuro.
- **Logica:** Creare una `LogoutServlet` che:
  1. Invalida la sessione corrente (`session.invalidate()`).
  2. Reindirizza l'utente alla pagina di login o alla homepage con un messaggio di avvenuto logout.

### 3. Sviluppo della Dashboard Utente Dinamica
- **Obiettivo:** Trasformare `dashboard.jsp` da una pagina statica a una pagina dinamica e personalizzata.
- **Logica:** Modificare la `dashboard.jsp` per utilizzare i dati dell'oggetto `Utente` salvato in sessione. Ad esempio, mostrare un messaggio di benvenuto personalizzato (es. "Benvenuto, Nome Cognome") e preparare l'area per visualizzare contenuti specifici dell'utente, come i biglietti acquistati.
