-- ============================================
-- SCHEMA DATABASE MIGLIORATO - OMNIRIDE
-- Versione con gestione orari semplificata
-- ============================================

-- Elimina le tabelle esistenti in ordine di dipendenza
DROP TABLE IF EXISTS biglietto;
DROP TABLE IF EXISTS sessioni;
DROP TABLE IF EXISTS Sessione;
DROP TABLE IF EXISTS Dipendente;
DROP TABLE IF EXISTS Orario_Giorni;
DROP TABLE IF EXISTS Orario_Tratta;
DROP TABLE IF EXISTS Unica_Tratta;
DROP TABLE IF EXISTS Fermata_Tratta;
DROP TABLE IF EXISTS Avvisi_tratte;
DROP TABLE IF EXISTS Tratta_Orari;
DROP TABLE IF EXISTS Tratta;
DROP TABLE IF EXISTS Fermata;
DROP TABLE IF EXISTS Avvisi;
DROP TABLE IF EXISTS Azienda;
DROP TABLE IF EXISTS Utente;

-- Tabella Utenti
CREATE TABLE Utente (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome               VARCHAR(100)                          NOT NULL,
    cognome            VARCHAR(100)                          NOT NULL,
    email              VARCHAR(255)                          NOT NULL,
    password_hash      VARCHAR(255)                          NOT NULL COMMENT 'Password hashata con BCrypt',
    data_registrazione TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ruolo              VARCHAR(20) DEFAULT 'utente'          NOT NULL COMMENT 'Es: utente, admin, azienda',
    avatar             LONGBLOB                              NULL,
    CONSTRAINT email UNIQUE (email)
);

-- Tabella Aziende
CREATE TABLE Azienda (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50)  NOT NULL COMMENT 'Tipo di azienda di trasporto'
);

-- Tabella Fermate
CREATE TABLE Fermata (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(255)         NOT NULL,
    indirizzo   VARCHAR(255)         NULL,
    latitudine  DOUBLE               NOT NULL,
    longitudine DOUBLE               NOT NULL,
    tipo        VARCHAR(50)          NULL COMMENT 'Tipo di fermata',
    attiva      TINYINT(1) DEFAULT 1 NOT NULL
);

