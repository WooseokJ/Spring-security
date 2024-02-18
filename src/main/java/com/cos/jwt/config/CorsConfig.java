package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내서버가 응답할떄 json을 js에서 처리할수있게 할지 설정하는것. ( false이면 js로 요청하면 응답이 안와)
        config.addAllowedOrigin("*"); //  모든 ip에 응답 허용.
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 http 메서드 허용
        source.registerCorsConfiguration("/api/**", config); // 등록.
        return  new CorsFilter(source);

    }
}
