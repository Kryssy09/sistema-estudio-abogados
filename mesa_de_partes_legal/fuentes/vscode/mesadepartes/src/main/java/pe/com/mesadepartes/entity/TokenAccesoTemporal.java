package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_acceso_temporal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenAccesoTemporal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "clave_acceso", nullable = false, length = 20)
    private String claveAcceso;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoToken estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;

    @Column(name = "direccion_ip", length = 50)
    private String direccionIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "numero_accesos")
    private Integer numeroAccesos = 0;

    @Column(name = "maximo_accesos")
    private Integer maximoAccesos = 1;

    @Column(name = "estadoRegistro", length = 4)
    private String estadoRegistro = "ACTV";

    @Column(name = "idUsuarioCreador")
    private Integer idUsuarioCreador;

    @Column(name = "idUsuarioModificador")
    private Integer idUsuarioModificador;

    @Column(name = "fechaModificacion")
    private LocalDateTime fechaModificacion;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitante")
    private Solicitante solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_expediente")
    private Expediente expediente;

    @Column(name = "id_solicitante", insertable = false, updatable = false)
    private Integer idSolicitante;

    @Column(name = "id_expediente", insertable = false, updatable = false)
    private Integer idExpediente;

    public enum EstadoToken {
        ACTIVO, EXPIRADO, REVOCADO, USADO
    }

    public boolean isValido() {
        return estado == EstadoToken.ACTIVO &&
               fechaVencimiento.isAfter(LocalDateTime.now()) &&
               (maximoAccesos == null || numeroAccesos < maximoAccesos);
    }

    public void incrementarAcceso() {
        this.numeroAccesos++;
        if (numeroAccesos >= maximoAccesos) {
            this.estado = EstadoToken.USADO;
        }
        this.fechaUso = LocalDateTime.now();
    }

    public void expirar() {
        this.estado = EstadoToken.EXPIRADO;
        this.fechaModificacion = LocalDateTime.now();
    }
}