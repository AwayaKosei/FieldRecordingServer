package com.example.app.config;

import org.springframework.beans.factory.annotation.Value; // @Value をインポート
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // ResourceHandlerRegistry をインポート
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties で定義されたアップロードディレクトリのパスを注入
    @Value("${upload.directory}")
    private String uploadDirectory;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // /api/で始まるすべてのパスに対してCORSを適用
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // Reactアプリケーションのオリジンを許可
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 許可するHTTPメソッド
                .allowedHeaders("*") // すべてのヘッダーを許可
                .allowCredentials(true) // クッキーやHTTP認証情報を許可 (セッション管理に必要)
                .maxAge(3600); // プリフライトリクエストの結果をキャッシュする時間 (秒)
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /audio/ というURLパスでアクセスされた場合に、ローカルのアップロードディレクトリ内のファイルを公開する
        // 例: C:/Users/zd2S05/records/unique_filename.webm が http://localhost:8080/audio/unique_filename.webm でアクセス可能になる
        registry.addResourceHandler("/audio/**")
                .addResourceLocations("file:" + uploadDirectory + "/"); // ディレクトリパスの末尾に / を追加
    }
}
