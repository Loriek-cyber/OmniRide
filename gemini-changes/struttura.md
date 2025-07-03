# Struttura del Progetto Omniride

Questo file conterrà una descrizione della struttura del progetto, inclusi i moduli, le dipendenze e l'organizzazione dei file.

### Riferimenti da L01 - Overview Uri, HTTP, HTML.pdf

*   **URL e Struttura delle Risorse:** Il progetto Omniride utilizzerà URL per identificare le sue risorse (pagine JSP, servlet, immagini, ecc.). La struttura delle URL (path, query parameters) sarà fondamentale per l'organizzazione e l'accesso alle diverse funzionalità.
*   **HTML:** Le pagine JSP (`.jsp`) del progetto genereranno HTML, che è la base per la presentazione dei contenuti all'utente.

### L03a - HTML.pdf: Struttura e Elementi HTML

Questo documento approfondisce la struttura e gli elementi fondamentali dell'HTML, essenziali per la costruzione delle pagine web di Omniride.

**Struttura Base di un Documento HTML:**
Un documento HTML è composto da:
*   **`<!DOCTYPE html>`:** Dichiarazione del tipo di documento (HTML5).
*   **`<html>`:** Elemento radice che racchiude l'intero documento.
*   **`<head>`:** Contiene metadati e informazioni di servizio non visualizzate direttamente nel browser:
    *   `<title>`: Titolo della pagina (mostrato nella barra del browser).
    *   `<meta>`: Metadati (es. `charset="UTF-8"` per la codifica dei caratteri, `name="description"` per la descrizione della pagina, `http-equiv="refresh"` per il refresh automatico).
    *   `<link>`: Collegamenti a risorse esterne (es. fogli di stile CSS).
    *   `<style>`: Informazioni di stile CSS locali.
    *   `<script>`: Codice JavaScript eseguibile.
*   **`<body>`:** Contiene tutti i contenuti visualizzati dal browser.

**Elementi HTML Comuni (nel `<body>`):**
*   **Intestazioni:** `<h1>` a `<h6>` per titoli gerarchici.
*   **Paragrafi:** `<p>` per blocchi di testo.
*   **Formattazione del Testo:** `<b>` (bold), `<strong>` (strong importance), `<i>` (italic), `<em>` (emphasis), `<mark>` (marked/highlighted text), `<small>` (smaller text), `<del>` (deleted text), `<ins>` (inserted text), `<sub>` (subscript), `<sup>` (superscript).
*   **Liste:**
    *   `<ol>`: Liste ordinate (numerate), con elementi `<li>`. Attributi `type` (1, A, a, I, i) e `start`.
    *   `<ul>`: Liste non ordinate (puntate), con elementi `<li>`. Attributi `type` (disc, circle, square).
