package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "expediente")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idExpediente")
    private Integer idExpediente;

    @Column(name = "tipoExpediente", length = 3, nullable = false)
    private String tipoExpediente;

    @Column(name = "mutuoAcuerdo")
    private Boolean mutuoAcuerdo;

    @Column(name = "especialidad", nullable = false)
    private Integer especialidad;

    @Column(name = "codigoSeguimiento", length = 50, nullable = false)
    private String codigoSeguimiento;

    @Column(name = "estadoExpediente", length = 8, nullable = false)
    private String estadoExpediente;

    @Column(name = "estadoLegalCaso", length = 10)
    private String estadoLegalCaso;

    @Column(name = "rutaArchivoFormatoSolicitud", length = 1000, nullable = false)
    private String rutaArchivoFormatoSolicitud;

    @Column(name = "reseniaSolicitud", length = 4000, nullable = false)
    private String reseniaSolicitud;

    @Column(name = "fechaAsignacion")
    private Date fechaAsignacion;

    @Column(name = "slaParaAsignacion")
    private Integer slaParaAsignacion;

    @Column(name = "fechaNotificacionInicial")
    private Date fechaNotificacionInicial;

    @Column(name = "slaParaNotificacionInicial")
    private Integer slaParaNotificacionInicial;

    @Column(name = "idExpedienteOrigen")
    private Integer idExpedienteOrigen;

    @Column(name = "idUsuarioCreador", nullable = false)
    private Integer idUsuarioCreador;

    @Column(name = "fechaCreacion", nullable = false)
    private Date fechaCreacion;

    @Column(name = "idUsuarioModificador")
    private Integer idUsuarioModificador;

    @Column(name = "fechaModificacion")
    private Date fechaModificacion;

    @Column(name = "estadoRegistro", length = 4, nullable = false)
    private String estadoRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuarioAsignado", nullable = true)
    private Usuario usuarioAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSolicitante", nullable = false)
    private Solicitante personaSolicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idInvitado", nullable = true)
    private Invitado personaInvitada;
}