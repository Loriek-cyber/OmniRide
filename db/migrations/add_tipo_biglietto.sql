-- Script per aggiungere il campo tipo alla tabella biglietto
-- Questo script aggiunge il supporto per i diversi tipi di biglietto (NORMALE, GIORNALIERO, ANNUALE)

-- Aggiunge la colonna tipo come ENUM alla tabella biglietto
ALTER TABLE biglietto 
ADD COLUMN tipo ENUM('NORMALE', 'GIORNALIERO', 'ANNUALE') NOT NULL DEFAULT 'NORMALE';

-- Aggiorna i biglietti esistenti con il tipo NORMALE per default
UPDATE biglietto 
SET tipo = 'NORMALE' 
WHERE tipo IS NULL;

-- Aggiunge un indice per migliorare le performance delle query per tipo
CREATE INDEX idx_biglietto_tipo ON biglietto(tipo);

-- Aggiunge un indice per i biglietti ospite (id_utente = -1)
CREATE INDEX idx_biglietto_guest ON biglietto(id_utente) WHERE id_utente = -1;

-- Aggiunge un indice combinato per stato e tipo
CREATE INDEX idx_biglietto_stato_tipo ON biglietto(stato, tipo);

-- Inserisce un utente speciale per gli ospiti se non esiste
INSERT IGNORE INTO Utente (id, nome, cognome, email, password_hash, data_registrazione, ruolo) 
VALUES (-1, 'Guest', 'User', 'guest@omniride.local', 'GUEST_USER_NO_PASSWORD', NOW(), 'GUEST');

-- Commenti per documentare le modifiche
-- 1. Campo tipo: permette di distinguere tra biglietti normali, giornalieri e annuali
-- 2. Utente ospite: ID -1 per identificare biglietti acquistati senza registrazione
-- 3. Indici: migliorano le performance per le query comuni
