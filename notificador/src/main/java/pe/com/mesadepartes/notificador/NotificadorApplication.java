package pe.com.mesadepartes.notificador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificadorApplication.class, args);
	}

}
