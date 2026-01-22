package pe.com.mesadepartes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        registry.addResourceHandler("/files/**")
                // 1) archivos subidos (filesystem)
                .addResourceLocations(uploadDir.toUri().toString() + "/",
                        // 2) formatos base (classpath)
                        "classpath:/static/files/");
    }
}