package pe.com.mesadepartes.notificador.service;

import pe.com.mesadepartes.notificador.entity.Notificacion;
import pe.com.mesadepartes.notificador.entity.ResultadoProceso;
import pe.com.mesadepartes.notificador.repository.NotificacionRepository;
import pe.com.mesadepartes.notificador.util.Constantes;
import pe.com.mesadepartes.notificador.util.Util;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

public class NotificacionService {
    private NotificacionRepository repositorio;

    public NotificacionService() {
        this.repositorio = new NotificacionRepository();
    }

    public ResultadoProceso listarNotificacionesCreadas() {
        return this.repositorio.listarNotificacionesCreadas();
    }

    public boolean enviarCorreo(Notificacion notificacion) {
        boolean resultado = false;

	    Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
	    
        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(notificacion.getRemitente(), Constantes.CORREO_CONFIG_CUENTA_PWD);
            }
        });

        //session.setDebug(true);
	    
	    resultado = sendEmail(session, notificacion.getRemitente(), notificacion.getDestinatarios(),notificacion.getAsunto(), notificacion.getMensaje());

        if (resultado) {
            Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_INFORMATION, "Notificación [id=" + notificacion.getIdNotificacion() + "] enviada correctamente ...");
        }

        return resultado;
    }

    public boolean sendEmail(Session session, String from, String toEmail, String subject, String body){
        boolean resultado = false;

        try
	    {
	      Message msg = new MimeMessage(session);
	      
	      msg.setFrom(new InternetAddress(from));

	      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

	      msg.setSubject(subject);

	      msg.setContent(body, "text/html");

    	  Transport.send(msg);  

          resultado = true;
	    }
	    catch (Exception ex) {
            resultado = false;
	        Util.escribirLog(Constantes.APPLICATION_NAME, Constantes.LOG_ERROR, "NotificacionService.sendEmail - Error - Detalles: " + ex.getMessage());
	    }

        return resultado;
	}

    public ResultadoProceso registrarEnvioDeNotificacion(int idNotificacion, String destinatarios, int idUsuarioModificador) {
        return this.repositorio.registrarEnvioDeNotificacion(idNotificacion, destinatarios, idUsuarioModificador);
    }

    public ResultadoProceso listarNotificacionesAsignadas() {
        return this.repositorio.listarNotificacionesAsignadas();
    }
}
