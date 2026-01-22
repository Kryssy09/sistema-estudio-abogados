/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.com.mesadepartes.notificador.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import pe.com.mesadepartes.notificador.entity.Expediente;
import pe.com.mesadepartes.notificador.entity.Notificacion;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;
import pe.com.mesadepartes.notificador.entity.Usuario;
import pe.com.mesadepartes.notificador.entity.Persona;
import pe.com.mesadepartes.notificador.entity.Solicitante;
import pe.com.mesadepartes.notificador.entity.Area;

/**
 *
 * @author mmoreno
 */
public class NotificacionRepository extends BaseRepository {
    
    public ResultadoProceso listarNotificacionesCreadas() {
        ResultadoProceso resultado = new ResultadoProceso();
        List<Notificacion> lista = null;
        Notificacion entidad = null;
                
        PreparedStatement ps = null;
        ResultSet rs = null;
        Expediente expediente = null;

        try {
            resultado.setCodigoResultado(1);
            
            if (this.cn == null) {
                resultado = this.conectarBBDD("NotificacionRepository.listarNotificacionesCreadas");
            } else {
                if (this.cn.isClosed()) {
                    resultado = this.conectarBBDD("NotificacionRepository.listarNotificacionesCreadas");
                }
            }
            
            if (resultado.getCodigoResultado() == 1) {
                ps = this.cn.prepareStatement("call usp_notificacion_listar_creados ()", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
                rs = ps.executeQuery();

                while (rs.next()) {
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }

                    entidad = null;
                    entidad = new Notificacion();
                    
                    entidad.setIdNotificacion(rs.getInt("idNotificacion"));

                    expediente = new Expediente(rs.getInt("idExpediente")
                        , rs.getString("codigoSeguimiento")
                        , new Usuario(rs.getInt("idUsuarioCreador"), new Persona(rs.getString("apellidosYNombresUsuarioCreador"), rs.getString("correoElectronicoCreador")))
                        , rs.getString("cadenaFechaCreacion")
                        , new Solicitante(new Persona(rs.getString("apellidosYNombresSolicitante"))));

                    entidad.setExpediente(expediente);
                    
                    lista.add(entidad);
                }

                resultado.setCodigoResultado(1);
                resultado.setData(lista);
            } else {
                resultado.setCodigoResultado(0);
                resultado.setMensajeError("NotificacionRepository.listarNotificacionesCreadas - Error(2) - Detalles: No se pudo conectar a la BBDD");

                System.out.println(resultado.getMensajeError());
            }
        } catch (Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("NotificacionRepository.listarNotificacionesCreadas - Error(1) - Detalles: " + ex.getMessage());
            
            System.out.println(resultado.getMensajeError());
        } finally {
            if (lista != null) {
                lista = null;
            }
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
	
    public ResultadoProceso registrarEnvioDeNotificacion(int idNotificacion, String destinatarios, int idUsuarioModificador) {
        ResultadoProceso resultado = new ResultadoProceso();
        int cantidadFilasAfectadas = 0;
        PreparedStatement ps = null;
        
        try {
            resultado.setCodigoResultado(1);
            
            if (this.cn == null) {
                resultado = this.conectarBBDD("NotificacionRepository.registrarEnvioDeNotificacion");
            } else {
                if (this.cn.isClosed()) {
                    resultado = this.conectarBBDD("NotificacionRepository.registrarEnvioDeNotificacion");
                }
            }
            
            if (resultado.getCodigoResultado() == 1) {
                ps = this.cn.prepareStatement("call usp_notificacion_registrar_envio (?, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
                ps.setInt(1, idNotificacion);
                ps.setString(2, destinatarios);
                ps.setInt(3, idUsuarioModificador);

                cantidadFilasAfectadas = ps.executeUpdate();

                resultado.setCodigoResultado((cantidadFilasAfectadas > 0 ? 1 : 0));
            } else {
                resultado.setCodigoResultado(0);
                resultado.setMensajeError("NotificacionRepository.registrarEnvioDeNotificacion - Error(2) - Detalles: No se pudo conectar a la BBDD");

                System.out.println(resultado.getMensajeError());
            }
        } catch (Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("NotificacionRepository.registrarEnvioDeNotificacion - Error(1) - Detalles: " + ex.getMessage());
            
            System.out.println(resultado.getMensajeError());
        } finally {
            if (ps != null) {
                ps = null;
            }
        }

        return resultado;
    }
    
    public ResultadoProceso listarNotificacionesAsignadas() {
        ResultadoProceso resultado = new ResultadoProceso();
        List<Notificacion> lista = null;
        Notificacion entidad = null;
                
        PreparedStatement ps = null;
        ResultSet rs = null;
        Expediente expediente = null;
        Area area = null;

        try {
            resultado.setCodigoResultado(1);
            
            if (this.cn == null) {
                resultado = this.conectarBBDD("NotificacionRepository.listarNotificacionesAsignadas");
            } else {
                if (this.cn.isClosed()) {
                    resultado = this.conectarBBDD("NotificacionRepository.listarNotificacionesAsignadas");
                }
            }
            
            if (resultado.getCodigoResultado() == 1) {
                ps = this.cn.prepareStatement("call usp_notificacion_listar_recien_asignados ()", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
                rs = ps.executeQuery();

                while (rs.next()) {
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }

                    entidad = null;
                    entidad = new Notificacion();
                    
                    entidad.setIdNotificacion(rs.getInt("idNotificacion"));
                    entidad.setProcesoOrigen(rs.getInt("procesoOrigen"));

                    area = null;
                    area = new Area();
                    area.setNombre(rs.getString("nombreArea"));
                    area.setDirector(new Persona(rs.getString("apellidosYNombresDirectorArea"), rs.getString("correoElectronicoDirectorArea")));

                    expediente = new Expediente(rs.getInt("idExpediente")
                        , rs.getString("codigoSeguimiento")
                        , new Solicitante(new Persona(rs.getString("apellidosYNombresSolicitante"), rs.getString("correoElectronicoSolicitante"))));
                    expediente.setAreaAsignada(area);
                    expediente.setUsuarioAsignado(new Usuario(new Persona(rs.getString("apellidosYNombresEspecialistaAsignado"), rs.getString("correoElectronicoEspecialistaAsignado"))));
                    expediente.setCadenaFechaAsignacion(rs.getString("cadenaFechaAsignacion"));

                    entidad.setExpediente(expediente);
                    
                    lista.add(entidad);
                }

                resultado.setCodigoResultado(1);
                resultado.setData(lista);
            } else {
                resultado.setCodigoResultado(0);
                resultado.setMensajeError("NotificacionRepository.listarNotificacionesAsignadas - Error(2) - Detalles: No se pudo conectar a la BBDD");

                System.out.println(resultado.getMensajeError());
            }
        } catch (Exception ex) {
            resultado.setCodigoResultado(0);
            resultado.setMensajeError("NotificacionRepository.listarNotificacionesAsignadas - Error(1) - Detalles: " + ex.getMessage());
            
            System.out.println(resultado.getMensajeError());
        } finally {
            if (lista != null) {
                lista = null;
            }
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
