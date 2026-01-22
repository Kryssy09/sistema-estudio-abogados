package pe.com.mesadepartes.dtos.expediente;

import lombok.Data;

/**
 * DTO unificado para crear y editar expedientes
 */
@Data
public class ExpedienteForm {

    private Integer idExpediente; // Solo para edición

    private String tipoExpediente; // CON | PL

    private Boolean mutuoAcuerdo = true; // true/false

    private Integer especialidadId; // id

    private String reseniaSolicitud;

    private Integer solicitanteId; // Para creación
    private Integer invitadoId; // Para edición
    private Integer idUsuarioAsignado; // Para edición
    private Integer idExpedienteOrigen; // Para edición (solo PL)

    // Para manejo de archivo (solo creación)
    private String formatoArchivoNombre;

    // Constructor para creación
    public ExpedienteForm() {}

    // Constructor para edición
    public ExpedienteForm(Integer idExpediente, String tipoExpediente, Boolean mutuoAcuerdo,
                         Integer especialidadId, String reseniaSolicitud) {
        this.idExpediente = idExpediente;
        this.tipoExpediente = tipoExpediente;
        this.mutuoAcuerdo = mutuoAcuerdo;
        this.especialidadId = especialidadId;
        this.reseniaSolicitud = reseniaSolicitud;
    }
}