create table Avvisi
(
    id          bigint auto_increment
        primary key,
    descrizione text not null
);

create table Azienda
(
    id   bigint auto_increment
        primary key,
    nome varchar(255) not null,
    tipo varchar(50)  not null comment 'Mappa l''enum Azienda.TipoAzienda'
);

create table Fermata
(
    id          bigint auto_increment
        primary key,
    nome        varchar(255)         not null,
    indirizzo   varchar(255)         null,
    latitudine  double               not null,
    longitudine double               not null,
    tipo        varchar(50)          null comment 'Mappa l''enum Fermata.TipoFermata',
    attiva      tinyint(1) default 1 not null
);

create table Tratta
(
    id         bigint auto_increment
        primary key,
    nome       varchar(255) not null,
    id_azienda bigint       not null,
    costo      double       null,
    constraint fk_Tratta_Azienda
        foreign key (id_azienda) references Azienda (id)
            on update cascade on delete cascade
);

create table Avvisi_tratte
(
    avviso_id bigint not null,
    tratta_id bigint not null,
    primary key (avviso_id, tratta_id),
    constraint Avvisi_tratte_ibfk_1
        foreign key (avviso_id) references Avvisi (id)
            on delete cascade,
    constraint Avvisi_tratte_ibfk_2
        foreign key (tratta_id) references Tratta (id)
            on delete cascade
);

create index tratta_id
    on Avvisi_tratte (tratta_id);

create table Fermata_Tratta
(
    id                     bigint auto_increment
        primary key,
    id_tratta              bigint not null,
    id_fermata             bigint not null,
    sequenza               int    not null comment 'Ordine progressivo della fermata sulla tratta (0, 1, 2...)',
    tempo_prossima_fermata int    not null comment 'Minuti per raggiungere la fermata successiva',
    constraint uk_tratta_fermata_sequenza
        unique (id_tratta, id_fermata, sequenza),
    constraint fk_Fermata_Tratta_Fermata
        foreign key (id_fermata) references Fermata (id)
            on update cascade on delete cascade,
    constraint fk_Fermata_Tratta_Tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade
);

create table Unica_Tratta
(
    id        bigint auto_increment
        primary key,
    id_tratta bigint not null,
    constraint fk_Unica_Tratta_Tratta
        foreign key (id_tratta) references Tratta (id)
            on update cascade on delete cascade
);

create table Orario_Tratta
(
    id              bigint auto_increment
        primary key,
    id_unica_tratta bigint               not null,
    ora_inizio      time                 not null comment 'Orario di partenza dal capolinea',
    attivo          tinyint(1) default 1 not null,
    constraint idx_id_unica_tratta_unique
        unique (id_unica_tratta) comment 'Ogni Unica_Tratta ha un solo orario',
    constraint fk_Orario_Tratta_Unica_Tratta
        foreign key (id_unica_tratta) references Unica_Tratta (id)
            on update cascade on delete cascade
);

create table Orario_Giorni
(
    id_orario        bigint      not null,
    giorno_settimana varchar(20) not null comment 'Es: MONDAY, TUESDAY...',
    primary key (id_orario, giorno_settimana),
    constraint fk_Orario_Giorni_Orario_Tratta
        foreign key (id_orario) references Orario_Tratta (id)
            on update cascade on delete cascade
);

create table Utente
(
    id                 bigint auto_increment
        primary key,
    nome               varchar(100)                          not null,
    cognome            varchar(100)                          not null,
    email              varchar(255)                          not null,
    password_hash      varchar(255)                          not null comment 'Password hashata con BCrypt',
    data_registrazione timestamp   default CURRENT_TIMESTAMP not null,
    ruolo              varchar(20) default 'utente'          not null comment 'Es: utente, admin',
    avatar             longblob                              null,
    constraint email
        unique (email)
);

create table Dipendente
(
    id_utente  bigint not null,
    id_azienda bigint not null,
    primary key (id_utente, id_azienda),
    constraint Dipendente_ibfk_1
        foreign key (id_utente) references Utente (id),
    constraint Dipendente_ibfk_2
        foreign key (id_azienda) references Azienda (id)
);

create index id_azienda
    on Dipendente (id_azienda);

create table Sessione
(
    session_id       varchar(255) not null
        primary key,
    utente_id        bigint       not null,
    creation_time    bigint       not null,
    last_access_time bigint       not null,
    is_valid         tinyint(1)   not null,
    constraint Sessione_ibfk_1
        foreign key (utente_id) references Utente (id)
);

create index utente_id
    on Sessione (utente_id);

create table biglietto
(
    id             bigint auto_increment
        primary key,
    id_utente      bigint                                                     not null,
    id_tratta      bigint                                                     not null,
    data_acquisto  datetime                                                   not null,
    data_convalida datetime                                                   null,
    stato          enum ('ACQUISTATO', 'CONVALIDATO', 'SCADUTO', 'ANNULLATO') not null,
    constraint biglietto_ibfk_1
        foreign key (id_utente) references Utente (id),
    constraint biglietto_ibfk_2
        foreign key (id_tratta) references Tratta (id)
);

create index id_tratta
    on biglietto (id_tratta);

create index id_utente
    on biglietto (id_utente);

create table sessioni
(
    id               bigint auto_increment
        primary key,
    session_id       varchar(255)                         not null comment 'ID univoco della sessione generato dal sistema',
    utente_id        bigint                               not null comment 'ID dell''utente proprietario della sessione',
    creation_time    bigint                               not null comment 'Timestamp di creazione della sessione (epoch seconds)',
    last_access_time bigint                               not null comment 'Timestamp dell''ultimo accesso (epoch seconds)',
    is_valid         tinyint(1) default 1                 not null comment 'Indica se la sessione è ancora valida',
    created_at       timestamp  default CURRENT_TIMESTAMP not null,
    updated_at       timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint session_id
        unique (session_id),
    constraint fk_sessioni_utente
        foreign key (utente_id) references Utente (id)
            on update cascade on delete cascade
)
    comment 'Tabella per la gestione delle sessioni utente';

create index idx_last_access_time
    on sessioni (last_access_time, is_valid);

create index idx_session_id
    on sessioni (session_id);

create index idx_utente_id
    on sessioni (utente_id);

create index idx_valid_sessions
    on sessioni (session_id, is_valid);
