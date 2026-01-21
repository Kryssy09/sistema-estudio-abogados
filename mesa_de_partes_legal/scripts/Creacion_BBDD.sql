create database mesa_de_partes;

use mesa_de_partes;

CREATE TABLE Persona (
	idPersona  int  NOT NULL auto_increment,
	apellidoPaterno  varchar(100)  NOT NULL ,
	apellidoMaterno  varchar(100)  NULL ,
	nombres  varchar(100)  NOT NULL ,
	tipoDocumentoIdentidad  int  NOT NULL ,
	numeroDocumento  varchar(20)  NOT NULL ,
	correoElectronico  varchar(100)  NULL ,
	telefonoPersonal  varchar(20)  NULL ,
	sexo  int  NULL ,
	rutaFoto  varchar(1000)  null, 
	idUsuarioCreador int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT null,
	primary key (idPersona)
);

CREATE TABLE Usuario (
	idUsuario  int  NOT NULL auto_increment,
	idPersona  int  NOT NULL ,
	nombreUsuario  varchar(20)  NOT NULL ,
	clave  varchar(1000)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT null,
	primary key (idUsuario),
	FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

CREATE TABLE Invitado (
	idInvitado  int  NOT NULL auto_increment,
	idPersona  int  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idInvitado),
	FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

CREATE TABLE Solicitante (
	idSolicitante  int  NOT NULL auto_increment,
	idPersona  int  NOT NULL ,
	idUsuarioCreador  integer  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  integer  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(20)  NOT NULL  ,
	primary key (idSolicitante),
	FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

CREATE TABLE Area (
	idArea  int  NOT NULL auto_increment,
	idUsuarioDirector  int  NULL ,
	nombreArea  varchar(100)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idArea),
	FOREIGN KEY (idUsuarioDirector) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Documento_Normativo (
	idDocumentoNormativo  int  NOT NULL auto_increment,
	nombreDocumento  varchar(250)  NOT NULL ,
	rutaArchivo  varchar(1000)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idDocumentoNormativo)
);

CREATE TABLE Dominio (
	idDominio  int  NOT NULL auto_increment,
	nombre  varchar(100)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idDominio)
);

CREATE TABLE Dominio_Detalle (
	idDominioDetalle  int  NOT NULL auto_increment,
	idDominio  int  NOT NULL ,
	codigo  int  NOT NULL ,
	nombre  varchar(100)  NOT NULL ,
	sigla  varchar(20)  NOT NULL ,
	valorCadena  varchar(4000)  NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NULL ,
	primary key (idDominioDetalle),
	FOREIGN KEY (idDominio) REFERENCES Dominio(idDominio)
);

CREATE TABLE Modulo (
	idModulo  int  NOT NULL auto_increment,
	nombre  varchar(100)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idModulo)
);

CREATE TABLE Rol (
	idRol  int  NOT NULL auto_increment,
	nombreRol  varchar(50)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idRol)
);

CREATE TABLE Rol_Modulo (
	idRol  int  NOT NULL ,
	idModulo  int  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idRol, idModulo),
	FOREIGN KEY (idRol) REFERENCES Rol(idRol),
	FOREIGN KEY (idModulo) REFERENCES Modulo(idModulo)
);

CREATE TABLE Usuario_Rol (
	idUsuario  int  NOT NULL ,
	idRol  int  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idUsuario, idRol),
	FOREIGN KEY (idRol) REFERENCES Rol(idRol),
	FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Expediente (
	idExpediente  int  NOT NULL auto_increment,
	tipoExpediente  varchar(3)  NOT NULL ,
	mutuoAcuerdo  bit  NULL ,
	especialidad  int  NOT NULL ,
	codigoSeguimiento  varchar(50)  NOT NULL ,
	estadoExpediente  varchar(8)  NOT NULL ,
	estadoLegalCaso  varchar(10)  NULL ,
	idSolicitante  int  NOT NULL ,
	rutaArchivoFormatoSolicitud  varchar(1000)  NOT NULL ,
	reseniaSolicitud  varchar(4000)  NOT NULL ,
	idInvitado  int  NULL ,
	idUsuarioAsignado  int  NULL ,
	fechaAsignacion  datetime  NULL ,
	slaParaAsignacion  int  NULL ,
	fechaNotificacionInicial  datetime  NULL ,
	slaParaNotificacionInicial  int  NULL ,
	idExpedienteOrigen  int  NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idExpediente),
	FOREIGN KEY (idSolicitante) REFERENCES Solicitante(idSolicitante),
	FOREIGN KEY (idInvitado) REFERENCES Invitado(idInvitado),
	FOREIGN KEY (idUsuarioAsignado) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Expediente_Sesion (
	idExpedienteSesion  int  NOT NULL auto_increment,
	idExpediente  int  NOT NULL ,
	secuencia  int  NOT NULL ,
	fechaSesion  datetime  NOT NULL ,
	estadoSesion  varchar(6)  NOT NULL ,
	detallesSesion  varchar(4000)  NOT null, 
	resolucionSesion  int  NULL ,
	rutaArchivoActa  varchar(1000)  NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL,
	primary key (idExpedienteSesion),
	FOREIGN KEY (idExpediente) REFERENCES Expediente(idExpediente)
);

CREATE TABLE Expediente_Sesion_Archivo (
	idExpedienteSesionArchivo  int  NOT NULL auto_increment,
	idExpedienteSesion  int  NOT NULL ,
	nombreDocumento  varchar(250)  NOT NULL ,
	rutaArchivo  varchar(1000)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idExpedienteSesionArchivo),
	FOREIGN KEY (idExpedienteSesion) REFERENCES Expediente_Sesion(idExpedienteSesion)
);

CREATE TABLE Expediente_Enlace_Reporte (
	idEnlace  int  NOT NULL auto_increment,
	idExpediente  int  NOT NULL ,
	tokenAcceso  varchar(1000)  NOT NULL ,
	estadoToken  varchar(4)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idEnlace),
	FOREIGN KEY (idExpediente) REFERENCES Expediente(idExpediente)
);

CREATE TABLE Notificacion (
	idNotificacion  int  NOT NULL auto_increment,
	idExpediente  int  NOT NULL ,
	procesoOrigen  int  NOT NULL ,
	estadoNotificacion  varchar(4)  NOT NULL ,
	fechaEnvio  datetime  NULL ,
	remitente  varchar(100)  NOT NULL ,
	destinatarios  varchar(1000)  NULL ,
	esPorCorreo  bit  NOT NULL ,
	conCopia  varchar(1000)  NULL ,
	conCopiaOculta  varchar(1000)  NULL ,
	asunto  varchar(1000)  NOT NULL ,
	mensaje  varchar(4000)  NOT NULL ,
	idUsuarioCreador  int  NOT NULL ,
	fechaCreacion  datetime  NOT NULL ,
	idUsuarioModificador  int  NULL ,
	fechaModificacion  datetime  NULL ,
	estadoRegistro  varchar(4)  NOT NULL ,
	primary key (idNotificacion),
	FOREIGN KEY (idExpediente) REFERENCES Expediente(idExpediente)
);