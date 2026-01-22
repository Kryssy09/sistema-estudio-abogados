/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import pe.com.mesadepartes.entity.ResultadoProceso;
import pe.com.mesadepartes.util.Constantes;

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
            this.cn = DriverManager.getConnection(
                    "jdbc:mysql://" + Constantes.KS_MYSQL_BBDD_IP + ":" + Constantes.KI_MYSQL_BBDD_PUERTO + "/"
                            + Constantes.KS_MYSQL_BBDD_NOMBRE + "?serverTimezone=UTC",
                    Constantes.KS_MYSQL_BBDD_USUARIO, Constantes.KS_MYSQL_BBDD_CLAVEUSUARIO);

            resultado.setCodigoResultado(1);

            System.out.println("BaseRepository.conectarBBDD - Conexion Exitosa..."
                    + (!metodoSolicitante.equals("") ? " Para <" + metodoSolicitante + ">" : ""));
        } catch (Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("BaseRepository.conectarBBDD - Error(1) - Detalles: " + ex.getMessage());

            System.out.println(resultado.getMensajeError());
        }

        return resultado;
    }
}
