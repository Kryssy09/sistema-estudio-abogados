/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mmoreno
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dominio extends EntidadBase {
    public int idDominio;
    public String nombre;
}
