# TODO COMPLETO - PROGETTO OMNIRIDE 🚌

**Data creazione:** 06 Luglio 2025  
**Stato progetto:** In sviluppo  

---

## 📋 INDICE
1. [TODO ALTA PRIORITÀ](#-todo-alta-priorità)
2. [TODO MEDIA PRIORITÀ](#-todo-media-priorità)
3. [TODO BASSA PRIORITÀ](#-todo-bassa-priorità)
4. [PROBLEMI DA RISOLVERE](#-problemi-da-risolvere)
5. [MIGLIORAMENTI ARCHITETTURALI](#-miglioramenti-architetturali)
6. [DEBUG E LOGGING](#-debug-e-logging)
7. [DATABASE E SCHEMA](#-database-e-schema)

---

## 🚨 TODO ALTA PRIORITÀ

### 1. Gestione Aziende e Dipendenti
**File:** `RegisterAziendaServlet.java`
- **Linea 128:** Implementare associazione utente-azienda in tabella di relazione
- **Linea 135:** Implementare cancellazione azienda per rollback in caso di errore
- **Linea 150:** Implementare rollback completo per cancellazione azienda

### 2. Gestione Sessioni
**File:** `DashboardAziendaServlet.java`
- **Linea 78:** Caricare statistiche specifiche dell'azienda (numero tratte, fermate gestite, ricavi)

### 3. Sistema di Autenticazione
**File:** `LoginServlet.java`
- Rimuovere tutti i print di debug in produzione (linee 40, 41, 62, 68, 69, 78, 81, 88, 90, 94, 99, 106, 130)

---

## 📊 TODO MEDIA PRIORITÀ

### 1. Validazione e Sicurezza
- Implementare validazione lato server più robusta per tutti i form
- Aggiungere token CSRF per protezione form
- Implementare rate limiting per login

### 2. Gestione Errori
- Standardizzare la gestione degli errori in tutti i servlet
- Implementare logging strutturato invece di System.out.println
- Creare pagine di errore personalizzate

### 3. Performance
- Ottimizzare query database
- Implementare caching per dati frequentemente acceduti
- Aggiungere pool di connessioni database

---

## 🔧 TODO BASSA PRIORITÀ

### 1. User Experience
- Migliorare interfaccia dashboard azienda
- Aggiungere notifiche real-time
- Implementare sistema di feedback utenti

### 2. Funzionalità Aggiuntive
- Sistema di reporting avanzato
- Export dati in formato CSV/Excel
- Integrazione con API esterne per orari trasporti

---

## ⚠️ PROBLEMI DA RISOLVERE

### 1. Schema Database
**File:** `database_schema.sql` e `recreate_improved_schema.sql`
- Risolvere duplicazione tabelle `Sessione` e `sessioni`
- Standardizzare nomi tabelle (alcune maiuscole, altre minuscole)
- Verificare coerenza foreign key

### 2. Gestione Eccezioni
**File:** `LoginServlet.java`, `RegisterAziendaServlet.java`
- Gestire SQLException in modo più specifico
- Implementare rollback transazioni database
- Migliorare messaging errori per l'utente

### 3. Session Management
**File:** `LoginServlet.java`, `LogoutServlet.java`
- Sincronizzare gestione sessioni HTTP e database
- Implementare pulizia automatica sessioni scadute
- Gestire casi edge di sessioni orfane

---

## 🏗️ MIGLIORAMENTI ARCHITETTURALI

### 1. Separation of Concerns
- Separare logica business dai servlet
- Implementare service layer
- Creare DTO per trasferimento dati

### 2. Configurazione
- Esternalizzare configurazioni database
- Implementare sistema di properties
- Aggiungere profili ambiente (dev, test, prod)

### 3. Testing
- Implementare unit test per DAO
- Aggiungere integration test per servlet
- Creare test end-to-end per flussi principali

---

## 🐛 DEBUG E LOGGING

### 1. Rimozione Debug Code
**File:** `LoginServlet.java`
- **Linee 40-41:** Rimuovere log email e password
- **Linee 62, 68-69:** Rimuovere log dati utente
- **Linee 78, 81, 88, 90:** Rimuovere log verifica password
- **Linee 94, 99, 106:** Rimuovere log aggiornamento hash
- **Linea 130:** Rimuovere log session ID

### 2. Logging Strutturato
- Sostituire System.out.println con logger appropriato
- Implementare livelli di log (DEBUG, INFO, WARN, ERROR)
- Configurare log rotation e archiving

---

## 💾 DATABASE E SCHEMA

### 1. Schema Improvements
- Consolidare tabelle sessioni
- Aggiungere vincoli integrità mancanti
- Ottimizzare indici per performance query frequenti

### 2. Dati di Test
- Popolare database con dati realistici
- Creare script setup ambiente sviluppo
- Implementare seeding automatico per test

---

## 📋 REQUISITI MANCANTI

### Dal file REQUIREMENT.md:

#### Cliente non registrato ✅
- [x] Inserimento prodotti nel carrello
- [x] Gestione quantità prodotti
- [x] Rimozione prodotti
- [x] Svuotamento carrello

#### Cliente registrato ✅
- [x] Registrazione account
- [x] Login e logout
- [ ] **TODO:** Finalizzazione ordini con dati spedizione e pagamento
- [ ] **TODO:** Visualizzazione storico ordini
- [ ] **TODO:** Carrello svuotato dopo conferma ordine

#### Amministratore ⚠️
- [x] Login dedicato
- [ ] **TODO:** Gestione catalogo completa (inserimento, modifica, visualizzazione, cancellazione)
- [ ] **TODO:** Visualizzazione ordini filtrati per date e cliente

---

## 🎯 PROSSIMI STEP RACCOMANDATI

### Immediati (questa settimana)
1. Implementare associazione utente-azienda
2. Rimuovere debug logging da produzione
3. Consolidare schema database

### A breve termine (prossime 2 settimane)
1. Implementare gestione carrello e ordini
2. Completare funzionalità amministratore
3. Aggiungere sistema logging strutturato

### A lungo termine (prossimo mese)
1. Implementare testing automatizzato
2. Ottimizzare performance database
3. Aggiungere funzionalità avanzate UX

---

## 📊 STATISTICHE TODO

- **Totale TODO:** 25+
- **Alta Priorità:** 6
- **Media Priorità:** 8
- **Bassa Priorità:** 5
- **Problemi Critici:** 6

---

**Ultimo aggiornamento:** 06 Luglio 2025  
**Prossima revisione:** 13 Luglio 2025
