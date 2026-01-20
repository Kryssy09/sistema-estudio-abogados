package pe.com.mesadepartes.notificador;

import java.util.List;
import java.util.ArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.com.mesadepartes.notificador.util.Constantes;
import pe.com.mesadepartes.notificador.util.Util;
import pe.com.mesadepartes.notificador.entity.DominioDetalle;
import pe.com.mesadepartes.notificador.entity.Notificacion;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;
import pe.com.mesadepartes.notificador.service.NotificacionService;
import pe.com.mesadepartes.notificador.service.DominioDetalleService;

@Component
public class Scheduler {

    private NotificacionService service;
    private DominioDetalleService serviceDominioDetalle;

    //@Scheduled(fixedRate = 1000 * 60 * 1)
    @Scheduled(fixedRate = 1000 * 10)
    public void iteraction() {
        //Mostrar dato en consola, ejemplo:
        Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Iniciando iteraction...");

        Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Obteniendo listado de notificaciones recien creadas...");

        serviceDominioDetalle = new DominioDetalleService();
        service = new NotificacionService();

        enviarNotificacionesExpedientesRecienCreados();

        Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Obteniendo listado de notificaciones asignadas...");

        enviarNotificacionesExpedientesAsignados();

        Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Terminando iteraction...");
    }

    private void enviarNotificacionesExpedientesRecienCreados() {
        DominioDetalle cuerpoCorreoRecienCreado = this.obtenerCuerpoCorreoExpedienteRecienCreado();
        String cuerpoCorreo = "";
        String cuerpoCorreoTemporal = "";
        DominioDetalle asuntoCorreoRecienCreado = this.obtenerAsuntoCorreoExpedienteRecienCreado();
        String asuntoCorreo = "";
        String asuntoCorreoTemporal = "";

        if (cuerpoCorreoRecienCreado == null) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesRecienCreados - Error - Detalles: No existe configuracion de correo para notificacion (1)");
        } else {
            cuerpoCorreo = cuerpoCorreoRecienCreado.getValorCadena();
            asuntoCorreo = asuntoCorreoRecienCreado.getValorCadena();
            
            List<Notificacion> listaDeNotificacionesRecienCreadas = this.listarNotificacionesPorExpedientesRecienCreados();
            
            if (listaDeNotificacionesRecienCreadas == null) {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesRecienCreados - Error - Detalles: No existen notificaciones para enviar (1)");
            } else {
                if (listaDeNotificacionesRecienCreadas.size() > 0) {
                    for (int i = 0; i < listaDeNotificacionesRecienCreadas.size(); i++) {
                        asuntoCorreoTemporal = asuntoCorreo;
                        asuntoCorreoTemporal = asuntoCorreoTemporal.replace(Constantes.CUERPO_CORREO_EXPEDIENTE_CODIGO_SEGUIMIENTO, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getCodigoSeguimiento());
                        asuntoCorreoTemporal = asuntoCorreoTemporal.replace(Constantes.CUERPO_CORREO_CLIENTE_APELLIDOS_Y_NOMBRES, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getSolicitante().getPersona().getApellidosYNombres());

                        cuerpoCorreoTemporal = cuerpoCorreo;
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_NUEVO_EXP_EXPEDIENTE_CREADOR_APELLIDOS_Y_NOMBRES, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getUsuarioCreador().getPersona().getApellidosYNombres());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_CLIENTE_APELLIDOS_Y_NOMBRES, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getSolicitante().getPersona().getApellidosYNombres());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_EXPEDIENTE_CODIGO_SEGUIMIENTO, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getCodigoSeguimiento());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_NUEVO_EXP_EXPEDIENTE_FECHA_Y_HORA_CREACION, listaDeNotificacionesRecienCreadas.get(i).getExpediente().getCadenaFechaCreacion());

                        //Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Scheduler.iteraction: " + cuerpoCorreoTemporal);
                        listaDeNotificacionesRecienCreadas.get(i).setAsunto(asuntoCorreoTemporal);
                        listaDeNotificacionesRecienCreadas.get(i).setMensaje(cuerpoCorreoTemporal);

                        listaDeNotificacionesRecienCreadas.get(i).setRemitente(Constantes.CORREO_CONFIG_CUENTA);
                        
