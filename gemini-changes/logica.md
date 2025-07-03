# Logica del Progetto Omniride

Questo file conterrà una descrizione della logica di business del progetto, i flussi di dati, le interazioni tra i componenti e le funzionalità principali.

### L01 - Overview Uri, HTTP, HTML.pdf

Questo documento introduce i concetti fondamentali delle tecnologie web che sono alla base del funzionamento di applicazioni come Omniride.

**Concetti Chiave:**

*   **URI, URL, URN:**
    *   **URI (Uniform Resource Identifier):** Meccanismo generale per identificare una risorsa.
    *   **URL (Uniform Resource Locator):** Specializzazione di URI che identifica una risorsa tramite il suo meccanismo di accesso primario (es. locazione di rete).
    *   **URN (Uniform Resource Name):** Specializzazione di URI che identifica una risorsa tramite un nome unico e duraturo, indipendente dalla sua locazione.
    *   Sintassi degli URL HTTP-like: `<protocol>://[<username>:<password>@]<host>[:<port>][/<path>[?<query>][#fragment]]`.

*   **HTTP (HyperText Transfer Protocol):**
    *   Protocollo a livello applicativo per il trasferimento di risorse web (pagine o elementi di pagina) da server a client.
    *   **Stateless:** Né server né client mantengono, a livello di protocollo, informazioni relative ai messaggi precedentemente scambiati.
    *   **Terminologia:** Client, Server, Connessione, Messaggio (Request/Response), Resource, Entity.
    *   **HTTP 1.0 vs 1.1:**
        *   **1.0:** Connessioni non persistenti (una connessione per ogni richiesta/risposta).
        *   **1.1:** Connessioni persistenti (più richieste/risposte sulla stessa connessione), supporta pipelining (invio di richieste multiple prima di ricevere tutte le risposte).
    *   **HTTP 2.0:** Miglioramenti per ridurre la latenza (compressione header, server push, multiplexing su singola connessione TCP per caricamento parallelo).

*   **Metodi HTTP:**
    *   **GET:** Richiede una risorsa dal server. Parametri nella URL (query string). Lunghezza limitata, visibile, meno sicuro per dati sensibili.
    *   **POST:** Richiede una risorsa e invia dati nel corpo del messaggio. Nessun limite di lunghezza per i parametri, non visibile nella URL, più sicuro per dati sensibili (es. form di login).
    *   **PUT:** Memorizza una risorsa all'URL specificato (creazione o sostituzione).
    *   **DELETE:** Richiede la cancellazione di una risorsa.
    *   **HEAD, OPTIONS, TRACE:** Metodi per metadati, opzioni di comunicazione e diagnostica.

*   **Risposta HTTP:**
    *   Composta da **Header** (protocollo, successo richiesta, tipo contenuto) e **Body** (contenuto effettivo, es. HTML).
    *   **Content-Type (MIME types):** Specifica il formato del file inviato dal server (es. `text/html`, `image/jpeg`).
    *   **Codici di Stato HTTP:** Numeri a tre cifre che indicano l'esito della richiesta (1xx Informational, 2xx Successful, 3xx Redirection, 4xx Client Error, 5xx Server Error). Esempi comuni: 200 OK, 404 Not Found, 500 Internal Server Error.

*   **Cookie:**
    *   Struttura dati usata per il **mantenimento di stato** in HTTP (protocollo stateless).
    *   Generati dal server, memorizzati nel browser, passati ad ogni richiesta/risposta.
    *   Attributi: `Name=Valore`, `Domain`, `Path`, `Max-age`/`Expires`, `Secure` (solo HTTPS), `HttpOnly` (no JS access), `SameSite` (controllo cross-site).
    *   Header: `Set-Cookie` (risposta server), `Cookie` (richiesta client).

*   **Autenticazione:**
    *   Tecniche per restringere l'accesso: filtro IP, HTTP Basic, HTTP Digest, **Form-based (comunemente usato con POST)**.
    *   Svantaggi autenticazione basata su IP. HTTP Digest in disuso.

*   **Sicurezza (dei Dati):**
    *   Proprietà desiderabili: Confidenzialità, Integrità, Autenticità, Non ripudio.
    *   **SSL (Secure Sockets Layer) / TLS (Transport Layer Security):** Livelli aggiunti per gestione di confidenzialità, autenticità e integrità (alla base di HTTPS). Basato su crittografia a chiave pubblica.

*   **Cache del Browser (User Agent Cache):**
    *   Il browser mantiene una cache delle pagine visitate.
    *   Meccanismi HTTP per la gestione della cache: Freshness, Validation, Invalidation.

Questo documento fornisce le basi per comprendere come Omniride, essendo un'applicazione web, interagisce con i client e gestisce le richieste e le risposte. La logica di gestione delle sessioni (tramite cookie), l'autenticazione (probabilmente form-based, dato il contesto Java Web Dynamic), e la sicurezza (HTTPS) sono aspetti direttamente influenzati da questi concetti.

### L03a - HTML.pdf: Ruolo e Convalida HTML

Questo documento rafforza la comprensione del ruolo dell'HTML e delle buone pratiche di sviluppo web.

**Ruolo di HTML e CSS:**
*   **HTML (HyperText Markup Language):** Utilizzato per **strutturare** la pagina web, definendo gli elementi e il loro ordine.
*   **CSS (Cascading Style Sheets):** Utilizzato per **controllare la presentazione** e lo stile degli elementi HTML, separando il contenuto dalla sua visualizzazione.

**Convalida delle Pagine Web:**
*   Un **validatore** è un programma software che controlla le pagine web rispetto agli standard web (HTML, XHTML, CSS).
*   È una buona pratica **validare regolarmente** le pagine web per assicurare la conformità agli standard, migliorare la compatibilità tra browser e facilitare la manutenzione.

Questi aspetti sono cruciali per la logica di sviluppo e manutenzione di Omniride, garantendo che le pagine siano ben formate e presentate correttamente.

### L03b - HTML Forms.pdf: Logica di Interazione e Trasmissione Dati dei Moduli

Questo documento approfondisce la logica dietro l'interazione con i moduli HTML e la trasmissione dei dati al server, aspetti cruciali per le funzionalità di Omniride.

**Flusso di Dati dei Moduli:**
*   Un utente compila un form HTML e lo invia.
*   Il browser impacchetta tutti i dati del form e li invia al server web.
*   Il server web riceve i dati del form e li passa a uno script lato server (es. una Servlet Java in Omniride) per l'elaborazione.
*   Lo script lato server elabora i dati, crea una nuova pagina HTML come risposta e la restituisce al server web.
*   Il server web invia la risposta dello script lato server al browser, che la visualizza all'utente.

**Metodi di Invio Dati (`method` attribute del `<form>`):**
*   **`GET`:** I dati del form vengono aggiunti all'URL come stringa di query. Visibili nella barra del browser, lunghezza limitata, meno sicuri per dati sensibili.
*   **`POST`:** I dati del form vengono inviati nel corpo del messaggio HTTP. Non visibili nell'URL, nessun limite di lunghezza, più sicuri per dati sensibili e per l'upload di file.

**Ruolo degli Attributi `name` e `value`:**
*   L'attributo `name` è fondamentale per identificare ogni controllo di input. Il server utilizza questo `name` per recuperare il valore inserito dall'utente.
*   L'attributo `value` contiene il dato effettivo associato al controllo di input che viene inviato al server.

**Codifica dei Dati (`enctype` attribute del `<form>`):**
*   Quando si utilizza il metodo `POST`, l'attributo `enctype` specifica come i dati del form devono essere codificati.
*   `application/x-www-form-urlencoded`: Default per la maggior parte dei form, adatto per dati testuali semplici.
*   `multipart/form-data`: Necessario per l'upload di file, poiché consente di inviare tipi di dati diversi (testo e binario) all'interno dello stesso messaggio HTTP.

**Accessibilità e Usabilità (`<label>`):**
*   L'associazione di un'etichetta (`<label>`) a un controllo di input migliora significativamente l'accessibilità, consentendo agli utenti di cliccare sull'etichetta per attivare il controllo corrispondente. Questo è un aspetto importante per la logica di design dell'interfaccia utente di Omniride.

Questi principi di gestione dei moduli e trasmissione dei dati sono centrali per la logica di business di Omniride, in quanto definiscono come l'applicazione raccoglie e processa le informazioni dagli utenti, dalle credenziali di accesso ai dettagli di ricerca e prenotazione.

### L05a - Servlet.pdf: Logica di Funzionamento e Gestione dello Stato

Questo documento illustra la logica operativa delle Servlet, il loro ciclo di vita, la gestione delle richieste/risposte HTTP e il mantenimento dello stato, elementi fondamentali per la logica di business di Omniride.

