package edu.progavud.parcial3pa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Parcial3paApplication {

    public static void main(String[] args) {
        SpringApplication.run(Parcial3paApplication.class, args);
    }

    @Bean
    //metodo para configurar el acceso al proyecto desde otros dominio o puertos
    //este metodo permite resolver el problema con las politicas CORS de los navegadores
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*")                   // Aplica a todas las rutas, despues cada controller le digo cual es pa que sea bonito
                .allowedOrigins("")                 // Permite cualquier origen, asi no nos complicamos en mirar que puerto trabaja el ide del Frontend jiji
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Sólo estos métodos
                .allowedHeaders("*");                // Permite cualquier encabezado
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();

    }

}
