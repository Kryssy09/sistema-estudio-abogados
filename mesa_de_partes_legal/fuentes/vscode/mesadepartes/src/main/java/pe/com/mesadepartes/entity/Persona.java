/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.*;

/**
 *
 * @author mmoreno
 */
@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Persona implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPersona")
    public int idPersona;

    @Column(name = "apellidoPaterno", nullable = false, length = 100)
    public String apellidoPaterno;

    @Column(name = "apellidoMaterno", nullable = false, length = 100)
    public String apellidoMaterno;

    @Column(name = "nombres", nullable = false, length = 100)
    public String nombres;

    @Column(name = "tipoDocumentoIdentidad", nullable = false)
    public Integer tipoDocumentoIdentidad;

    @Column(name = "numeroDocumento", nullable = false, length = 20)
    public String numeroDocumento;

    @Column(name = "correoElectronico", nullable = true, length = 100)
    public String correoElectronico;

    @Column(name = "telefonoPersonal", nullable = true, length = 20)
    public String telefonoPersonal;

    @Column(name = "sexo", nullable = true)
    public Integer sexo;

    @Column(name = "rutaFoto", nullable = true, length = 1000)
    public String rutaFoto;

    @Column(name = "estadoRegistro", nullable = false, length = 4)
    public String estadoRegistro;

    @Column(name = "idUsuarioCreador", nullable = false)
    public Integer idUsuarioCreador;

    @Column(name = "idUsuarioModificador", nullable = true)
    public Integer idUsuarioModificador;

    @Column(name = "fechaCreacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    //public LocalDateTime fechaCreacion = LocalDateTime.now();
    public Date fechaCreacion;

    @Column(name = "fechaModificacion", nullable = true)
    public LocalDateTime fechaModificacion;
}
