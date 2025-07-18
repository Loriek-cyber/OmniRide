-- Script per resettare completamente il database
-- ATTENZIONE: Questo script eliminerà tutti i dati!

-- Disabilita i controlli delle foreign key per evitare errori durante il DROP
SET FOREIGN_KEY_CHECKS = 0;

-- Elimina tutte le tabelle nell'ordine corretto
DROP TABLE IF EXISTS sessioni;
DROP TABLE IF EXISTS Biglietto_Tratta;
DROP TABLE IF EXISTS biglietto;
DROP TABLE IF EXISTS Carte_Credito;
DROP TABLE IF EXISTS Dipendente;
DROP TABLE IF EXISTS Utente;
DROP TABLE IF EXISTS Tratta_Orari;
DROP TABLE IF EXISTS Fermata_Tratta;
DROP TABLE IF EXISTS Avvisi_tratte;
DROP TABLE IF EXISTS Tratta;
DROP TABLE IF EXISTS Fermata;
DROP TABLE IF EXISTS Azienda;
DROP TABLE IF EXISTS Avvisi;

-- Riabilita i controlli delle foreign key
SET FOREIGN_KEY_CHECKS = 1;

-- Ricrea tutte le tabelle
create table Avvisi
(
    id          bigint auto_increment
        primary key,
    descrizione text                                  not null,
    data_inizio timestamp   default CURRENT_TIMESTAMP not null,
    tipo        varchar(20) default 'INFO'            not null comment 'INFO, WARNING, EMERGENCY'
);

create table Azienda
(
    id   bigint auto_increment
        primary key,
    nome varchar(255) not null,
    tipo varchar(50)  not null comment 'Tipo di azienda di trasporto'
);

create table Fermata
(
    id          bigint auto_increment
        primary key,
    nome        varchar(255)         not null,
    indirizzo   varchar(255)         null,
    latitudine  double               not null,
    longitudine double               not null,
    tipo        varchar(50)          null comment 'Tipo di fermata',
    attiva      tinyint(1) default 1 not null
);

create table Tratta
(
    id         bigint auto_increment
        primary key,
    nome       varchar(255)         not null,
    id_azienda bigint               not null,
    costo      double               null,
    attiva     tinyint(1) default 1 not null,
    constraint fk_tratta_azienda
        foreign key (id_azienda) references Azienda (id)
            on update cascade on delete cascade
);

create table Avvisi_tratte
(
    avviso_id bigint not null,
    tratta_id bigint not null,
    primary key (avviso_id, tratta_id),
    constraint fk_at_avviso
        foreign key (avviso_id) references Avvisi (id)
            on delete cascade,
    constraint fk_at_tratta
        foreign key (tratta_id) references Tratta (id)
            on delete cascade
);

create table Fermata_Tratta
(
    id                     bigint auto_increment
        primary key,
    id_tratta              bigint not null,
    id_fermata             bigint not null,
    sequenza               int    not null comment 'Ordine progressivo (0,1,2...)',
    tempo_prossima_fermata int    not null comment 'Minuti per la tappa seguente',
    constraint uk_tratta_sequenza
        unique (id_tratta, sequenza),
    constraint fk_ft_fermata
        foreign key (id_fermata) references Fermata (id)
            on update cascade on delete cascade,
    constraint fk_ft_tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade
);

create index idx_ft_fermata
    on Fermata_Tratta (id_fermata);

create index idx_ft_tratta
    on Fermata_Tratta (id_tratta);

create index idx_tratta_azienda
    on Tratta (id_azienda);

create table Tratta_Orari
(
    id               bigint auto_increment
        primary key,
    id_tratta        bigint               not null,
    ora_partenza     time                 not null comment 'Partenza capolinea',
    ora_arrivo       time                 null comment 'Arrivo (calcolato)',
    giorni_settimana varchar(100)         not null comment 'LUN,MAR,...',
    attivo           tinyint(1) default 1 not null,
    note             text                 null,
    constraint fk_to_tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade
);

create index idx_to_tratta
    on Tratta_Orari (id_tratta);

create table Utente
(
    id                 bigint auto_increment
        primary key,
    nome               varchar(100)                          not null,
    cognome            varchar(100)                          not null,
    email              varchar(255)                          not null,
    password_hash      varchar(255)                          not null comment 'BCrypt',
    data_registrazione timestamp   default CURRENT_TIMESTAMP not null,
    ruolo              varchar(20) default 'utente'          not null,
    avatar             longblob                              null,
    constraint email
        unique (email)
);

create table Carte_Credito
(
    id_utente         bigint       not null
        primary key,
    nome_intestatario varchar(255) null,
    numero_carta      varchar(16)  null,
    data_scadenza     varchar(5)   null,
    cvv               varchar(4)   null,
    constraint Carte_Credito_ibfk_1
        foreign key (id_utente) references Utente (id)
            on delete cascade
);

create table Dipendente
(
    id_utente       bigint                                not null,
    id_azienda      bigint                                not null,
    ruolo           varchar(50) default 'AUTISTA'         not null comment 'AUTISTA, CONTROLLORE, GESTORE, SUPERVISORE',
    data_assunzione timestamp   default CURRENT_TIMESTAMP not null,
    attivo          tinyint(1)  default 1                 not null,
    primary key (id_utente, id_azienda),
    constraint fk_dipendente_azienda
        foreign key (id_azienda) references Azienda (id)
            on update cascade on delete cascade,
    constraint fk_dipendente_utente
        foreign key (id_utente) references Utente (id)
            on update cascade on delete cascade
);

create index idx_dipendente_azienda
    on Dipendente (id_azienda);

create table biglietto
(
    id             bigint auto_increment
        primary key,
    id_utente      bigint                                                     not null,
    id_tratta      bigint                                                     not null,
    id_orario      bigint                                                     null,
    data_acquisto  datetime                                                   not null,
    data_convalida datetime                                                   null,
    data_scadenza  datetime                                                   null,
    stato          enum ('ACQUISTATO', 'CONVALIDATO', 'SCADUTO', 'ANNULLATO') not null,
    prezzo_pagato  double                                                     not null,
    constraint fk_b_orario
        foreign key (id_orario) references Tratta_Orari (id)
            on update cascade on delete set null,
    constraint fk_b_tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade,
    constraint fk_b_utente
        foreign key (id_utente) references Utente (id)
            on update cascade on delete cascade
);

create index idx_bo
    on biglietto (id_orario);

create index idx_bt
    on biglietto (id_tratta);

create index idx_bu
    on biglietto (id_utente);

create table Biglietto_Tratta
(
    id_biglietto       bigint not null,
    id_tratta          bigint not null,
    fermate_percorse   int    not null comment 'Numero di fermate percorse',
    primary key (id_biglietto, id_tratta),
    constraint fk_bt_biglietto
        foreign key (id_biglietto) references biglietto (id)
            on update cascade on delete cascade,
    constraint fk_bt_tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade
);

create table sessioni
(
    id               bigint auto_increment
        primary key,
    session_id       varchar(255)                         not null,
    utente_id        bigint                               not null,
    creation_time    bigint                               not null,
    last_access_time bigint                               not null,
    is_valid         tinyint(1) default 1                 not null,
    created_at       timestamp  default CURRENT_TIMESTAMP not null,
    updated_at       timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint uniq_session
        unique (session_id),
    constraint fk_s_utente
        foreign key (utente_id) references Utente (id)
            on update cascade on delete cascade
);

create index idx_s_utente
    on sessioni (utente_id);

-- Reset completato
