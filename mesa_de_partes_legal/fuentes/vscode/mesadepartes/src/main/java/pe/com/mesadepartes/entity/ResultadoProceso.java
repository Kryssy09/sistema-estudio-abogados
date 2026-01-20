/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.entity;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mmoreno
 */
@Getter
@Setter
public class ResultadoProceso {
    private int codigoResultado; //1: OK, 0: ERROR
    private String mensajeError; //Si "codigoResultado == 0", entonces aqui se detalla el error ocurrido
    private Object data;
}