**Modello Request-Response delle Servlet:**
*   All'arrivo di una richiesta HTTP, il Servlet Container crea un oggetto `HttpServletRequest` e un oggetto `HttpServletResponse` e li passa alla Servlet.
*   La Servlet elabora la richiesta e genera una risposta.

**Ciclo di Vita delle Servlet:**
*   **`init()`:** Chiamato una sola volta al caricamento della Servlet dal Web Container. Utilizzato per inizializzazioni (es. connessioni a database). Può essere sovrascritto.
*   **`service()`:** Chiamato ad ogni richiesta HTTP. Determina il metodo HTTP (GET, POST, ecc.) e invoca il metodo `doGet()` o `doPost()` corrispondente. Non dovrebbe essere sovrascritto direttamente in `HttpServlet`.
*   **`doGet()` / `doPost()`:** Metodi da sovrascrivere per implementare la logica specifica per le richieste GET e POST. Ogni richiesta viene gestita in un thread separato.
*   **`destroy()`:** Chiamato una sola volta quando la Servlet viene disattivata (es. rimozione). Utilizzato per rilasciare risorse (es. chiudere connessioni DB).

**Gestione delle Richieste HTTP (`HttpServletRequest`):**
*   L'oggetto `request` contiene i dati inviati dal client al server.
*   **Metodi chiave per accedere alle informazioni della richiesta:**
    *   `getParameter(String name)`: Ottiene il valore di un parametro del form o della query string.
    *   `getParameterNames()`: Restituisce un'enumerazione di tutti i nomi dei parametri.
    *   `getParameterValues(String name)`: Restituisce tutti i valori per un parametro con nomi multipli.
    *   `getHeader(String name)`: Ottiene il valore di un header HTTP (es. `User-Agent`).
    *   `getCookies()`: Ottiene un array di oggetti `Cookie` inviati dal client.
    *   `getSession()`: Ottiene la sessione `HttpSession` associata al client (o ne crea una nuova).
    *   `getMethod()`: Ottiene il metodo HTTP della richiesta (GET, POST, ecc.).
    *   `getContextPath()`: Ottiene la parte dell'URL che indica il contesto dell'applicazione Web.
    *   `getQueryString()`: Ottiene la query string dell'URL.
    *   `getRequestURI()`: Ottiene l'URI della richiesta fino alla query string.
    *   `getInputStream()`: Ottiene un input stream per leggere dati binari dalla richiesta (es. upload di file).

**Generazione delle Risposte HTTP (`HttpServletResponse`):**
*   L'oggetto `response` contiene le informazioni restituite al client.
*   **Metodi chiave per costruire la risposta:**
    *   `setStatus(int statusCode)`: Imposta il codice di stato HTTP (es. 200 OK, 404 Not Found).
    *   `sendError(int statusCode, String message)`: Invia un codice di errore e un messaggio al client.
    *   `setContentType(String type)`: Configura il tipo di contenuto della risposta (MIME type, es. `text/html`).
    *   `getWriter()`: Ottiene un `PrintWriter` per scrivere testo (HTML) nella risposta.
    *   `getOutputStream()`: Ottiene un `ServletOutputStream` per scrivere dati binari (immagini) nella risposta.
    *   `addCookie(Cookie cookie)`: Aggiunge un cookie alla risposta.
    *   `sendRedirect(String url)`: Invia una risposta di reindirizzamento (status 302) al browser, forzandolo a richiedere un nuovo URL.

**Gestione dello Stato (Session Tracking):**
*   HTTP è stateless, quindi le applicazioni web necessitano di meccanismi per mantenere lo stato tra diverse richieste.
*   **`HttpSession`:** Entità gestita dal Web Container per mantenere lo stato del client durante le interazioni.
    *   Identificata da un `session ID`.
    *   Condivisa tra tutte le richieste dello stesso client.
    *   Può memorizzare dati specifici dell'utente come attributi (coppie nome/valore) usando `setAttribute()`, `getAttribute()`, `removeAttribute()`, `getAttributeNames()`.
    *   Metodi per la gestione della sessione: `getId()`, `isNew()`, `invalidate()`, `getCreationTime()`, `getLastAccessedTime()`.
    *   **Sincronizzazione della Sessione:** Con chiamate Ajax, più richieste dallo stesso utente possono arrivare concorrentemente, rendendo necessaria la sincronizzazione (es. `synchronized (session)`).

**Inoltro e Inclusione di Risorse (`RequestDispatcher`):**
*   **`RequestDispatcher`:** Oggetto per includere o inoltrare richieste a un'altra risorsa (Servlet, JSP, HTML).
    *   Ottenuto da `request.getRequestDispatcher(url)` o `servletContext.getRequestDispatcher(url)`.
    *   **`include(request, response)`:** Include il contenuto di un'altra risorsa nella risposta corrente. La risorsa inclusa condivide gli oggetti `request` e `response`.
    *   **`forward(request, response)`:** Inoltra la richiesta a un'altra risorsa. La gestione della risposta è delegata esclusivamente alla risorsa di destinazione. Non si deve aver scritto nulla nella risposta prima di un `forward`.

**Ambiti (Scopes) degli Oggetti:**
*   **`HttpServletRequest` (Request Scope):** Tempo di vita limitato alla singola richiesta. Accessibile solo dalla Servlet corrente e da eventuali pagine incluse o inoltrate.
*   **`HttpSession` (Session Scope):** Tempo di vita legato alla sessione utente. Condiviso tra tutte le richieste dello stesso client.
*   **`ServletContext` (Application Scope):** Tempo di vita legato all'intera applicazione. Condiviso tra tutti gli utenti, tutte le richieste e tutte le Servlet dell'applicazione.

La comprensione di questi concetti è fondamentale per la logica di programmazione lato server di Omniride, dalla gestione delle interazioni utente alla persistenza dei dati di sessione e alla navigazione tra le pagine.

### L05b - JSP.pdf: Logica di Generazione Contenuto Dinamico e Accesso ai Dati

Questo documento illustra la logica di funzionamento delle JSP, la loro interazione con i dati e i componenti Java, e l'uso dell'Expression Language (EL) per la generazione dinamica dei contenuti in Omniride.

**Funzionamento delle JSP:**
*   Quando una richiesta arriva a una JSP, il server la traduce in una Servlet (JspServlet) e la compila in una classe Java. Questo avviene solo se il codice JSP è cambiato.
*   Il codice Java all'interno della JSP viene eseguito sul server per generare il contenuto HTML dinamico.
*   La pagina HTML risultante (parte statica + parte dinamica) viene restituita al client.

**JSP vs. Servlet: Scelta e Ruoli:**
*   **Servlet:** Offrono un controllo completo sull'applicazione, ideali per logiche di business complesse e per fornire contenuti differenziati in base a parametri e condizioni.
*   **JSP:** Semplificano la presentazione di documenti HTML/XML dinamici, ideali per pagine dinamiche semplici e di uso frequente. Permettono di separare il lavoro tra grafici (HTML) e programmatori (Java).

**Oggetti Impliciti (Built-in Objects) JSP:**
Le JSP forniscono 9 oggetti impliciti, riferimenti a oggetti Java corrispondenti nella tecnologia Servlet, utilizzabili senza doverli istanziare:
*   `page`: Rappresenta l'istanza corrente della Servlet (poco usato, si preferisce `this`).
*   `config`: Contiene la configurazione della Servlet (`ServletConfig`), inclusi i parametri di inizializzazione della Servlet.
*   `request`: Rappresenta la richiesta HTTP (`HttpServletRequest`), consente l'accesso a URL, header, cookie, parametri, sessione, metodo HTTP, contesto dell'applicazione, query string, URI della richiesta e input stream.
*   `response`: Rappresenta la risposta HTTP (`HttpServletResponse`), consente di impostare status code, header, content type, scrivere output (testo/binario), aggiungere cookie e reindirizzare.
*   `out`: Stream di caratteri (`JspWriter`) per scrivere output nella pagina.
*   `session`: Rappresenta la sessione corrente per l'utente (`HttpSession`), per il mantenimento dello stato.
*   `application`: Fornisce informazioni sul contesto dell'applicazione (`ServletContext`), accesso a parametri di inizializzazione e attributi a livello di applicazione.
*   `pageContext`: Fornisce informazioni sull'intero contesto di esecuzione della pagina JSP, consente l'accesso a tutti gli oggetti impliciti e la gestione di attributi a livello di pagina.
*   `exception`: Rappresenta l'eccezione non gestita (disponibile solo nelle pagine di errore JSP con `isErrorPage="true"`).

