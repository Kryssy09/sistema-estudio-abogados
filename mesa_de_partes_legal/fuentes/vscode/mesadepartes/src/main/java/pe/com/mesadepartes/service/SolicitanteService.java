package pe.com.mesadepartes.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import pe.com.mesadepartes.dto.SolicitanteRowDto;
import pe.com.mesadepartes.entity.Persona;
import pe.com.mesadepartes.entity.Solicitante;
import pe.com.mesadepartes.repository.PersonaRepository;
import pe.com.mesadepartes.repository.SolicitanteRepository;

@Service
@AllArgsConstructor
@Transactional
public class SolicitanteService {
    private final PersonaRepository personaRepository;
    private final SolicitanteRepository solicitanteRepository;
    private final pe.com.mesadepartes.repository.ExpedienteRepository expedienteRepository;

    public static final String ESTADO_ACTIVO = "ACT";
    public static final String ESTADO_INACTIVO = "INAC";

    @PersistenceContext
    private EntityManager em;

    public List<Solicitante> listarActivos() {
        String jpql = """
                  SELECT s
                  FROM Solicitante s
                  WHERE s.estadoRegistro = :est
                  ORDER BY s.persona.apellidoPaterno ASC, s.persona.apellidoMaterno ASC, s.persona.nombres ASC
                """;
        return em.createQuery(jpql, Solicitante.class)
                .setParameter("est", ESTADO_ACTIVO)
                .getResultList();
    }

    public java.util.Optional<Solicitante> buscarPorId(Integer idSolicitante) {
        return solicitanteRepository.findById(idSolicitante);
    }

    public void eliminar(Integer idSolicitante) {
        solicitanteRepository.deleteById(idSolicitante);
    }

    public Long contarExpedientes(Integer idSolicitante) {
        return expedienteRepository.countByIdSolicitante(idSolicitante);
    }

    // ============================================================
    // GUARDAR / REGISTRAR SOLICITANTE
    // ============================================================
    public Solicitante guardarPersona(Persona personaPayload, Integer idUsuarioCreador) {
        if (personaPayload.getNumeroDocumento() == null || personaPayload.getNumeroDocumento().isBlank()) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío");

        }

        // Buscar persona existente por documento y actualizar si ya existe
        Persona persona = personaRepository.findByNumeroDocumento(personaPayload.getNumeroDocumento())
                .map(existing -> {
                    existing.setApellidoPaterno(personaPayload.getApellidoPaterno());
                    existing.setApellidoMaterno(personaPayload.getApellidoMaterno());
                    existing.setNombres(personaPayload.getNombres());
                    existing.setCorreoElectronico(personaPayload.getCorreoElectronico());
                    existing.setTelefonoPersonal(personaPayload.getTelefonoPersonal());
                    existing.setTipoDocumentoIdentidad(personaPayload.getTipoDocumentoIdentidad());
                    existing.setSexo(personaPayload.getSexo());
                    return existing;
                })
                .orElseGet(() -> {
                    personaPayload.setIdUsuarioCreador(idUsuarioCreador);
                    personaPayload.setFechaCreacion(new Date());
                    personaPayload.setEstadoRegistro("ACT");
                    return personaPayload;
                });

        persona = personaRepository.save(persona);

        // Crear nuevo solicitante asociado a la persona
        Solicitante solicitante = new Solicitante();
        solicitante.setPersona(persona);
        solicitante.setIdUsuarioCreador(idUsuarioCreador);
        solicitante.setFechaCreacion(LocalDateTime.now());
        solicitante.setEstadoRegistro("ACT");

        return solicitanteRepository.save(solicitante);
    }

    // ============================================================
    // LISTAR SOLICITANTES (ENTIDAD COMPLETA)
    // ============================================================
    @Transactional(readOnly = true)
    public List<Solicitante> listarSolicitantes() {
        return solicitanteRepository.findAll();
    }

    // ============================================================
    // LISTAR SOLICITANTES PAGINADO (BÚSQUEDA OPCIONAL)
    // ============================================================
    @Transactional(readOnly = true)
    public Page<Solicitante> listarSolicitantesPaginado(int page, int size, String busqueda) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            return solicitanteRepository.buscarSolicitantes(busqueda.trim(), pageable);
        }
        return solicitanteRepository.findAll(pageable);
    }

    // ============================================================
    // OBTENER SOLICITANTE CON PERSONA (para PDF, detalles)
    // ============================================================
    private Solicitante obtenerPorIdConPersona(Integer id) {
        return solicitanteRepository.findByIdFetchPersona(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado: " + id));
    }

    // ============================================================
    // GENERAR PDF DE FICHA DE SOLICITANTE
    // ============================================================
    public byte[] generarPdfSolicitante(Integer id) {
        Solicitante s = obtenerPorIdConPersona(id);
        Persona p = s.getPersona();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("Ficha de Solicitante"));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Documento: " + safe(p.getNumeroDocumento())));
            doc.add(new Paragraph("Nombre: " + safe(p.getApellidoPaterno()) + " "
                    + safe(p.getApellidoMaterno()) + " " + safe(p.getNombres())));
            doc.add(new Paragraph("Correo: " + safe(p.getCorreoElectronico())));
            doc.add(new Paragraph("Teléfono: " + safe(p.getTelefonoPersonal())));
            doc.add(new Paragraph("Fecha registro: " +
                    (s.getFechaCreacion() != null ? s.getFechaCreacion().toLocalDate() : "")));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    // ============================================================
    // NUEVO: LISTAR PARA DASHBOARD (DTO PLANO)
    // ============================================================
    @Transactional(readOnly = true)
    public List<SolicitanteRowDto> listarFilas() {
        return solicitanteRepository.listarFilas();
    }
}
