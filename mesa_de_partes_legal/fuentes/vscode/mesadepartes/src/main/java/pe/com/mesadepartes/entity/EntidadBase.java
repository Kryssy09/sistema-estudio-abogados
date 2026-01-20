/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mmoreno
 */
@Getter
@Setter
public class EntidadBase {
    private Usuario usuarioCreador;
    private Date fechaCreacion;
    private Usuario usuarioModificador;
    private Date fechaModificacion;
    private String estadoRegistro;
}