**Azioni JSP (`<jsp:...>`):**
Comandi JSP con sintassi XML per gestire l'interazione con altre risorse o componenti JavaBean:
*   **`<jsp:forward page="localURL" />`:** Inoltra il controllo a un'altra risorsa (JSP, HTML, Servlet). L'output non deve essere stato emesso prima del forward. Gli oggetti `request`, `response` e `session` sono gli stessi, ma viene istanziato un nuovo `pageContext`.
*   **`<jsp:include page="localURL" />`:** Include il contenuto generato da un'altra risorsa nell'output corrente. Gli oggetti `session` e `request` sono gli stessi, ma viene istanziato un nuovo `pageContext`.
*   **`<jsp:useBean id="beanName" class="class" scope="scope" />`:** Istanzia un JavaBean e gli associa un identificativo. L'attributo `scope` definisce l'ambito di accessibilità (`page`, `request`, `session`, `application`).
*   **`<jsp:getProperty>`:** Accede e produce come output il valore di una proprietà del bean. Attributi: `name` (nome del bean), `property` (nome della proprietà).
*   **`<jsp:setProperty>`:** Modifica il valore di una proprietà del bean. Attributi: `name`, `property`, `value`.
*   `plugin`: Genera contenuto per scaricare plug-in Java (meno comune).

**JSP Expression Language (EL):**
*   Introdotto in JSP 2.0 come linguaggio abbreviato per valutare e produrre i valori di oggetti Java memorizzati in posizioni standard (ambiti).
*   Sintassi: `${expression}` (sempre tra parentesi graffe e prefissato da `$`).
*   Permette di accedere a proprietà annidate di oggetti (es. `${person.dog.name}`).
*   Gli oggetti impliciti EL non sono gli stessi degli oggetti impliciti JSP scripting, tranne `pageContext`.
*   L'operatore `.` (dot) e `[]` (parentesi quadre) sono usati per accedere a proprietà di bean o chiavi di Map/indici di List/Array.
*   Gestisce i valori `null` in modo elegante (es. in espressioni aritmetiche `null` è trattato come `0`, in espressioni logiche come `false`).

Questi meccanismi di generazione dinamica dei contenuti e di accesso ai dati sono al centro della logica di presentazione di Omniride, consentendo di creare interfacce utente interattive e personalizzate.

### L05c - Form Data - Header - Filter - Listener.pdf: Logica di Gestione Dati, Filtri e Listener

Questo documento illustra la logica avanzata per la gestione dei dati dei form, l'intercettazione delle richieste/risposte e la gestione degli eventi a livello di applicazione in Omniride.

**Lettura dei Dati dei Form nelle Servlet:**
*   `request.getParameter("name")`: Restituisce il valore URL-decodificato della prima occorrenza di un parametro. Funziona identicamente per richieste GET e POST. Restituisce `null` se il parametro non esiste.
*   `request.getParameterValues("name")`: Restituisce un array di valori URL-decodificati per tutte le occorrenze di un parametro. Restituisce un array a un elemento se il parametro non è ripetuto. Restituisce `null` se il parametro non esiste.
*   `request.getParameterNames()`: Restituisce un'enumerazione dei nomi di tutti i parametri della richiesta.

**Gestione dei Dati Mancanti:**
*   È fondamentale gestire esplicitamente i dati mancanti o malformati.
*   Un campo di testo non presente nel form HTML restituisce `null` da `getParameter()`.
*   Un campo di testo vuoto (`""`) o con soli spazi (`" "`) restituisce una stringa vuota o una stringa con spazi da `getParameter()`.
*   È buona pratica controllare sia `null` che stringhe vuote/con spazi (es. `if (value != null && !value.trim().equals(""))`).

