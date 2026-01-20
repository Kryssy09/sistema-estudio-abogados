package pe.com.mesadepartes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.mesadepartes.dto.AccesoExternoRequest;
import pe.com.mesadepartes.dto.AccesoExternoResponse;
import pe.com.mesadepartes.entity.Expediente;
import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.entity.TokenAccesoTemporal;
import pe.com.mesadepartes.repository.ExpedienteRepository;
import pe.com.mesadepartes.repository.PersonaRepository;
import pe.com.mesadepartes.repository.SolicitanteRepository;
import pe.com.mesadepartes.repository.TokenAccesoTemporalRepository;
import pe.com.mesadepartes.security.jwt.JwtTokenUtil;
import pe.com.mesadepartes.repository.UsuarioRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.core.io.InputStreamResource;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;

@Service
@Transactional
public class AccesoExternoService {

    @Autowired
    private TokenAccesoTemporalRepository tokenAccesoTemporalRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AccesoExternoResponse generarAccesoExterno(AccesoExternoRequest request) {
        try {
            // Buscar solicitante por DNI
            Optional<Persona> personaOpt = personaRepository.findByNumeroDocumento(request.getDni());
            if (personaOpt.isEmpty()) {
                return AccesoExternoResponse.error("DNI no registrado en el sistema");
            }

            Persona persona = personaOpt.get();

            // Buscar solicitante asociado a la persona
            Optional<Solicitante> solicitanteOpt = solicitanteRepository.findByPersona(persona);
            if (solicitanteOpt.isEmpty()) {
                return AccesoExternoResponse.error("No se encontró solicitante asociado al DNI");
            }

            Solicitante solicitante = solicitanteOpt.get();

            // Buscar el último expediente del solicitante
            Optional<Expediente> expedienteOpt = expedienteRepository
                    .findTopByPersonaSolicitanteOrderByIdExpedienteDesc(solicitante);

            if (expedienteOpt.isEmpty()) {
                return AccesoExternoResponse.error("El solicitante no tiene expedientes registrados");
            }

            Expediente expediente = expedienteOpt.get();

            // Verificar si ya tiene un acceso vigente
            Optional<TokenAccesoTemporal> accesoVigenteOpt = tokenAccesoTemporalRepository
                    .findAccesoVigentePorSolicitanteYExpediente(
                            solicitante.getIdSolicitante(),
                            expediente.getIdExpediente(),
                            LocalDateTime.now());

            if (accesoVigenteOpt.isPresent()) {
                TokenAccesoTemporal accesoExistente = accesoVigenteOpt.get();
                return AccesoExternoResponse.exitoso(
                        accesoExistente.getToken(),
                        accesoExistente.getFechaVencimiento(),
                        Long.valueOf(accesoExistente.getExpediente().getIdExpediente()),
                        accesoExistente.getExpediente().getCodigoSeguimiento(),
                        accesoExistente.getExpediente().getEstadoExpediente());
            }

            // Buscar un usuario del sistema para asignar como creador (fallback)
            Integer idUsuarioCreador = 1; // Default
            try {
                var usuarios = usuarioRepository.findAll();
                if (!usuarios.isEmpty()) {
                    idUsuarioCreador = usuarios.get(0).getIdUsuario();
                }
            } catch (Exception ex) {
                System.err.println("Error al buscar usuario creador: " + ex.getMessage());
            }

            // Crear nuevo token de acceso
            TokenAccesoTemporal nuevoToken = new TokenAccesoTemporal();
            nuevoToken.setToken(
                    jwtTokenUtil.generarToken(null, request.getDni(), Long.valueOf(expediente.getIdExpediente())));
            nuevoToken.setClaveAcceso(request.getClaveAcceso());
            nuevoToken.setEstado(TokenAccesoTemporal.EstadoToken.ACTIVO);
            nuevoToken.setFechaVencimiento(LocalDateTime.now().plusHours(24));
            nuevoToken.setMaximoAccesos(5); // Puede acceder hasta 5 veces
            nuevoToken.setSolicitante(solicitante);
            nuevoToken.setExpediente(expediente);
            nuevoToken.setIdSolicitante(solicitante.getIdSolicitante());
            nuevoToken.setIdExpediente(expediente.getIdExpediente());
            nuevoToken.setEstadoRegistro("ACTV");
            nuevoToken.setIdUsuarioCreador(idUsuarioCreador);

            tokenAccesoTemporalRepository.save(nuevoToken);

            return AccesoExternoResponse.exitoso(
                    nuevoToken.getToken(),
                    nuevoToken.getFechaVencimiento(),
                    Long.valueOf(expediente.getIdExpediente()),
                    expediente.getCodigoSeguimiento(),
                    expediente.getEstadoExpediente());

        } catch (Exception e) {
            e.printStackTrace();
            return AccesoExternoResponse.error("Error al generar acceso externo: " + e.getMessage());
        }
    }