                        listaDeNotificacionesRecienCreadas.get(i).setDestinatarios(listaDeNotificacionesRecienCreadas.get(i).getExpediente().getUsuarioCreador().getPersona().getCorreoElectronico());

                        if (service.enviarCorreo(listaDeNotificacionesRecienCreadas.get(i))) {
                            service.registrarEnvioDeNotificacion(listaDeNotificacionesRecienCreadas.get(i).getIdNotificacion()
                                , listaDeNotificacionesRecienCreadas.get(i).getExpediente().getUsuarioCreador().getPersona().getCorreoElectronico()
                                , listaDeNotificacionesRecienCreadas.get(i).getExpediente().getUsuarioCreador().getIdUsuario());
                        }
                    }
                }
            }
        }
    }

    private List<Notificacion> listarNotificacionesPorExpedientesRecienCreados() {
        List<Notificacion> lista = null;
        ResultadoProceso rp = null;

        try {
            rp = service.listarNotificacionesCreadas();

            if (rp.getCodigoResultado() == 1) {
                if (rp.getData() == null) {
                    lista = new ArrayList<Notificacion>();
                } else {
                    lista = (List<Notificacion>)rp.getData();
                }
                
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.listarNotificacionesPorExpedientesRecienCreados - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.listarNotificacionesPorExpedientesRecienCreados - Error - Detalles: " + ex.getMessage());
        }

        return lista;
    }

    private DominioDetalle obtenerCuerpoCorreoExpedienteRecienCreado() {
        DominioDetalle entidad = null;
        ResultadoProceso rp = null;

        try {
            rp = serviceDominioDetalle.listarDominioDetallePorId(Constantes.DOMINIO_PROCESO_NOTIFICACION, Constantes.DOMINIO_DETALLE_NOTIFICACION_CREACION_EXPEDIENTE);

            if (rp.getCodigoResultado() == 1) {
                entidad = (DominioDetalle)rp.getData();
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerCuerpoCorreoExpedienteRecienCreado - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerCuerpoCorreoExpedienteRecienCreado - Error - Detalles: " + ex.getMessage());
        }

        return entidad;
    }

    private DominioDetalle obtenerAsuntoCorreoExpedienteRecienCreado() {
        DominioDetalle entidad = null;
        ResultadoProceso rp = null;

        try {
            rp = serviceDominioDetalle.listarDominioDetallePorId(Constantes.DOMINIO_PROCESO_NOTIFICACION, Constantes.DOMINIO_DETALLE_ASUNTO_NOTIFICACION_CREACION_EXPEDIENTE);

            if (rp.getCodigoResultado() == 1) {
                entidad = (DominioDetalle)rp.getData();
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerAsuntoCorreoExpedienteRecienCreado - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerAsuntoCorreoExpedienteRecienCreado - Error - Detalles: " + ex.getMessage());
        }

        return entidad;
    }

    private void enviarNotificacionesExpedientesAsignados() {
        DominioDetalle cuerpoCorreoAsignadoDirector = this.obtenerCuerpoCorreoExpedienteAsignado(Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR);
        DominioDetalle cuerpoCorreoAsignadoSolicitante = this.obtenerCuerpoCorreoExpedienteAsignado(Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_SOLICITANTE);
        DominioDetalle cuerpoCorreoAsignadoEspecialista = this.obtenerCuerpoCorreoExpedienteAsignado(Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_ESPECIALISTA);
        String cuerpoCorreoDirector = "";
        String cuerpoCorreoSolicitante = "";
        String cuerpoCorreoEspecialista = "";
        String cuerpoCorreoTemporal = "";
        DominioDetalle asuntoCorreoAsignado = this.obtenerAsuntoCorreoExpedienteAsignado();
        String asuntoCorreo = "";
        String asuntoCorreoTemporal = "";
        boolean continuarDirectores = true;
        boolean continuarSolicitanes = true;
        boolean continuarEspecialistas = true;

        if (cuerpoCorreoAsignadoDirector == null) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesAsignados - Error - Detalles: No existe configuracion de correo para notificacion de directores (1)");
            continuarDirectores = false;
        }

        if (cuerpoCorreoAsignadoSolicitante == null) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesAsignados - Error - Detalles: No existe configuracion de correo para notificacion de solicitantes (1)");
            continuarSolicitanes = false;
        }

        if (cuerpoCorreoAsignadoEspecialista == null) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesAsignados - Error - Detalles: No existe configuracion de correo para notificacion al especialista asignado (1)");
            continuarEspecialistas = false;
        }

        if (continuarDirectores || continuarSolicitanes || continuarEspecialistas) {
            List<Notificacion> listaDeNotificacionesAsignados = this.listarNotificacionesPorExpedientesAsignados();

            cuerpoCorreoDirector = cuerpoCorreoAsignadoDirector.getValorCadena();
            cuerpoCorreoSolicitante = cuerpoCorreoAsignadoSolicitante.getValorCadena();
            cuerpoCorreoEspecialista = cuerpoCorreoAsignadoEspecialista.getValorCadena();
            asuntoCorreo = asuntoCorreoAsignado.getValorCadena();
            
            if (listaDeNotificacionesAsignados == null) {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.enviarNotificacionesExpedientesAsignados - Error - Detalles: No existen notificaciones para enviar (1)");
            } else {
                if (listaDeNotificacionesAsignados.size() > 0) {
                    for (int i = 0; i < listaDeNotificacionesAsignados.size(); i++) {
                        asuntoCorreoTemporal = asuntoCorreo;
                        asuntoCorreoTemporal = asuntoCorreoTemporal.replace(Constantes.CUERPO_CORREO_EXPEDIENTE_CODIGO_SEGUIMIENTO, listaDeNotificacionesAsignados.get(i).getExpediente().getCodigoSeguimiento());
                        asuntoCorreoTemporal = asuntoCorreoTemporal.replace(Constantes.CUERPO_CORREO_CLIENTE_APELLIDOS_Y_NOMBRES, listaDeNotificacionesAsignados.get(i).getExpediente().getSolicitante().getPersona().getApellidosYNombres());

                        if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR) {
                            cuerpoCorreoTemporal = cuerpoCorreoDirector;
                        } else if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_SOLICITANTE) {
                            cuerpoCorreoTemporal = cuerpoCorreoSolicitante;
                        } else {
                            cuerpoCorreoTemporal = cuerpoCorreoEspecialista;
                        }

                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_ASIG_EXP_AREA_DIRECTOR_APELLIDOS_Y_NOMBRES, listaDeNotificacionesAsignados.get(i).getExpediente().getAreaAsignada().getDirector().getApellidosYNombres());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_ASIG_EXP_AREA_NOMBRE, listaDeNotificacionesAsignados.get(i).getExpediente().getAreaAsignada().getNombre());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_EXPEDIENTE_CODIGO_SEGUIMIENTO, listaDeNotificacionesAsignados.get(i).getExpediente().getCodigoSeguimiento());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_CLIENTE_APELLIDOS_Y_NOMBRES, listaDeNotificacionesAsignados.get(i).getExpediente().getSolicitante().getPersona().getApellidosYNombres());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_ASIG_EXP_EXPEDIENTE_ASIGNADO_APELLIDOS_Y_NOMBRES, listaDeNotificacionesAsignados.get(i).getExpediente().getUsuarioAsignado().getPersona().getApellidosYNombres());
                        cuerpoCorreoTemporal = cuerpoCorreoTemporal.replace(Constantes.CUERPO_CORREO_ASIG_EXP_EXPEDIENTE_FECHA_Y_HORA_ASIGNACION, listaDeNotificacionesAsignados.get(i).getExpediente().getCadenaFechaAsignacion());

                        //Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Scheduler.iteraction: " + cuerpoCorreoTemporal);
                        listaDeNotificacionesAsignados.get(i).setAsunto(asuntoCorreoTemporal);
                        listaDeNotificacionesAsignados.get(i).setMensaje(cuerpoCorreoTemporal);

                        listaDeNotificacionesAsignados.get(i).setRemitente(Constantes.CORREO_CONFIG_CUENTA);
                        
                        if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR) {
                            listaDeNotificacionesAsignados.get(i).setDestinatarios(listaDeNotificacionesAsignados.get(i).getExpediente().getAreaAsignada().getDirector().getCorreoElectronico());
                        } else if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_SOLICITANTE) {
                            listaDeNotificacionesAsignados.get(i).setDestinatarios(listaDeNotificacionesAsignados.get(i).getExpediente().getSolicitante().getPersona().getCorreoElectronico());
                        } else {
                            listaDeNotificacionesAsignados.get(i).setDestinatarios(listaDeNotificacionesAsignados.get(i).getExpediente().getUsuarioAsignado().getPersona().getCorreoElectronico());
                        }

                        if (service.enviarCorreo(listaDeNotificacionesAsignados.get(i))) {
                            if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR) {
                                service.registrarEnvioDeNotificacion(listaDeNotificacionesAsignados.get(i).getIdNotificacion()
                                    , listaDeNotificacionesAsignados.get(i).getExpediente().getAreaAsignada().getDirector().getCorreoElectronico()
                                    , Constantes.USUARIO_ADMINISTRADOR_ID);
                            } else if (listaDeNotificacionesAsignados.get(i).getProcesoOrigen() == Constantes.DOMINIO_DETALLE_NOTIFICACION_ASIGNACION_EXPEDIENTE_SOLICITANTE) {
                                service.registrarEnvioDeNotificacion(listaDeNotificacionesAsignados.get(i).getIdNotificacion()
                                    , listaDeNotificacionesAsignados.get(i).getExpediente().getSolicitante().getPersona().getCorreoElectronico()
                                    , Constantes.USUARIO_ADMINISTRADOR_ID);
                            } else {
                                service.registrarEnvioDeNotificacion(listaDeNotificacionesAsignados.get(i).getIdNotificacion()
                                    , listaDeNotificacionesAsignados.get(i).getExpediente().getUsuarioAsignado().getPersona().getCorreoElectronico()
                                    , Constantes.USUARIO_ADMINISTRADOR_ID);
                            }
                        }
                    }
                }
            }
        }
    }

    private DominioDetalle obtenerCuerpoCorreoExpedienteAsignado(int codigoDestinatario) {
        DominioDetalle entidad = null;
        ResultadoProceso rp = null;

        try {
            rp = serviceDominioDetalle.listarDominioDetallePorId(Constantes.DOMINIO_PROCESO_NOTIFICACION, codigoDestinatario);

            if (rp.getCodigoResultado() == 1) {
                entidad = (DominioDetalle)rp.getData();
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerCuerpoCorreoExpedienteAsignado - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerCuerpoCorreoExpedienteAsignado - Error - Detalles: " + ex.getMessage());
        }

        return entidad;
    }

    private DominioDetalle obtenerAsuntoCorreoExpedienteAsignado() {
        DominioDetalle entidad = null;
        ResultadoProceso rp = null;

        try {
            rp = serviceDominioDetalle.listarDominioDetallePorId(Constantes.DOMINIO_PROCESO_NOTIFICACION, Constantes.DOMINIO_DETALLE_ASUNTO_NOTIFICACION_ASIGNACION_EXPEDIENTE_DIRECTOR);

            if (rp.getCodigoResultado() == 1) {
                entidad = (DominioDetalle)rp.getData();
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerAsuntoCorreoExpedienteAsignado - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.obtenerAsuntoCorreoExpedienteAsignado - Error - Detalles: " + ex.getMessage());
        }

        return entidad;
    }

    private List<Notificacion> listarNotificacionesPorExpedientesAsignados() {
        List<Notificacion> lista = null;
        ResultadoProceso rp = null;

        try {
            rp = service.listarNotificacionesAsignadas();

            if (rp.getCodigoResultado() == 1) {
                if (rp.getData() == null) {
                    lista = new ArrayList<Notificacion>();
                } else {
                    lista = (List<Notificacion>)rp.getData();
                }
            } else {
                Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.listarNotificacionesPorExpedientesAsignados - Error - Detalles: " + rp.getMensajeError());
            }
        } catch (Exception ex) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "Scheduler.listarNotificacionesPorExpedientesAsignados - Error - Detalles: " + ex.getMessage());
        }

        return lista;
    }
}
