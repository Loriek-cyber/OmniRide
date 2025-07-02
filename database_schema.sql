-- =================================================================
--  SCRIPT DI CREAZIONE TABELLE PER IL PROGETTO OMNIRIDE
--  Questo schema è generato per mappare esattamente le classi Java
--  del modello dati (sdata, udata).
-- =================================================================

-- -----------------------------------------------------
-- Tabella: Azienda
-- Corrisponde alla classe: model.udata.Azienda
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Azienda` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `nome` VARCHAR(255) NOT NULL,
                                         `tipo` VARCHAR(50) NOT NULL COMMENT 'Mappa l''enum Azienda.TipoAzienda',
                                         PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Fermata
-- Corrisponde alla classe: model.sdata.Fermata
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Fermata` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `nome` VARCHAR(255) NOT NULL,
                                         `indirizzo` VARCHAR(255) NULL,
                                         `latitudine` DOUBLE NOT NULL,
                                         `longitudine` DOUBLE NOT NULL,
                                         `tipo` VARCHAR(50) NULL COMMENT 'Mappa l''enum Fermata.TipoFermata',
                                         `attiva` BOOLEAN NOT NULL DEFAULT TRUE,
                                         PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Tratta
-- Corrisponde alla classe: model.sdata.Tratta
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Tratta` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `nome` VARCHAR(255) NOT NULL,
                                        `id_azienda` BIGINT NOT NULL,
                                        PRIMARY KEY (`id`),
                                        CONSTRAINT `fk_Tratta_Azienda`
                                            FOREIGN KEY (`id_azienda`)
                                                REFERENCES `Azienda` (`id`)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Fermata_Tratta
-- Modella la lista List<FermataTratta> dentro la classe Tratta.
-- La colonna 'sequenza' è FONDAMENTALE per l'algoritmo di pathfinding.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Fermata_Tratta` (
                                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                `id_tratta` BIGINT NOT NULL,
                                                `id_fermata` BIGINT NOT NULL,
                                                `sequenza` INT NOT NULL COMMENT 'Ordine progressivo della fermata sulla tratta (0, 1, 2...)',
                                                `tempo_prossima_fermata` INT NOT NULL COMMENT 'Minuti per raggiungere la fermata successiva',
                                                PRIMARY KEY (`id`),
                                                UNIQUE KEY `uk_tratta_fermata_sequenza` (`id_tratta`, `id_fermata`, `sequenza`),
                                                CONSTRAINT `fk_Fermata_Tratta_Tratta`
                                                    FOREIGN KEY (`id_tratta`)
                                                        REFERENCES `Tratta` (`id`)
                                                        ON DELETE CASCADE
                                                        ON UPDATE CASCADE,
                                                CONSTRAINT `fk_Fermata_Tratta_Fermata`
                                                    FOREIGN KEY (`id_fermata`)
                                                        REFERENCES `Fermata` (`id`)
                                                        ON DELETE CASCADE
                                                        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Unica_Tratta
-- Corrisponde alla classe: model.sdata.UnicaTratta.
-- Rappresenta una singola corsa di una Tratta (es. la corsa delle 8:05).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Unica_Tratta` (
                                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                                              `id_tratta` BIGINT NOT NULL,
                                              PRIMARY KEY (`id`),
                                              CONSTRAINT `fk_Unica_Tratta_Tratta`
                                                  FOREIGN KEY (`id_tratta`)
                                                      REFERENCES `Tratta` (`id`)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Orario_Tratta
-- Corrisponde alla classe: model.sdata.OrarioTratta.
-- Contiene i dettagli dell'orario di una singola corsa (Unica_Tratta).
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Orario_Tratta` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT,
                                               `id_unica_tratta` BIGINT NOT NULL,
                                               `ora_inizio` TIME NOT NULL COMMENT 'Orario di partenza dal capolinea',
                                               `attivo` BOOLEAN NOT NULL DEFAULT TRUE,
                                               PRIMARY KEY (`id`),
                                               UNIQUE INDEX `idx_id_unica_tratta_unique` (`id_unica_tratta` ASC) COMMENT 'Ogni Unica_Tratta ha un solo orario',
                                               CONSTRAINT `fk_Orario_Tratta_Unica_Tratta`
                                                   FOREIGN KEY (`id_unica_tratta`)
                                                       REFERENCES `Unica_Tratta` (`id`)
                                                       ON DELETE CASCADE
                                                       ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Orario_Giorni
-- Modella il Set<DayOfWeek> dentro la classe OrarioTratta.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Orario_Giorni` (
                                               `id_orario` BIGINT NOT NULL,
                                               `giorno_settimana` VARCHAR(20) NOT NULL COMMENT 'Es: MONDAY, TUESDAY...',
                                               PRIMARY KEY (`id_orario`, `giorno_settimana`),
                                               CONSTRAINT `fk_Orario_Giorni_Orario_Tratta`
                                                   FOREIGN KEY (`id_orario`)
                                                       REFERENCES `Orario_Tratta` (`id`)
                                                       ON DELETE CASCADE
                                                       ON UPDATE CASCADE
) ENGINE = InnoDB;


-- =================================================================
-- SEZIONE UTENTI E BIGLIETTI
-- =================================================================

-- -----------------------------------------------------
-- Tabella: Utente
-- Gestisce gli utenti registrati al sistema.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Utente` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL,
    `cognome` VARCHAR(100) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL COMMENT 'Password hashata con BCrypt',
    `data_registrazione` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ruolo` VARCHAR(20) NOT NULL DEFAULT 'utente' COMMENT 'Es: utente, admin',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Tipo_Biglietto
-- Definisce i tipi di biglietti disponibili per l'acquisto.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Tipo_Biglietto` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL,
    `descrizione` TEXT NULL,
    `prezzo` DECIMAL(10, 2) NOT NULL,
    `validita_giorni` INT NOT NULL COMMENT 'Durata del biglietto in giorni dall''attivazione',
    `id_azienda` BIGINT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_Tipo_Biglietto_Azienda`
        FOREIGN KEY (`id_azienda`)
        REFERENCES `Azienda` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Tabella: Biglietto_Acquistato
-- Traccia i biglietti acquistati dagli utenti.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biglietto_Acquistato` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `id_utente` BIGINT NOT NULL,
    `id_tipo_biglietto` BIGINT NOT NULL,
    `data_acquisto` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `data_attivazione` TIMESTAMP NULL COMMENT 'Data di prima validazione/attivazione',
    `data_scadenza` TIMESTAMP NULL COMMENT 'Calcolata da data_attivazione + validita_giorni',
    `codice_biglietto` VARCHAR(255) NOT NULL UNIQUE COMMENT 'Codice univoco del biglietto, es. UUID',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_Biglietto_Acquistato_Utente`
        FOREIGN KEY (`id_utente`)
        REFERENCES `Utente` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_Biglietto_Acquistato_Tipo_Biglietto`
        FOREIGN KEY (`id_tipo_biglietto`)
        REFERENCES `Tipo_Biglietto` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;