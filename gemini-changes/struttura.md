# Struttura del Progetto Omniride

## 1. Panoramica Generale
Il progetto Omniride è un'applicazione web basata su Maven, sviluppata in Java 21, che gestisce servizi di trasporto pubblico. Segue un'architettura Model-View-Controller (MVC) e interagisce con un database MySQL.

## 2. Struttura delle Cartelle Principali
- `src/main/java`: Contiene il codice sorgente Java dell'applicazione.
- `src/main/webapp`: Contiene le risorse web (JSP, CSS, JavaScript, immagini).
- `src/test/java`: (Attualmente contiene `Test.java` che dovrebbe essere spostato qui) Contiene i test unitari e di integrazione.
- `gemini-changes`: Cartella per la documentazione interna generata dall'AI.
- `LTWS`: Contiene la documentazione PDF relativa al corso.

## 3. Componenti Chiave

### 3.1. Configurazione e Build
- **`pom.xml`**: File di configurazione Maven. Definisce le dipendenze (Jakarta Servlet, JSTL, MySQL Connector, jbcrypt, JSON-java, JUnit 5) e i plugin di build (maven-war-plugin, maven-compiler-plugin).
- **`web.xml`**: File di configurazione del deployer web (Jakarta EE). Definisce la pagina di benvenuto (`index.jsp`) e il timeout di sessione (30 minuti). Le servlet e i filtri sono configurati tramite annotazioni.

### 3.2. Database
- **`database_schema.sql`**: Script SQL per la creazione delle tabelle del database MySQL. Modella entità come `Azienda`, `Fermata`, `Tratta`, `Unica_Tratta`, `Orario_Tratta`, `Utente`, `Tipo_Biglietto`, `Biglietto_Acquistato`.
- **`model.db.DBConnector`**: Classe responsabile della connessione al database. Attualmente utilizza `DriverManager` e credenziali hardcoded, senza un connection pool.

### 3.3. Livello Model (Java)

#### 3.3.1. `model.udata` (User Data)
Contiene le classi che rappresentano i dati relativi agli utenti e alle aziende.
- **`Azienda.java`**: Rappresenta un'azienda di trasporto. Include un `enum` `TipoAzienda`.
- **`Utente.java`**: Rappresenta un utente del sistema. Include campi per dati personali, `passwordHash` e `ruolo`.

#### 3.3.2. `model.sdata` (System Data)
Contiene le classi che rappresentano i dati del sistema di trasporto (fermate, tratte, orari, percorsi).
- **`Coordinate.java`**: Rappresenta una coppia latitudine/longitudine, con metodo per calcolare la distanza Haversine.
- **`Fermata.java`**: Rappresenta una fermata. Include un `enum` `TipoFermata`.
- **`FermataTratta.java`**: Classe intermedia che associa una fermata a una tratta, con informazioni sulla prossima fermata e il tempo di percorrenza.
- **`OrarioTratta.java`**: Dettagli dell'orario di una corsa, inclusi giorni della settimana.
- **`Percorso.java`**: Rappresenta un percorso calcolato, composto da `Tappa` (classe interna).
- **`Risultati.java`**: Contiene una lista di `Percorso` per i risultati di ricerca.
- **`Tratta.java`**: Rappresenta una tratta di trasporto, con metodi per verificare la presenza di fermate e calcolare tempi di percorrenza.
- **`UnicaTratta.java`**: Rappresenta una singola corsa di una tratta con il suo orario.

#### 3.3.3. `model.dao` (Data Access Objects)
Contiene le classi DAO per l'interazione con il database. Implementano operazioni CRUD per le rispettive entità.
- **`AziendaDAO.java`**
- **`FermataDAO.java`**
- **`FermataTrattaDAO.java`**
- **`OrarioTrattaDAO.java`**
- **`TesterDAO.java`**
- **`TrattaDAO.java`**: Recupera le tratte e le loro dipendenze.
- **`UnicaTrattaDAO.java`**
- **`UtenteDAO.java`**: Gestisce utenti, inclusi hashing della password con BCrypt e recupero avatar.

#### 3.3.4. `model.pathfinding`
Contiene la logica per la ricerca di percorsi.
- **`PathFinding.java`**: Implementa un algoritmo di Dijkstra modificato per trovare il percorso ottimale (minimo tempo di arrivo) nel trasporto pubblico.
- **`Test.java`**: (Dovrebbe essere spostato in `src/test/java`) Contiene un test di integrazione per l'algoritmo `PathFinding`.

### 3.4. Livello Controller (Java Servlets)
Le servlet gestiscono le richieste HTTP, elaborano la logica di business e interagiscono con il Model e la View.
- **`control.AvatarServlet`**: Gestisce il recupero e la visualizzazione degli avatar utente.
- **`control.TratteServlet`**: Recupera e visualizza tutte le tratte disponibili.
- **`control.UpdateProfileServlet`**: Gestisce l'aggiornamento del profilo utente, inclusa la modifica della password.
- **`control.login.LoginServlet`**: Gestisce il processo di login utente.
- **`control.login.LogoutServlet`**: Gestisce il logout utente.
- **`control.login.RegisterServlet`**: Gestisce la registrazione di nuovi utenti.
- **`control.filter.AuthenticationFilter`**: Filtro di sicurezza che protegge le aree `/prvUser/*` e `/prvAdmin/*` basandosi sullo stato di login e sul ruolo dell'utente.
- **`control.admin.AdminModeServlet`**: (Non analizzata direttamente, ma implicita dalla struttura)
- **`control.admin.GestoreTratteServlet`**: (Non analizzata direttamente, ma implicita dalla struttura)
- **`control.test.PathFindServlet`**: (Non analizzata direttamente, ma implicita dalla struttura)

### 3.5. Livello View (JSP)
Le pagine JSP sono responsabili della presentazione dei dati all'utente.
- **`index.jsp`**: Homepage con form di ricerca tratte.
- **`tratte.jsp`**: Visualizza l'elenco delle tratte.
- **`result.jsp`**: Placeholder per i risultati di ricerca.
- **`biglietti.jsp`**: Placeholder per i biglietti acquistati.
- **`about.jsp`**: Pagina "Chi Siamo".
- **`import/`**: Contiene frammenti JSP riutilizzabili.
    - **`footer.jsp`**: Piè di pagina.
    - **`header.jsp`**: Intestazione del sito con navigazione e azioni utente condizionali.
    - **`metadata.jsp`**: Metadati HTML e inclusione di CSS/JS globali.
- **`login/login.jsp`**: Form di login.
- **`register/register.jsp`**: Form di registrazione.
- **`prvAdmin/admin.jsp`**: Dashboard amministrativa.
- **`prvAdmin/gestoreTratte.jsp`**: Pagina per la gestione delle tratte da parte dell'admin.
- **`prvUser/dashboard.jsp`**: Dashboard utente con form di aggiornamento profilo.

## 4. Stili e Script
- **`Styles/`**: Contiene vari file CSS per la stilizzazione dell'applicazione.
- **`Images/`**: Contiene immagini come il logo e l'avatar di default.
- **`js/main.js`**: (Riferito in `metadata.jsp`, ma non analizzato) Presumibilmente contiene script JavaScript globali.
- **Script In-page**: Alcune JSP (es. `index.jsp`) contengono script JavaScript direttamente incorporati per funzionalità specifiche.