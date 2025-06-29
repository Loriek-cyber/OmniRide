-- Tabella aziende di trasporto
CREATE TABLE aziende (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nome VARCHAR(100) NOT NULL UNIQUE,
                         tipo ENUM('URBANA', 'EXTRAURBANA', 'MISTA') NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabella fermate
CREATE TABLE fermate (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nome VARCHAR(150) NOT NULL,
                         latitudine DECIMAL(10, 8) NOT NULL,
                         longitudine DECIMAL(11, 8) NOT NULL,
                         tipo ENUM('URBANA', 'EXTRAURBANA', 'INTERCHANGE') DEFAULT 'URBANA',
                         attiva BOOLEAN DEFAULT TRUE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         INDEX idx_coordinate (latitudine, longitudine),
                         INDEX idx_nome (nome),
                         INDEX idx_attiva (attiva)
);

-- Tabella tratte (linee di autobus)
CREATE TABLE tratte (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        nome VARCHAR(100) NOT NULL,
                        azienda_id BIGINT NOT NULL,
                        tipo ENUM('URBANA', 'EXTRAURBANA') NOT NULL,
                        costo_base DECIMAL(5, 2) DEFAULT 0.00,
                        attiva BOOLEAN DEFAULT TRUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (azienda_id) REFERENCES aziende(id) ON DELETE CASCADE,
                        INDEX idx_azienda (azienda_id),
                        INDEX idx_tipo (tipo),
                        INDEX idx_attiva (attiva)
);

-- Tabella collegamento fermate-tratte (ordinato)
CREATE TABLE tratte_fermate (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                tratta_id BIGINT NOT NULL,
                                fermata_id BIGINT NOT NULL,
                                ordine_fermata INT NOT NULL,
                                tempo_prossima_fermata INT DEFAULT 0, -- in secondi
                                distanza_prossima_fermata DECIMAL(8, 2) DEFAULT 0.00, -- in km
                                FOREIGN KEY (tratta_id) REFERENCES tratte(id) ON DELETE CASCADE,
                                FOREIGN KEY (fermata_id) REFERENCES fermate(id) ON DELETE CASCADE,
                                UNIQUE KEY uk_tratta_ordine (tratta_id, ordine_fermata),
                                INDEX idx_tratta (tratta_id),
                                INDEX idx_fermata (fermata_id)
);

-- Tabella orari e frequenze
CREATE TABLE orari_tratte (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              tratta_id BIGINT NOT NULL,
                              ora_inizio TIME NOT NULL,
                              ora_fine TIME NOT NULL,
                              frequenza_minuti INT NOT NULL, -- frequenza di passaggio
                              giorni_settimana SET('LUN','MAR','MER','GIO','VEN','SAB','DOM') NOT NULL,
                              attivo BOOLEAN DEFAULT TRUE,
                              FOREIGN KEY (tratta_id) REFERENCES tratte(id) ON DELETE CASCADE,
                              INDEX idx_tratta_orari (tratta_id, ora_inizio, ora_fine),
                              INDEX idx_attivo (attivo)
);

-- Tabella orari specifici (per orari non frequenziali)
CREATE TABLE partenze_tratte (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 tratta_id BIGINT NOT NULL,
                                 ora_partenza TIME NOT NULL,
                                 giorni_settimana SET('LUN','MAR','MER','GIO','VEN','SAB','DOM') NOT NULL,
                                 attivo BOOLEAN DEFAULT TRUE,
                                 FOREIGN KEY (tratta_id) REFERENCES tratte(id) ON DELETE CASCADE,
                                 INDEX idx_tratta_partenze (tratta_id, ora_partenza),
                                 INDEX idx_attivo (attivo)
);

-- Indici per performance geospaziali
CREATE INDEX idx_fermate_geo ON fermate(latitudine, longitudine);
