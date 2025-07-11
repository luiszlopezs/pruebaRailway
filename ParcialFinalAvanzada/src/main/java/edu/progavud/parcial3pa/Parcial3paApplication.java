package edu.progavud.parcial3pa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication
public class Parcial3paApplication {

    public static void main(String[] args) {
        SpringApplication.run(Parcial3paApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // ✅ Acepta cualquier origen de forma compatible con allowCredentials
                        .allowedMethods("*") // ✅ Acepta cualquier método (GET, POST, etc)
                        .allowedHeaders("*") // ✅ Acepta cualquier cabecera
                        .allowCredentials(true) // ✅ Permite enviar cookies / sesiones si se usa
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();

    }

}
