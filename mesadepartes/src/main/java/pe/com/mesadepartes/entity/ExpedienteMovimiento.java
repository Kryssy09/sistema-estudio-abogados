package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "expediente_movimiento")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMovimiento")
    private Integer idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idExpediente", nullable = false)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuarioActor", nullable = false)
    private Usuario usuarioActor;

    @Column(name = "accion", length = 50, nullable = false)
    private String accion;

    @Column(name = "estadoAnterior", length = 50)
    private String estadoAnterior;

    @Column(name = "estadoNuevo", length = 50)
    private String estadoNuevo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuarioAsignadoAnterior")
    private Usuario usuarioAsignadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuarioAsignadoNuevo")
    private Usuario usuarioAsignadoNuevo;

    @Column(name = "detalles", length = 4000)
    private String detalles;

    @Column(name = "fechaCreacion", nullable = false)
    private Date fechaCreacion;

    @Column(name = "estadoRegistro", length = 4, nullable = false)
    private String estadoRegistro;
}
