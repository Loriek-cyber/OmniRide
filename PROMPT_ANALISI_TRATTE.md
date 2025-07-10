# PROMPT PER CHAT: ANALISI SISTEMA TRATTE OMNIRIDE

## CONTESTO
Il sistema Omniride gestisce tratte di trasporto pubblico con una struttura complessa che coinvolge multiple entità e relazioni. Analizza questa composizione per capire il flusso completo dalla creazione di una tratta sulla pagina "addTratta" fino alla tratta completamente funzionante.

## ENTITÀ PRINCIPALI E LORO RUOLI

### 1. **TRATTA** (Tabella: `Tratta`)
- **Classe**: `model.sdata.Tratta`
- **DAO**: `model.dao.TrattaDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico
  - `nome` (String): Nome descrittivo della tratta
  - `id_azienda` (Long): Riferimento all'azienda che gestisce la tratta
  - `costo` (Double): Costo del biglietto per l'intera tratta
  - `attiva` (Boolean): Indica se la tratta è attiva o meno
- **Relazioni**:
  - Ha una lista di `FermataTratta` (percorso con fermate in sequenza)
  - Ha una lista di `OrarioTratta` (orari di servizio)
  - Ha una lista di `UnicaTratta` (per compatibilità legacy)
  - Appartiene a un'`Azienda`

### 2. **FERMATA** (Tabella: `Fermata`)
- **Classe**: `model.sdata.Fermata`
- **DAO**: `model.dao.FermataDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico
  - `nome` (String): Nome della fermata
  - `indirizzo` (String): Indirizzo fisico
  - `latitudine` e `longitudine` (Double): Coordinate GPS
  - `tipo` (Enum): Tipo di fermata (FERMATA_NORMALE, etc.)
  - `attiva` (Boolean): Indica se la fermata è attiva

### 3. **FERMATA_TRATTA** (Tabella: `Fermata_Tratta`)
- **Classe**: `model.sdata.FermataTratta`
- **DAO**: `model.dao.FermataTrattaDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico della relazione
  - `id_tratta` (Long): Riferimento alla tratta
  - `id_fermata` (Long): Riferimento alla fermata
  - `sequenza` (Int): Ordine della fermata nel percorso (1, 2, 3, ...)
  - `tempo_prossima_fermata` (Int): Tempo in minuti per raggiungere la fermata successiva
- **Funzione**: Crea il percorso ordinato delle fermate per ogni tratta

### 4. **ORARIO_TRATTA** (Tabella: `Tratta_Orari`)
- **Classe**: `model.sdata.OrarioTratta`
- **DAO**: `model.dao.OrarioTrattaDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico
  - `id_tratta` (Long): Riferimento alla tratta
  - `ora_partenza` (LocalTime): Ora specifica di partenza dalla prima fermata
  - `ora_arrivo` (LocalTime): Ora specifica di arrivo all'ultima fermata (calcolata automaticamente)
  - `giorni_settimana` (String): Giorni di servizio (es: "LUN,MAR,MER,GIO,VEN")
  - `tipo_servizio` (Enum): NORMALE, FESTIVO, NOTTURNO, EXPRESS
  - `frequenza_minuti` (Integer): **OPZIONALE** - Se presente, indica che questo orario si ripete ogni X minuti
  - `attivo` (Boolean): Indica se l'orario è attivo
  - `note` (String): Note aggiuntive
- **Funzione**: Ogni record rappresenta un **singolo orario specifico** di partenza, NON un intervallo di frequenza

