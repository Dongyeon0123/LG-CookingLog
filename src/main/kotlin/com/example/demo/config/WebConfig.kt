package com.example.demo.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // 업로드된 이미지 파일 서빙
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/")
    }
    
    override fun addCorsMappings(registry: org.springframework.web.servlet.config.annotation.CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000", "https://after-ungratifying-lilyanna.ngrok-free.dev")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}