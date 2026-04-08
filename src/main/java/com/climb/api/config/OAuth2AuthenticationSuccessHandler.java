package com.climb.api.config;

import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(AuthenticationService authenticationService, ObjectMapper objectMapper) {
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            writeJson(response, HttpStatus.UNAUTHORIZED, ApiResponse.error("O Google nao retornou um e-mail valido"));
            return;
        }

        try {
            LoginResponseDTO loginResponse = authenticationService.autenticarComGoogle(email);
            writeJson(response, HttpStatus.OK, ApiResponse.ok(loginResponse, "Login Google realizado com sucesso"));
        } catch (RuntimeException ex) {
            writeJson(response, HttpStatus.UNAUTHORIZED, ApiResponse.error(ex.getMessage()));
        }
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
