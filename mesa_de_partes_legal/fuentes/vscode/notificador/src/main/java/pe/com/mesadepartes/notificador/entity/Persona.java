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
public class Persona extends EntidadBase {
    public int idPersona;
    public String apellidoPaterno;
    public String apellidoMaterno;
    public String nombres;
    public DominioDetalle tipoDocumentoIdentidad;
    public String numeroDocumento;
    public String correoElectronico;
    public String telefonoPersonal;
    public DominioDetalle sexo;
    public String rutaFoto;
    public String apellidosYNombres;

    public Persona(String apellidosYNombres) {
        this.apellidosYNombres = apellidosYNombres;
    }

    public Persona(String apellidosYNombres, String correoElectronico) {
        this.apellidosYNombres = apellidosYNombres;
        this.correoElectronico = correoElectronico;
    }
}