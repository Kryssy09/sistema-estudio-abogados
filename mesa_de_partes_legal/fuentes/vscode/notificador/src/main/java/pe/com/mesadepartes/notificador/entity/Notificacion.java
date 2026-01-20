package pe.com.mesadepartes.notificador.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class Notificacion extends EntidadBase {
    private int idNotificacion;
    private Expediente expediente;
    private int procesoOrigen;
    private String estadoNotificacion;
    private Date fechaEnvio;
    private String remitente;
    private String destinatarios;
    private boolean porCorreo;
    private String conCopia;
    private String conCopiaOculta;
    private String asunto;
    private String mensaje;
}
