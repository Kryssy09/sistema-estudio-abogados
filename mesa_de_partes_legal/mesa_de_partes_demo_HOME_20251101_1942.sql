/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.14-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mesa_de_partes_demo
-- ------------------------------------------------------
-- Server version	10.11.14-MariaDB-0+deb12u2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `area`
--

DROP TABLE IF EXISTS `area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `area` (
  `estadoRegistro` varchar(4) NOT NULL,
  `idArea` int(11) NOT NULL AUTO_INCREMENT,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `nombreArea` varchar(100) NOT NULL,
  PRIMARY KEY (`idArea`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `area`
--

LOCK TABLES `area` WRITE;
/*!40000 ALTER TABLE `area` DISABLE KEYS */;
INSERT INTO `area` VALUES
('ACT',2,1,NULL,'2025-10-31 05:31:13.000000',NULL,'DIRECCIÓN LEGAL');
/*!40000 ALTER TABLE `area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documento_normativo`
--

DROP TABLE IF EXISTS `documento_normativo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `documento_normativo` (
  `idDocumentoNormativo` int(11) NOT NULL AUTO_INCREMENT,
  `nombreDocumento` varchar(250) NOT NULL,
  `rutaArchivo` varchar(1000) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idDocumentoNormativo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documento_normativo`
--

LOCK TABLES `documento_normativo` WRITE;
/*!40000 ALTER TABLE `documento_normativo` DISABLE KEYS */;
/*!40000 ALTER TABLE `documento_normativo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dominio`
--

DROP TABLE IF EXISTS `dominio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dominio` (
  `idDominio` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idDominio`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dominio`
--

LOCK TABLES `dominio` WRITE;
/*!40000 ALTER TABLE `dominio` DISABLE KEYS */;
INSERT INTO `dominio` VALUES
(1,'TIPO DE DOCUMENTO DE IDENTIDAD',1,'2025-10-06 16:39:18',NULL,NULL,'ACT'),
(2,'SEXO DE PERSONAS',1,'2025-10-06 16:44:16',NULL,NULL,'ACT'),
(3,'PROCESO PARA ENVIO DE NOTIFICACION',1,'2025-10-14 21:27:06',NULL,NULL,'ACT');
/*!40000 ALTER TABLE `dominio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dominio_detalle`
--

DROP TABLE IF EXISTS `dominio_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dominio_detalle` (
  `idDominioDetalle` int(11) NOT NULL AUTO_INCREMENT,
  `idDominio` int(11) NOT NULL,
  `codigo` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `sigla` varchar(20) NOT NULL,
  `valorCadena` varchar(10000) DEFAULT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`idDominioDetalle`),
  KEY `idDominio` (`idDominio`),
  CONSTRAINT `dominio_detalle_ibfk_1` FOREIGN KEY (`idDominio`) REFERENCES `dominio` (`idDominio`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dominio_detalle`
--

LOCK TABLES `dominio_detalle` WRITE;
/*!40000 ALTER TABLE `dominio_detalle` DISABLE KEYS */;
INSERT INTO `dominio_detalle` VALUES
(1,1,1,'DOCUMENTO NACIONAL DE IDENTIDAD','DNI',NULL,1,'2025-10-06 16:41:15',NULL,NULL,'ACT'),
(2,2,1,'MASCULINO','M',NULL,1,'2025-10-06 16:44:26',NULL,NULL,'ACT'),
(3,2,2,'FEMENINO','F',NULL,1,'2025-10-06 16:44:29',NULL,NULL,'ACT'),
(4,3,1,'NOTIFICACION POR CREACION DE EXPEDIENTE','CREA_EXP','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Creación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica de la creación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Creador(a) del expediente</b></td>\n            <td>: $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES <b>\"COORDINADOR(A)\"</b></td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Creación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_CREACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-14 21:27:56',NULL,NULL,'ACT'),
(5,3,2,'ASUNTO - NOTIFICACION POR CREACION DE EXPEDIENTE','ASUNTO_CREA_EXP','Creación de Expediente Nro. $EXPEDIENTE_CODIGO_SEGUIMIENTO - Solicitante: $CLIENTE_APELLIDOS_Y_NOMBRES',1,'2025-10-27 23:51:49',NULL,NULL,'ACT'),
(6,3,3,'NOTIFICACION POR ASIGNACION DE EXPEDIENTE - DIRECTOR','ASIG_EXP_DIR','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 04:51:15',NULL,NULL,'ACT'),
(7,3,4,'ASUNTO - NOTIFICACION POR ASIGNACION DE EXPEDIENTE','ASUNTO_ASIG_EXP','Asignación de Expediente Nro. $EXPEDIENTE_CODIGO_SEGUIMIENTO - Cliente: $CLIENTE_APELLIDOS_Y_NOMBRES',1,'2025-10-31 05:07:55',NULL,NULL,'ACT'),
(8,3,5,'NOTIFICACION POR ASIGNACION DE EXPEDIENTE - SOLICITANTE','ASIG_EXP_SOL','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) solicitante, $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 17:56:54',NULL,NULL,'ACT'),
(9,3,6,'NOTIFICACION POR ASIGNACION DE EXPEDIENTE - ESPECIALISTA','ASIG_EXP_ESPE','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) especialista, $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:48:41',NULL,NULL,'ACT');
/*!40000 ALTER TABLE `dominio_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expediente`
--

DROP TABLE IF EXISTS `expediente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `expediente` (
  `especialidad` int(11) NOT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  `idExpediente` int(11) NOT NULL AUTO_INCREMENT,
  `idExpedienteOrigen` int(11) DEFAULT NULL,
  `idInvitado` int(11) DEFAULT NULL,
  `idSolicitante` int(11) NOT NULL,
  `idUsuarioAsignado` int(11) DEFAULT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `mutuoAcuerdo` bit(1) DEFAULT NULL,
  `slaParaAsignacion` int(11) DEFAULT NULL,
  `slaParaNotificacionInicial` int(11) DEFAULT NULL,
  `tipoExpediente` varchar(3) NOT NULL,
  `estadoExpediente` varchar(8) NOT NULL,
  `fechaAsignacion` datetime(6) DEFAULT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `fechaNotificacionInicial` datetime(6) DEFAULT NULL,
  `estadoLegalCaso` varchar(10) DEFAULT NULL,
  `codigoSeguimiento` varchar(50) NOT NULL,
  `rutaArchivoFormatoSolicitud` varchar(1000) NOT NULL,
  `reseniaSolicitud` varchar(4000) NOT NULL,
  PRIMARY KEY (`idExpediente`),
  KEY `FKpjxaqj62q27hxe53bjoojrlda` (`idInvitado`),
  KEY `FKekf7fkulrrtlm3ylr9tnrg969` (`idSolicitante`),
  KEY `FK3y0ulbojfa2jq16x98wci3chj` (`idUsuarioAsignado`),
  CONSTRAINT `FK3y0ulbojfa2jq16x98wci3chj` FOREIGN KEY (`idUsuarioAsignado`) REFERENCES `usuario` (`idUsuario`),
  CONSTRAINT `FKekf7fkulrrtlm3ylr9tnrg969` FOREIGN KEY (`idSolicitante`) REFERENCES `solicitante` (`idSolicitante`),
  CONSTRAINT `FKpjxaqj62q27hxe53bjoojrlda` FOREIGN KEY (`idInvitado`) REFERENCES `invitado` (`idInvitado`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expediente`
--

LOCK TABLES `expediente` WRITE;
/*!40000 ALTER TABLE `expediente` DISABLE KEYS */;
INSERT INTO `expediente` VALUES
(1,'INAC',1,NULL,NULL,2,NULL,1,NULL,'',NULL,NULL,'CON','EN_ATE',NULL,'2025-10-26 02:35:50.225000',NULL,NULL,NULL,'N-E-X-76662','expedientes/2025/10/26/a7f68b20-1b67-4dd9-94a1-692e95b72d45/a7f68b20-1b67-4dd9-94a1-692e95b72d45_solicitud_AvanceProy2_Grupo1.pdf','asdasd as da sd asd asd as d'),
(3,'INAC',2,NULL,NULL,1,2,1,NULL,'',NULL,NULL,'CON','EN_ATE','2025-10-26 15:48:42.632000','2025-10-26 02:36:16.737000',NULL,NULL,NULL,'S-Y-Q-40222','expedientes/2025/10/26/6627fc04-93b3-4271-ab7f-25ee5c3f0854/6627fc04-93b3-4271-ab7f-25ee5c3f0854_solicitud_CV_DOC_2025.pdf','asdasdas asd asd a s asd as d '),
(1,'INAC',3,NULL,NULL,3,1,1,NULL,'\0',NULL,NULL,'CON','EN_ATE','2025-10-26 15:13:44.260000','2025-10-26 02:55:02.323000',NULL,NULL,NULL,'S-N-I-32528','expedientes/2025/10/26/6d8a6835-5616-4e54-9d0e-c0d84003aa8f/6d8a6835-5616-4e54-9d0e-c0d84003aa8f_solicitud_S08_s1 - Material -  Angular.pdf','123 3 3w esta es una reseña de pruebas de nuevos '),
(1,'INAC',4,NULL,NULL,3,2,1,NULL,'',NULL,NULL,'CON','EN_ATE','2025-10-27 02:29:05.386000','2025-10-26 11:30:04.755000',NULL,NULL,NULL,'W-D-J-67169','expedientes/2025/10/26/c379d0ac-e87e-4a38-a4c7-fd49a20dd3fc/c379d0ac-e87e-4a38-a4c7-fd49a20dd3fc_solicitud_2. MV - FEBRERO (1).pdf','asdas asd a da '),
(2,'INAC',5,NULL,NULL,2,NULL,1,NULL,'',NULL,NULL,'CON','EN_ATE',NULL,'2025-10-26 16:59:53.697000',NULL,NULL,NULL,'D-W-D-97086','expedientes/2025/10/26/2c06704d-f2f4-424e-b7bc-45e9f61c9cf0/2c06704d-f2f4-424e-b7bc-45e9f61c9cf0_solicitud_AvanceProy2_Grupo1.pdf','esta es un expediente de prueba'),
(4,'INAC',6,NULL,NULL,3,NULL,1,NULL,'\0',NULL,NULL,'PL','EN_ATE',NULL,'2025-10-26 17:04:41.572000',NULL,NULL,NULL,'A-N-F-80540','expedientes/2025/10/26/2025-10-26_solicitud_CV_DOC_2025.pdf','esta es una solicitud que debemos ver asdas'),
(1,'ACT',7,NULL,NULL,1,2,1,NULL,'',NULL,NULL,'CON','EN_ATE','2025-10-26 17:10:18.890000','2025-10-26 17:07:14.305000',NULL,NULL,NULL,'U-K-U-70866','expedientes/2025/10/26/20387_solicitud_2. MV - FEBRERO - SIGN.pdf','aasd asd asd a sdasd asd asd '),
(1,'INAC',8,NULL,NULL,3,3,1,NULL,'\0',NULL,NULL,'CON','ASIG','2025-10-31 18:59:47.000000','2025-10-27 15:31:59.243000',NULL,NULL,NULL,'R-W-H-85390','expedientes/2025/10/27/20388_solicitud_FORMATO A-Solicitud para conciliar_DEMO.pdf','DE MUTUO ACUERDO CON EL PADRE DE MI MENOR HIJO, EL SR. FERNANDO ROJAS FLORES, SOLICITAMOS QUE NOS BRINDE EL SERVICIO DE CONCILIACIÓN PARA DETERMINAR LOS APORTES QUE CADA UNO DEBE REALIZAR PARA SOLVENTAR LOS GASTOS DEL PEQUEÑO JOSE MANUEL'),
(1,'INAC',9,NULL,NULL,1,2,1,NULL,'\0',NULL,NULL,'CON','ASIG','2025-10-31 20:16:52.000000','2025-10-31 15:03:31.000000',NULL,NULL,NULL,'I-J-Z-12330','expedientes/2025/10/31/20392_solicitud_FORMATO A-Solicitud para conciliar_DEMO.pdf','abc def'),
(1,'INAC',10,NULL,NULL,1,2,1,NULL,'\0',NULL,NULL,'CON','ASIG','2025-10-31 20:38:46.000000','2025-10-31 20:38:33.000000',NULL,NULL,NULL,'F-R-Q-16590','expedientes/2025/10/31/20392_solicitud_FORMATO A-Solicitud para conciliar_DEMO.pdf','DE MUTUO ACUERDO CON EL PADRE DE MI MENOR HIJO, EL SR. FERNANDO ROJAS FLORES,\r\nSOLICITAMOS QUE NOS BRINDE EL SERVICIO DE CONCILIACIÓN PARA DETERMINAR LOS\r\nAPORTES QUE CADA UNO DEBE REALIZAR PARA SOLVENTAR LOS GASTOS DEL PEQUEÑO JOSE\r\nMANUEL'),
(1,'ACT',11,NULL,NULL,3,2,1,NULL,'\0',NULL,NULL,'CON','ASIG','2025-10-31 21:27:45.000000','2025-10-31 21:26:10.000000',NULL,NULL,NULL,'T-N-S-19874','expedientes/2025/10/31/20392_solicitud_FORMATO A-Solicitud para conciliar_DEMO.pdf','DE MUTUO ACUERDO CON EL PADRE DE MI MENOR HIJO, EL SR. FERNANDO ROJAS FLORES,\r\nSOLICITAMOS QUE NOS BRINDE EL SERVICIO DE CONCILIACIÓN PARA DETERMINAR LOS\r\nAPORTES QUE CADA UNO DEBE REALIZAR PARA SOLVENTAR LOS GASTOS DEL PEQUEÑO JOSE\r\nMANUEL.');
/*!40000 ALTER TABLE `expediente` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `tr_ins_expediente` AFTER INSERT ON `expediente` FOR EACH ROW begin
	insert into Notificacion (idExpediente, procesoOrigen, estadoNotificacion, remitente, esPorCorreo
		, asunto
		, mensaje, idUsuarioCreador, fechaCreacion, estadoRegistro)
	select new.idExpediente, dd.codigo, 'REG', 'swmiguel2@yahoo.es', 1
		, concat('Creación de Expediente Nro. ', new.codigoSeguimiento, ' - Cliente: '
			, (	select concat(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres)
				from Solicitante s
				inner join Persona p
					on p.idPersona = s.idPersona
				where s.idSolicitante = new.idSolicitante)
			)
		, dd.valorCadena, new.idUsuarioCreador, now(), 'ACT'
	from Dominio_Detalle dd
	where dd.idDominio = 3
		and dd.codigo = 1
		and dd.estadoRegistro = 'ACT';
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 trigger tr_before_upd_expediente_por_asignacion
before update on expediente for each row
begin
	if old.estadoExpediente = 'SIN_ASIG' and ifnull(new.idUsuarioAsignado, 0) > 0 then
		set new.estadoExpediente = 'ASIG';

		insert into Notificacion (idExpediente, procesoOrigen, estadoNotificacion, remitente, esPorCorreo
			, asunto
			, mensaje, idUsuarioCreador, fechaCreacion, estadoRegistro)
		select new.idExpediente, dd.codigo, 'REG', 'swmiguel4@gmail.com', 1
			, concat('Asignación de Expediente Nro. ', new.codigoSeguimiento, ' - Cliente: '
				, (	select concat(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres)
					from Solicitante s
					inner join Persona p
						on p.idPersona = s.idPersona
					where s.idSolicitante = new.idSolicitante)
				)
			, dd.valorCadena, new.idUsuarioCreador, now(), 'ACT'
		from Dominio_Detalle dd
		where dd.idDominio = 3
			and dd.codigo = 3 -- ASIG_EXP_DIR
			and dd.estadoRegistro = 'ACT'
		union
		select new.idExpediente, dd.codigo, 'REG', 'swmiguel4@gmail.com', 1
			, concat('Asignación de Expediente Nro. ', new.codigoSeguimiento, ' - Cliente: '
				, (	select concat(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres)
					from Solicitante s
					inner join Persona p
						on p.idPersona = s.idPersona
					where s.idSolicitante = new.idSolicitante)
				)
			, dd.valorCadena, new.idUsuarioCreador, now(), 'ACT'
		from Dominio_Detalle dd
		where dd.idDominio = 3
			and dd.codigo = 5 -- ASIG_EXP_SOL
			and dd.estadoRegistro = 'ACT'
		union
		select new.idExpediente, dd.codigo, 'REG', 'swmiguel4@gmail.com', 1
			, concat('Asignación de Expediente Nro. ', new.codigoSeguimiento, ' - Cliente: '
				, (	select concat(p.apellidoPaterno, ' ', p.apellidoMaterno, ' ', p.nombres)
					from Solicitante s
					inner join Persona p
						on p.idPersona = s.idPersona
					where s.idSolicitante = new.idSolicitante)
				)
			, dd.valorCadena, new.idUsuarioCreador, now(), 'ACT'
		from Dominio_Detalle dd
		where dd.idDominio = 3
			and dd.codigo = 6 -- ASIG_EXP_ESPE
			and dd.estadoRegistro = 'ACT';
	end if;	
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `expediente_enlace_reporte`
--

DROP TABLE IF EXISTS `expediente_enlace_reporte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `expediente_enlace_reporte` (
  `idEnlace` int(11) NOT NULL AUTO_INCREMENT,
  `idExpediente` int(11) NOT NULL,
  `tokenAcceso` varchar(1000) NOT NULL,
  `estadoToken` varchar(4) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idEnlace`),
  KEY `idExpediente` (`idExpediente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expediente_enlace_reporte`
--

LOCK TABLES `expediente_enlace_reporte` WRITE;
/*!40000 ALTER TABLE `expediente_enlace_reporte` DISABLE KEYS */;
/*!40000 ALTER TABLE `expediente_enlace_reporte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expediente_sesion`
--

DROP TABLE IF EXISTS `expediente_sesion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `expediente_sesion` (
  `idExpedienteSesion` int(11) NOT NULL AUTO_INCREMENT,
  `detallesSesion` varchar(4000) NOT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  `estadoSesion` varchar(6) NOT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `fechaSesion` datetime(6) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `resolucionSesion` int(11) DEFAULT NULL,
  `rutaArchivoActa` varchar(1000) DEFAULT NULL,
  `secuencia` int(11) NOT NULL,
  `idExpediente` int(11) NOT NULL,
  PRIMARY KEY (`idExpedienteSesion`),
  KEY `FKlmpadomqmq8tol6uhx0m0qbn3` (`idExpediente`),
  CONSTRAINT `FKlmpadomqmq8tol6uhx0m0qbn3` FOREIGN KEY (`idExpediente`) REFERENCES `expediente` (`idExpediente`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expediente_sesion`
--

LOCK TABLES `expediente_sesion` WRITE;
/*!40000 ALTER TABLE `expediente_sesion` DISABLE KEYS */;
INSERT INTO `expediente_sesion` VALUES
(2,'ASASFASFA AS FAS F','ACT','EN_CUR','2025-10-26 20:35:58.383471',NULL,'2025-10-26 20:35:00.000000',1,NULL,1,NULL,1,7),
(3,'asdasasfasf asf asfa sf asf asf asf asf ','ACT','EN_CUR','2025-10-26 21:21:13.749565',NULL,'2025-10-26 21:19:00.000000',1,NULL,2,NULL,2,7),
(4,'asdasas asf asfas f','ACT','EN_CUR','2025-10-26 21:23:59.460982',NULL,'2025-10-26 21:21:00.000000',1,NULL,3,NULL,3,7),
(5,'asdasfasfa asfasf','ACT','ANUL','2025-10-26 21:25:35.564778',NULL,'2025-10-26 21:23:00.000000',1,NULL,3,NULL,4,7),
(6,'cambio de prueba 2','ACT','ANUL','2025-10-26 21:25:54.861950','2025-10-27 07:39:55.254741','2025-10-26 21:25:00.000000',1,1,3,NULL,5,7),
(7,'cambiamos esta vaina  asdaasasd','ACT','PROG','2025-10-26 21:26:21.866267','2025-10-27 09:04:18.352862','2025-10-26 21:25:00.000000',1,1,2,NULL,6,7),
(8,'abcdefg. adjunta pago','ACT','EN_CUR','2025-10-31 15:13:03.000000',NULL,'2025-10-31 15:11:00.000000',1,NULL,2,NULL,1,7),
(9,'sesion inicial','ACT','EN_CUR','2025-10-31 21:30:14.000000',NULL,'2025-10-31 21:27:00.000000',1,NULL,3,NULL,1,11);
/*!40000 ALTER TABLE `expediente_sesion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expediente_sesion_archivo`
--

DROP TABLE IF EXISTS `expediente_sesion_archivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `expediente_sesion_archivo` (
  `idExpedienteSesionArchivo` int(11) NOT NULL AUTO_INCREMENT,
  `estadoRegistro` varchar(4) NOT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `nombreDocumento` varchar(250) NOT NULL,
  `rutaArchivo` varchar(1000) NOT NULL,
  `idExpedienteSesion` int(11) NOT NULL,
  PRIMARY KEY (`idExpedienteSesionArchivo`),
  KEY `FKd8u4aksgjkn6e7oouqxu6jnqk` (`idExpedienteSesion`),
  CONSTRAINT `FKd8u4aksgjkn6e7oouqxu6jnqk` FOREIGN KEY (`idExpedienteSesion`) REFERENCES `expediente_sesion` (`idExpedienteSesion`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expediente_sesion_archivo`
--

LOCK TABLES `expediente_sesion_archivo` WRITE;
/*!40000 ALTER TABLE `expediente_sesion_archivo` DISABLE KEYS */;
INSERT INTO `expediente_sesion_archivo` VALUES
(1,'ACT','2025-10-26 20:35:58.486471',NULL,1,NULL,'UNA FOTO','sesiones/2025/10/26/20387_sesion_2_pdf',2),
(2,'ACT','2025-10-26 20:35:58.516477',NULL,1,NULL,'OTRO DOCUMENTO','sesiones/2025/10/26/20387_sesion_2_pdf',2),
(3,'ACT','2025-10-26 21:23:59.499967',NULL,1,NULL,'Documento de boletas','sesiones/2025/10/26/20387_sesion_4_CURSO 1.docx',4),
(5,'ACT','2025-10-31 15:13:03.000000',NULL,1,NULL,'constancia de pago','sesiones/2025/10/31/20392_sesion_8_Ejemplo_Transferencia_Interbancaria.png',8),
(6,'ACT','2025-10-31 21:30:14.000000',NULL,1,NULL,'constancia de pago','sesiones/2025/10/31/20392_sesion_9_Ejemplo_Transferencia_Interbancaria.png',9);
/*!40000 ALTER TABLE `expediente_sesion_archivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invitado`
--

DROP TABLE IF EXISTS `invitado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `invitado` (
  `estadoRegistro` varchar(4) NOT NULL,
  `idInvitado` int(11) NOT NULL AUTO_INCREMENT,
  `idPersona` int(11) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idInvitado`),
  KEY `FKac8akuj9g63rit3sst1kp3io3` (`idPersona`),
  CONSTRAINT `FKac8akuj9g63rit3sst1kp3io3` FOREIGN KEY (`idPersona`) REFERENCES `persona` (`idPersona`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invitado`
--

LOCK TABLES `invitado` WRITE;
/*!40000 ALTER TABLE `invitado` DISABLE KEYS */;
INSERT INTO `invitado` VALUES
('ACT',1,6,1,NULL,'2025-10-26 15:01:28.000000',NULL);
/*!40000 ALTER TABLE `invitado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modulo`
--

DROP TABLE IF EXISTS `modulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `modulo` (
  `idModulo` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idModulo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modulo`
--

LOCK TABLES `modulo` WRITE;
/*!40000 ALTER TABLE `modulo` DISABLE KEYS */;
/*!40000 ALTER TABLE `modulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacion` (
  `idNotificacion` int(11) NOT NULL AUTO_INCREMENT,
  `idExpediente` int(11) NOT NULL,
  `procesoOrigen` int(11) NOT NULL,
  `estadoNotificacion` varchar(4) NOT NULL,
  `fechaEnvio` datetime DEFAULT NULL,
  `remitente` varchar(100) NOT NULL,
  `destinatarios` varchar(1000) DEFAULT NULL,
  `esPorCorreo` bit(1) NOT NULL,
  `conCopia` varchar(1000) DEFAULT NULL,
  `conCopiaOculta` varchar(1000) DEFAULT NULL,
  `asunto` varchar(1000) NOT NULL,
  `mensaje` varchar(4000) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idNotificacion`),
  KEY `idExpediente` (`idExpediente`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacion`
--

LOCK TABLES `notificacion` WRITE;
/*!40000 ALTER TABLE `notificacion` DISABLE KEYS */;
INSERT INTO `notificacion` VALUES
(2,8,1,'ENV','2025-10-31 04:35:21','swmiguel2@yahoo.es','swmiguel2@yahoo.es','',NULL,NULL,'Creación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Creación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica de la creación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Cliente</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Creador del expediente</b></td>\n            <td>: $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES <b>\"COORDINADOR(A)\"</b></td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Creación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_CREACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 03:55:01',1,'2025-10-31 04:35:21','ACT'),
(4,8,3,'ENV','2025-10-31 06:50:46','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 06:20:23',1,'2025-10-31 06:50:46','ACT'),
(5,8,3,'ENV','2025-10-31 14:49:54','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 14:49:09',8,'2025-10-31 14:49:54','ACT'),
(6,9,1,'ENV','2025-10-31 15:04:42','swmiguel2@yahoo.es','swmiguel@hotmail.com','',NULL,NULL,'Creación de Expediente Nro. I-J-Z-12330 - Cliente: QUISPE LOPEZ JOSE LUIS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Creación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica de la creación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Creador(a) del expediente</b></td>\n            <td>: $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES <b>\"COORDINADOR(A)\"</b></td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Creación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_CREACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 15:03:31',1,'2025-10-31 15:04:42','ACT'),
(7,8,3,'ENV','2025-10-31 18:18:36','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:16:39',8,'2025-10-31 18:18:36','ACT'),
(8,8,5,'ENV','2025-10-31 18:18:41','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:16:39',8,'2025-10-31 18:18:41','ACT'),
(10,8,3,'ENV','2025-10-31 18:37:45','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:37:06',8,'2025-10-31 18:37:45','ACT'),
(11,8,5,'ENV','2025-10-31 18:37:49','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:37:06',8,'2025-10-31 18:37:49','ACT'),
(13,8,3,'ENV','2025-10-31 18:41:14','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:41:02',8,'2025-10-31 18:41:14','ACT'),
(14,8,5,'ENV','2025-10-31 18:41:19','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:41:02',8,'2025-10-31 18:41:19','ACT'),
(16,8,3,'ENV','2025-10-31 18:57:28','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:56:56',8,'2025-10-31 18:57:28','ACT'),
(17,8,5,'ENV','2025-10-31 18:57:33','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:56:56',8,'2025-10-31 18:57:33','ACT'),
(18,8,6,'ENV','2025-10-31 18:57:38','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:56:56',8,'2025-10-31 18:57:38','ACT'),
(19,8,3,'ENV','2025-10-31 19:00:05','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:59:47',8,'2025-10-31 19:00:05','ACT'),
(20,8,5,'ENV','2025-10-31 19:00:10','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) solicitante, $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:59:47',8,'2025-10-31 19:00:10','ACT'),
(21,8,6,'ENV','2025-10-31 19:00:15','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. R-W-H-85390 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) especialista, $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 18:59:47',8,'2025-10-31 19:00:15','ACT'),
(31,9,3,'ENV','2025-10-31 20:17:22','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. I-J-Z-12330 - Cliente: QUISPE LOPEZ JOSE LUIS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 20:16:52',8,'2025-10-31 20:17:22','ACT'),
(32,9,5,'ENV','2025-10-31 20:17:25','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. I-J-Z-12330 - Cliente: QUISPE LOPEZ JOSE LUIS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) solicitante, $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 20:16:52',8,'2025-10-31 20:17:25','ACT'),
(33,9,6,'ENV','2025-10-31 20:17:28','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. I-J-Z-12330 - Cliente: QUISPE LOPEZ JOSE LUIS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) especialista, $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 20:16:52',8,'2025-10-31 20:17:28','ACT'),
(38,11,1,'ENV','2025-10-31 21:26:38','swmiguel2@yahoo.es','swmiguel@hotmail.com','',NULL,NULL,'Creación de Expediente Nro. T-N-S-19874 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Creación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica de la creación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Creador(a) del expediente</b></td>\n            <td>: $EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES <b>\"COORDINADOR(A)\"</b></td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Creación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_CREACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 21:26:10',1,'2025-10-31 21:26:38','ACT'),
(39,11,3,'ENV','2025-10-31 21:27:58','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. T-N-S-19874 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) $AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, director(a) de la <b>\"$AREA_NOMBRE\"</b>.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Área asignada</b></td>\n            <td>: $AREA_NOMBRE</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 21:27:45',8,'2025-10-31 21:27:58','ACT'),
(40,11,5,'ENV','2025-10-31 21:28:01','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. T-N-S-19874 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) solicitante, $CLIENTE_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 21:27:45',8,'2025-10-31 21:28:01','ACT'),
(41,11,6,'ENV','2025-10-31 21:28:04','swmiguel4@gmail.com','swmiguel2@yahoo.es','',NULL,NULL,'Asignación de Expediente Nro. T-N-S-19874 - Cliente: MARTINEZ BELTRAN CARLOS','<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n    <title>Asignación de Expediente</title>\n    <style>\n      body {\n        font-family: Arial, Helvetica, sans-serif;\n      }\n    </style>\n  </head>\n  <body>\n    <div>\n      <div>\n        <div><br></div>\n        <div>Estimado(a) especialista, $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES.</div>\n        <div><br></div>\n        <div>Mediante el presente correo, se le notifica la asignación del siguiente expediente:</div>\n        <div><br></div>\n        <table>\n          <tr>\n            <td><b>Número de Expediente</b></td>\n            <td>: $EXPEDIENTE_CODIGO_SEGUIMIENTO</td>\n          </tr>\n          <tr>\n            <td><b>Solicitante</b></td>\n            <td>: $CLIENTE_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Asignado a</b></td>\n            <td>: $EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES</td>\n          </tr>\n          <tr>\n            <td><b>Fecha y Hora de Asignación</b></td>\n            <td>: $EXPEDIENTE_FECHA_Y_HORA_ASIGNACION</td>\n          </tr>\n        </table>\n        <div><br></div>\n        <div style=\"font-size:12.8px\">Atte.</div>\n        <div style=\"font-size:12.8px\">Servicio de Notificaciones</div>\n        <div style=\"font-size:12.8px\">Estudio Vidal S.A.</div>\n        <div><br /></div>\n        <div style=\"font-size:12.8px\">\n          <img src=\"http://72.60.58.123/logo_EstudioJuridico.jpeg\"\n            alt=\"\"\n            width=\"195\" height=\"68\"\n            style=\"margin-right:0px; border-width: 1px; border-color: gray; border-style: solid;\">\n        </div>\n      </div>\n    </div>\n  </body>\n</html>',1,'2025-10-31 21:27:45',8,'2025-10-31 21:28:04','ACT');
/*!40000 ALTER TABLE `notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persona`
--

DROP TABLE IF EXISTS `persona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `persona` (
  `estadoRegistro` varchar(4) NOT NULL,
  `idPersona` int(11) NOT NULL AUTO_INCREMENT,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `sexo` int(11) DEFAULT NULL,
  `tipoDocumentoIdentidad` int(11) NOT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `numeroDocumento` varchar(20) NOT NULL,
  `telefonoPersonal` varchar(20) DEFAULT NULL,
  `apellidoMaterno` varchar(100) NOT NULL,
  `apellidoPaterno` varchar(100) NOT NULL,
  `correoElectronico` varchar(100) DEFAULT NULL,
  `nombres` varchar(100) NOT NULL,
  `rutaFoto` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idPersona`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persona`
--

LOCK TABLES `persona` WRITE;
/*!40000 ALTER TABLE `persona` DISABLE KEYS */;
INSERT INTO `persona` VALUES
('ACT',1,1,NULL,1,1,'2025-10-25 18:02:36.000000',NULL,'42481765','993808061','LOPEZ','QUISPE','swmiguel2@yahoo.es','JOSE LUIS',NULL),
('ACT',2,1,NULL,1,1,'2025-10-25 18:02:45.000000',NULL,'42481265','993808061','PEREZ','TORRES','swmiguel2@yahoo.es','CAMILA',NULL),
('ACT',3,1,NULL,1,1,'2025-10-25 18:03:00.000000',NULL,'42481265','993808061','BELTRAN','MARTINEZ','swmiguel2@yahoo.es','CARLOS',NULL),
('ACT',4,1,NULL,1,1,'2025-10-25 18:06:20.769065',NULL,'1233415124','','Villanueva','Martinez','swmiguel@hotmail.com','Judith',NULL),
('ACT',5,1,NULL,1,1,'2025-10-25 18:12:18.526032',NULL,'40494549','932234567','Rivera','Bedregal','swmiguel2@yahoo.es','Angela',NULL),
('ACT',6,1,NULL,1,1,'2025-10-26 14:59:33.000000',NULL,'42481265','993808061','Avelino','Caceres','swmiguel2@yahoo.es','Martha',NULL),
('ACT',7,1,NULL,1,1,'2025-10-27 09:19:50.014805',NULL,'74936207','932251505','Bedregal','Villarroel','swmiguel2@yahoo.es','Manuel Benjamin','fotos/perfiles/20388_foto_9c8d43ad-7d43-4d51-9ff9-e8868181b949'),
('ACT',8,1,NULL,1,1,'2025-10-31 14:33:30.000000',NULL,'43481765','993808061','HERNANDEZ','MORENO','swmiguel3@gmail.com','MIGUEL ANGEL',NULL);
/*!40000 ALTER TABLE `persona` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `idRol` int(11) NOT NULL AUTO_INCREMENT,
  `nombreRol` varchar(50) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_modulo`
--

DROP TABLE IF EXISTS `rol_modulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_modulo` (
  `idRol` int(11) NOT NULL,
  `idModulo` int(11) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idRol`,`idModulo`),
  KEY `idModulo` (`idModulo`),
  CONSTRAINT `rol_modulo_ibfk_1` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`),
  CONSTRAINT `rol_modulo_ibfk_2` FOREIGN KEY (`idModulo`) REFERENCES `modulo` (`idModulo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_modulo`
--

LOCK TABLES `rol_modulo` WRITE;
/*!40000 ALTER TABLE `rol_modulo` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol_modulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solicitante`
--

DROP TABLE IF EXISTS `solicitante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `solicitante` (
  `estadoRegistro` varchar(4) NOT NULL,
  `idPersona` int(11) NOT NULL,
  `idSolicitante` int(11) NOT NULL AUTO_INCREMENT,
  `idUsuarioCreador` int(11) NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idSolicitante`),
  KEY `FKbfynegqbsncv8mm9lhrw9j446` (`idPersona`),
  CONSTRAINT `FKbfynegqbsncv8mm9lhrw9j446` FOREIGN KEY (`idPersona`) REFERENCES `persona` (`idPersona`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solicitante`
--

LOCK TABLES `solicitante` WRITE;
/*!40000 ALTER TABLE `solicitante` DISABLE KEYS */;
INSERT INTO `solicitante` VALUES
('ACT',1,1,1,NULL,'2025-10-25 18:10:09.000000',NULL),
('ACT',2,2,1,NULL,'2025-10-25 18:10:20.000000',NULL),
('ACT',3,3,1,NULL,'2025-10-25 18:10:32.000000',NULL);
/*!40000 ALTER TABLE `solicitante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `estadoRegistro` varchar(4) NOT NULL,
  `idArea` int(11) DEFAULT NULL,
  `idPersona` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `fechaCreacion` datetime(6) NOT NULL,
  `fechaModificacion` datetime(6) DEFAULT NULL,
  `nombreUsuario` varchar(20) NOT NULL,
  `clave` varchar(1000) NOT NULL,
  `idUsuarioCreador` int(11) DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `UKtwkb1cn3q5otpldb8pwwlilj` (`idPersona`),
  UNIQUE KEY `UKer88wqvyub668lglqw9uge6xd` (`nombreUsuario`),
  KEY `FKkfub2gatp3jk729shtuf3bs5c` (`idArea`),
  CONSTRAINT `FK5hgb9ywdo22pwcoysiw5khfm4` FOREIGN KEY (`idPersona`) REFERENCES `persona` (`idPersona`),
  CONSTRAINT `FKkfub2gatp3jk729shtuf3bs5c` FOREIGN KEY (`idArea`) REFERENCES `area` (`idArea`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES
('ACT',NULL,4,1,'2025-10-25 18:06:20.877332','2025-10-25 18:11:11.389682','judithmartinez123','123456',8),
('ACT',NULL,5,2,'2025-10-25 18:12:18.533029','2025-10-25 23:53:23.557334','angela123','123456',8),
('ACT',NULL,7,3,'2025-10-27 09:19:50.111809',NULL,'mvillarroel1797','123456',8),
('ACT',NULL,8,4,'2025-10-31 14:37:42.000000',NULL,'mmoreno','alphacentauri==#',8);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_rol` (
  `idUsuario` int(11) NOT NULL,
  `idRol` int(11) NOT NULL,
  `idUsuarioCreador` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL,
  `idUsuarioModificador` int(11) DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `estadoRegistro` varchar(4) NOT NULL,
  PRIMARY KEY (`idUsuario`,`idRol`),
  KEY `idRol` (`idRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_rol`
--

LOCK TABLES `usuario_rol` WRITE;
/*!40000 ALTER TABLE `usuario_rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario_rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'mesa_de_partes_demo'
--
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `usp_notificacion_listar_creados` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE  PROCEDURE `usp_notificacion_listar_creados`()
begin
	select n.idNotificacion
		, concat(ucper.apellidoPaterno, ' ', ifnull(ucper.apellidoMaterno, ''), ' ', ucper.nombres) as apellidosYNombresUsuarioCreador
		, concat(sper.apellidoPaterno, ' ', ifnull(sper.apellidoMaterno, ''), ' ', sper.nombres) as apellidosYNombresSolicitante
		, date_format(exp.fechaCreacion, '%d/%m/%Y %H:%i:%s') as cadenaFechaCreacion
		, exp.codigoSeguimiento
		, ucper.correoElectronico as correoElectronicoCreador
		, exp.idExpediente
		, uc.idUsuario as idUsuarioCreador
	from Notificacion n
	left join Dominio_Detalle proc
		on proc.idDominio = 3
		and proc.codigo = n.procesoOrigen
	left join Expediente exp
		on exp.idExpediente = n.idExpediente
	left join Usuario uc
		on uc.idUsuario = exp.idUsuarioCreador
	left join Persona ucper
		on ucper.idPersona = uc.idPersona
	left join Solicitante s
		on s.idSolicitante = exp.idSolicitante
	left join Persona sper
		on sper.idPersona = s.idPersona
	where n.estadoRegistro = 'ACT'
		and n.estadoNotificacion = 'REG'
		and n.procesoOrigen = 1 -- CREA_EXP
	order by n.fechaCreacion, n.idNotificacion;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `usp_notificacion_listar_recien_asignados` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE  PROCEDURE `usp_notificacion_listar_recien_asignados`()
begin
	select n.idNotificacion
		, exp.idExpediente
		, exp.codigoSeguimiento
		, concat(sper.apellidoPaterno, ' ', ifnull(sper.apellidoMaterno, ''), ' ', sper.nombres) as apellidosYNombresSolicitante
		, (select a.nombreArea from area a where a.idArea = 2) as nombreArea
		, 'DIAZ DIAZ ESTEBAN' as apellidosYNombresDirectorArea
		, concat(uaper.apellidoPaterno, ' ', ifnull(uaper.apellidoMaterno, ''), ' ', uaper.nombres) as apellidosYNombresEspecialistaAsignado
		, date_format(exp.fechaAsignacion, '%d/%m/%Y %H:%i:%s') as cadenaFechaAsignacion
		, sper.correoElectronico as correoElectronicoSolicitante
		, 'swmiguel2@yahoo.es' as correoElectronicoDirectorArea
		, n.procesoOrigen
		, uaper.correoElectronico as correoElectronicoEspecialistaAsignado
	from Notificacion n
	left join Dominio_Detalle proc
		on proc.idDominio = 3
		and proc.codigo = n.procesoOrigen
	left join Expediente exp
		on exp.idExpediente = n.idExpediente
	left join Solicitante s
		on s.idSolicitante = exp.idSolicitante
	left join Persona sper
		on sper.idPersona = s.idPersona
	left join Usuario ua
		on ua.idUsuario = exp.idUsuarioAsignado
	left join Persona uaper
		on uaper.idPersona = ua.idPersona
	where n.estadoRegistro = 'ACT'
		and n.estadoNotificacion = 'REG'
		and n.procesoOrigen IN (3,5,6) -- ASIG_EXP_DIR,ASIG_EXP_SOL,ASIG_EXP_ESPE
	order by n.fechaCreacion, n.idNotificacion;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `usp_notificacion_registrar_envio` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE  PROCEDURE `usp_notificacion_registrar_envio`(
	pIdNotificacion int
	, pDestinatarios varchar(1000)
	, pIdUsuarioModificador int
)
begin
	update Notificacion
	set estadoNotificacion = 'ENV'
		, fechaEnvio = now()
		, destinatarios = pDestinatarios
		, idUsuarioModificador = pIdUsuarioModificador
		, fechaModificacion = now()
	where idNotificacion = pIdNotificacion;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-01 19:42:27