**Filtraggio dei Caratteri Specifici dell'HTML:**
*   Non è sicuro inserire stringhe arbitrarie direttamente nell'output della Servlet, poiché caratteri come `<`, `>`, `&`, `"`, `'` possono causare problemi (es. XSS).
*   È necessario codificare questi caratteri in entità HTML (es. `&lt;` per `<`).

**Gestione degli Header HTTP:**
*   **Impostazione di `Content-Type`:** Cruciale per indicare al browser come interpretare la risposta (es. `response.setContentType("application/vnd.ms-excel")` per generare fogli di calcolo Excel).
*   **Gestione delle Immagini:** Le Servlet possono recuperare immagini da un database (es. tramite un DAO) e inviarle al client impostando il `Content-Type` appropriato (es. `image/jpeg`) e scrivendo i byte dell'immagine nell'`OutputStream` della risposta.
*   **Controllo degli Header Mancanti:** È buona pratica controllare che gli header richiesti non siano `null` prima di usarli (es. `request.getHeader("User-Agent")`).

**Servlet Filters: Logica Operativa:**
*   **Scopo:** Intercettare richieste e risposte per eseguire operazioni pre-processing (es. autenticazione, validazione input, logging) o post-processing (es. compressione dati, trasformazione XML).
*   **Metodi del Ciclo di Vita del Filter:**
    *   `init(FilterConfig filterConfig)`: Chiamato una sola volta all'inizializzazione del filtro. Utilizzato per inizializzazioni.
    *   `doFilter(ServletRequest request, ServletResponse response, FilterChain chain)`: Chiamato per ogni coppia richiesta/risposta che passa attraverso la catena di filtri. Il filtro può manipolare la richiesta/risposta e poi passare il controllo al filtro successivo o alla risorsa finale tramite `chain.doFilter(request, response)`.
    *   `destroy()`: Chiamato una sola volta quando il filtro viene rimosso dal servizio. Utilizzato per rilasciare risorse.
*   **Ordine di Esecuzione:** L'ordine in cui i filtri sono dichiarati nel deployment descriptor (`web.xml`) o tramite annotazioni determina l'ordine di esecuzione.

**Event Listeners: Logica di Gestione Eventi:**
*   **Scopo:** Fornire un meccanismo per reagire a eventi specifici nel ciclo di vita dell'applicazione, delle richieste o delle sessioni, consentendo l'esecuzione di codice in momenti chiave.
*   **Categorie di Listener (Interfacce):**
    *   `ServletContextListener`: Notifiche per l'inizializzazione e la distruzione del `ServletContext` (utile per inizializzare risorse globali come connessioni DB all'avvio dell'applicazione).
    *   `ServletContextAttributeListener`: Notifiche per cambiamenti negli attributi del `ServletContext`.
    *   `ServletRequestListener`: Notifiche per l'inizializzazione e la distruzione di `ServletRequest`.
    *   `ServletRequestAttributeListener`: Notifiche per cambiamenti negli attributi di `ServletRequest`.
    *   `HttpSessionListener`: Notifiche per la creazione e distruzione di `HttpSession`.
    *   `HttpSessionAttributeListener`: Notifiche per cambiamenti negli attributi di `HttpSession`.
    *   `HttpSessionBindingListener`: Notifiche quando un oggetto viene aggiunto o rimosso da una sessione.
    *   `HttpSessionActivationListener`: Notifiche per eventi di migrazione di oggetti di sessione tra VM.

Questi concetti avanzati sono fondamentali per la logica di Omniride, permettendo una gestione robusta dei dati utente (inclusi gli upload), l'implementazione di logiche di sicurezza e validazione centralizzate tramite filtri, e la gestione del ciclo di vita dell'applicazione e delle sue risorse tramite listener.

### L07 - Autenticazione, restrizione degli accessi e cenni di sicurezza.pdf: Logica di Sicurezza e Autenticazione

Questo documento illustra la logica di implementazione della sicurezza nelle applicazioni web, inclusi i meccanismi di autenticazione, restrizione degli accessi e protezione contro attacchi comuni, aspetti critici per Omniride.

**Aspetti Principali della Sicurezza Web:**
1.  **Impedire accessi non autorizzati a dati sensibili:** Richiede restrizione degli accessi e autenticazione.
2.  **Impedire agli attaccanti di rubare dati in transito:** Richiede l'uso di crittografia (TLS/SSL, HTTPS).

**Restrizione degli Accessi: Approcci:**
*   **Maniera Dichiarativa:**
    *   Vantaggio: Nessun codice sensibile alla sicurezza nelle Servlet/JSP.
    *   Svantaggio: Dipendente dal Servlet Container, le applicazioni non sono portabili.
    *   Logica: Il Servlet Container gestisce la restrizione degli accessi basandosi su parametri di configurazione in `web.xml` (es. `security-constraint`, `url-pattern`, `transport-guarantee`).
*   **Maniera Programmatica:**
    *   Vantaggio: Applicazioni Web portabili, approccio flessibile.
    *   Svantaggio: Il programmatore deve scrivere codice per proteggere le risorse.
    *   Logica: Ogni Servlet/JSP protetta deve verificare che l'utente sia autenticato e autorizzato. Se non lo è, l'utente viene reindirizzato a una pagina di login o di errore.

**Linee Guida per la Restrizione degli Accessi:**
*   Identificare le Servlet/JSP che necessitano di protezione.
*   Identificare i gruppi di utenti che possono accedere a specifiche risorse (es. solo amministratori, utenti e amministratori).
*   Organizzare le pagine JSP in cartelle dedicate (es. `admin/` per amministratori, `common/` per utenti e amministratori).
*   Fare attenzione agli URL relativi nelle pagine protette, specialmente con `response.sendRedirect()`.

**Implementazione Programmatica: Token nella Sessione:**
*   **Idea:** Utilizzare un "token" (es. un flag `isAdmin` o un oggetto `Utente`) memorizzato nella sessione (`HttpSession`) per indicare lo stato di autenticazione e i diritti dell'utente.
*   **Flusso di Login:**
    1.  L'utente fornisce nome utente e password tramite una pagina di login (form `POST`).
    2.  La Servlet di login verifica le credenziali (idealmente caricandole da un database e confrontando gli hash delle password).
    3.  Se le credenziali sono valide, un token viene salvato nella sessione (es. `session.setAttribute("isAdmin", Boolean.TRUE)`).
    4.  L'utente viene reindirizzato a una pagina protetta.
*   **Controllo Accessi:** In ogni Servlet/JSP protetta, si verifica la presenza e il valore del token nella sessione. Se il token non è presente o non indica i diritti necessari, l'utente viene reindirizzato alla pagina di login o a una pagina di errore.
*   **Logout:** Invalida la sessione (`session.invalidate()`) per rimuovere il token e terminare la sessione utente.
*   **Soluzione Migliore:** Utilizzare un **filtro Servlet** per centralizzare la logica di controllo degli accessi, evitando la ripetizione di codice in più Servlet/JSP (es. `AccessControlFilter`).

**Protezione dei Dati in Transito (HTTPS/SSL/TLS):**
*   Per salvaguardare i dati di rete, ogni Servlet/JSP deve verificare il protocollo utilizzato (`request.isSecure()`). Se si tenta di accedere tramite HTTP a una risorsa che richiede HTTPS, la Servlet/JSP dovrebbe reindirizzare manualmente all'equivalente HTTPS.
*   La configurazione di SSL/TLS (HTTPS) in Tomcat è necessaria sia per la restrizione dichiarativa che programmatica.

**Attacchi Clickjacking e Difesa:**
*   **Clickjacking:** Tecnica di attacco che inganna l'utente inducendolo a cliccare su elementi nascosti/trasparenti sovrapposti a contenuti legittimi.
*   **Difesa:** Implementata tramite un filtro Servlet (es. `HttpHeaderSecurityFilter`) che imposta l'header HTTP `X-Frame-Options` nella risposta (es. `DENY` per impedire l'embedding in frame, `SAMEORIGIN` per consentire solo dalla stessa origine).

**Memorizzazione Sicura delle Password:**
*   Le password **non devono essere memorizzate in chiaro** nel database.
*   Deve essere memorizzato l'**hash** della password (funzione non invertibile).
*   Al login, l'hash della password fornita dall'utente viene calcolato e confrontato con l'hash memorizzato.
*   Algoritmi di hashing raccomandati: SHA-512 (più sicuro di SHA-1 e SHA-256).

Questi principi di sicurezza sono fondamentali per la logica di Omniride, garantendo la protezione dei dati degli utenti, la gestione sicura degli accessi e la resilienza contro le vulnerabilità comuni del web.

### L07 - Storage.pdf: Logica di Persistenza dei Dati e Interazione con il Database

Questo documento illustra la logica di gestione della persistenza dei dati, l'interazione con i database relazionali tramite JDBC e i pattern di design per l'accesso ai dati, aspetti cruciali per la logica di business di Omniride.

**Gestione della Persistenza:**
*   Una parte rilevante dello sviluppo di applicazioni web si concentra sul "layer" di persistenza, ovvero l'accesso e la gestione di dati persistenti, tipicamente mantenuti in un database relazionale.
*   Il mapping Object/Relational risolve il mismatch tra i dati nel DB relazionale e il loro processamento tramite oggetti in esecuzione.

**Accesso Diretto alle Basi di Dati (JDBC):**
*   È il metodo più comune per inserire istruzioni SQL direttamente nel codice Java.
*   **Flusso Generale di Utilizzo di JDBC (CLI - Call Level Interface):**
    1.  Aprire una connessione alla sorgente dati.
    2.  Inviare istruzioni SQL (interrogazione/aggiornamento) tramite la connessione.
    3.  Processare i risultati (se presenti).
    4.  Chiudere la connessione.
*   **JDBC (Java Database Connectivity):** API Java standard per l'accesso uniforme e indipendente dalla piattaforma ai database.
    *   I driver JDBC sono collezioni di classi Java che implementano le specifiche JDBC.
    *   **`Class.forName("driver.class.name")`:** Carica e registra automaticamente il driver JDBC nel `DriverManager`.
    *   **`DriverManager.getConnection(url, username, password)`:** Ottiene una `Connection` al DBMS.
    *   **`Connection.createStatement()`:** Crea un oggetto `Statement` per eseguire query SQL.
    *   **`Connection.prepareStatement(sql)`:** Crea un oggetto `PreparedStatement` per query SQL parametrizzate (previene SQL Injection).
    *   **`Statement.executeQuery(sql)`:** Esegue una query `SELECT` e restituisce un `ResultSet`.
    *   **`Statement.executeUpdate(sql)`:** Esegue statement `INSERT`, `UPDATE`, `DELETE` o DDL (CREATE TABLE, DROP TABLE), restituendo il numero di righe modificate.
    *   **`ResultSet.next()`:** Sposta il cursore alla riga successiva del risultato.
    *   **`ResultSet.getTYPE(column-name/number)`:** Legge il valore di una colonna dal `ResultSet`.
    *   **`ResultSet.wasNull()`:** Controlla se l'ultimo valore letto era SQL `NULL`.
    *   **`PreparedStatement.setTYPE(n, value)`:** Assegna i parametri a uno statement SQL parametrizzato.

**Connection Pool:**
*   Invece di creare una nuova connessione per ogni richiesta, si utilizza un pool di connessioni già pronte.
*   **Vantaggio:** Riduce l'overhead e migliora le prestazioni, specialmente in applicazioni web con molte richieste.
*   **Implementazione:** Può essere gestito manualmente (es. `DriverManagerConnectionPool`) o tramite `DataSource`.

**JNDI (Java Naming and Directory Interface) e DataSource:**
*   **JNDI:** Servizio di naming che permette di accedere a oggetti identificati da nomi logici, rendendo l'applicazione più configurabile.
*   **DataSource:** Un oggetto `javax.sql.DataSource` è una factory di connessioni che incapsula un connection pool. Viene pubblicato su JNDI e configurato esternamente (es. in `web.xml` e `context.xml`).
*   **Logica di Accesso con DataSource:** Si effettua un `lookup` da JNDI per ottenere l'istanza del `DataSource`, e poi si usa `ds.getConnection()` per ottenere una connessione dal pool.

**Prevenzione dell'SQL Injection:**
*   **Problema:** Vulnerabilità che permette a un utente malintenzionato di "iniettare" statement SQL arbitrari tramite l'input dell'utente, portando a divulgazione di dati o esecuzione di codice dannoso.
*   **Causa:** Dati di input non filtrati o non tipati correttamente, incastonati direttamente in statement SQL.
*   **Soluzione:** Utilizzare **`PreparedStatement`** con parametri (`?`) invece di concatenare stringhe. I dati di input vengono assegnati ai parametri in modo sicuro (bounding), impedendo l'interpretazione come codice SQL.
*   **Attenzione:** Anche con `PreparedStatement`, clausole come `ORDER BY` che possono essere dinamiche devono essere gestite con una whitelist per prevenire attacchi.

**Pattern DAO (Data Access Object) e DTO (Data Transfer Object):**
*   **DAO:** Pattern di design che incapsula la logica di accesso ai dati, separandola dalla logica di business. Ogni DAO gestisce le operazioni CRUD (Create, Retrieve, Update, Delete) per una specifica entità del dominio.
*   **DTO:** Oggetti semplici (simili a JavaBean) usati per scambiare dati tra il DB e il resto dell'applicazione (es. tra il DAO e la logica di business o la presentazione). Contengono solo dati e metodi per accedervi (getter/setter).
*   **Vantaggio:** Migliora la manutenibilità, la testabilità e la flessibilità dell'applicazione, riducendo l'accoppiamento tra la logica di business e i dettagli di persistenza.

**Paginazione:**
*   Tecnica per restituire un sottoinsieme (parziale) di risultati da un database o servizio, utile per gestire grandi quantità di dati.
*   Logica: Definire `Length` (dimensione del `ResultSet`), `Limit` (max risultati per pagina), `Page` (pagina corrente) e utilizzare clausole SQL come `LIMIT` e `OFFSET`.

Questi concetti di persistenza e interazione con il database sono fondamentali per la logica di business di Omniride, in quanto definiscono come l'applicazione memorizza, recupera e gestisce i dati relativi a utenti, tratte, prenotazioni e altre entità, garantendo efficienza e sicurezza.

### L08a - CSS.pdf: Logica di Applicazione e Gestione degli Stili

Questo documento illustra la logica di applicazione e gestione degli stili tramite CSS, fondamentale per l'aspetto visivo e l'usabilità di Omniride.

**Vantaggi dell'uso dei CSS:**
*   **Riusabilità del Contenuto:** Lo stesso contenuto HTML può essere presentato in modi diversi cambiando solo il foglio di stile, adattandosi a vari dispositivi (PC, smartphone) o media (stampa, video).
*   **Separazione delle Responsabilità:** Permette di dividere il lavoro tra chi gestisce il contenuto (HTML) e chi si occupa della grafica (CSS), migliorando l'organizzazione del progetto.
*   **Riduzione dei Tempi di Download:** Se un file CSS è condiviso tra più pagine, viene scaricato una sola volta e poi recuperato dalla cache del browser, ottimizzando le prestazioni.

**Attribuzione di uno Stile a un Elemento:**
*   Il browser deve riuscire ad applicare uno stile a ogni elemento HTML.
*   Anche se non ci sono regole CSS esplicite, ogni browser ha un foglio di stile di default.
*   L'attribuzione può essere:
    *   **Diretta:** Stile inline sull'elemento o regole CSS con selettori che puntano direttamente all'elemento.
    *   **Indiretta (Ereditarietà):** Un elemento figlio "eredita" gli stili dai suoi nodi antenati nel DOM (Document Object Model). Questo è un meccanismo differenziale, simile all'ereditarietà nei linguaggi a oggetti. Non tutte le proprietà sono ereditate (es. quelle del box model).

**Conflitti di Stile e Meccanismo a Cascata:**
*   Quando più regole di stile si riferiscono allo stesso elemento, possono sorgere conflitti.
*   Il meccanismo a **Cascata** risolve questi conflitti basandosi su tre criteri di priorità:
    1.  **Importanza e Origine:** Le regole con la parola chiave `!important` hanno la massima precedenza. Tra le origini, le regole definite nei fogli di stile dell'utente hanno precedenza su quelle dell'autore, che a loro volta hanno precedenza su quelle del browser.
    2.  **Specificità:** Misura quanto una regola è specifica in base al tipo di selettore utilizzato. I punti di specificità sono:
        *   Stili inline: 1000 punti.
        *   Selettore di ID: 100 punti.
        *   Selettore di classe o pseudo-classe: 10 punti.
        *   Selettore di elemento o pseudo-elemento: 1 punto.
        *   La specificità aumenta combinando più selettori.
    3.  **Ordine di Dichiarazione:** A parità di importanza e specificità, prevale la regola definita per ultima nel foglio di stile.

**Validazione CSS:**
*   È una buona pratica utilizzare un **validatore CSS** (es. [http://jigsaw.W3.Org/css-validator/](http://jigsaw.W3.Org/css-validator/)) per controllare la conformità dei fogli di stile agli standard web.
*   La validazione assicura la corretta interpretazione degli stili da parte dei browser e facilita la manutenzione.

La logica di gestione degli stili in Omniride si baserà su questi principi per garantire un'interfaccia utente coerente, manutenibile e performante, sfruttando la potenza dei CSS per un design flessibile e responsivo.

### L09a - Javascript (1).pdf: Logica di Programmazione e Interazione con il DOM

Questo documento illustra la logica di programmazione JavaScript, i suoi concetti fondamentali e come interagisce con il Document Object Model (DOM) per creare interattività nelle pagine web di Omniride.

**Cos'è JavaScript?**
*   Linguaggio di scripting client-side (standard ECMAScript) per dare interattività alle pagine HTML.
*   È interpretato (non compilato) e debolmente tipizzato (i tipi sono assegnati dinamicamente).
*   **Differenze con Java:** Sono linguaggi completamente diversi, con la sola similitudine nella sintassi di base (C-like).

**Cosa si può fare con JavaScript:**
*   Accedere e modificare elementi della pagina HTML (tramite DOM).
*   Reagire a eventi generati dall'interazione utente (es. click, submit, mouseover, keyup, load, unload, resize).
*   Validare i dati inseriti dall'utente (lato client, riducendo il carico sul server).
*   Interagire con il browser (rilevare tipo di browser, dimensioni finestra, gestire cookie).
*   Creare dinamicità nelle pagine web (DHTML = JavaScript + DOM + CSS).

**Sintassi del Linguaggio:**
*   Case-sensitive.
*   Istruzioni terminate da `;` (opzionale).
*   Commenti: `//` (singola linea), `/* ... */` (multilinea).
*   Identificatori: lettere, cifre, `_`, `$`. Non possono iniziare con una cifra.
*   Parole chiave riservate (es. `var`, `let`, `const`, `function`, `if`, `else`, `for`, `while`, `switch`, `break`, `continue`, `return`, `try`, `catch`, `finally`, `debugger`).

