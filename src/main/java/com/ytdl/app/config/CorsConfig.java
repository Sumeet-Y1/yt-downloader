package com.ytdl.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "https://yt-converter-downloader.pages.dev",
                                "http://localhost:5173",
                                "http://127.0.0.1:5173"
                        )
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Content-Disposition")
                        .maxAge(3600);
            }
        };
    }
}
