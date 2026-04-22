package com.climb.api.controller;

import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.GoogleAuthorizationUrlResponseDTO;
import com.climb.api.model.dto.LoginRequestDTO;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.model.dto.RefreshTokenRequestDTO;
import com.climb.api.service.AuthenticationService;
import com.climb.api.service.GoogleOAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final GoogleOAuthService googleOAuthService;

    public AuthController(AuthenticationService authenticationService, GoogleOAuthService googleOAuthService) {
        this.authenticationService = authenticationService;
        this.googleOAuthService = googleOAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto) {
        try {
            LoginResponseDTO response = authenticationService.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.status(200).body(ApiResponse.ok(response, "Login realizado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(@RequestBody RefreshTokenRequestDTO dto) {
        try {
            String newAccessToken = authenticationService.refreshAccessToken(dto.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.ok(newAccessToken, "Token renovado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/google/url")
    public ResponseEntity<?> googleAuthorizationUrl() {
        try {
            GoogleAuthorizationUrlResponseDTO response = googleOAuthService.gerarUrlAutorizacao();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Void> googleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error) {
        try {
            if (error != null && !error.isBlank()) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, googleOAuthService.gerarRedirecionamentoErro("Google retornou erro: " + error).toString())
                        .build();
            }

            if (code == null || code.isBlank()) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, googleOAuthService.gerarRedirecionamentoErro("Parametro code e obrigatorio").toString())
                        .build();
            }

            var response = googleOAuthService.trocarCodePorToken(code);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, googleOAuthService.gerarRedirecionamentoFrontend(response).toString())
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, googleOAuthService.gerarRedirecionamentoErro(e.getMessage()).toString())
                    .build();
        }
    }
}
