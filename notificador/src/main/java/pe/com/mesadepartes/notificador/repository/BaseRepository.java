/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;
import pe.com.mesadepartes.notificador.util.Constantes;
import pe.com.mesadepartes.notificador.util.Util;

/**
 *
 * @author mmoreno
 */
public class BaseRepository {
    protected Connection cn = null;
    
    public ResultadoProceso conectarBBDD(String metodoSolicitante) {
        ResultadoProceso resultado = new ResultadoProceso();
        
        try {
            this.cn = null;
            this.cn = DriverManager.getConnection("jdbc:mysql://" + Constantes.MYSQL_BBDD_IP + ":" + Constantes.MYSQL_BBDD_PUERTO + "/" + Constantes.MYSQL_BBDD_NOMBRE + "?serverTimezone=UTC"
                                                    , Constantes.MYSQL_BBDD_USUARIO, Constantes.MYSQL_BBDD_CLAVEUSUARIO);
            
            resultado.setCodigoResultado(1);
            
            //Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "BaseRepository.conectarBBDD - Conexion Exitosa..." + (!metodoSolicitante.equals("") ? " Para <" + metodoSolicitante + ">" : ""));
        } catch(Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("BaseRepository.conectarBBDD - Error(1) - Detalles: " + ex.getMessage());
            
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, resultado.getMensajeError());
        }
        
        return resultado;
    }
}
