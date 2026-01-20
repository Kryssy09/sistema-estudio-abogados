/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author mmoreno
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DominioDetalle extends EntidadBase {
    private int idDominioDetalle;
    private Dominio dominio;
    private int codigo;
    private String nombre;
    private String sigla;
    private String valorCadena;
}