**Variabili e Scope:**
*   Le variabili non hanno un tipo predefinito, lo assumono dinamicamente.
*   Dichiarazione con `var`, `let`, `const` (o nessuna parola chiave, sconsigliato).
*   **`const`:** Per valori che non devono cambiare (non riassegnabile, ma le proprietà di oggetti/array possono essere modificate).
*   **`let`:** Introdotto con ECMAScript 6, preferito per variabili con scope di blocco.
*   **`var`:** Scope di funzione o globale.
*   **Tipi di Scope:**
    *   **Blocco (`{}`):** Variabili dichiarate con `let` o `const` sono accessibili solo all'interno del blocco.
    *   **Funzione:** Variabili dichiarate all'interno di una funzione sono accessibili solo all'interno di essa.
    *   **Globale:** Variabili dichiarate fuori da funzioni (o con `var` globalmente) sono accessibili ovunque.

**Tipi Primitivi e Valori Speciali:**
*   **Tipi Primitivi:** `number` (floating point a 8 byte, include `NaN` e `Infinity`), `boolean` (`true`/`false`), `string` (UNICODE a 16 bit), `undefined`.
*   **Valori Speciali:**
    *   `null`: Rappresenta l'assenza di un valore (diverso da `0` o `""`).
    *   `undefined`: Indica che una variabile è stata dichiarata ma non ha ancora un valore assegnato.
*   **Operatore `typeof`:** Restituisce il tipo di dato di un operando come stringa.

**Operatori:**
*   **Aritmetici:** `+`, `-`, `*`, `/`, `%`, `++`, `--`, `**` (esponenziazione, ECMAScript 7).
*   **Assegnamento:** `=`, `+=`, `-=`, `*=`, `/=`, `%=`.
*   **Confronto:** `==` (uguaglianza con coercizione di tipo), `===` (uguaglianza stretta, senza coercizione), `!=`, `!==`, `>`, `<`, `>=`, `<=`.
*   **Logici:** `&&` (AND), `||` (OR), `!` (NOT). Supportano short-circuit evaluation.
*   **Ternario:** `condition ? value1 : value2`.
*   **`in`:** Verifica se una proprietà esiste in un oggetto.
*   **`instanceof`:** Verifica se un oggetto è un'istanza di un tipo specifico.

**Funzioni:**
*   Frammenti di codice riutilizzabili, definiti con la parola chiave `function`.
*   Possono avere zero o più parametri (privi di tipo) e possono restituire un valore (privo di tipo).
*   **`function literal`:** Permettono di definire una funzione e assegnarla a una variabile, utili per aggiungere metodi a oggetti esistenti.
*   **Passaggio di Argomenti:** JavaScript passa gli argomenti alle funzioni per **passaggio per valore**. Per gli oggetti, viene passata una copia del riferimento, quindi le modifiche alle proprietà dell'oggetto all'interno della funzione influenzano l'oggetto originale.
*   **`this`:** Parola chiave che, all'interno di una funzione assegnata come metodo di un oggetto, si riferisce all'oggetto stesso.

**Strutture di Controllo:**
*   `if`, `else if`, `else`: Per logiche condizionali. JavaScript ha una definizione "liberale" di `false` (false, null, undefined, `""`, `0`, `NaN`).
*   `while`: Ciclo condizionale.
*   `for`: Ciclo iterativo.
*   `for/in`: Per scorrere le proprietà di un oggetto (o gli elementi di un array).
*   `try...catch...finally`: Per la gestione degli errori.
*   `debugger`: Parola chiave per fermare l'esecuzione e attivare il debugger.

**Comunicazione con l'Utente e Debugging:**
*   **`alert("message")`:** Mostra un messaggio in una finestra di dialogo (da usare con parsimonia).
*   **`document.write("content")`:** Scrive direttamente HTML nel documento (generalmente sconsigliato).
*   **`console.log("message")`:** Scrive messaggi nella console del browser (utile per il debugging).

