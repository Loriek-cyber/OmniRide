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