    public Optional<TokenAccesoTemporal> validarAccesoToken(String token) {
        if (!jwtTokenUtil.esTokenValido(token)) {
            return Optional.empty();
        }

        return tokenAccesoTemporalRepository.findTokenValido(token, LocalDateTime.now());
    }

    public void incrementarAcceso(TokenAccesoTemporal tokenAccesoTemporal) {
        tokenAccesoTemporal.incrementarAcceso();
        tokenAccesoTemporalRepository.save(tokenAccesoTemporal);
    }

    public InputStreamResource generarPdfExpediente(Integer idExpediente) {
        try {
            // Buscar el expediente
            Optional<Expediente> expedienteOpt = expedienteRepository.findById(idExpediente);
            if (expedienteOpt.isEmpty()) {
                return null;
            }

            Expediente expediente = expedienteOpt.get();

            // Crear el PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Título y encabezado
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("IINCADE 4.0 - MESA DE PARTES", titleFont));
            document.add(new Paragraph("CONSTANCIA DE EXPEDIENTE", titleFont));
            document.add(Chunk.NEWLINE);
            document.add(
                    new Paragraph("_________________________________________________________________", normalFont));
            document.add(Chunk.NEWLINE);

            // Información del expediente
            document.add(new Paragraph("DATOS DEL EXPEDIENTE", headerFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Código de Seguimiento: " + expediente.getCodigoSeguimiento(), normalFont));
            document.add(new Paragraph("ID Expediente: " + expediente.getIdExpediente(), normalFont));
            document.add(new Paragraph("Estado: " + expediente.getEstadoExpediente().replace("_", " "), normalFont));
            document.add(new Paragraph("Tipo: " + expediente.getTipoExpediente(), normalFont));
            document.add(new Paragraph("Especialidad: " + expediente.getEspecialidad(), normalFont));

            // Fechas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            if (expediente.getFechaCreacion() != null) {
                document.add(new Paragraph("Fecha de Creación: " +
                        new java.util.Date(expediente.getFechaCreacion().getTime()).toString(), normalFont));
            }
            if (expediente.getFechaAsignacion() != null) {
                document.add(new Paragraph("Fecha de Asignación: " +
                        new java.util.Date(expediente.getFechaAsignacion().getTime()).toString(), normalFont));
            }

            document.add(Chunk.NEWLINE);

            // Información del solicitante
            if (expediente.getPersonaSolicitante() != null &&
                    expediente.getPersonaSolicitante().getPersona() != null) {

                document.add(new Paragraph("DATOS DEL SOLICITANTE", headerFont));
                document.add(Chunk.NEWLINE);

                Persona solicitante = expediente.getPersonaSolicitante().getPersona();
                document.add(new Paragraph("Nombre Completo: " +
                        solicitante.getNombres() + " " +
                        solicitante.getApellidoPaterno() + " " +
                        solicitante.getApellidoMaterno(), normalFont));
                document.add(
                        new Paragraph("Tipo de Documento: " + solicitante.getTipoDocumentoIdentidad(), normalFont));
                document.add(new Paragraph("Número de Documento: " + solicitante.getNumeroDocumento(), normalFont));
                document.add(Chunk.NEWLINE);
            }

            // Resumen de la solicitud
            document.add(new Paragraph("RESUMEN DE LA SOLICITUD", headerFont));
            document.add(Chunk.NEWLINE);

            if (expediente.getReseniaSolicitud() != null && !expediente.getReseniaSolicitud().trim().isEmpty()) {
                document.add(new Paragraph(expediente.getReseniaSolicitud(), normalFont));
            } else {
                document.add(
                        new Paragraph("No se ha registrado una descripción detallada de la solicitud.", normalFont));
            }

            document.add(Chunk.NEWLINE);

            // Información del abogado asignado
            if (expediente.getUsuarioAsignado() != null) {
                document.add(new Paragraph("ABOGADO ASIGNADO", headerFont));
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("Nombre: " +
                        expediente.getUsuarioAsignado().getPersona().getNombres() + " " +
                        expediente.getUsuarioAsignado().getPersona().getApellidoPaterno() + " " +
                        expediente.getUsuarioAsignado().getPersona().getApellidoMaterno(), normalFont));
                document.add(Chunk.NEWLINE);
            }

            // Pie de página
            document.add(Chunk.NEWLINE);
            document.add(
                    new Paragraph("_________________________________________________________________", normalFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Este documento es una constancia oficial generada desde el sistema IINCADE 4.0",
                    normalFont));
            document.add(new Paragraph("Fecha de generación: " +
                    LocalDateTime.now().format(formatter), normalFont));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Esta constancia tiene validez por 24 horas desde su generación.", normalFont));

            document.close();

            // Retornar el PDF como InputStreamResource
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new InputStreamResource(bais);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}