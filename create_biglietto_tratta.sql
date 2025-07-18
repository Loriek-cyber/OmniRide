-- Script per creare la tabella Biglietto_Tratta
CREATE TABLE Biglietto_Tratta
(
    id_biglietto     bigint NOT NULL,
    id_tratta        bigint NOT NULL,
    fermate_percorse int    NOT NULL COMMENT 'Numero di fermate percorse',
    PRIMARY KEY (id_biglietto, id_tratta),
    CONSTRAINT fk_bt_biglietto
        FOREIGN KEY (id_biglietto) REFERENCES biglietto (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_bt_tratta
        FOREIGN KEY (id_tratta) REFERENCES Tratta (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);
