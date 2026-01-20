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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 *
 * @author mmoreno
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "idPersona", nullable = false, unique = true)
    private Persona persona;

    @Column(name = "nombreUsuario", nullable = false, length = 20, unique = true)
    private String nombreUsuario;

    @Column(name = "clave", nullable = false, length = 1000)
    private String clave;

    @Column(name = "estadoRegistro", nullable = false, length = 4)
    private String estadoRegistro;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fechaModificacion")
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idArea", nullable = true)
    private Area idArea;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UsuarioRol> roles = new ArrayList<>();

}
