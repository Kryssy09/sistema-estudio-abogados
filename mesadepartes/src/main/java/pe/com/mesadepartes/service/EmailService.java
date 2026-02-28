package pe.com.mesadepartes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@estudioabogados.com}")
    private String fromEmail;

    public void notificarCierreExpediente(String emailDestino, String codigoExpediente, String motivoCierre) {
        if (emailDestino == null || emailDestino.isBlank()) {
            logger.warning("No se puede enviar correo de cierre para " + codigoExpediente
                    + " porque no hay email de destino.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailDestino);
            message.setSubject("Aviso de Conclusión de Trámite - Expediente " + codigoExpediente);

            String text = "Estimado/a cliente,\n\n"
                    + "Le informamos que su trámite correspondiente al expediente N° " + codigoExpediente
                    + " ha concluido formalmente y ha sido cerrado en nuestro sistema.\n\n"
                    + "Motivo o Resolución de cierre:\n"
                    + motivoCierre + "\n\n"
                    + "Atentamente,\n"
                    + "Estudio de Abogados";

            message.setText(text);

            mailSender.send(message);
            logger.info("Correo de cierre enviado exitosamente a " + emailDestino + " para el expediente "
                    + codigoExpediente);
        } catch (Exception e) {
            logger.severe("Error al enviar correo de cierre a " + emailDestino + ": " + e.getMessage());
            // No lanzamos excepcion para no bloquear el proceso web si falla el correo.
        }
    }
}
