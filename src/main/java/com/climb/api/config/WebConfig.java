package com.climb.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PermissaoRouteInterceptor permissaoRouteInterceptor;

    public WebConfig(PermissaoRouteInterceptor permissaoRouteInterceptor) {
        this.permissaoRouteInterceptor = permissaoRouteInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissaoRouteInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/auth/**",
                "/login",
                "/oauth2/**",
                "/oauth/**",
                "/actuator/**",
                "/error",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**"
            );
    }
}
