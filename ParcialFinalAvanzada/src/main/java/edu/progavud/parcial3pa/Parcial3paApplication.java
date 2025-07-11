package edu.progavud.parcial3pa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase principal que inicia la aplicación Spring Boot.
 */
@SpringBootApplication
public class Parcial3paApplication {

    /**
     * Método principal que ejecuta la aplicación.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(Parcial3paApplication.class, args);
    }

    /**
     * Configura las reglas CORS para permitir solicitudes desde otros dominios o puertos.
     *
     * @return configuración personalizada de CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*")
                        .allowedOrigins("")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }

    /**
     * Crea una instancia de RestTemplate para consumir servicios REST.
     *
     * @return objeto RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
