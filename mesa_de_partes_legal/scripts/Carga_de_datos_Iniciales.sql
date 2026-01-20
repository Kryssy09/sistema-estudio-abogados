use mesa_de_partes;

-- TIPO DE DOCUMENTO DE IDENTIDAD
insert into Dominio (nombre, idUsuarioCreador, fechaCreacion, estadoRegistro)
values ('TIPO DE DOCUMENTO DE IDENTIDAD', 1, NOW(), 'ACT');

insert into Dominio_Detalle (idDominio, codigo, nombre, sigla, valorCadena
	, idUsuarioCreador, fechaCreacion, estadoRegistro)
values (1, 1, 'DOCUMENTO NACIONAL DE IDENTIDAD', 'DNI', null
	, 1, NOW(), 'ACT');

-- SEXO
insert into Dominio (nombre, idUsuarioCreador, fechaCreacion, estadoRegistro)
values ('SEXO DE PERSONAS', 1, NOW(), 'ACT');

insert into Dominio_Detalle (idDominio, codigo, nombre, sigla, valorCadena
	, idUsuarioCreador, fechaCreacion, estadoRegistro)
values (2, 1, 'MASCULINO', 'M', null
	, 1, NOW(), 'ACT');
insert into Dominio_Detalle (idDominio, codigo, nombre, sigla, valorCadena
	, idUsuarioCreador, fechaCreacion, estadoRegistro)
values (2, 2, 'FEMENINO', 'F', null
	, 1, NOW(), 'ACT');

insert into Persona (apellidoPaterno, apellidoMaterno, nombres
	, tipoDocumentoIdentidad, numeroDocumento, correoElectronico, telefonoPersonal
	, sexo, rutaFoto
	, idUsuarioCreador, fechaCreacion, estadoRegistro)
values ('MORENO', 'HERNANDEZ', 'MIGUEL ANGEL'
	, 1, '43481765', 'swmiguel3@gmail.com', '993808061'
	, 1, null
	, 1, NOW(), 'ACT');

select * from Dominio;
select * from Dominio_Detalle;
select * from Persona;