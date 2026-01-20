package pe.com.mesadepartes.entity;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "documento_normativo")
@Getter @Setter
public class DocumentoNormativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDocumentoNormativo;

    @Column(nullable = false, length = 255)
    private String nombreDocumento;

    @Column(nullable = false, length = 500)
    private String rutaArchivo;

    @Column(nullable = false, length = 3)
    private String estadoRegistro; // ACT/INA

    @Column(nullable = false)
    private Integer idUsuarioCreador;

    // Estas dos líneas hacen la magia:
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (estadoRegistro == null) estadoRegistro = "ACT";
        if (idUsuarioCreador == null) idUsuarioCreador = 1; // o el usuario real
        // si tu BD no soporta default para fechaModificacion, la igualamos:
        if (fechaModificacion == null) fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}
