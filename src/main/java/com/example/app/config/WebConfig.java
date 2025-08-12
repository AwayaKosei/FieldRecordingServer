package com.example.app.config;

//src/main/java/com/example/app/config/WebConfig.java (修正案)

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

 @Value("${upload.directory}")
 private String uploadDirectory;

 @Override
 public void addCorsMappings(CorsRegistry registry) {
     // /api/** パスへのCORS設定
     registry.addMapping("/api/**")
             .allowedOrigins("http://localhost:3000", "http://localhost:5173")
             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
             .allowedHeaders("*")
             .allowCredentials(true)
             .maxAge(3600);

     // ★★★ 修正箇所：/audio/** パスにもCORSを適用 ★★★
     registry.addMapping("/audio/**")
             .allowedOrigins("http://localhost:3000", "http://localhost:5173")
             .allowedMethods("GET", "HEAD") // 静的ファイル取得は通常GET/HEAD
             .allowedHeaders("*")
             .allowCredentials(true)
             .maxAge(3600);
 }

 @Override
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
     registry.addResourceHandler("/audio/**")
             .addResourceLocations("file:" + uploadDirectory + "/");
 }
}