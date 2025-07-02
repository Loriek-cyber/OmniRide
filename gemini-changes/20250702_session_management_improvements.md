# Analisi e Miglioramenti per la Gestione della Sessione (2025-07-02)

**Obiettivo:** Migliorare la persistenza e la sicurezza della sessione utente nell'applicazione.

**Analisi della Gestione della Sessione Attuale:**

*   **LoginServlet:**
    *   Verifica la presenza di una sessione e dell'attributo "utente" per reindirizzare alla dashboard se già loggato.
    *   In caso di login riuscito, crea una nuova sessione (`req.getSession(true)`) e imposta l'attributo `session.setAttribute("utente", utente);`.
    *   Non gestisce esplicitamente la persistenza della sessione oltre il timeout del server o la chiusura del browser.
*   **LogoutServlet:**
    *   Invalida la sessione esistente (`session.invalidate()`) e reindirizza alla pagina di login.
*   **RegisterServlet:**
    *   Crea un nuovo utente e reindirizza alla pagina di login con un messaggio di successo o errore.
*   **AuthenticationFilter:**
    *   Controlla l'attributo "utente" nella sessione per l'autenticazione e il ruolo (per l'area admin), reindirizzando o negando l'accesso di conseguenza.
*   **JSP (header.jsp, dashboard.jsp, admin.jsp):**
    *   Utilizzano `sessionScope.utente` per la visualizzazione condizionale di contenuti e link.

**Punti di Forza Attuali:**

*   Utilizzo di `HttpSession` per la gestione dello stato.
*   Filtro di autenticazione per la protezione delle aree riservate.
*   Hashing delle password con BCrypt (presumibilmente gestito dal `UtenteDAO`).

**Suggerimenti di Miglioramento per la Persistenza e la Sicurezza della Sessione:**

1.  **Persistenza della Sessione ("Ricordami"):**
    *   **Problema:** La sessione è legata alla durata del browser o al timeout del server; l'utente deve riloggarsi dopo la chiusura del browser.
    *   **Soluzione Concettuale:**
        *   Implementare una funzionalità "Ricordami" che, al login, generi un token persistente (es. UUID) salvato nel database e in un cookie persistente nel browser.
        *   Al riavvio del browser, se la sessione HTTP non è valida ma il cookie "Ricordami" è presente, il server dovrebbe validare il token, recuperare l'utente e ricreare la sessione HTTP.
        *   **Sicurezza:** I token dovrebbero essere monouso o rigenerati ad ogni utilizzo per prevenire attacchi di "token theft" e invalidati al logout o al cambio password.

2.  **Sicurezza della Sessione:**
    *   **Session Fixation:**
        *   **Problema:** Un attaccante potrebbe predefinire un ID di sessione che l'utente autentica, permettendo all'attaccante di riutilizzarlo.
        *   **Soluzione Concettuale:** In `LoginServlet`, dopo un login riuscito, invalidare esplicitamente la sessione corrente (`req.getSession(false).invalidate();`) e creare una nuova sessione (`req.getSession(true);`). Questo cambia l'ID di sessione.
    *   **Timeout della Sessione:**
        *   **Problema:** Le sessioni possono rimanere attive troppo a lungo, aumentando il rischio di accesso non autorizzato.
        *   **Soluzione Concettuale:** Configurare un timeout di sessione appropriato nel `web.xml` (es. `<session-config><session-timeout>30</session-timeout></session-config>`) o programmaticamente (`session.setMaxInactiveInterval(secondi);`).
    *   **HTTPS:**
        *   **Problema:** I cookie di sessione e i dati di login possono essere intercettati su connessioni non sicure.
        *   **Soluzione Concettuale:** Assicurarsi che l'applicazione sia sempre servita tramite HTTPS in ambiente di produzione per proteggere i dati in transito e i cookie di sessione.
    *   **HttpOnly e Secure Flags per i Cookie:**
        *   **Problema:** I cookie di sessione potrebbero essere accessibili via JavaScript (rischio XSS) o inviati su connessioni non sicure anche con HTTPS.
        *   **Soluzione Concettuale:** Configurare il server delle applicazioni (es. Tomcat) per impostare automaticamente i flag `HttpOnly` (impedisce accesso JS) e `Secure` (invia solo su HTTPS) sui cookie di sessione.

3.  **Miglioramento della Gestione degli Attributi di Sessione:**
    *   **Problema:** L'intero oggetto `Utente` viene salvato direttamente nella sessione, potenzialmente inefficiente o rischioso se contiene dati sensibili non strettamente necessari.
    *   **Soluzione Concettuale:** Salvare nella sessione solo le informazioni essenziali dell'utente (es. `idUtente`, `ruolo`). Recuperare altri dati dell'utente dal database solo quando strettamente necessari.

4.  **Messaggi Utente (Flash Messages):**
    *   **Problema:** I messaggi di successo/errore impostati come attributi della richiesta (`req.setAttribute`) scompaiono al ricaricamento della pagina o dopo un redirect.
    *   **Soluzione Concettuale:** Implementare un meccanismo di "Flash Messages" che salvi i messaggi in sessione e li rimuova automaticamente dopo essere stati visualizzati una volta. Questo può essere fatto manualmente (salvando in sessione e poi rimuovendo nella JSP) o tramite librerie dedicate.

Questa analisi fornisce una base per migliorare la robustezza e la sicurezza della gestione delle sessioni nel progetto Omniride.
