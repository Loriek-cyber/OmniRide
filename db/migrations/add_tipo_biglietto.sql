-- Migration: Add tipo column to Biglietto table
-- Date: 2025-07-19
-- Description: Adds the 'tipo' column to support different ticket types (NORMALE, GIORNALIERO, ANNUALE)

USE omniride;

-- Add the tipo column to the Biglietto table
ALTER TABLE Biglietto 
ADD COLUMN tipo ENUM('NORMALE', 'GIORNALIERO', 'ANNUALE') 
NOT NULL DEFAULT 'NORMALE' 
COMMENT 'Tipo di biglietto: NORMALE (4 ore), GIORNALIERO (24 ore), ANNUALE (365 giorni)'
AFTER prezzo_pagato;

-- Update existing records to have NORMALE as default tipo
UPDATE Biglietto 
SET tipo = 'NORMALE' 
WHERE tipo IS NULL;

-- Add index for better query performance on tipo column
CREATE INDEX idx_biglietto_tipo ON Biglietto(tipo);

-- Add index for combined queries (user + tipo)
CREATE INDEX idx_biglietto_utente_tipo ON Biglietto(id_utente, tipo);

-- Add index for combined queries (stato + tipo)
CREATE INDEX idx_biglietto_stato_tipo ON Biglietto(stato, tipo);

-- Insert a special guest user if it doesn't exist
-- Note: This allows guest users to purchase tickets with id_utente = -1
INSERT IGNORE INTO Utente (id, nome, cognome, email, password_hash, data_registrazione, ruolo) 
VALUES (-1, 'Guest', 'User', 'guest@omniride.local', 'GUEST_USER_NO_PASSWORD', NOW(), 'utente');

-- Verify the changes
DESCRIBE Biglietto;

-- Show sample data to verify the migration
SELECT id, id_utente, stato, prezzo_pagato, tipo 
FROM Biglietto 
LIMIT 5;

COMMIT;

-- Migration completed successfully!
-- 1. Campo tipo: allows distinction between normal, daily and annual tickets
-- 2. Guest user: ID -1 for tickets purchased without registration
-- 3. Indexes: improve performance for common queries