### 5. **UNICA_TRATTA** (Tabella: `Unica_Tratta`)
- **Classe**: `model.sdata.UnicaTratta`
- **DAO**: `model.dao.UnicaTrattaDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico
  - `id_tratta` (Long): Riferimento alla tratta
  - `id_orario` (Long): Riferimento all'orario specifico
- **Funzione**: Rappresenta una singola istanza di tratta con orario specifico (sistema legacy)

### 6. **AZIENDA** (Tabella: `Azienda`)
- **Classe**: `model.udata.Azienda`
- **DAO**: `model.dao.AziendaDAO`
- **Campi principali**:
  - `id` (Long): Identificativo unico
  - `nome` (String): Nome dell'azienda di trasporto
  - `tipo` (String): Tipo di azienda

## PROCESSO DI CREAZIONE TRATTA

### FASE 1: PAGINA ADD TRATTA (`addTratta.jsp`)
**Input dell'utente:**
- **Nome tratta**: Descrizione della linea (es: "Linea 1 - Centro-Periferia")
- **Costo**: Prezzo del biglietto (es: 2.50€)
- **Fermate**: Lista ordinata di fermate con ricerca dinamica
- **Orari**: Orari di partenza multipli
- **Giorni**: Giorni della settimana di servizio

**Campi nascosti nel form:**
- `fermateSelezionate`: Lista ID delle fermate selezionate
- `tempiTraFermate`: Tempi di percorrenza tra fermate consecutive

### FASE 2: SERVLET PROCESSING (`AddTrattaServlet`)
**Stato attuale**: Il servlet è incompleto - manca la logica di elaborazione
**Dovrebbe gestire**:
1. Validazione dei dati ricevuti
2. Creazione dell'oggetto Tratta
3. Salvataggio nella tabella `Tratta`
4. Creazione delle relazioni `FermataTratta`
5. Creazione degli `OrarioTratta`
6. Eventuale creazione delle `UnicaTratta`

### FASE 3: PERSISTENZA DATABASE
**Operazioni necessarie**:
1. **INSERT** in `Tratta` → Ottieni `tratta_id`
2. **INSERT** multipli in `Fermata_Tratta` con:
   - `id_tratta = tratta_id`
   - `id_fermata` per ogni fermata selezionata
   - `sequenza` progressiva (1, 2, 3, ...)
   - `tempo_prossima_fermata` per ogni fermata
3. **INSERT** multipli in `Tratta_Orari` per ogni orario specificato
4. **INSERT** in `Unica_Tratta` per ogni combinazione tratta-orario

## DOMANDE CHIAVE PER LA CHAT

### 1. **Completamento del flusso**
"Il servlet AddTrattaServlet è incompleto. Come dovrei implementare la logica per processare tutti i dati del form e creare una tratta completa nel database? Quali sono i passaggi specifici e l'ordine delle operazioni?"

### 2. **Gestione dei tempi tra fermate**
"Come dovrei gestire i tempi di percorrenza tra fermate consecutive? Dovrei implementare un sistema di calcolo automatico basato sulle coordinate GPS o permettere all'utente di inserire manualmente i tempi?"

### 3. **Relazione tra OrarioTratta e UnicaTratta**
"Qual è la differenza pratica tra OrarioTratta e UnicaTratta? Quando dovrei creare una UnicaTratta e quando posso utilizzare solo OrarioTratta?"

### 4. **Validazione e integrità dei dati**
"Quali validazioni dovrei implementare per garantire l'integrità dei dati? Come gestisco i casi edge come fermate duplicate, orari sovrapposti, o sequenze di fermate non valide?"

### 5. **Ottimizzazione delle query**
"Considerando che TrattaDAO carica tutte le relazioni (FermataTratta, OrarioTratta) nel metodo `getTrattaFromResultSet`, come posso ottimizzare le performance per tratte con molte fermate e orari?"

### 6. **Gestione degli errori**
"Come dovrei gestire gli errori durante la creazione di una tratta? Se fallisce l'inserimento di una FermataTratta, dovrei fare rollback di tutta la transazione?"

### 7. **JavaScript e UX**
"Il file `addTratta.js` dovrebbe gestire la logica lato client per la selezione delle fermate e il calcolo dei tempi. Come posso implementare un'interfaccia intuitiva per l'ordinamento delle fermate e l'inserimento dei tempi?"

### 8. **Estensibilità futura**
"Come posso progettare il sistema per supportare future funzionalità come fermate facoltative, variazioni di percorso, o tariffe differenziate per tratte parziali?"

## CHIARIMENTO IMPORTANTE: COME FUNZIONANO GLI ORARI

### **OrarioTratta NON è una frequenza, ma un insieme di orari specifici**

Cada record `OrarioTratta` rappresenta **UN SINGOLO ORARIO SPECIFICO** di partenza della tratta, non un intervallo o una frequenza.

**Esempi pratici:**

#### Caso 1: Orari discreti (più comune)
```
OrarioTratta 1: ora_partenza=08:00, giorni="LUN,MAR,MER,GIO,VEN", frequenza_minuti=NULL
OrarioTratta 2: ora_partenza=08:30, giorni="LUN,MAR,MER,GIO,VEN", frequenza_minuti=NULL
OrarioTratta 3: ora_partenza=09:00, giorni="LUN,MAR,MER,GIO,VEN", frequenza_minuti=NULL
OrarioTratta 4: ora_partenza=10:00, giorni="SAB,DOM", frequenza_minuti=NULL
```
**Significato**: La tratta parte ESATTAMENTE alle 08:00, 08:30, 09:00 nei giorni feriali, e alle 10:00 nel weekend.

#### Caso 2: Con frequenza (opzionale)
```
OrarioTratta 1: ora_partenza=08:00, giorni="LUN,MAR,MER,GIO,VEN", frequenza_minuti=30
```
**Significato**: La tratta parte alle 08:00 e poi ogni 30 minuti (08:30, 09:00, 09:30, etc.) nei giorni feriali.

### **Relazione OrarioTratta ↔ UnicaTratta**

- **OrarioTratta**: Definisce QUANDO parte la tratta
- **UnicaTratta**: Rappresenta una SINGOLA CORSA con orario specifico

**Esempio:**
```
OrarioTratta (id=1): partenza=08:00, giorni="LUN,MAR,MER"
OrarioTratta (id=2): partenza=08:30, giorni="LUN,MAR,MER"

