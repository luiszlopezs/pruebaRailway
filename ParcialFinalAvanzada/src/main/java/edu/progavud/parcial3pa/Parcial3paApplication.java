package edu.progavud.parcial3pa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
            registry.addMapping("/**") // 🔥 Aplica CORS a todos los endpoints
                    .allowedOrigins("http://localhost:8383") // ✅ tu frontend local
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Opciones necesarias para fetch con JSON
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    };
}


        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        
    }

}
