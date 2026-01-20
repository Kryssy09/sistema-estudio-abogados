/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pe.com.mesadepartes.notificador.entity.DominioDetalle;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;

/**
 *
 * @author mmoreno
 */
public class DominioDetalleRepository extends BaseRepository {
    
    public ResultadoProceso listarDominioDetallePorId(int idDominio, int codigo) {
        ResultadoProceso resultado = new ResultadoProceso();
        DominioDetalle entidad = null;
                
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            resultado.setCodigoResultado(1);
            
            if (this.cn == null) {
                resultado = this.conectarBBDD("DominioDetalleRepository.listarDominioDetallePorId");
            } else {
                if (this.cn.isClosed()) {
                    resultado = this.conectarBBDD("DominioDetalleRepository.listarDominioDetallePorId");
                }
            }
            
            if (resultado.getCodigoResultado() == 1) {
                ps = this.cn.prepareStatement("select dd.* from Dominio_Detalle dd where dd.idDominio = ? and dd.codigo = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
                ps.setInt(1, idDominio);
                ps.setInt(2, codigo);

                rs = ps.executeQuery();

                while (rs.next()) {
                    entidad = null;
                    entidad = new DominioDetalle();
                    
                    entidad.setIdDominioDetalle(rs.getInt("idDominioDetalle"));
                    entidad.setValorCadena(rs.getString("valorCadena"));
                }

                resultado.setCodigoResultado(1);
                resultado.setData(entidad);
            } else {
                resultado.setCodigoResultado(0);
                resultado.setMensajeError("DominioDetalleRepository.listarDominioDetallePorId - Error(2) - Detalles: No se pudo conectar a la BBDD");

                System.out.println(resultado.getMensajeError());
            }
        } catch (Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("DominioDetalleRepository.listarDominioDetallePorId - Error(1) - Detalles: " + ex.getMessage());
            
            System.out.println(resultado.getMensajeError());
        } finally {
            if (entidad != null) {
                entidad = null;
            }
            if (rs != null) {
                rs = null;
            }
            if (ps != null) {
                ps = null;
            }
        }

        return resultado;
    }
	
}

