package pe.com.mesadepartes.config;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.storage.root:${user.home}/mesadepartes/uploads}")
  private String storageRoot;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Sirve el sistema de archivos bajo /files/**
    String location = Paths.get(storageRoot).toAbsolutePath().normalize().toUri().toString();
    registry.addResourceHandler("/files/**")
        .addResourceLocations(location) // ej: file:/home/usuario/mesadepartes/uploads/
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
        .resourceChain(true)
        .addResolver(new PathResourceResolver());
  }
}