**Gestione degli Eventi (Event Handling):**
*   JavaScript consente di associare script agli eventi causati dall'interazione dell'utente con la pagina (es. `onclick`, `onkeyup`, `onmouseover`, `onmouseout`, `onfocus`, `onblur`, `onsubmit`, `onchange`, `onload`, `onunload`, `onresize`).
*   I gestori di eventi sono associati agli elementi HTML tramite attributi (es. `<button onclick="myFunction()">`).
*   Nelle risposte agli eventi, si può intervenire sul DOM per modificare dinamicamente la struttura, il contenuto e lo stile della pagina (DHTML).
*   **`addEventListener()`:** Metodo moderno e preferibile per associare gestori di eventi agli elementi, consentendo più gestori per lo stesso evento e una migliore separazione del codice.

**Interazione con i Form tramite JavaScript:**
*   Un documento può contenere più oggetti form, accessibili tramite `document.forms[]` o `document.nomeForm`.
*   Gli elementi di un form sono accessibili tramite `form.elements[]` o `form.nomeElemento`.
*   Ogni elemento di controllo ha proprietà (es. `value`, `name`, `type`, `disabled`, `checked`, `defaultValue`, `maxLength`, `readOnly`, `size`) e metodi (es. `focus()`, `blur()`, `click()`, `select()`).
*   **Validazione dei Form (lato client):** Uno degli utilizzi più frequenti di JavaScript. Riduce il carico sul server e il ritardo per l'utente.
    *   Si valida durante l'inserimento (`onchange`) o al submit (`onclick` sul bottone submit, `onsubmit` sul form).
    *   La funzione di validazione deve restituire `true` (successo) o `false` (fallimento, impedendo il submit).
*   **Constraint Validation API:** Set di metodi e proprietà per la validazione dei form HTML5 (es. `checkValidity()`, `setCustomValidity()`, proprietà `validity`).

Questi concetti di programmazione JavaScript e di interazione con il DOM sono fondamentali per la logica di Omniride, consentendo di creare un'interfaccia utente dinamica, reattiva e convalidata lato client, migliorando l'esperienza utente e l'efficienza dell'applicazione.

### L09b - Advanced Concepts on HTML and CSS.pdf: Logica di Layout e Responsività

Questo documento illustra la logica di creazione di layout complessi e responsivi tramite HTML e CSS, aspetti cruciali per l'adattabilità dell'interfaccia utente di Omniride a diversi dispositivi.

**Elementi Strutturali e Semantici per il Layout:**
*   L'uso di `<div>` e `<span>` per raggruppare e stilizzare il contenuto è fondamentale per la costruzione del layout.
*   L'introduzione degli **elementi semantici di HTML5** (`<header>`, `<nav>`, `<section>`, `<article>`, `<aside>`, `<footer>`) permette di dare un significato strutturale al contenuto, migliorando l'organizzazione e la comprensione della pagina da parte dei browser e dei motori di ricerca. Questi elementi sono utilizzati per definire le diverse aree logiche del layout.

**Gestione dei Contenuti Multimediali:**
*   L'incorporazione di video tramite il tag `<video>` e l'uso di più tag `<source>` con diversi formati (`MP4`, `WebM`, `Ogg`) è una logica per garantire la compatibilità cross-browser, poiché non tutti i browser supportano gli stessi codec.
*   L'uso di attributi come `autoplay`, `controls`, `poster`, `loop`, `muted` definisce la logica di riproduzione e interazione con il video.
*   L'incorporazione di contenuti esterni (es. video di YouTube) tramite `<iframe>` segue una logica di embedding di documenti all'interno della pagina corrente.

**Layout con Tabelle (HTML e CSS):**
*   Le tabelle (`<table>`, `<th>`, `<td>`, `<caption>`) sono utilizzate per organizzare dati tabellari. Attributi come `rowspan` e `colspan` definiscono la logica di estensione delle celle.
*   Le proprietà CSS per le tabelle (`caption-side`, `border-collapse`, `border-spacing`) definiscono la logica di presentazione visiva delle tabelle.

**Stilizzazione delle Liste (CSS):**
*   Le proprietà CSS per le liste (`list-style-image`, `list-style-position`, `list-style-type`) definiscono la logica di come i marcatori di elenco vengono visualizzati, consentendo personalizzazioni visive.

**Responsive Web Design: Logica di Adattamento ai Dispositivi:**
*   **Concetto:** Un sito web responsivo si adatta e risponde alle dimensioni dello schermo dell'utente, ridimensionando e riordinando il design, adattando immagini, tipografia e colonne, invece di fornire un sito specifico per ogni dispositivo.
*   **`Viewport` Meta Tag:** L'elemento `<meta name="viewport" content="width=device-width, initial-scale=1.0">` è cruciale. La sua logica è quella di istruire il browser su come controllare le dimensioni e lo scaling della pagina, impostando la larghezza della pagina alla larghezza del dispositivo (`width=device-width`) e lo zoom iniziale a 1.0 (`initial-scale=1.0`).
*   **Media Queries (CSS):** Consentono di applicare stili dipendenti dalle caratteristiche del media (es. larghezza dello schermo, altezza, orientamento).
    *   Sintassi: `@media mediatype and (mediafeature) { CSS-Code; }`.
    *   Logica: Permettono di definire "punti di interruzione" (`break points`) in cui il layout della pagina cambia per adattarsi a diverse dimensioni dello schermo (es. `max-device-width`, `min-device-width`).
*   **Tipi di Layout Responsivi:**
    *   **Fluid Layout:** Basato su unità relative (percentuali) invece di pixel assoluti. La logica è che gli elementi si ridimensionano proporzionalmente alla larghezza del browser.
    *   **Adaptive Layout:** Utilizza dimensioni fisse ma adatta il layout a specifici "punti di interruzione" definiti tramite media queries. La logica è di modificare non solo le dimensioni dei blocchi ma anche il loro posizionamento (es. sidebar che si sposta sotto il contenuto).
    *   **Responsive Layout:** Una combinazione di Fluid e Adaptive. Utilizza unità relative per la flessibilità e media queries con punti di interruzione per riordinare il layout in momenti specifici. La logica è di adattarsi automaticamente e di riorganizzare il design dove si verificano "rotture" visive.

La logica di responsive design è fondamentale per Omniride, garantendo che l'applicazione sia utilizzabile e visivamente accattivante su una vasta gamma di dispositivi, dai desktop agli smartphone, migliorando l'esperienza utente complessiva.

### L09b - Javascript regular expressions.pdf: Logica di Validazione con Espressioni Regolari

Questo documento illustra la logica di utilizzo delle espressioni regolari (regex) in JavaScript per la validazione dell'input utente, un aspetto cruciale per la robustezza e la sicurezza di Omniride.

**Cos'è un'Espressione Regolare?**
*   Un oggetto che descrive un pattern di caratteri.
*   Utilizzate per funzioni di "pattern-matching" (trovare corrispondenze) e "search-and-replace" (cerca e sostituisci) su testo.

