mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 9.3.0, for Linux (x86_64)
--
-- Host: localhost    Database: omniride
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Avvisi`
--

DROP TABLE IF EXISTS `Avvisi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Avvisi` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descrizione` text NOT NULL,
  `data_inizio` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_fine` timestamp NULL DEFAULT NULL,
  `tipo` varchar(20) NOT NULL DEFAULT 'INFO' COMMENT 'INFO, WARNING, EMERGENCY',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Avvisi`
--

LOCK TABLES `Avvisi` WRITE;
/*!40000 ALTER TABLE `Avvisi` DISABLE KEYS */;
/*!40000 ALTER TABLE `Avvisi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Avvisi_tratte`
--

DROP TABLE IF EXISTS `Avvisi_tratte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Avvisi_tratte` (
  `avviso_id` bigint NOT NULL,
  `tratta_id` bigint NOT NULL,
  PRIMARY KEY (`avviso_id`,`tratta_id`),
  KEY `Avvisi_tratte_ibfk_2` (`tratta_id`),
  CONSTRAINT `Avvisi_tratte_ibfk_1` FOREIGN KEY (`avviso_id`) REFERENCES `Avvisi` (`id`) ON DELETE CASCADE,
  CONSTRAINT `Avvisi_tratte_ibfk_2` FOREIGN KEY (`tratta_id`) REFERENCES `Tratta` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Avvisi_tratte`
--

LOCK TABLES `Avvisi_tratte` WRITE;
/*!40000 ALTER TABLE `Avvisi_tratte` DISABLE KEYS */;
/*!40000 ALTER TABLE `Avvisi_tratte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Azienda`
--

DROP TABLE IF EXISTS `Azienda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Azienda` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `tipo` varchar(50) NOT NULL COMMENT 'Tipo di azienda di trasporto',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Azienda`
--

LOCK TABLES `Azienda` WRITE;
/*!40000 ALTER TABLE `Azienda` DISABLE KEYS */;
INSERT INTO `Azienda` VALUES (1,'Bus Urbano SpA','URBANO'),(2,'Trasporti Extraurbani','EXTRAURBANO'),(3,'Metro City','METROPOLITANO'),(4,'TurEnco','turistica'),(5,'ATM Milano','Urbano'),(6,'ATAC Roma','Urbano'),(7,'GTT Torino','Urbano'),(8,'FlixBus','Extraurbano'),(9,'Trenitalia','Ferroviario'),(10,'Italo','Ferroviario'),(11,'SITA Sud','Extraurbano'),(12,'Autolinee Varese','Extraurbano'),(13,'ATM Milano','Urbano'),(14,'ATAC Roma','Urbano'),(15,'GTT Torino','Urbano'),(16,'FlixBus','Extraurbano'),(17,'Trenitalia','Ferroviario'),(18,'Italo','Ferroviario'),(19,'SITA Sud','Extraurbano'),(20,'Autolinee Varese','Extraurbano');
/*!40000 ALTER TABLE `Azienda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Dipendente`
--

DROP TABLE IF EXISTS `Dipendente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Dipendente` (
  `id_utente` bigint NOT NULL,
  `id_azienda` bigint NOT NULL,
  `ruolo` varchar(50) NOT NULL DEFAULT 'AUTISTA' COMMENT 'AUTISTA, CONTROLLORE, GESTORE, SUPERVISORE',
  `data_assunzione` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `attivo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_utente`,`id_azienda`),
  KEY `Dipendente_ibfk_2` (`id_azienda`),
  CONSTRAINT `Dipendente_ibfk_1` FOREIGN KEY (`id_utente`) REFERENCES `Utente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Dipendente_ibfk_2` FOREIGN KEY (`id_azienda`) REFERENCES `Azienda` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Dipendente`
--

LOCK TABLES `Dipendente` WRITE;
/*!40000 ALTER TABLE `Dipendente` DISABLE KEYS */;
INSERT INTO `Dipendente` VALUES (4,1,'GESTORE','2025-07-04 21:56:15',1);
/*!40000 ALTER TABLE `Dipendente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Fermata`
--

DROP TABLE IF EXISTS `Fermata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Fermata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `indirizzo` varchar(255) DEFAULT NULL,
  `latitudine` double NOT NULL,
  `longitudine` double NOT NULL,
  `tipo` varchar(50) DEFAULT NULL COMMENT 'Tipo di fermata',
  `attiva` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Fermata`
--

LOCK TABLES `Fermata` WRITE;
/*!40000 ALTER TABLE `Fermata` DISABLE KEYS */;
INSERT INTO `Fermata` VALUES (1,'Stazione Centrale','Piazza Stazione 1',40.8518,14.2681,'STAZIONE',1),(2,'Piazza Garibaldi','Piazza Garibaldi',40.8533,14.2739,'URBANA',1),(3,'Aeroporto','Via Aeroporto 100',40.886,14.2908,'EXTRAURBANA',1),(4,'Universit├á','Via Universit├á 50',40.8394,14.25,'URBANA',1),(5,'Ospedale','Via Ospedale 20',40.86,14.28,'URBANA',1),(6,'Duomo','Piazza del Duomo, Milano',45.4642,9.19,'Metro',1),(7,'Porta Garibaldi','Piazzale Porta Garibaldi, Milano',45.4851,9.1877,'Metro',1),(8,'Centrale FS','Piazza Duca d\'Aosta, Milano',45.4862,9.2051,'Stazione',1),(9,'Termini','Piazza dei Cinquecento, Roma',41.901,12.5013,'Stazione',1),(10,'Colosseo','Piazza del Colosseo, Roma',41.8902,12.4923,'Metro',1),(11,'Porta Nuova','Corso Giulio Cesare, Torino',45.0627,7.6782,'Stazione',1),(12,'Piazza Castello','Via Po, Torino',45.0721,7.6858,'Urbano',1),(13,'Aeroporto Malpensa','Via Aeroporto, Ferno',45.6306,8.7281,'Aeroporto',1),(14,'Stazione Tiburtina','Piazzale della Stazione Tiburtina, Roma',41.9097,12.5311,'Stazione',1),(15,'Piazza Venezia','Piazza Venezia, Roma',41.8958,12.4826,'Urbano',1),(16,'Porta Susa','Via Sacchi, Torino',45.0703,7.6659,'Stazione',1),(17,'San Siro','Via dei Piccolomini, Milano',45.4781,9.1239,'Metro',1),(18,'Navigli','Porta Ticinese, Milano',45.4484,9.177,'Urbano',1),(19,'Trastevere','Viale Trastevere, Roma',41.8733,12.4663,'Stazione',1),(20,'Lingotto','Via Nizza, Torino',45.0302,7.6631,'Metro',1),(21,'Duomo','Piazza del Duomo, Milano',45.4642,9.19,'Metro',1),(22,'Porta Garibaldi','Piazzale Porta Garibaldi, Milano',45.4851,9.1877,'Metro',1),(23,'Centrale FS','Piazza Duca d\'Aosta, Milano',45.4862,9.2051,'Stazione',1),(24,'Termini','Piazza dei Cinquecento, Roma',41.901,12.5013,'Stazione',1),(25,'Colosseo','Piazza del Colosseo, Roma',41.8902,12.4923,'Metro',1),(26,'Porta Nuova','Corso Giulio Cesare, Torino',45.0627,7.6782,'Stazione',1),(27,'Piazza Castello','Via Po, Torino',45.0721,7.6858,'Urbano',1),(28,'Aeroporto Malpensa','Via Aeroporto, Ferno',45.6306,8.7281,'Aeroporto',1),(29,'Stazione Tiburtina','Piazzale della Stazione Tiburtina, Roma',41.9097,12.5311,'Stazione',1),(30,'Piazza Venezia','Piazza Venezia, Roma',41.8958,12.4826,'Urbano',1),(31,'Porta Susa','Via Sacchi, Torino',45.0703,7.6659,'Stazione',1),(32,'San Siro','Via dei Piccolomini, Milano',45.4781,9.1239,'Metro',1),(33,'Navigli','Porta Ticinese, Milano',45.4484,9.177,'Urbano',1),(34,'Trastevere','Viale Trastevere, Roma',41.8733,12.4663,'Stazione',1),(35,'Lingotto','Via Nizza, Torino',45.0302,7.6631,'Metro',1);
/*!40000 ALTER TABLE `Fermata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Fermata_Tratta`
--

DROP TABLE IF EXISTS `Fermata_Tratta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Fermata_Tratta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_tratta` bigint NOT NULL,
  `id_fermata` bigint NOT NULL,
  `sequenza` int NOT NULL COMMENT 'Ordine progressivo della fermata sulla tratta (0, 1, 2...)',
  `tempo_prossima_fermata` int NOT NULL COMMENT 'Minuti per raggiungere la fermata successiva',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tratta_fermata_sequenza` (`id_tratta`,`sequenza`),
  KEY `idx_fermata_tratta_tratta` (`id_tratta`),
  KEY `idx_fermata_tratta_fermata` (`id_fermata`),
  KEY `idx_fermata_tratta_sequenza` (`id_tratta`,`sequenza`),
  CONSTRAINT `fk_Fermata_Tratta_Fermata` FOREIGN KEY (`id_fermata`) REFERENCES `Fermata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_Fermata_Tratta_Tratta` FOREIGN KEY (`id_tratta`) REFERENCES `Tratta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Fermata_Tratta`
--

LOCK TABLES `Fermata_Tratta` WRITE;
/*!40000 ALTER TABLE `Fermata_Tratta` DISABLE KEYS */;
INSERT INTO `Fermata_Tratta` VALUES (1,1,1,0,5),(2,1,2,1,10),(3,1,4,2,0),(4,2,1,0,25),(5,2,3,1,0),(6,3,1,0,8),(7,3,2,1,6),(8,3,5,2,12),(9,3,1,3,0);
/*!40000 ALTER TABLE `Fermata_Tratta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tratta`
--

DROP TABLE IF EXISTS `Tratta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tratta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `id_azienda` bigint NOT NULL,
  `costo` double DEFAULT NULL,
  `attiva` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_tratta_azienda` (`id_azienda`),
  CONSTRAINT `fk_Tratta_Azienda` FOREIGN KEY (`id_azienda`) REFERENCES `Azienda` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tratta`
--

LOCK TABLES `Tratta` WRITE;
/*!40000 ALTER TABLE `Tratta` DISABLE KEYS */;
INSERT INTO `Tratta` VALUES (1,'Linea 1 - Centro-Universit├á',1,1.5,1),(2,'Linea 2 - Stazione-Aeroporto',2,3,1),(3,'Linea 3 - Circolare Centro',1,1.2,1),(4,'Linea M1 - Duomo-Porta Garibaldi',1,1.5,1),(5,'Linea M2 - Centrale-Navigli',1,1.5,1),(6,'Linea A - Termini-Colosseo',2,1.5,1),(7,'Linea B - Termini-Tiburtina',2,1.5,1),(8,'Linea 1 - Porta Nuova-Castello',3,1.3,1),(9,'Milano-Roma Express',4,35,1),(10,'Roma-Napoli AV',5,45,1),(11,'Milano-Venezia',6,39,1),(12,'Torino-Milano',7,15,1),(13,'Malpensa Express',8,13,1),(14,'Circolare Centro Milano',1,1.5,1),(15,'Bus Turistico Roma',2,5,1),(16,'Metro Torino Linea 1',3,1.3,1),(17,'Roma-Fiumicino',4,8,1),(18,'Linea Notturna Milano',1,2,1),(19,'Linea M1 - Duomo-Porta Garibaldi',1,1.5,1),(20,'Linea M2 - Centrale-Navigli',1,1.5,1),(21,'Linea A - Termini-Colosseo',2,1.5,1),(22,'Linea B - Termini-Tiburtina',2,1.5,1),(23,'Linea 1 - Porta Nuova-Castello',3,1.3,1),(24,'Milano-Roma Express',4,35,1),(25,'Roma-Napoli AV',5,45,1),(26,'Milano-Venezia',6,39,1),(27,'Torino-Milano',7,15,1),(28,'Malpensa Express',8,13,1),(29,'Circolare Centro Milano',1,1.5,1),(30,'Bus Turistico Roma',2,5,1),(31,'Metro Torino Linea 1',3,1.3,1),(32,'Roma-Fiumicino',4,8,1),(33,'Linea Notturna Milano',1,2,1);
/*!40000 ALTER TABLE `Tratta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tratta_Orari`
--

DROP TABLE IF EXISTS `Tratta_Orari`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tratta_Orari` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_tratta` bigint NOT NULL,
  `ora_partenza` time NOT NULL COMMENT 'Orario di partenza dal capolinea',
  `ora_arrivo` time DEFAULT NULL COMMENT 'Orario di arrivo al capolinea (calcolato automaticamente)',
  `giorni_settimana` varchar(50) NOT NULL COMMENT 'Giorni della settimana: LUN,MAR,MER,GIO,VEN,SAB,DOM',
  `tipo_servizio` varchar(50) NOT NULL DEFAULT 'NORMALE' COMMENT 'NORMALE, FESTIVO, NOTTURNO, EXPRESS',
  `frequenza_minuti` int DEFAULT NULL COMMENT 'Frequenza in minuti per servizi ripetitivi',
  `attivo` tinyint(1) NOT NULL DEFAULT '1',
  `note` text COMMENT 'Note aggiuntive sull orario',
  PRIMARY KEY (`id`),
  KEY `idx_tratta_orari_tratta` (`id_tratta`),
  KEY `idx_tratta_orari_partenza` (`ora_partenza`),
  KEY `idx_tratta_orari_giorni` (`giorni_settimana`),
  CONSTRAINT `fk_Tratta_Orari_Tratta` FOREIGN KEY (`id_tratta`) REFERENCES `Tratta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tratta_Orari`
--

LOCK TABLES `Tratta_Orari` WRITE;
/*!40000 ALTER TABLE `Tratta_Orari` DISABLE KEYS */;
INSERT INTO `Tratta_Orari` VALUES (1,1,'06:00:00',NULL,'LUN,MAR,MER,GIO,VEN','NORMALE',15,1,NULL),(2,1,'06:30:00',NULL,'SAB','NORMALE',20,1,NULL),(3,1,'08:00:00',NULL,'DOM','FESTIVO',30,1,NULL),(4,2,'05:30:00',NULL,'LUN,MAR,MER,GIO,VEN,SAB,DOM','EXPRESS',60,1,NULL),(5,2,'23:00:00',NULL,'VEN,SAB','NOTTURNO',120,1,NULL),(6,3,'07:00:00',NULL,'LUN,MAR,MER,GIO,VEN','NORMALE',10,1,NULL),(7,3,'09:00:00',NULL,'SAB,DOM','FESTIVO',20,1,NULL);
/*!40000 ALTER TABLE `Tratta_Orari` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Utente`
--

DROP TABLE IF EXISTS `Utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Utente` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `cognome` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL COMMENT 'Password hashata con BCrypt',
  `data_registrazione` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ruolo` varchar(20) NOT NULL DEFAULT 'utente' COMMENT 'Es: utente, admin, azienda',
  `avatar` longblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Utente`
--

LOCK TABLES `Utente` WRITE;
/*!40000 ALTER TABLE `Utente` DISABLE KEYS */;
INSERT INTO `Utente` VALUES (1,'Mario','Rossi','mario.rossi@email.com','$2a$10$example_hash_1','2025-07-04 21:56:15','utente',NULL),(2,'Giulia','Bianchi','giulia.bianchi@email.com','$2a$10$example_hash_2','2025-07-04 21:56:15','utente',NULL),(3,'Admin','Sistema','admin@omniride.com','$2a$10$example_hash_admin','2025-07-04 21:56:15','admin',NULL),(4,'Trasporti','SpA','info@trasporti.com','$2a$10$example_hash_azienda','2025-07-04 21:56:15','azienda',NULL),(5,'Arjel','Buzi','arjel.buzi@gmail.com','$2a$10$NqDeBiSV9xStwDa3lbNpd.mFEyfe5ttboY.DEvTr3d8ZUN4mzot7y','2025-07-05 00:23:14','utente',NULL),(6,'Arjel','Buzi','turenco@priv.com','$2a$10$Jv5AXke2ozsa1SRv5jGmkOsTQsfqK2fTaMqENyAh1ltWnV8y/C3Im','2025-07-05 00:27:48','azienda',NULL);
/*!40000 ALTER TABLE `Utente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biglietto`
--

DROP TABLE IF EXISTS `biglietto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biglietto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_utente` bigint NOT NULL,
  `id_tratta` bigint NOT NULL,
  `id_orario` bigint DEFAULT NULL COMMENT 'Riferimento all orario specifico',
  `data_acquisto` datetime NOT NULL,
  `data_convalida` datetime DEFAULT NULL,
  `data_scadenza` datetime DEFAULT NULL,
  `stato` enum('ACQUISTATO','CONVALIDATO','SCADUTO','ANNULLATO') NOT NULL,
  `prezzo_pagato` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_biglietto_utente` (`id_utente`),
  KEY `idx_biglietto_tratta` (`id_tratta`),
  KEY `idx_biglietto_orario` (`id_orario`),
  KEY `idx_biglietto_stato` (`stato`),
  CONSTRAINT `biglietto_ibfk_1` FOREIGN KEY (`id_utente`) REFERENCES `Utente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `biglietto_ibfk_2` FOREIGN KEY (`id_tratta`) REFERENCES `Tratta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `biglietto_ibfk_3` FOREIGN KEY (`id_orario`) REFERENCES `Tratta_Orari` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biglietto`
--

LOCK TABLES `biglietto` WRITE;
/*!40000 ALTER TABLE `biglietto` DISABLE KEYS */;
/*!40000 ALTER TABLE `biglietto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessioni`
--

DROP TABLE IF EXISTS `sessioni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessioni` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(255) NOT NULL COMMENT 'ID univoco della sessione',
  `utente_id` bigint NOT NULL,
  `creation_time` bigint NOT NULL COMMENT 'Timestamp creazione (epoch seconds)',
  `last_access_time` bigint NOT NULL COMMENT 'Timestamp ultimo accesso (epoch seconds)',
  `is_valid` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `session_id` (`session_id`),
  KEY `idx_sessioni_session_id` (`session_id`),
  KEY `idx_sessioni_utente` (`utente_id`),
  KEY `idx_sessioni_valid` (`session_id`,`is_valid`),
  KEY `idx_sessioni_access_time` (`last_access_time`,`is_valid`),
  CONSTRAINT `fk_sessioni_utente` FOREIGN KEY (`utente_id`) REFERENCES `Utente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessioni`
--

LOCK TABLES `sessioni` WRITE;
/*!40000 ALTER TABLE `sessioni` DISABLE KEYS */;
INSERT INTO `sessioni` VALUES (1,'ozqA695ok6BGtxeTCnr_w7v4WueU4QJ-jhOwZzZaGmw',5,1751667801,1751667801,1,'2025-07-04 22:23:21','2025-07-04 22:23:21'),(2,'9KUGEMXR5oJd_vaIq46qw-CX81T1IegXV4wFnT62oNA',6,1751668073,1751668196,1,'2025-07-04 22:27:53','2025-07-04 22:29:56'),(3,'6JWBS8fTYs0mhYB3hZnCakhFNW85R1ozOQ0vYCX7Vpk',5,1751668332,1751668361,0,'2025-07-04 22:32:12','2025-07-04 22:32:43'),(4,'ldrcPKUNDyt81k5pj36zdkg8pJFssMBibPzvI2JPX5o',6,1751668374,1751668887,1,'2025-07-04 22:32:54','2025-07-04 22:41:27'),(5,'S-bzCRYf_KAsg7hchwK8KXmWsD7x0ey8TpQOfXoBeJA',6,1751668930,1751670036,0,'2025-07-04 22:42:10','2025-07-04 23:00:43'),(6,'EsML30QzBmC-KrnlKzFgcAhzCLmRNiLxzUizJoJ4yp0',5,1751670048,1751670231,0,'2025-07-04 23:00:48','2025-07-04 23:04:05'),(7,'AmmiLpKbO-B00Oxx2oSSf5efj9mrLt-drXOreyOf-LM',5,1751713823,1751713838,1,'2025-07-05 11:10:23','2025-07-05 11:10:38'),(8,'_2pTHgprok125HGJvpx5WwNdE3zCdsjnYkpWKbvrPfE',5,1751713904,1751713912,0,'2025-07-05 11:11:44','2025-07-05 11:13:40'),(9,'hH_ajG-5X98Ti6ZXkW7BXvdU8logWUU6qBuTN_b8QUg',5,1751714411,1751714446,1,'2025-07-05 11:20:11','2025-07-05 11:20:46'),(10,'BRNTwKVSrJAN-tLcvojkWDnEJRDXBxojg0W_fDnPIHQ',5,1751714499,1751714552,0,'2025-07-05 11:21:39','2025-07-05 11:22:45'),(11,'cSWbYNZqwJtTC2BLYdgDKYj5Nj2WPiYJb5H_BU2_CIo',5,1751715508,1751715508,1,'2025-07-05 11:38:28','2025-07-05 11:38:28'),(12,'3bzSyHtzncHf7WQCXQ5IrlF4-t8dJm8U-wC-eyB6mfc',5,1751752314,1751752314,1,'2025-07-05 21:51:54','2025-07-05 21:51:54'),(13,'JW_-lZInATIlsRAhNvFucitmWVXlk1f0sXlhJCIxHaU',5,1751755988,1751755988,1,'2025-07-05 22:53:08','2025-07-05 22:53:08'),(14,'2fGwCDx_ZrYbpOTyh1S7ULQe-SGS7MrDy5U8hKG_nHQ',6,1751845305,1751845364,0,'2025-07-06 23:41:45','2025-07-06 23:43:09'),(15,'_eA-7XS8_Y7c6pgNoYCDM8-A3wAuRUIfGlXfEwlBtCg',5,1751845392,1751845554,0,'2025-07-06 23:43:12','2025-07-06 23:45:58');
/*!40000 ALTER TABLE `sessioni` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-07 10:29:17