-- Tabella Tratte
CREATE TABLE Tratta (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(255) NOT NULL,
    id_azienda BIGINT       NOT NULL,
    costo      DOUBLE       NULL,
    attiva     TINYINT(1)   DEFAULT 1 NOT NULL,
    CONSTRAINT fk_Tratta_Azienda
        FOREIGN KEY (id_azienda) REFERENCES Azienda (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabella di collegamento Fermata-Tratta (con sequenza)
CREATE TABLE Fermata_Tratta (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_tratta              BIGINT NOT NULL,
    id_fermata             BIGINT NOT NULL,
    sequenza               INT    NOT NULL COMMENT 'Ordine progressivo della fermata sulla tratta (0, 1, 2...)',
    tempo_prossima_fermata INT    NOT NULL COMMENT 'Minuti per raggiungere la fermata successiva',
    CONSTRAINT uk_tratta_fermata_sequenza
        UNIQUE (id_tratta, sequenza),
    CONSTRAINT fk_Fermata_Tratta_Fermata
        FOREIGN KEY (id_fermata) REFERENCES Fermata (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_Fermata_Tratta_Tratta
        FOREIGN KEY (id_tratta) REFERENCES Tratta (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabella Orari delle Tratte (SEMPLIFICATA)
CREATE TABLE Tratta_Orari (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_tratta         BIGINT       NOT NULL,
    ora_partenza      TIME         NOT NULL COMMENT 'Orario di partenza dal capolinea',
    ora_arrivo        TIME         NULL COMMENT 'Orario di arrivo al capolinea (calcolato automaticamente)',
    giorni_settimana  VARCHAR(50)  NOT NULL COMMENT 'Giorni della settimana: LUN,MAR,MER,GIO,VEN,SAB,DOM',
    tipo_servizio     VARCHAR(20)  DEFAULT 'NORMALE' NOT NULL COMMENT 'NORMALE, FESTIVO, NOTTURNO, EXPRESS',
    frequenza_minuti  INT          NULL COMMENT 'Frequenza in minuti per servizi ripetitivi',
    attivo            TINYINT(1)   DEFAULT 1 NOT NULL,
    note              TEXT         NULL COMMENT 'Note aggiuntive sull orario',
    CONSTRAINT fk_Tratta_Orari_Tratta
        FOREIGN KEY (id_tratta) REFERENCES Tratta (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabella Avvisi
CREATE TABLE Avvisi (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    descrizione TEXT NOT NULL,
    data_inizio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_fine   TIMESTAMP NULL,
    tipo        VARCHAR(20) DEFAULT 'INFO' NOT NULL COMMENT 'INFO, WARNING, EMERGENCY'
);

-- Tabella di collegamento Avvisi-Tratte
CREATE TABLE Avvisi_tratte (
    avviso_id BIGINT NOT NULL,
    tratta_id BIGINT NOT NULL,
    PRIMARY KEY (avviso_id, tratta_id),
    CONSTRAINT Avvisi_tratte_ibfk_1
        FOREIGN KEY (avviso_id) REFERENCES Avvisi (id)
            ON DELETE CASCADE,
    CONSTRAINT Avvisi_tratte_ibfk_2
        FOREIGN KEY (tratta_id) REFERENCES Tratta (id)
            ON DELETE CASCADE
);

-- Tabella Dipendenti
CREATE TABLE Dipendente (
    id_utente  BIGINT NOT NULL,
    id_azienda BIGINT NOT NULL,
    ruolo      VARCHAR(50) DEFAULT 'AUTISTA' NOT NULL COMMENT 'AUTISTA, CONTROLLORE, GESTORE, SUPERVISORE',
    data_assunzione TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    attivo     TINYINT(1) DEFAULT 1 NOT NULL,
    PRIMARY KEY (id_utente, id_azienda),
    CONSTRAINT Dipendente_ibfk_1
        FOREIGN KEY (id_utente) REFERENCES Utente (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT Dipendente_ibfk_2
        FOREIGN KEY (id_azienda) REFERENCES Azienda (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabella Sessioni
CREATE TABLE sessioni (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id       VARCHAR(255)                         NOT NULL COMMENT 'ID univoco della sessione',
    utente_id        BIGINT                               NOT NULL,
    creation_time    BIGINT                               NOT NULL COMMENT 'Timestamp creazione (epoch seconds)',
    last_access_time BIGINT                               NOT NULL COMMENT 'Timestamp ultimo accesso (epoch seconds)',
    is_valid         TINYINT(1) DEFAULT 1                 NOT NULL,
    created_at       TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT session_id UNIQUE (session_id),
    CONSTRAINT fk_sessioni_utente
        FOREIGN KEY (utente_id) REFERENCES Utente (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabella Biglietti
CREATE TABLE biglietto (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utente      BIGINT                                                     NOT NULL,
    id_tratta      BIGINT                                                     NOT NULL,
    id_orario      BIGINT                                                     NULL COMMENT 'Riferimento all orario specifico',
    data_acquisto  DATETIME                                                   NOT NULL,
    data_convalida DATETIME                                                   NULL,
    data_scadenza  DATETIME                                                   NULL,
    stato          ENUM ('ACQUISTATO', 'CONVALIDATO', 'SCADUTO', 'ANNULLATO') NOT NULL,
    prezzo_pagato  DECIMAL(10,2)                                              NOT NULL,
    CONSTRAINT biglietto_ibfk_1
        FOREIGN KEY (id_utente) REFERENCES Utente (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT biglietto_ibfk_2
        FOREIGN KEY (id_tratta) REFERENCES Tratta (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT biglietto_ibfk_3
        FOREIGN KEY (id_orario) REFERENCES Tratta_Orari (id)
            ON UPDATE CASCADE ON DELETE SET NULL
);

-- ============================================
-- INDICI PER PERFORMANCE
-- ============================================

CREATE INDEX idx_tratta_azienda ON Tratta (id_azienda);
CREATE INDEX idx_fermata_tratta_tratta ON Fermata_Tratta (id_tratta);
CREATE INDEX idx_fermata_tratta_fermata ON Fermata_Tratta (id_fermata);
CREATE INDEX idx_fermata_tratta_sequenza ON Fermata_Tratta (id_tratta, sequenza);
CREATE INDEX idx_tratta_orari_tratta ON Tratta_Orari (id_tratta);
CREATE INDEX idx_tratta_orari_partenza ON Tratta_Orari (ora_partenza);
CREATE INDEX idx_tratta_orari_giorni ON Tratta_Orari (giorni_settimana);
CREATE INDEX idx_biglietto_utente ON biglietto (id_utente);
CREATE INDEX idx_biglietto_tratta ON biglietto (id_tratta);
CREATE INDEX idx_biglietto_orario ON biglietto (id_orario);
CREATE INDEX idx_biglietto_stato ON biglietto (stato);
CREATE INDEX idx_sessioni_session_id ON sessioni (session_id);
CREATE INDEX idx_sessioni_utente ON sessioni (utente_id);
CREATE INDEX idx_sessioni_valid ON sessioni (session_id, is_valid);
CREATE INDEX idx_sessioni_access_time ON sessioni (last_access_time, is_valid);

-- ============================================
-- DATI DI ESEMPIO
-- ============================================

-- Inserisci aziende di esempio
INSERT INTO Azienda (nome, tipo) VALUES 
('Bus Urbano SpA', 'URBANO'),
('Trasporti Extraurbani', 'EXTRAURBANO'),
('Metro City', 'METROPOLITANO');

-- Inserisci fermate di esempio
INSERT INTO Fermata (nome, indirizzo, latitudine, longitudine, tipo) VALUES 
('Stazione Centrale', 'Piazza Stazione 1', 40.8518, 14.2681, 'STAZIONE'),
('Piazza Garibaldi', 'Piazza Garibaldi', 40.8533, 14.2739, 'URBANA'),
('Aeroporto', 'Via Aeroporto 100', 40.8860, 14.2908, 'EXTRAURBANA'),
('Università', 'Via Università 50', 40.8394, 14.2500, 'URBANA'),
('Ospedale', 'Via Ospedale 20', 40.8600, 14.2800, 'URBANA');

-- Inserisci tratte di esempio
INSERT INTO Tratta (nome, id_azienda, costo) VALUES 
('Linea 1 - Centro-Università', 1, 1.50),
('Linea 2 - Stazione-Aeroporto', 2, 3.00),
('Linea 3 - Circolare Centro', 1, 1.20);

-- Inserisci fermate per le tratte
INSERT INTO Fermata_Tratta (id_tratta, id_fermata, sequenza, tempo_prossima_fermata) VALUES 
-- Linea 1: Stazione -> Piazza Garibaldi -> Università
(1, 1, 0, 5),  -- Stazione Centrale
(1, 2, 1, 10), -- Piazza Garibaldi
(1, 4, 2, 0),  -- Università (ultima fermata)

-- Linea 2: Stazione -> Aeroporto
(2, 1, 0, 25), -- Stazione Centrale
(2, 3, 1, 0),  -- Aeroporto (ultima fermata)

-- Linea 3: Circolare
(3, 1, 0, 8),  -- Stazione Centrale
(3, 2, 1, 6),  -- Piazza Garibaldi
(3, 5, 2, 12), -- Ospedale
(3, 1, 3, 0);  -- Ritorno Stazione Centrale

-- Inserisci orari per le tratte
INSERT INTO Tratta_Orari (id_tratta, ora_partenza, giorni_settimana, tipo_servizio, frequenza_minuti) VALUES 
-- Linea 1 - servizio frequente nei giorni feriali
(1, '06:00:00', 'LUN,MAR,MER,GIO,VEN', 'NORMALE', 15),
(1, '06:30:00', 'SAB', 'NORMALE', 20),
(1, '08:00:00', 'DOM', 'FESTIVO', 30),

-- Linea 2 - servizio aeroporto
(2, '05:30:00', 'LUN,MAR,MER,GIO,VEN,SAB,DOM', 'EXPRESS', 60),
(2, '23:00:00', 'VEN,SAB', 'NOTTURNO', 120),

-- Linea 3 - circolare centro
(3, '07:00:00', 'LUN,MAR,MER,GIO,VEN', 'NORMALE', 10),
(3, '09:00:00', 'SAB,DOM', 'FESTIVO', 20);

-- Inserisci utenti di esempio
INSERT INTO Utente (nome, cognome, email, password_hash, ruolo) VALUES 
('Mario', 'Rossi', 'mario.rossi@email.com', '$2a$10$example_hash_1', 'utente'),
('Giulia', 'Bianchi', 'giulia.bianchi@email.com', '$2a$10$example_hash_2', 'utente'),
('Admin', 'Sistema', 'admin@omniride.com', '$2a$10$example_hash_admin', 'admin'),
('Trasporti', 'SpA', 'info@trasporti.com', '$2a$10$example_hash_azienda', 'azienda');

-- Associa utente azienda
INSERT INTO Dipendente (id_utente, id_azienda, ruolo) VALUES 
(4, 1, 'GESTORE');

COMMIT;
