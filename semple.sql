-- ====================================================================
-- SCRIPT DI INSERIMENTO DATI PER LA TRATTA 08-CE (CORRETTO)
-- ====================================================================

SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE Orario_Giorni;
TRUNCATE TABLE Orario_Tratta;
TRUNCATE TABLE Unica_Tratta;
TRUNCATE TABLE Fermata_Tratta;
TRUNCATE TABLE Tratta;
TRUNCATE TABLE Fermata;
TRUNCATE TABLE Azienda;

-- 1. AZIENDA (tipo corretto in base all'enum Java)
INSERT INTO Azienda (nome, tipo) VALUES ('ATC', 'URBANA');
SET @id_azienda = LAST_INSERT_ID();

-- 2. FERMATE (aggiunti indirizzo e tipo)
INSERT INTO Fermata (nome, indirizzo, latitudine, longitudine, tipo, attiva) VALUES
                                                                                 ('Caserta, I.T.I.S. Giordani', '', 0.0, 0.0, 'URBANA', TRUE),
                                                                                 ('Caserta, Stadio Pinto', '', 0.0, 0.0, 'URBANA', TRUE),
                                                                                 -- ... (tutte le altre fermate con stesso pattern)
                                                                                 ('Caserta, Viale Medaglie d''Oro (Stadio)', '', 0.0, 0.0, 'URBANA', TRUE);

-- 3. TRATTA ANDATA
INSERT INTO Tratta (nome, id_azienda) VALUES ('08-CE - Andata (Caserta -> Parete)', @id_azienda);
SET @id_tratta_andata = LAST_INSERT_ID();

INSERT INTO Fermata_Tratta (id_tratta, id_fermata, sequenza, tempo_prossima_fermata)
SELECT @id_tratta_andata, f.id, 0, 5 FROM Fermata f WHERE f.nome = 'Caserta, Terminal Bus FF.SS.'
UNION ALL
-- ... (sequenze come nello script originale)
UNION ALL
SELECT @id_tratta_andata, f.id, 22, 0 FROM Fermata f WHERE f.nome = 'Parete, Via Roberto Follereau Capolinea';

-- Calcola durata totale tratta (110 minuti)
SET @total_duration = '01:50:00';

INSERT INTO Unica_Tratta (id_tratta) VALUES (@id_tratta_andata);
SET @id_unica_tratta_andata = LAST_INSERT_ID();

-- Inserisci ora_fine calcolata
INSERT INTO Orario_Tratta (id_unica_tratta, ora_inizio, ora_fine, attivo) VALUES
                                                                              (@id_unica_tratta_andata, '05:15', ADDTIME('05:15', @total_duration), TRUE),
                                                                              (@id_unica_tratta_andata, '05:45', ADDTIME('05:45', @total_duration), TRUE),
                                                                              -- ... (tutti gli altri orari)
                                                                              (@id_unica_tratta_andata, '20:45', ADDTIME('20:45', @total_duration), TRUE);

-- 4. TRATTA RITORNO (stessa struttura)
INSERT INTO Tratta (nome, id_azienda) VALUES ('08-CE - Ritorno (Parete -> Caserta)', @id_azienda);
SET @id_tratta_ritorno = LAST_INSERT_ID();

INSERT INTO Fermata_Tratta (id_tratta, id_fermata, sequenza, tempo_prossima_fermata)
SELECT @id_tratta_ritorno, f.id, 0, 5 FROM Fermata f WHERE f.nome = 'Parete, Via Roberto Follereau Capolinea'
UNION ALL
-- ... (sequenze ritorno)
UNION ALL
SELECT @id_tratta_ritorno, f.id, 22, 0 FROM Fermata f WHERE f.nome = 'Caserta, Terminal Bus FF.SS.';

INSERT INTO Unica_Tratta (id_tratta) VALUES (@id_tratta_ritorno);
SET @id_unica_tratta_ritorno = LAST_INSERT_ID();

INSERT INTO Orario_Tratta (id_unica_tratta, ora_inizio, ora_fine, attivo) VALUES
                                                                              (@id_unica_tratta_ritorno, '06:40', ADDTIME('06:40', @total_duration), TRUE),
                                                                              -- ... (altri orari ritorno)
                                                                              (@id_unica_tratta_ritorno, '21:05', ADDTIME('21:05', @total_duration), TRUE);

-- 5. GIORNI SETTIMANA (resta invariato)
INSERT INTO Orario_Giorni (id_orario, giorno_settimana)
SELECT ot.id, 'MONDAY' FROM Orario_Tratta ot
WHERE ot.id_unica_tratta IN (@id_unica_tratta_andata, @id_unica_tratta_ritorno)
UNION ALL
-- ... (altri giorni);

SET FOREIGN_KEY_CHECKS=1;