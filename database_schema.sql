-- 1) Disabilito i controlli sulle chiavi esterne
SET FOREIGN_KEY_CHECKS = 0;

-- 2) Elimino tutte le tabelle in un colpo solo (ordine inverso di dipendenze)
DROP TABLE IF EXISTS
    sessioni,
    biglietto,
    Tratta_Orari,
    Avvisi_tratte,
    Fermata_Tratta,
    Fermata,
    Tratta,
    Dipendente,
    Utente,
    Azienda,
    Avvisi;

-- 3) Riattivo i controlli FK
SET FOREIGN_KEY_CHECKS = 1;

-- 4) Creo tutte le tabelle

CREATE TABLE Avvisi (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        descrizione TEXT NOT NULL,
                        data_inizio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        data_fine TIMESTAMP NULL DEFAULT NULL,
                        tipo VARCHAR(20) NOT NULL DEFAULT 'INFO' COMMENT 'INFO, WARNING, EMERGENCY',
                        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Azienda (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(255) NOT NULL,
                         tipo VARCHAR(50) NOT NULL COMMENT 'Tipo di azienda di trasporto',
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Utente (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        nome VARCHAR(100) NOT NULL,
                        cognome VARCHAR(100) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt',
                        data_registrazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        ruolo VARCHAR(20) NOT NULL DEFAULT 'utente',
                        avatar LONGBLOB,
                        PRIMARY KEY (id),
                        UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Dipendente (
                            id_utente BIGINT NOT NULL,
                            id_azienda BIGINT NOT NULL,
                            ruolo VARCHAR(50) NOT NULL DEFAULT 'AUTISTA' COMMENT 'AUTISTA, CONTROLLORE, GESTORE, SUPERVISORE',
                            data_assunzione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            attivo TINYINT(1) NOT NULL DEFAULT '1',
                            PRIMARY KEY (id_utente,id_azienda),
                            KEY idx_dipendente_azienda (id_azienda),
                            CONSTRAINT fk_dipendente_utente FOREIGN KEY (id_utente)
                                REFERENCES Utente (id) ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT fk_dipendente_azienda FOREIGN KEY (id_azienda)
                                REFERENCES Azienda (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Tratta (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        nome VARCHAR(255) NOT NULL,
                        id_azienda BIGINT NOT NULL,
                        costo DOUBLE DEFAULT NULL,
                        attiva TINYINT(1) NOT NULL DEFAULT '1',
                        PRIMARY KEY (id),
                        KEY idx_tratta_azienda (id_azienda),
                        CONSTRAINT fk_tratta_azienda FOREIGN KEY (id_azienda)
                            REFERENCES Azienda (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Fermata (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(255) NOT NULL,
                         indirizzo VARCHAR(255) DEFAULT NULL,
                         latitudine DOUBLE NOT NULL,
                         longitudine DOUBLE NOT NULL,
                         tipo VARCHAR(50) DEFAULT NULL COMMENT 'Tipo di fermata',
                         attiva TINYINT(1) NOT NULL DEFAULT '1',
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Fermata_Tratta (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                id_tratta BIGINT NOT NULL,
                                id_fermata BIGINT NOT NULL,
                                sequenza INT NOT NULL COMMENT 'Ordine progressivo (0,1,2...)',
                                tempo_prossima_fermata INT NOT NULL COMMENT 'Minuti per la tappa seguente',
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_tratta_sequenza (id_tratta,sequenza),
                                KEY idx_ft_tratta (id_tratta),
                                KEY idx_ft_fermata (id_fermata),
                                CONSTRAINT fk_ft_tratta FOREIGN KEY (id_tratta)
                                    REFERENCES Tratta (id) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT fk_ft_fermata FOREIGN KEY (id_fermata)
                                    REFERENCES Fermata (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Avvisi_tratte (
                               avviso_id BIGINT NOT NULL,
                               tratta_id BIGINT NOT NULL,
                               PRIMARY KEY (avviso_id,tratta_id),
                               CONSTRAINT fk_at_avviso FOREIGN KEY (avviso_id)
                                   REFERENCES Avvisi (id) ON DELETE CASCADE,
                               CONSTRAINT fk_at_tratta FOREIGN KEY (tratta_id)
                                   REFERENCES Tratta (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Tratta_Orari (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              id_tratta BIGINT NOT NULL,
                              ora_partenza TIME NOT NULL COMMENT 'Partenza capolinea',
                              ora_arrivo TIME DEFAULT NULL COMMENT 'Arrivo (calcolato)',
                              giorni_settimana VARCHAR(50) NOT NULL COMMENT 'LUN,MAR,...',
                              tipo_servizio VARCHAR(50) NOT NULL DEFAULT 'NORMALE' COMMENT 'NORMALE, FESTIVO, NOTTURNO, EXPRESS',
                              frequenza_minuti INT DEFAULT NULL,
                              attivo TINYINT(1) NOT NULL DEFAULT '1',
                              note TEXT,
                              PRIMARY KEY (id),
                              KEY idx_to_tratta (id_tratta),
                              CONSTRAINT fk_to_tratta FOREIGN KEY (id_tratta)
                                  REFERENCES Tratta (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE biglietto (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           id_utente BIGINT NOT NULL,
                           id_tratta BIGINT NOT NULL,
                           id_orario BIGINT DEFAULT NULL,
                           data_acquisto DATETIME NOT NULL,
                           data_convalida DATETIME DEFAULT NULL,
                           data_scadenza DATETIME DEFAULT NULL,
                           stato ENUM('ACQUISTATO','CONVALIDATO','SCADUTO','ANNULLATO') NOT NULL,
                           prezzo_pagato DECIMAL(10,2) NOT NULL,
                           PRIMARY KEY (id),
                           KEY idx_bu (id_utente),
                           KEY idx_bt (id_tratta),
                           KEY idx_bo (id_orario),
                           CONSTRAINT fk_b_utente FOREIGN KEY (id_utente)
                               REFERENCES Utente (id) ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT fk_b_tratta FOREIGN KEY (id_tratta)
                               REFERENCES Tratta (id) ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT fk_b_orario FOREIGN KEY (id_orario)
                               REFERENCES Tratta_Orari (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sessioni (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          session_id VARCHAR(255) NOT NULL,
                          utente_id BIGINT NOT NULL,
                          creation_time BIGINT NOT NULL,
                          last_access_time BIGINT NOT NULL,
                          is_valid TINYINT(1) NOT NULL DEFAULT '1',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uniq_session (session_id),
                          KEY idx_s_utente (utente_id),
                          CONSTRAINT fk_s_utente FOREIGN KEY (utente_id)
                              REFERENCES Utente (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;