*   **Immagini:** `<img>` (tag singolo) con attributi `src` (URL dell'immagine), `alt` (testo alternativo per accessibilità), `width`, `height`.
*   **Link Ipertestuali:** `<a>` con attributo `href` (URL di destinazione). Possono essere assoluti (`http://...`), relativi (`./file.html`, `../dir/file.html`, `/root/file.html`), o a sezioni (`#section_id`). L'attributo `id` è usato per nominare le sezioni.
*   **Form (Moduli Elettronici):** Campi di input, checkbox, radio button, menu a tendina, bottoni.
*   **Contenuti Multimediali:** Audio, video, animazioni.
*   **Contenuti Interattivi:** Script, applicazioni esterne.
*   **Citazioni:** `<q>` per citazioni brevi inline, `<blockquote>` per blocchi di citazione.

**Classificazione degli Elementi per Layout:**
*   **Block-level:** Occupano un blocco completo e vanno a capo (es. `<p>`, `<h1>`, `<table>`, `<form>`). Possono contenere altri elementi block-level o inline.
*   **Inline:** Non vanno a capo e si integrano nel testo (es. `<a>`, `<img>`, `<em>`). Possono contenere solo altri elementi inline.

**Attributi HTML:**
*   Coppie `nome="valore"` che forniscono informazioni aggiuntive agli elementi.

**Commenti HTML:**
*   `<!-- Questo è un commento -->`

**Organizzazione della Struttura del Sito:**
È consigliabile organizzare i file del sito web in modo logico fin dall'inizio per facilitare la gestione dei percorsi e la scalabilità del progetto.

Questi concetti sono direttamente applicabili alla struttura delle pagine JSP e HTML utilizzate in Omniride, definendo come i contenuti sono organizzati e presentati all'utente.

### L03b - HTML Forms.pdf: Moduli HTML e Controlli di Input

Questo documento descrive in dettaglio la struttura e gli elementi dei moduli HTML, fondamentali per l'interazione utente in Omniride.

**Elemento `<form>`:**
*   Racchiude tutti gli elementi di controllo del modulo.
*   **Attributi chiave:**
    *   `action`: URI dell'agente/servizio che riceverà i dati del form (es. una Servlet in Omniride).
    *   `method`: Specifica come i dati vengono inviati (`GET` o `POST`).
    *   `enctype`: Specifica il tipo di codifica dei dati quando il metodo è `POST` (es. `application/x-www-form-urlencoded` per default, `multipart/form-data` per upload di file).

**Elementi di Input (`<input>`):**
L'elemento `<input>` è versatile e il suo comportamento è definito dall'attributo `type`:
*   `type="text"`: Campo di testo a riga singola. Attributi opzionali: `maxlength`, `size`, `placeholder`.
*   `type="submit"`: Bottone per inviare il form al server.
*   `type="reset"`: Bottone per reimpostare tutti i valori del form ai valori predefiniti.
*   `type="button"`: Bottone generico per script lato client (non invia il form).
*   `type="radio"`: Bottone di opzione (solo uno selezionabile in un gruppo con lo stesso `name`).
*   `type="checkbox"`: Casella di spunta (possono essere selezionate più opzioni con lo stesso `name`).
*   `type="number"`: Input numerico con attributi `min`, `max`, `step`.
*   `type="range"`: Slider per input numerico.
*   `type="color"`: Selettore di colore.
*   `type="date"`: Selettore di data.
*   `type="email"`: Input per indirizzi email (con tastiera personalizzata su mobile).
*   `type="url"`: Input per URL (con tastiera personalizzata su mobile).
*   `type="tel"`: Input per numeri di telefono (con tastiera personalizzata su mobile).
*   `type="file"`: Permette l'upload di file dal client. Richiede `enctype="multipart/form-data"` sul form.
*   `type="password"`: Campo di testo mascherato per password. Attributo opzionale `minlength`.

**Altri Attributi di Input Comuni:**
*   `name`: Essenziale per identificare il controllo e inviare il suo valore al server.
*   `value`: Valore iniziale o corrente del controllo.
*   `checked`: Per checkbox e radio button, indica che l'opzione è preselezionata.
*   `required`: Indica che il campo è obbligatorio (non supportato da tutti i browser).
*   `readonly`: Campo non modificabile dall'utente.
*   `disabled`: Campo disabilitato.
*   `autocomplete="off"`: Disabilita l'autocompletamento del browser.

**Elementi per Input Multilinea e Selezione:**
*   **`<textarea>`:** Crea un'area di testo multilinea. Attributi `rows` e `cols`.
*   **`<select>`:** Crea un menu a discesa per la selezione di opzioni.
    *   **`<option>`:** Definisce una singola opzione all'interno di un `<select>`. Attributo `value`.
    *   Attributo `multiple` su `<select>` per consentire selezioni multiple.

**Organizzazione di Form Complessi:**
*   **`<fieldset>`:** Raggruppa logicamente i campi correlati in un form.
*   **`<legend>`:** Fornisce una didascalia per un `<fieldset>`.
*   **`<label>`:** Associa un'etichetta a un controllo di input, migliorando l'accessibilità. Può essere implicita (racchiudendo l'input) o esplicita (usando l'attributo `for` che corrisponde all' `id` del controllo).

Questi elementi e attributi sono fondamentali per la creazione delle interfacce utente di Omniride, in particolare per le pagine di login, registrazione, ricerca e gestione dei dati, dove gli utenti interagiranno attivamente con il sistema.

### L05a - Servlet.pdf: Struttura delle Servlet e Deployment

Questo documento introduce le Servlet Java, componenti chiave per la logica lato server di Omniride, e la loro configurazione.

**Cos'è una Servlet?**
*   Una classe Java che estende le funzionalità di un Web Server, generando contenuti dinamici.
*   Eseguita in un Web Container (Application Server o Servlet Container).
*   Comunica con il client tramite protocolli request/response (principalmente HTTP).

**Gerarchia delle Servlet:**
*   `jakarta.servlet.Servlet` (interfaccia base)
*   `jakarta.servlet.GenericServlet` (classe astratta che implementa `Servlet`)
*   `jakarta.servlet.http.HttpServlet` (classe astratta che estende `GenericServlet` e gestisce richieste HTTP)
*   Le Servlet di Omniride estenderanno `HttpServlet` (es. `LoginServlet`, `RegisterServlet`, `AdminModeServlet`, `AvatarServlet`, `PathFindServlet`).

**Deployment Descriptor (`web.xml`):**
*   File di configurazione in formato XML (`WEB-INF/web.xml`) che fornisce informazioni per il deployment dell'applicazione Web.
*   **Contenuto principale:**
    *   Elenco delle Servlet e loro parametri di configurazione (`<servlet>`, `<init-param>`).
    *   Mappatura degli URL alle Servlet (`<servlet-mapping>`, `<url-pattern>`).
    *   Definizione delle pagine di benvenuto (`<welcome-file-list>`) e delle pagine di errore (`<error-page>`).
    *   Configurazione delle caratteristiche di sicurezza.
*   **Annotazioni (dalla versione 3.0):** Alternative ai tag XML per la configurazione di Servlet, Filter, Listener e InitParams (es. `@WebServlet`, `@WebInitParam`).

**Parametri di Inizializzazione della Servlet (`ServletConfig`):**
*   Ogni Servlet può accedere ai propri parametri di configurazione definiti in `web.xml` o tramite annotazioni.
*   Accessibili tramite l'interfaccia `ServletConfig` (ottenuta nel metodo `init(ServletConfig config)` o tramite `getServletConfig()`).
*   I parametri di inizializzazione vengono letti una sola volta all'inizializzazione della Servlet.

**Contesto della Servlet (`ServletContext`):**
*   Ogni applicazione Web viene eseguita in un contesto, con una corrispondenza 1:1 tra applicazione e contesto.
*   `ServletContext` è un'interfaccia che fornisce accesso a:
    *   Parametri di inizializzazione del contesto (definiti in `web.xml` con `<context-param>`).
    *   Attributi del contesto (variabili "globali" accessibili a tutte le Servlet e richieste della stessa applicazione).
    *   Risorse statiche della Web application (es. immagini) tramite `getResourceAsStream(String path)`.
*   **Importante:** `ServletContext` è condiviso tra tutti gli utenti, le richieste e le Servlet della stessa applicazione.

La struttura delle Servlet e la loro configurazione tramite `web.xml` o annotazioni sono fondamentali per definire l'architettura lato server di Omniride, gestendo il routing delle richieste e l'inizializzazione dei componenti.

### L05b - JSP.pdf: Struttura delle JavaServer Pages (JSP) e JavaBeans

Questo documento descrive la struttura delle JSP, la loro sintassi e l'integrazione con i JavaBeans, elementi chiave per la presentazione dinamica in Omniride.

**Cos'è una JSP?**
*   Componente della tecnologia Jakarta EE per la parte Web.
*   Template per la generazione di contenuto dinamico, estendendo HTML con codice Java custom.
*   Vengono tradotte e compilate in Servlet dal Web Container (JspServlet).

**Tipi di Tag JSP:**
Le parti variabili della pagina sono contenute all'interno di tag speciali, con due tipi di sintassi:
*   **Scripting-oriented tags (più diffusi):**
    *   **`<%! ... %>` (Dichiarazione):** Per dichiarare variabili e metodi Java a livello di classe della Servlet generata.
    *   **`<%= ... %>` (Espressione):** Valuta un'espressione Java e inserisce il risultato (convertito in stringa) direttamente nell'output HTML.
    *   **`<% ... %>` (Scriptlet):** Per aggiungere frammenti di codice Java eseguibile, tipicamente logiche di controllo di flusso.
    *   **`<%@ ... %>` (Direttiva):** Comandi JSP valutati a tempo di compilazione, non producono output visibile. Le più importanti sono:
        *   `page`: Definisce attributi per l'intera pagina (es. `language`, `import`, `session`, `buffer`, `autoFlush`, `isThreadSafe`, `info`, `errorPage`, `isErrorPage`, `contentType`).
        *   `include`: Include il contenuto di un altro documento (HTML o JSP) al momento della compilazione.
        *   `taglib`: Carica una libreria di custom tag (es. JSTL) specificando `uri` e `prefix`.
*   **XML-oriented tags:** Equivalenti XML dei tag scripting-oriented (es. `<jsp:declaration>`, `<jsp:expression>`, `<jsp:scriptlet>`, `<jsp:directive>`).

**Libreria di Tag JSTL (JavaServer Pages Standard Tag Library):**
*   Raccolta di tag custom per il controllo di flusso condizionale e iterativo all'interno di una pagina JSP.
*   Richiede la direttiva `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` e l'aggiunta delle librerie JSTL al classpath (`WEB-INF/lib`).

**JavaBeans (Beans):**
*   Componenti software riutilizzabili che seguono una specifica convenzione:
    *   Classe `public` con costruttore `public` di default (senza argomenti).
    *   Espone proprietà tramite coppie di metodi `getProp()` e `setProp()` (convenzione standard).
    *   Implementa l'interfaccia `java.io.Serializable`.
*   **Tag di Azione JSP per JavaBeans:**
    *   **`<jsp:useBean id="beanName" class="class" scope="scope" />`:** Istanzia un JavaBean e gli associa un identificativo. L'attributo `scope` definisce l'ambito di accessibilità (`page`, `request`, `session`, `application`).
    *   **`<jsp:getProperty>`:** Accede e produce come output il valore di una proprietà del bean. Attributi: `name` (nome del bean), `property` (nome della proprietà).
    *   **`<jsp:setProperty>`:** Modifica il valore di una proprietà del bean. Attributi: `name`, `property`, `value`.

La struttura delle pagine JSP di Omniride, l'uso dei tag JSP per la logica di presentazione e l'integrazione con i JavaBeans per la gestione dei dati sono aspetti fondamentali per la costruzione dell'interfaccia utente dinamica.

### L05c - Form Data - Header - Filter - Listener.pdf: Struttura per la Gestione di Dati, Filtri e Listener

Questo documento introduce strutture avanzate per la gestione dei dati dei form, l'intercettazione delle richieste/risposte tramite filtri e la gestione degli eventi a livello di applicazione in Omniride.

**Gestione dell'Upload di File:**
*   **Input HTML:** `<input type="file" name="attach">` per selezionare file dal client.
*   **Configurazione del Form:** Il tag `<form>` deve avere `enctype="multipart/form-data"` quando si gestiscono upload di file via `POST`.
*   **Configurazione Servlet (`web.xml` o Annotazione `@MultipartConfig`):**
    *   `file-size-threshold`: Dimensione oltre la quale il file viene temporaneamente memorizzato su disco.
    *   `max-file-size`: Dimensione massima consentita per un singolo file caricato.
    *   `max-request-size`: Dimensione massima totale consentita per tutti i file caricati in una singola richiesta.

**Servlet Filters:**
*   Classi Java che possono intercettare richieste prima che raggiungano una risorsa (Servlet/JSP/HTML) e manipolare risposte prima che vengano inviate al client.
*   **Implementazione:** Una classe Filter implementa l'interfaccia `jakarta.servlet.Filter`.
*   **Configurazione:**
    *   Tramite `web.xml` (`<filter>`, `<filter-mapping>`) mappando a nomi di Servlet o URL patterns.
    *   Tramite annotazione `@WebFilter` (specificando `filterName`, `urlPatterns`, `initParams`).
*   **Ordine di Esecuzione:** I filtri vengono eseguiti nell'ordine in cui sono dichiarati nel deployment descriptor.

**Event Listeners:**
*   Classi separate (non Servlet o JSP) che possono "ascoltare" eventi chiave nel ciclo di vita di un'applicazione Web, di una richiesta o di una sessione.
*   **Implementazione:** Una classe Listener implementa un'interfaccia specifica (es. `jakarta.servlet.ServletContextListener`).
*   **Configurazione:**
    *   Tramite `web.xml` (`<listener>`, `<listener-class>`).
    *   Tramite annotazione `@WebListener`.
*   **Esempio:** `ServletContextListener` per eventi di inizializzazione e distruzione del contesto dell'applicazione (utile per inizializzare/chiudere connessioni a database o caricare risorse globali).

Queste strutture avanzate sono essenziali per implementare funzionalità come l'upload di avatar utente, la validazione dei dati di input, l'autenticazione e la gestione centralizzata di risorse e configurazioni in Omniride.

### L07 - Autenticazione, restrizione degli accessi e cenni di sicurezza.pdf: Strutture per la Sicurezza

Questo documento introduce le strutture per implementare la sicurezza nelle applicazioni web, inclusa la restrizione degli accessi e la protezione dei dati.

**Restrizione degli Accessi (Dichiarativa):**
*   Gestita dal Servlet Container tramite configurazione in `web.xml`.
*   Non richiede codice sensibile alla sicurezza nelle singole Servlet/JSP.
*   **Elementi di configurazione in `web.xml`:**
    *   **`<security-constraint>`:** Definisce un vincolo di sicurezza.
        *   **`<web-resource-collection>`:** Specifica le risorse web da proteggere.
            *   `<web-resource-name>`: Nome della collezione di risorse.
            *   `<url-pattern>`: Pattern URL delle risorse da proteggere (es. `/*` per tutte le risorse).
        *   **`<user-data-constraint>`:** Specifica i requisiti di protezione dei dati.
            *   `<transport-guarantee>`: Livello di protezione del trasporto (es. `CONFIDENTIAL` per HTTPS/SSL/TLS).

**Difesa da Clickjacking:**
*   Tecnica di attacco che inganna l'utente inducendolo a cliccare su elementi nascosti/trasparenti.
*   **Implementazione:** Utilizzo di un filtro Servlet come `HttpHeaderSecurityFilter` (es. di Tomcat) configurato in `web.xml` o tramite annotazione `@WebFilter`.
*   **Parametri di configurazione del filtro:**
    *   `antiClickJackingEnabled`: Abilita la protezione (es. `true`).
    *   `antiClickJackingOption`: Opzione per l'header X-Frame-Options (es. `DENY` per impedire l'embedding in frame, `SAMEORIGIN` per consentire solo dalla stessa origine).

Queste strutture sono fondamentali per definire le politiche di sicurezza a livello di applicazione in Omniride, garantendo che solo gli utenti autorizzati possano accedere a determinate risorse e che l'applicazione sia protetta da attacchi comuni.

### L07 - Storage.pdf: Strutture per la Persistenza dei Dati

Questo documento descrive le strutture e i pattern per la gestione della persistenza dei dati in applicazioni web, essenziali per Omniride.

**JDBC (Java Database Connectivity):**
*   API Java standard per l'accesso uniforme e indipendente dalla piattaforma ai database relazionali.
*   **Componenti chiave:**
    *   `Driver`: Punto di partenza per ottenere una connessione a un DBMS (es. `com.mysql.cj.jdbc.Driver`).
    *   `DriverManager`: Gestisce i driver JDBC e facilita l'ottenimento delle connessioni.
    *   `Connection`: Rappresenta una connessione attiva con il DBMS. Permette di creare oggetti `Statement` e `PreparedStatement`.
    *   `Statement`: Usato per eseguire query SQL semplici (aggiornamenti o interrogazioni).
    *   `PreparedStatement`: Estende `Statement`, usato per query SQL parametrizzate e precompilate (previene SQL Injection).
    *   `ResultSet`: Rappresenta il risultato di un'interrogazione `SELECT` (una tabella di righe e colonne). Permette di scorrere i record (`next()`) e leggere i valori (`getTYPE(column-name/number)`).

**Connection Pool:**
*   Insieme di connessioni al database già pronte all'uso.
*   Vantaggio: Elimina l'overhead di creazione di nuove connessioni per ogni richiesta, migliorando le prestazioni.
*   Implementato tramite classi come `DriverManagerConnectionPool` o `DataSource`.

**JNDI (Java Naming and Directory Interface):**
*   API Java standard per l'accesso uniforme a servizi di naming (es. DNS, File System, DataSource).
*   Basato su una struttura ad albero di coppie chiave-valore.
*   `Context` e `InitialContext`: Interfacce/classi centrali per le operazioni di naming (binding, lookup, unbinding, renaming).

**DataSource (Metodo basato sul naming):**
*   Factory di connessioni verso sorgenti dati fisiche, rappresentate da oggetti `javax.sql.DataSource`.
*   Gli oggetti `DataSource` vengono pubblicati su JNDI e configurati in descrittori (es. `web.xml` e `context.xml`).
*   È un wrapper di connection pool, fornendo un modo standardizzato per ottenere connessioni da un pool.
*   **Configurazione in `web.xml` (`<resource-ref>`):** Definisce il riferimento JNDI al DataSource.
*   **Configurazione in `context.xml` (`<Resource>`):** Definisce i dettagli del DataSource (nome, autenticazione, driver, URL, credenziali, parametri di connessione).

**Pattern DAO (Data Access Object) e DTO (Data Transfer Object):**
*   **DAO:** Pattern per separare la logica di business dalla logica di persistenza.
    *   Gli oggetti DAO hanno il permesso di "vedere" il DB ed espongono metodi di accesso per gli altri componenti.
    *   Implementano operazioni CRUD (Create, Retrieve, Update, Delete).
*   **DTO:** Oggetti semplici (simili a JavaBean) usati per scambiare dati tra il DB e il resto dell'applicazione.
    *   Contengono campi privati e metodi getter/setter.

Queste strutture sono fondamentali per la gestione efficiente e sicura dei dati nel database di Omniride, consentendo un accesso robusto e la separazione delle responsabilità tra i diversi strati dell'applicazione.

### L08a - CSS.pdf: Struttura e Sintassi CSS

Questo documento descrive la struttura e la sintassi dei CSS (Cascading Style Sheets), fondamentali per la presentazione visiva di Omniride.

**Cos'è il CSS?**
*   Linguaggio per descrivere come gli elementi HTML devono essere visualizzati su schermo, carta o altri media.
*   I fogli di stile CSS esterni possono essere memorizzati in file `.css` separati.

**Inclusione di CSS in HTML:**
Gli stili CSS possono essere inclusi in una pagina HTML in tre modi:
1.  **Fogli di stile esterni:** Il metodo preferibile. Un file `.css` separato è collegato alla pagina HTML tramite il tag `<link>` nell' `<head>` (es. `<link rel="stylesheet" href="style.css">`).
2.  **Stili interni:** Le regole CSS sono definite all'interno del tag `<style>` nell' `<head>` della pagina HTML.
3.  **Stili inline:** Le regole CSS sono applicate direttamente a singoli elementi HTML tramite l'attributo `style` (es. `<p style="color: blue;">`). Questo metodo è generalmente sconsigliato per la scarsa separazione tra contenuto e presentazione.

**Struttura di una Regola CSS:**
Una regola CSS è composta da un **selettore** e una o più **dichiarazioni**.
*   **Selettore:** Indica a quali elementi HTML applicare lo stile.
*   **Dichiarazione:** Coppia `proprietà: valore;` che definisce un aspetto dello stile (es. `color: blue;`).

**Tipi di Selettori CSS:**
*   **Selettore Universale (`*`):** Applica lo stile a qualunque elemento (es. `* { color: black; }`).
*   **Selettore di Tipo (Elemento):** Applica lo stile a tutti gli elementi di un determinato tipo (es. `p { font-size: 16px; }`).
*   **Selettore di Classe (`.classname`):** Applica lo stile a tutti gli elementi con l'attributo `class="classname"` (es. `.highlight { background-color: yellow; }`). Un elemento può avere più classi.
*   **Selettore di ID (`#idname`):** Applica lo stile a un singolo elemento con l'attributo `id="idname"` (es. `#header { border-bottom: 1px solid black; }`). Gli ID devono essere unici nella pagina.
*   **Combinazione di Selettori:** I selettori possono essere combinati (es. `p.intro { ... }` per paragrafi con classe `intro`, `div#main { ... }` per un div con ID `main`).
*   **Pseudo-classi (`:pseudo-class`):** Applicano stili a elementi in uno stato specifico (es. `a:hover` per link al passaggio del mouse, `input:focus` per input con focus).
*   **Pseudo-elementi (`::pseudo-element`):** Applicano stili a parti specifiche di un elemento (es. `p::first-line` per la prima linea di un paragrafo).
*   **Selettori Gerarchici:** Applicano stili a elementi con un determinato legame gerarchico:
    *   `tipo1 tipo2`: `tipo2` discendente di `tipo1`.
    *   `tipo1 > tipo2`: `tipo2` figlio diretto di `tipo1`.
    *   `tipo1 + tipo2`: `tipo2` fratello adiacente di `tipo1`.

**Commenti CSS:**
*   `/* Questo è un commento CSS */`

**Proprietà CSS e Valori:**
*   **Proprietà Singole vs. Shorthand Properties:** Le proprietà possono essere definite singolarmente o in forma abbreviata (es. `margin-top`, `margin-right`, `margin-bottom`, `margin-left` vs. `margin: 10px 8px 10px 8px;`).
*   **Unità di Misura:**
    *   **Relative:** `em` (relativo alla dimensione del font corrente), `rem` (relativo alla dimensione del font dell'elemento `<html>`), `px` (pixel, relativi al dispositivo).
    *   **Assolute:** `in` (pollici), `cm` (centimetri), `mm` (millimetri), `pt` (punti tipografici).
*   **Colori:** Definiti tramite parole chiave (es. `red`, `blue`), valori esadecimali (`#RRGGBB`), o funzioni `rgb()`/`rgba()`.
*   **Proprietà di Testo e Font:**
    *   `font-family`: Definisce la famiglia di font (es. `sans-serif`, `serif`). È buona pratica specificare più font e terminare con una famiglia generica.
    *   `font-size`: Definisce la dimensione del testo (parole chiave, unità assolute/relative, percentuali).
    *   `font-weight`: Definisce il peso/grassetto del font (numeri, `normal`, `bold`, `bolder`, `lighter`).
    *   `font-style`: Definisce lo stile del font (`normal`, `italic`, `oblique`).
    *   `font-variant`: Definisce varianti del testo (es. `small-caps`).
    *   `font`: Proprietà sintetica per definire più attributi del font in un colpo solo.
    *   `line-height`: Definisce l'altezza della riga (interlinea).
    *   `text-align`: Allineamento del testo (`left`, `right`, `center`, `justify`).
    *   `text-decoration`: Decorazioni del testo (`none`, `underline`, `overline`, `line-through`).
    *   `text-indent`: Indentazione della prima riga.
    *   `text-transform`: Trasformazione del testo (`none`, `capitalize`, `uppercase`, `lowercase`).
*   **Proprietà di Colore e Sfondo:**
    *   `color`: Colore del testo (foreground).
    *   `background-color`: Colore di sfondo.
    *   `background-image`: Immagine di sfondo.
    *   `background-repeat`: Ripetizione dell'immagine di sfondo (`repeat`, `no-repeat`, `repeat-x`, `repeat-y`).
    *   `background-position`: Posizione dell'immagine di sfondo.
    *   `background-attachment`: Comportamento dello sfondo allo scroll (`scroll`, `fixed`).
    *   `background`: Proprietà sintetica per lo sfondo.

**Box Model:**
Ogni elemento HTML è avvolto da un box, composto da:
*   **Content Area:** Area del contenuto (testo, immagini), con `width` e `height`.
*   **Padding:** Spazio vuoto tra il contenuto e il bordo (interno al bordo).
*   **Border:** Bordo dell'elemento (colore, stile, spessore).
*   **Margin:** Spazio tra l'elemento e gli altri elementi adiacenti (esterno al bordo).
*   **`overflow`:** Definisce il comportamento del contenuto che deborda (`visible`, `hidden`, `scroll`, `auto`).
*   **`position`:** Proprietà fondamentale per la gestione della posizione degli elementi (`static`, `relative`, `absolute`, `fixed`).
*   **`left`, `top`, `right`, `bottom`:** Coordinate per il posizionamento.
*   **`visibility`:** Determina la visibilità dell'elemento (`visible`, `hidden`).
*   **`z-index`:** Gestisce la sovrapposizione degli elementi (quale sta sopra/sotto).

La comprensione di queste proprietà e della loro applicazione è cruciale per la creazione di un'interfaccia utente esteticamente gradevole e funzionale per Omniride, garantendo un controllo preciso sul layout e sullo stile degli elementi.

### L09a - Javascript (1).pdf: Struttura del Codice JavaScript e del DOM

Questo documento introduce la struttura del codice JavaScript e la sua interazione con il Document Object Model (DOM) per creare pagine web dinamiche in Omniride.

**Inclusione del Codice JavaScript in HTML:**
Il codice JavaScript può essere inserito in una pagina HTML in diversi modi:
1.  **Inline nell' `<head>`:** Il codice viene caricato prima del `<body>`. Può causare un ritardo nella visualizzazione della pagina.
2.  **Inline nel `<body>` (prima del tag di chiusura `</body>`):** Il codice viene caricato dopo il contenuto HTML. Permette agli utenti di vedere il contenuto della pagina mentre il codice JavaScript viene caricato.
3.  **File Esterno (`.js`):** Il codice è separato dall'HTML e collegato tramite l'attributo `src` del tag `<script>` (es. `<script src="scripts/my-script.js"></script>`). Questo è il metodo migliore per la manutenibilità e la riusabilità. È buona pratica posizionare il tag `<script>` che carica file esterni alla fine del `<body>` per non bloccare il rendering della pagina.

**Struttura degli Oggetti JavaScript:**
*   Gli oggetti sono tipi composti che contengono proprietà (attributi) e metodi (funzioni).
*   Le proprietà hanno un `nome` e un `valore`.
*   Si accede alle proprietà con l'operatore `.` (punto) o `[]` (parentesi quadre).
*   Le proprietà possono essere aggiunte dinamicamente.
*   Gli oggetti possono essere creati usando l'operatore `new Object()` (sconsigliato) o, preferibilmente, con **object literal** (`{ prop1: val1, prop2: val2 }`).
*   Le funzioni possono essere assegnate come proprietà di un oggetto, diventando metodi dell'oggetto.

**Struttura degli Array JavaScript:**
*   Gli array sono tipi composti i cui elementi sono accessibili tramite un indice numerico (a partire da 0).
*   Non hanno una dimensione prefissata (simili a `ArrayList` in Java).
*   Possono contenere elementi di tipo eterogeneo.
*   Si dichiarano con **array literal** (`[val1, val2, ...]`). L'uso di `new Array()` è sconsigliato.
*   Un array dichiarato con `const` non può essere riassegnato, ma le sue proprietà e i suoi elementi possono essere modificati.
*   Espongono proprietà (es. `length`) e metodi (es. `toString()`, `join()`, `reverse()`, `push()`, `pop()`, `concat()`, `sort()`, `filter()`, `slice()`, `indexOf()`, `splice()`).

**Document Object Model (DOM):**
*   Quando una pagina HTML viene caricata, il browser crea un **Document Object Model (DOM)**, che è una rappresentazione ad albero della pagina.
*   Il punto di partenza per accedere al DOM è l'oggetto `document`.
*   **L'oggetto `document` espone collezioni di elementi HTML:**
    *   `forms[]`: Tutti gli elementi `<form>`.
    *   `images[]`: Tutti gli elementi `<img>`.
    *   `links[]`: Tutti gli elementi `<a>` e `<area>` con attributo `href`.
*   **Metodi per accedere agli elementi del DOM:**
    *   `document.getElementById("id")`: Restituisce l'elemento con l'ID specificato.
    *   `document.getElementsByClassName("classname")`: Restituisce un array di elementi con la classe specificata.
    *   `document.getElementsByTagName("tagname")`: Restituisce un array di elementi con il tag specificato.
    *   `document.querySelectorAll("css_selector")`: Restituisce un array di elementi che corrispondono a un selettore CSS.
*   **Proprietà per manipolare il contenuto e gli attributi:**
    *   `innerHTML`: Legge o imposta il contenuto HTML (inner HTML) di un elemento.
    *   `nodeValue`: Accede al valore del nodo di testo.
    *   `setAttribute("name", "value")`: Imposta il valore di un attributo HTML (sconsigliato per lo stile).
    *   `removeAttribute("name")`: Rimuove un attributo HTML.
    *   `className`: Legge o imposta l'attributo `class` di un elemento.
    *   `style.property`: Accede e modifica direttamente le proprietà CSS di un elemento (es. `element.style.backgroundColor = "green";`).
*   **Creazione e Rimozione di Elementi:**
    *   `document.createElement("tagname")`: Crea un nuovo elemento HTML.
    *   `document.createTextNode("text")`: Crea un nuovo nodo di testo.
    *   `parentNode.insertBefore(newNode, referenceNode)`: Inserisce un nuovo nodo prima di un nodo di riferimento.
    *   `parentNode.appendChild(newNode)`: Aggiunge un nuovo nodo come ultimo figlio.
    *   `parentNode.removeChild(node)`: Rimuove un nodo figlio.
*   **Navigazione tra Nodi:** Proprietà come `parentNode`, `childNodes`, `firstChild`, `lastChild`, `nextSibling`, `previousSibling` per navigare nell'albero del DOM.

La comprensione della struttura del codice JavaScript e del DOM è fondamentale per lo sviluppo dell'interfaccia utente dinamica di Omniride, consentendo di manipolare gli elementi della pagina in risposta alle interazioni dell'utente e di aggiornare i contenuti in tempo reale.

### L09b - Advanced Concepts on HTML and CSS.pdf: Strutture HTML Avanzate e CSS per Layout

Questo documento introduce elementi HTML avanzati e tecniche CSS per la creazione di layout complessi e responsivi, rilevanti per la struttura e la presentazione di Omniride.

**Elementi HTML Strutturali e Semantici:**
*   **`<hr>`:** Inserisce una riga di separazione orizzontale.
*   **`<div>`:** Contenitore generico di tipo **block-level** per altri elementi HTML e testo. Molto utilizzato per raggruppare sezioni della pagina e applicare stili o manipolazioni JavaScript. Attributi comuni: `align`, `style`, `class`, `id`. Preferito rispetto a `<center>` per l'allineamento.
*   **`<span>`:** Contenitore generico di tipo **inline** per altri elementi HTML inline e testo. Non va a capo e continua sulla stessa linea del tag che lo include. Utilizzato per applicare stili o evidenziare parti di testo all'interno di un blocco.
*   **HTML5 Semantic Elements:** Nuovi tag introdotti in HTML5 per dare un significato strutturale al contenuto, migliorando l'accessibilità e la SEO. Esempi:
    *   **`<header>`:** Contenuto introduttivo o un gruppo di aiuti alla navigazione.
    *   **`<nav>`:** Sezione di link di navigazione.
    *   **`<section>`:** Sezione generica di contenuto raggruppato tematicamente.
    *   **`<article>`:** Contenuto autonomo e indipendente (es. un post di blog).
    *   **`<aside>`:** Contenuto correlato ma separato dal contenuto principale (es. sidebar).
    *   **`<footer>`:** Contenuto di chiusura (es. informazioni di copyright).

**Elementi Multimediali:**
*   **`<video>`:** Incorpora un video nella pagina HTML.
    *   **Attributi:**
        *   `controls`: Aggiunge i controlli video predefiniti del browser.
        *   `autoplay`: Avvia automaticamente la riproduzione del video.
        *   `poster`: Specifica un'immagine da visualizzare prima della riproduzione del video.
        *   `loop`: Riproduce il video in loop.
        *   `src`: URL del file video.
        *   `width`, `height`: Dimensioni dell'area di visualizzazione del video.
        *   `muted`: Silenzia l'audio del video all'avvio.
    *   **`<source>`:** Utilizzato all'interno di `<video>` per specificare più sorgenti video con diversi formati (es. MP4, WebM, Ogg) per garantire la compatibilità cross-browser. Attributi: `src`, `type` (MIME type del video).
*   **`<iframe>` (Inline Frame):** Incorpora un altro documento HTML all'interno del documento corrente. Utilizzato, ad esempio, per incorporare video di YouTube.
    *   **Attributi:** `src` (URL del documento da incorporare), `width`, `height`.

**CSS per Tabelle:**
*   **`<table>`:** Elemento per la creazione di tabelle.
*   **`<th>`:** Cella di intestazione della tabella.
*   **`<td>`:** Cella di dati della tabella.
*   **`<caption>`:** Didascalia della tabella.
*   **`rowspan`:** Attributo per celle che si estendono su più righe.
*   **`colspan`:** Attributo per celle che si estendono su più colonne.
*   **Proprietà CSS per tabelle:**
    *   `caption-side`: Posizione della didascalia (`top`, `bottom`).
    *   `border-collapse`: Controlla se i bordi della tabella sono collassati (`collapse`) o separati (`separate`).
    *   `border-spacing`: Spaziatura tra i bordi delle celle (se `border-collapse` è `separate`).
    *   `tr:nth-child(odd/even)`: Pseudo-classe per stilizzare righe alterne.

**CSS per Liste:**
*   **`list-style-image`:** Definisce un'immagine personalizzata come marcatore di elenco (es. `url(image.gif)`).
*   **`list-style-position`:** Indica la posizione del marcatore (`inside`, `outside`).
*   **`list-style-type`:** Definisce il tipo di marcatore di elenco (es. `disc`, `circle`, `square`, `none`, `decimal`, `lower-roman`, `upper-alpha`, ecc.).

Questi elementi e proprietà sono fondamentali per la costruzione di interfacce utente ricche e ben strutturate in Omniride, consentendo l'integrazione di contenuti multimediali e la creazione di layout flessibili e adattabili.

### L09b - Javascript regular expressions.pdf: Struttura per la Validazione con Espressioni Regolari

Questo documento si concentra sulla struttura delle espressioni regolari (regex) in JavaScript, utilizzate per la validazione dell'input utente in Omniride.

**Espressioni Regolari (Regex):**
*   Oggetti che descrivono un pattern di caratteri.
*   Sintassi: `/pattern/flags` (es. `/^[A-z]+$/g`).
*   **Caratteri Speciali e Quantificatori:**
    *   Ancore: `^` (inizio stringa), `$` (fine stringa).
    *   Caratteri generici: `.` (qualsiasi carattere).
    *   Classi di caratteri: `\d` (cifra), `\D` (non cifra), `\w` (carattere di parola), `\W` (non carattere di parola), `\s` (spazio bianco), `\S` (non spazio bianco).
    *   Quantificatori: `*` (zero o più), `+` (uno o più), `?` (zero o uno), `{n}` (esattamente n), `{n,}` (n o più), `{n,m}` (tra n e m).
    *   Gruppi: `()` per raggruppare, `[]` per set di caratteri, `|` per OR logico.
*   **Modificatori (Flags):**
    *   `g` (global): Trova tutte le corrispondenze.
    *   `i` (case-insensitive): Ignora maiuscole/minuscole.
    *   `m` (multiline): Corrispondenza su più linee.

Queste strutture sono essenziali per definire i pattern di validazione lato client per i campi di input di Omniride, garantendo che i dati inseriti dagli utenti rispettino i formati attesi.

### Struttura del Codice Java del Progetto Omniride

Il progetto Omniride segue un'architettura a strati, tipica delle applicazioni web Java, con una chiara separazione delle responsabilità tra i diversi componenti.

**1. Livello di Controllo (`src/main/java/control`):**
Contiene le Servlet che gestiscono le richieste HTTP in entrata, elaborano la logica di controllo e delegano le operazioni ai livelli inferiori (Model, DAO) o inoltrano/reindirizzano alle pagine di vista (JSP).

*   **`control/AvatarServlet.java`**: Gestisce il recupero e la visualizzazione degli avatar degli utenti dal database.
*   **`control/admin/AdminModeServlet.java`**: Servlet per l'accesso alla modalità amministrativa, imposta un attributo di sessione per indicare il ruolo admin e inoltra alla pagina JSP di amministrazione.
*   **`control/filter/AuthenticationFilter.java`**: Un filtro Servlet che intercetta le richieste per le aree protette (`/prvUser/*`, `/prvAdmin/*`). Esegue controlli di autenticazione (utente loggato) e autorizzazione (ruolo admin per `/prvAdmin/*`) prima di consentire l'accesso alla risorsa richiesta.
*   **`control/login/LoginServlet.java`**: Gestisce le richieste di login. Autentica gli utenti confrontando le credenziali fornite con quelle nel database (utilizzando `UtenteDAO` e BCrypt per la verifica della password hashata). In caso di successo, crea una sessione utente; altrimenti, reindirizza alla pagina di login con un messaggio di errore.
*   **`control/login/LogoutServlet.java`**: Gestisce le richieste di logout. Invalida la sessione HTTP dell'utente e reindirizza alla pagina di login.
*   **`control/login/RegisterServlet.java`**: Gestisce le richieste di registrazione di nuovi utenti. Verifica se l'email è già in uso e, in caso contrario, crea un nuovo record utente nel database (utilizzando `UtenteDAO`, che si occupa dell'hashing della password).
*   **`control/test/PathFindServlet.java`**: Una servlet di test, attualmente vuota, probabilmente destinata a integrare la logica di pathfinding.

**2. Livello del Modello (`src/main/java/model`):**
Questo livello incapsula la logica di business, l'accesso ai dati e le strutture dati dell'applicazione.

*   **2.1. Data Access Objects (DAO - `model/dao`):**
    Forniscono un'interfaccia astratta per l'interazione con il database, nascondendo i dettagli di implementazione JDBC. Ogni DAO gestisce le operazioni CRUD (Create, Retrieve, Update, Delete) per una specifica entità del dominio.
    *   **`model/dao/AziendaDAO.java`**: Gestisce le operazioni di recupero per l'entità `Azienda`.
    *   **`model/dao/FermataDAO.java`**: Gestisce le operazioni di recupero per l'entità `Fermata`.
    *   **`model/dao/FermataTrattaDAO.java`**: Recupera le associazioni tra fermate e tratte per una specifica tratta.
    *   **`model/dao/OrarioTrattaDAO.java`**: Recupera gli orari per le singole corse (`UnicaTratta`).
    *   **`model/dao/TesterDAO.java`**: Classe con un metodo `main` per testare le funzionalità dei DAO.
    *   **`model/dao/TrattaDAO.java`**: Gestisce le operazioni di recupero per l'entità `Tratta`.
    *   **`model/dao/UnicaTrattaDAO.java`**: Gestisce le operazioni di recupero per l'entità `UnicaTratta`.
    *   **`model/dao/UtenteDAO.java`**: Gestisce le operazioni CRUD per l'entità `Utente`, inclusa la ricerca per email, la creazione di nuovi utenti (con hashing della password tramite BCrypt) e il recupero degli avatar.

*   **2.2. Connettore Database (`model/db`):**
    *   **`model/db/DBConnector.java`**: Fornisce un metodo statico per stabilire e gestire le connessioni al database MySQL utilizzando JDBC `DriverManager`. Contiene le credenziali di accesso al database.

*   **2.3. Logica di Pathfinding (`model/pathfinding`):**
    *   **`model/pathfinding/PathFinding.java`**: Implementa l'algoritmo principale per la ricerca del percorso ottimale (basato su Dijkstra) nel grafo del trasporto pubblico, minimizzando l'orario di arrivo.
    *   **`model/pathfinding/Test.java`**: Contiene un metodo `main` per eseguire test unitari e di integrazione dell'algoritmo di pathfinding con dati mock.

*   **2.4. Strutture Dati (POJO - `model/sdata` e `model/udata`):**
    Queste classi sono Plain Old Java Objects (POJO) che rappresentano le entità del dominio dell'applicazione. Contengono solo campi dati, costruttori, getter, setter e metodi `equals`/`hashCode`/`toString` per la rappresentazione dei dati.

    *   **`model/sdata/Coordinate.java`**: Rappresenta una coppia di coordinate geografiche (latitudine, longitudine) e include una funzione per calcolare la distanza Haversine.
    *   **`model/sdata/Fermata.java`**: Rappresenta una fermata del trasporto pubblico, con dettagli come ID, nome, indirizzo, coordinate, tipo (URBANA, EXTRAURBANA, ecc.) e stato attivo.
    *   **`model/sdata/FermataTratta.java`**: Classe di supporto che modella l'appartenenza di una `Fermata` a una `Tratta`, inclusa la sequenza e il tempo di percorrenza fino alla prossima fermata.
    *   **`model/sdata/OrarioTratta.java`**: Rappresenta l'orario di una specifica corsa (`UnicaTratta`), inclusi ora di inizio, ora di fine e i giorni della settimana in cui è attiva.
    *   **`model/sdata/Percorso.java`**: Rappresenta un percorso completo trovato dall'algoritmo di pathfinding, composto da una lista di `Tappa` (segmenti di viaggio su una tratta). Include logica per calcolare durata totale, orari complessivi e numero di cambi.
    *   **`model/sdata/Risultati.java`**: Classe per incapsulare i risultati di una ricerca di percorso (lista di `Percorso`).
    *   **`model/sdata/Tratta.java`**: Rappresenta una linea/rotta di trasporto pubblico, con nome, azienda, elenco di corse (`UnicaTratta`) e elenco di fermate (`FermataTratta`). Include logica per calcolare la distanza tra due fermate sulla tratta.
    *   **`model/sdata/UnicaTratta.java`**: Rappresenta una singola corsa all'interno di una tratta, collegata a un `OrarioTratta`.
    *   **`model/udata/Azienda.java`**: Rappresenta un'azienda di trasporto, con ID, nome e tipo (URBANA, EXTRAURBANA, ecc.).
    *   **`model/udata/Utente.java`**: Rappresenta un utente del sistema, con ID, nome, cognome, email, hash della password, data di registrazione, ruolo e avatar.

*   **2.5. Utility (`model/util`):**
    *   **`model/util/Geolock.java`**: Fornisce un metodo per ottenere le coordinate geografiche (latitudine e longitudine) da un indirizzo testuale, utilizzando l'API Nominatim di OpenStreetMap.

Questa struttura riflette una chiara separazione delle preoccupazioni (MVC-like), con le Servlet che agiscono come controller, le classi `model.sdata` e `model.udata` come modelli di dati, e le classi `model.dao` che gestiscono l'accesso ai dati. La logica di pathfinding è incapsulata nel pacchetto `model.pathfinding`.

Ora aggiornerò `logica.md` con i dettagli sul flusso di controllo e la logica di business.
