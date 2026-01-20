/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mmoreno
 */
@Getter
@Setter
public class Usuario extends EntidadBase {
    private int idUsuario;
    private Persona persona;
    private String nombreUsuario;
    private String clave;

    public Usuario(int idUsuario, Persona persona) {
        this.idUsuario = idUsuario;
        this.persona = persona;
    }

    public Usuario(Persona persona) {
        this.persona = persona;
    }
}