**Sintassi delle Espressioni Regolari in JavaScript:**
*   Le regex sono specificate con `/pattern/` (non come stringhe, a differenza di Java).
*   **Caratteri Speciali Comuni:**
    *   `^`: Inizio della stringa.
    *   `$`: Fine della stringa.
    *   `.`: Qualsiasi carattere singolo (eccetto newline).
    *   `\`: Carattere di escape per caratteri speciali (es. `\d` per una cifra).
    *   `*`: Zero o più occorrenze.
    *   `+`: Una o più occorrenze.
    *   `?`: Zero o una occorrenza.
    *   `{n}`: Esattamente `n` occorrenze.
    *   `{n,}`: `n` o più occorrenze.
    *   `{n,m}`: Tra `n` e `m` occorrenze.
    *   `[]`: Gruppo di caratteri (es. `[A-z]` per qualsiasi lettera).
    *   `[^]`: Non nel gruppo.
    *   `\s`: Spazio bianco; `\S`: Non spazio bianco.
    *   `\w`: Carattere di parola (lettera o numero); `\W`: Non carattere di parola.
    *   `\d`: Cifra; `\D`: Non cifra.
    *   `(x|y)`: Alternative (x o y).
    *   `(xy)`: Raggruppamento.
*   **Modificatori (Flags):**
    *   `g` (global): Trova tutte le corrispondenze (non solo la prima).
    *   `i` (case-insensitive): Esegue la corrispondenza senza distinzione tra maiuscole e minuscole.
    *   `m` (multiline): Esegue la corrispondenza su più linee.

**Metodi delle Stringhe che Usano le Espressioni Regolari:**
*   `string.match(regexp)`: Restituisce un array delle corrispondenze o `null` se non ci sono corrispondenze.
*   `string.replace(regexp, replacement)`: Sostituisce tutte le occorrenze del pattern con una stringa di sostituzione.
*   `string.split(regexp)`: Divide una stringa in un array di sottostringhe basandosi sul pattern.
*   `string.search(regexp)`: Restituisce la posizione della prima corrispondenza o `-1` se non trovata.

**Metodo `RegExp.prototype.test()`:**
*   `regexp.test(string)`: Esegue la ricerca di una corrispondenza tra una regex e una stringa specificata. Restituisce `true` o `false`.
*   **Differenza con `match()`:** `test()` è più efficiente per la sola validazione (vero/falso), mentre `match()` restituisce i dettagli delle corrispondenze.
*   Poiché `null` (restituito da `match()` in caso di non corrispondenza) valuta a `false` in un contesto booleano, entrambi possono essere usati per la validazione, ma `test()` è più diretto.

**Approccio Pratico alla Validazione dell'Input Utente:**
*   Utilizzare funzioni JavaScript che prendono il valore dell'input e una regex per validarlo.
*   Se la validazione fallisce, è buona pratica:
    *   Aggiungere una classe di errore all'elemento del form (es. per stilizzare il bordo in rosso).
    *   Impostare il focus sull'elemento.
    *   Mostrare un messaggio di errore e/o un suggerimento all'utente (es. in uno `<span>` accanto al campo).
    *   Restituire `false` per impedire il submit del form.
*   **Esempi di Validazione:**
    *   **Nome Utente:** Regex per sole lettere (es. `/^[A-z]+$/g`).
    *   **Indirizzo:** Regex per sequenze di parole/numeri separate da spazi (es. `/^\w+(\s\w+)+$/g`).
    *   **Email:** Regex per il formato base `utente@dominio.ext` (es. `/^\S+@\S+\.\S+$/g`).
    *   **Numero di Telefono:** Regex per un formato specifico (es. `/^\d{10}$/` per 10 cifre, o `/^\(\d{3}\)-\d{3}-\d{4}$/` per `(XXX)-XXX-XXXX`).

L'applicazione di espressioni regolari per la validazione lato client è una logica fondamentale per Omniride, in quanto permette di fornire un feedback immediato all'utente, ridurre il carico sul server e migliorare la qualità dei dati inseriti, contribuendo alla robustezza e all'usabilità dell'applicazione).

### L09c - Esercizio responsive.pdf: Applicazione Pratica del Responsive Design

Questo documento fornisce un esempio pratico di come i principi di responsive design (coperti in `L09b - Advanced Concepts on HTML and CSS.pdf`) vengono applicati per creare un sito web che si adatta a diverse dimensioni di schermo.

**Concetti Chiave Rinforzati:**
*   **Layout Responsivo:** Una combinazione di layout fluidi (basati su unità relative come percentuali) e layout adattivi (che utilizzano punti di interruzione specifici per modificare il layout).
*   **`Viewport` Meta Tag:** L'importanza di includere `<meta name="viewport" content="width=device-width, initial-scale=1.0">` nell'`<head>` per istruire il browser sul controllo delle dimensioni e dello scaling della pagina.
*   **Media Queries (`@media` rules):** L'uso di `@media screen and (max-width: XXXpx)` per applicare stili CSS specifici a intervalli di larghezza dello schermo, consentendo al layout di adattarsi (es. sidebar che si sposta sotto il contenuto principale su schermi più piccoli).
*   **Unità Relative:** L'applicazione di larghezze e margini in percentuale (`%`) per consentire agli elementi di ridimensionarsi proporzionalmente.

Questo esercizio pratico dimostra come le tecniche di HTML e CSS avanzate si combinano per creare un'esperienza utente ottimale su una varietà di dispositivi, un obiettivo chiave per l'interfaccia utente di Omniride.

### L11 - Ajax and JSON.pdf: Logica di Interazione Asincrona (AJAX) e Scambio Dati (JSON)

Questo documento illustra la logica di AJAX (Asynchronous JavaScript And XML) e JSON (JavaScript Object Notation), fondamentali per creare interfacce utente dinamiche e reattive in Omniride, riducendo i ricaricamenti completi della pagina.

**Limiti del Modello a Ricaricamento di Pagina (Postback):**
*   Le applicazioni web tradizionali seguono un modello di interazione sincrono ("Click, wait, and refresh").
*   Ogni interazione utente che richiede dati dal server comporta un ricaricamento completo della pagina, portando a un'interattività limitata e a tempi di attesa percepibili.

**AJAX: Un Nuovo Modello di Interazione Asincrona:**
*   **AJAX (Asynchronous JavaScript And XML):** Tecnologia nata per superare i limiti del modello sincrono, consentendo di aggiornare una pagina web senza ricaricarla completamente.
*   **Idea:** Permettere agli script JavaScript di interagire direttamente con il server in background.
*   **Componenti chiave:** JavaScript/Eventi, DOM, XML (o JSON), HTML, CSS.
*   **Obiettivo:** Supportare applicazioni user-friendly con elevata interattività (Rich Interface Application - RIA).
*   **Comunicazione Asincrona:** Il client non interrompe l'interazione con l'utente anche quando è in attesa di risposte dal server (thread dedicato per la richiesta).

**Tipica Sequenza AJAX:**
1.  Un evento utente (es. click su un bottone, cambio di un campo input) si verifica sulla pagina web.
2.  Una funzione JavaScript viene eseguita:
    *   Viene istanziato un oggetto `XMLHttpRequest`.
    *   L'oggetto `XMLHttpRequest` viene configurato (es. associando una funzione di callback per gestire la risposta).
    *   Viene effettuata una chiamata asincrona al server.
3.  Il server elabora la richiesta e risponde (spesso con dati in formato XML o JSON).
4.  Il browser invoca la funzione di callback, che:
    *   Elabora il risultato ricevuto dal server.
    *   Aggiorna dinamicamente il DOM della pagina per mostrare i risultati, senza ricaricare l'intera pagina.

**`XMLHttpRequest` (XHR): L'Elemento Centrale di AJAX:**
*   Oggetto JavaScript che effettua richieste HTTP a un server web.
*   Può inviare dati come parametri (GET o POST).
*   **Richieste Asincrone (preferite):** Non bloccano il flusso di esecuzione di JavaScript né le operazioni dell'utente sulla pagina.
*   **Creazione di un'istanza:** `var xhr = new XMLHttpRequest();` (con gestione della compatibilità per browser più vecchi tramite `ActiveXObject`).
*   **Metodi chiave:**
    *   `open(method, url, async, [user], [password])`: Inizializza la richiesta (es. `xhr.open("GET", "pagina.html?p1=v1", true)`).
    *   `setRequestHeader(name, value)`: Imposta gli header HTTP della richiesta (es. `Content-Type`, `Connection`).
    *   `send(body)`: Invia la richiesta al server. Il parametro `body` è `null` per GET, contiene i dati per POST.
    *   `abort()`: Interrompe l'operazione di invio/ricezione.
*   **Proprietà chiave per gestire la risposta:**
    *   `readyState`: Stato di avanzamento della richiesta (0: uninitialized, 1: open, 2: sent, 3: receiving, 4: loaded).
    *   `onreadystatechange`: Funzione di callback invocata ad ogni cambio di `readyState`. È qui che si gestisce la risposta.
    *   `status`: Codice di stato HTTP della risposta (es. 200 OK, 404 Not Found).
    *   `statusText`: Descrizione testuale del codice di stato.
    *   `responseText`: Contiene il body della risposta come stringa (disponibile quando `readyState` è 4).
    *   `responseXML`: Contiene il body della risposta convertito in documento XML (se possibile, disponibile quando `readyState` è 4). Permette la navigazione del documento XML via JavaScript.
*   **Gestione di Callback Multiple:** Per eseguire più task AJAX, si creano funzioni per l'esecuzione di XHR e funzioni di callback separate per ciascun task.

**Criticità nell'Interazione con l'Utente (AJAX):**
*   Sebbene AJAX migliori l'interattività, può disorientare l'utente se non è chiaro cosa stia succedendo in background.
*   Soluzioni: Rendere visibile l'andamento della chiamata (es. barre di scorrimento) e implementare timeout per interrompere richieste che non terminano in tempo.

**Aspetti Critici per il Programmatore (AJAX):**
*   Aumenta la complessità delle Web Application.
*   Problemi di debug, test e mantenimento (il test del codice JavaScript è complesso).
*   Mancanza di standardizzazione di `XMLHttpRequest` e assenza di supporto nei browser più vecchi (oggi meno rilevante).

**JSON (JavaScript Object Notation): Formato di Scambio Dati Preferito:**
*   **Acronimo:** JavaScript Object Notation.
*   **Scelta Preferita:** Formato più efficiente e semplice da manipolare rispetto a XML per lo scambio di informazioni tramite AJAX.
*   **Vantaggi:** Leggero in termini di quantità di dati scambiati, molto semplice ed efficiente da elaborare da parte del runtime JavaScript, facile da leggere per gli umani, largamente supportato.
*   **Sintassi:** Basata sulla notazione degli object literal (`{}`) e array literal (`[]`) di JavaScript.
*   Una stringa JSON è la rappresentazione testuale di un object o array literal JavaScript.
*   **Parsing JSON:**
    *   **`JSON.parse(JSONstring)`:** Converte una stringa JSON in un oggetto JavaScript (preferibile a `eval()` per motivi di sicurezza).
    *   **`JSON.stringify(object)`:** Converte un oggetto JavaScript in una stringa JSON.
*   **Ricezione JSON (Lato Server):** La Servlet deve impostare il `Content-Type` a `application/json` e utilizzare una libreria parser JSON (es. JSON-Java, GSON) per creare la stringa JSON da inviare nel body della risposta HTTP.

La logica di AJAX e l'uso di JSON sono fondamentali per Omniride per creare un'esperienza utente moderna e fluida, consentendo aggiornamenti parziali della pagina e interazioni asincrone con il server, migliorando significativamente le prestazioni e la reattività dell'applicazione).

### Logica del Progetto Omniride (Analisi del Codice Java)

Il progetto Omniride è strutturato per gestire un sistema di trasporto pubblico, con funzionalità di autenticazione utente, gestione delle tratte e ricerca di percorsi. La logica è distribuita tra i livelli di Controllo, Modello (con sottolivelli per DAO, DB, Pathfinding, Strutture Dati e Utility).

**1. Flusso Generale dell'Applicazione:**
*   Le richieste HTTP in entrata vengono intercettate dalle **Servlet** (livello di Controllo) o dai **Filtri**.
*   Le Servlet elaborano la richiesta, interagiscono con il **livello Model** (in particolare i **DAO** per l'accesso ai dati e la logica di **Pathfinding** per i calcoli complessi) e preparano una risposta.
*   La risposta viene quindi inviata al client, spesso tramite l'inoltro a una **pagina JSP** (livello di Vista) per la generazione dinamica dell'HTML.
*   L'interazione lato client è gestita da **JavaScript**, che può effettuare chiamate AJAX per aggiornamenti parziali della pagina.

**2. Logica di Autenticazione e Autorizzazione (`control/login`, `control/filter`):**
*   **Login (`LoginServlet`):**
    *   Riceve `email` e `password` dal form di login (metodo `POST`).
    *   Utilizza `UtenteDAO.findByEmail()` per recuperare l'utente dal database.
    *   Verifica la password fornita con l'hash della password memorizzato nel database utilizzando `BCrypt.checkpw()`. Questo garantisce che le password non siano mai memorizzate in chiaro.
    *   In caso di successo, crea una `HttpSession` e memorizza l'oggetto `Utente` nella sessione (`session.setAttribute("utente", utente)`).
    *   Reindirizza l'utente alla dashboard (`/prvUser/dashboard.jsp`).
    *   In caso di fallimento, imposta un messaggio di errore (`errorMessage`) e inoltra nuovamente alla pagina di login.
*   **Registrazione (`RegisterServlet`):**
    *   Riceve `nome`, `cognome`, `email`, `password` dal form di registrazione (metodo `POST`).
    *   Prima di creare un nuovo utente, verifica se l'`email` esiste già nel database tramite `UtenteDAO.findByEmail()`. Se esiste, restituisce un errore.
    *   Crea un nuovo oggetto `Utente` e passa la password in chiaro a `UtenteDAO.create()`. Il DAO si occupa di generare un `salt` e di hashare la password con `BCrypt.hashpw()` prima di salvarla nel database.
    *   In caso di successo, reindirizza l'utente alla pagina di login con un messaggio di successo.
    *   In caso di errore, reindirizza alla pagina di registrazione con un messaggio di errore generico.
*   **Logout (`LogoutServlet`):**
    *   Invalida la sessione HTTP corrente (`session.invalidate()`), rimuovendo tutti gli attributi di sessione (incluso l'oggetto `Utente`).
    *   Reindirizza l'utente alla pagina di login.
*   **Filtro di Autenticazione (`AuthenticationFilter`):**
    *   Intercetta tutte le richieste per le aree protette (`/prvUser/*` e `/prvAdmin/*`).
    *   Per `/prvUser/*`: Verifica semplicemente se l'utente è loggato (se l'attributo `"utente"` è presente nella sessione). Se non lo è, reindirizza alla pagina di login.
    *   Per `/prvAdmin/*`: Oltre a verificare che l'utente sia loggato, controlla anche che il `ruolo` dell'utente sia `"admin"`. Se non è loggato o non è admin, restituisce un errore `SC_FORBIDDEN` (403).
    *   Questo filtro centralizza la logica di sicurezza, evitando la duplicazione del codice di controllo accessi in ogni Servlet/JSP protetta.

**3. Logica di Accesso ai Dati (DAO Layer - `model/dao`):**
*   Le classi DAO sono responsabili di tutte le interazioni con il database, incapsulando la logica SQL e la mappatura tra oggetti Java e record del database.
*   Utilizzano `DBConnector.getConnection()` per ottenere una connessione al database.
*   Fanno ampio uso di `PreparedStatement` per eseguire query parametrizzate, prevenendo attacchi di SQL Injection.
*   Eseguono operazioni di `SELECT` (`executeQuery()`) e `INSERT`/`UPDATE`/`DELETE` (`executeUpdate()`).
*   I metodi `doRetrieveById`, `doRetrieveAll`, `findByEmail`, `create`, `getAvatarByUserId`, `getFTfromTrattaID`, `getFromIdUT`, `getLUTfromIDT`, `getTrattaByID`, `getAllTratte` sono esempi di queste operazioni.
*   La mappatura dei `ResultSet` a oggetti POJO (es. `extractAziendaFromResultSet`, `extractFermataFromResultSet`, `getUtenteFromSet`, `getFTfromSet`, `getUTfromSet`, `getTrattaFromSET`) è gestita all'interno di ciascun DAO.
*   `DBConnector` gestisce il caricamento del driver JDBC e la connessione al database.

**4. Logica di Pathfinding (`model/pathfinding`):**
*   **`PathFinding.java`:** Implementa un algoritmo di ricerca del percorso (simile a Dijkstra) per trovare il percorso ottimale tra due fermate, minimizzando l'orario di arrivo.
    *   Utilizza una `PriorityQueue` per esplorare gli stati (fermata + orario di arrivo) in ordine di arrivo più precoce.
    *   Mantiene `orariArrivoMigliori` (mappa fermata -> orario) e `predecessori` (mappa fermata -> tappa precedente) per ricostruire il percorso.
    *   Itera su tutte le `Tratte` e le `UnicaTratta` (corse) per trovare le connessioni valide.
    *   Calcola gli orari di partenza programmati e gli orari di arrivo alle fermate successive, considerando i tempi di percorrenza tra le fermate sulla tratta (`tratta.getDistanceForTwoFermate()`).
    *   Ricostruisce il `Percorso` finale risalendo la mappa dei predecessori dalla destinazione alla partenza.
*   **`Test.java`:** Contiene la logica per creare dati di mock (fermate, tratte, orari) e per eseguire e verificare il funzionamento dell'algoritmo `PathFinding`.

**5. Logica di Utilità (`model/util`):**
*   **`Geolock.java`:** Fornisce la logica per la geocodifica degli indirizzi.
    *   Effettua richieste HTTP all'API Nominatim di OpenStreetMap per convertire un indirizzo testuale in coordinate geografiche (`latitudine`, `longitudine`).
    *   Gestisce la codifica dell'URL e la lettura della risposta JSON.

**6. Gestione degli Avatar (`AvatarServlet`):**
*   Riceve un `userId` come parametro.
*   Utilizza `UtenteDAO.getAvatarByUserId()` per recuperare i dati binari dell'avatar dal database.
*   Imposta il `Content-Type` della risposta a `image/png` (o `image/jpeg`) e scrive i byte dell'immagine direttamente nell'`OutputStream` della risposta HTTP.
*   Se l'avatar non è presente, reindirizza a un'immagine di default (`/Images/default_avatar.png`).

**7. Gestione degli Errori e Reindirizzamenti:**
*   Le Servlet gestiscono gli errori impostando attributi di richiesta (`errorMessage`) e inoltrando a pagine JSP dedicate per la visualizzazione degli errori (es. `login.jsp`, `register.jsp`).
*   Vengono utilizzati `sendError()` per errori HTTP specifici (es. 400 Bad Request, 403 Forbidden) e `sendRedirect()` per reindirizzamenti lato client (es. dopo login/logout).
*   `RequestDispatcher.forward()` viene usato per inoltrare richieste a JSP o altre Servlet, mantenendo gli oggetti `request` e `response`.

Questa analisi dettagliata della logica del codice Java fornisce una comprensione approfondita del funzionamento interno di Omniride, dal flusso delle richieste utente alla manipolazione dei dati e alla logica di business complessa come la ricerca di percorsi.