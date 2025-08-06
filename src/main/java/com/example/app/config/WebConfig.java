package com.example.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/で始まるすべてのパスに対してCORSを適用
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // Reactアプリケーションのオリジンを許可
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 許可するHTTPメソッド
                .allowedHeaders("*") // すべてのヘッダーを許可
                .allowCredentials(true) // クッキーやHTTP認証情報を許可 (セッション管理に必要)
                .maxAge(3600); // プリフライトリクエストの結果をキャッシュする時間 (秒)
    }
}