↓ Genera ↓

UnicaTratta (id=1): tratta_id=1, orario_id=1  [Corsa delle 08:00]
UnicaTratta (id=2): tratta_id=1, orario_id=2  [Corsa delle 08:30]
```

### **Metodi chiave per gli orari:**
- `calcolaOraArrivo(int minutiPercorrenza)`: Calcola automaticamente l'ora di arrivo
- `isValidoPerGiorno(String giorno)`: Verifica se l'orario è valido per un giorno
- `getOrarioFormattato()`: Formatta l'orario per la visualizzazione
- `getGiorniDescrizione()`: Converte "LUN,MAR" in "Lunedì, Martedì"

## STRUTTURA DATI COMPLETA

```
TRATTA (id=1, nome="Linea Centro-Periferia", costo=2.50)
├── FERMATA_TRATTA (seq=1, fermata="Stazione Centrale", tempo_prossima=5min)
├── FERMATA_TRATTA (seq=2, fermata="Piazza Municipio", tempo_prossima=3min)
├── FERMATA_TRATTA (seq=3, fermata="Ospedale", tempo_prossima=7min)
├── FERMATA_TRATTA (seq=4, fermata="Università", tempo_prossima=0min) [ultima]
├── ORARIO_TRATTA (partenza=08:00, giorni="LUN,MAR,MER,GIO,VEN")
├── ORARIO_TRATTA (partenza=08:30, giorni="LUN,MAR,MER,GIO,VEN")
├── ORARIO_TRATTA (partenza=09:00, giorni="SAB,DOM")
├── UNICA_TRATTA (tratta_id=1, orario_id=1)
├── UNICA_TRATTA (tratta_id=1, orario_id=2)
└── UNICA_TRATTA (tratta_id=1, orario_id=3)
```

## METODI DAO DISPONIBILI

### TrattaDAO
- `create(Tratta)` → Long (ID generato)
- `getById(Long)` → Tratta (con tutte le relazioni)
- `update(Tratta)` → boolean
- `delete(Long)` → boolean
- `getAll()` → List<Tratta>
- `getTratteByAzienda(Long)` → List<Tratta>

### FermataTrattaDAO
- `insertFermataTratta(FermataTratta)` → Long
- `updateFermateForTratta(Long, List<FermataTratta>)` → void
- `findFermateByTrattaId(Long)` → List<FermataTratta>
- `deleteFermateByTratta(Long)` → boolean

### OrarioTrattaDAO
- `create(OrarioTratta)` → Long
- `update(OrarioTratta)` → boolean
- `getById(Long)` → OrarioTratta
- `findOrariByTrattaId(Long)` → List<OrarioTratta>
- `delete(Long)` → boolean

### UnicaTrattaDAO
- `create(UnicaTratta)` → Long
- `update(UnicaTratta)` → boolean
- `getById(Long)` → UnicaTratta
- `getLUTfromIDT(Long)` → List<UnicaTratta>
- `delete(Long)` → boolean

---

**Usa questo prompt per ottenere una comprensione completa del sistema e ricevere indicazioni specifiche su come implementare il flusso completo dalla pagina di creazione alla tratta funzionante.**
