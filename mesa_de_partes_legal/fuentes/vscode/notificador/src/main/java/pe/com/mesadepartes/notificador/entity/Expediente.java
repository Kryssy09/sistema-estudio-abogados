package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class Expediente extends EntidadBase {
    private Integer idExpediente;
    private String tipoExpediente;
    private boolean mutuoAcuerdo;
    private Integer especialidad; //DominioDetalle
    private String codigoSeguimiento;
    private String estadoExpediente;
    private String estadoLegalCaso;
    private Solicitante solicitante;
    private String rutaArchivoFormatoSolicitud;
    private String reseniaSolicitud;
    private Integer idInvitado;
    private Usuario usuarioAsignado;
    private Date fechaAsignacion;
    private Integer slaParaAsignacion;
    private Date fechaNotificacionInicial;
    private Integer slaParaNotificacionInicial;
    private Integer idExpedienteOrigen;
    private String cadenaFechaAsignacion;
    private Area areaAsignada;

    public Expediente(int idExpediente, String codigoSeguimiento, Solicitante solicitante) {
        this.idExpediente = idExpediente;
        this.solicitante = solicitante;
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public Expediente(int idExpediente, String codigoSeguimiento, Usuario usuarioCreador, String cadenaFechaCreacion, Solicitante solicitante) {
        this.idExpediente = idExpediente;
        this.setUsuarioCreador(usuarioCreador);
        this.solicitante = solicitante;
        this.codigoSeguimiento = codigoSeguimiento;
        super.setCadenaFechaCreacion(cadenaFechaCreacion);
    }
}