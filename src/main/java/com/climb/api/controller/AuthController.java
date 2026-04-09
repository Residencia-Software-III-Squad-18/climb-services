package com.climb.api.controller;

import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.CompleteGoogleRegistrationRequestDTO;
import com.climb.api.model.dto.GoogleAuthorizationUrlResponseDTO;
import com.climb.api.model.dto.LoginRequestDTO;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.model.dto.RefreshTokenRequestDTO;
import com.climb.api.config.OAuth2AuthenticationSuccessHandler;
import com.climb.api.service.AuthenticationService;
import com.climb.api.service.GoogleOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

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
            return ResponseEntity.ok(ApiResponse.ok(response, "Login realizado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(@RequestBody RefreshTokenRequestDTO dto) {
        try {
            String newAccessToken = authenticationService.refreshAccessToken(dto.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.ok(newAccessToken, "Token renovado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
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
                URI redirectUri = googleOAuthService.gerarRedirecionamentoErro("Google retornou erro: " + error);
                return redirect(redirectUri);
            }

            if (code == null || code.isBlank()) {
                URI redirectUri = googleOAuthService.gerarRedirecionamentoErro("Parametro code e obrigatorio");
                return redirect(redirectUri);
            }

            var response = googleOAuthService.trocarCodePorToken(code);
            URI redirectUri = googleOAuthService.gerarRedirecionamentoFrontend(response);
            return redirect(redirectUri);

        } catch (RuntimeException e) {
            URI redirectUri = googleOAuthService.gerarRedirecionamentoErro(e.getMessage());
            return redirect(redirectUri);
        }
    }

    private ResponseEntity<Void> redirect(URI uri) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, uri.toASCIIString())
                .build();
    }

    @GetMapping("/google")
    public RedirectView googleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @PostMapping("/google/complete-registration")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> completeGoogleRegistration(
            @RequestBody CompleteGoogleRegistrationRequestDTO dto) {
        try {
            LoginResponseDTO response = googleOAuthService.concluirCadastro(dto);
            return ResponseEntity.ok(ApiResponse.ok(response, "Cadastro Google concluido com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/google/link")
    public RedirectView googleLink(Authentication authentication, HttpServletRequest request) {
        Long usuarioId = extractAuthenticatedUserId(authentication);
        request.getSession(true).setAttribute(
                OAuth2AuthenticationSuccessHandler.GOOGLE_LINK_USER_ID_SESSION_KEY,
                usuarioId
        );
        return new RedirectView("/oauth2/authorization/google");
    }

    private Long extractAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new RuntimeException("Usuario nao autenticado");
        }

        Object details = authentication.getDetails();
        if (details instanceof Long longValue) {
            return longValue;
        }

        if (details instanceof Number numberValue) {
            return numberValue.longValue();
        }

        throw new RuntimeException("Nao foi possivel identificar o usuario autenticado");
    }
}
