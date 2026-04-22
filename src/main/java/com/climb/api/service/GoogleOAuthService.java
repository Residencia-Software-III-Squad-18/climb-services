package com.climb.api.service;

import com.climb.api.config.GoogleCalendarConfig;
import com.climb.api.model.dto.GoogleAuthorizationUrlResponseDTO;
import com.climb.api.model.dto.GoogleTokenResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class GoogleOAuthService {

    private static final String GOOGLE_AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/calendar";

    private final GoogleCalendarConfig googleCalendarConfig;
    private final RestClient restClient;

    public GoogleOAuthService(GoogleCalendarConfig googleCalendarConfig) {
        this.googleCalendarConfig = googleCalendarConfig;
        this.restClient = RestClient.builder().build();
    }

    public GoogleAuthorizationUrlResponseDTO gerarUrlAutorizacao() {
        validarConfiguracao();

        String authorizationUrl = UriComponentsBuilder
                .fromUriString(GOOGLE_AUTH_URI)
                .queryParam("client_id", googleCalendarConfig.getClientId())
                .queryParam("redirect_uri", googleCalendarConfig.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", GOOGLE_SCOPE)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build(true)
                .toUriString();

        return new GoogleAuthorizationUrlResponseDTO(
                authorizationUrl,
                googleCalendarConfig.getRedirectUri(),
                GOOGLE_SCOPE
        );
    }

    public GoogleTokenResponseDTO trocarCodePorToken(String code) {
        validarConfiguracao();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", googleCalendarConfig.getClientId());
        formData.add("client_secret", googleCalendarConfig.getClientSecret());
        formData.add("redirect_uri", googleCalendarConfig.getRedirectUri());
        formData.add("grant_type", "authorization_code");

        try {
            Map<String, Object> response = restClient.post()
                    .uri(GOOGLE_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("access_token") == null) {
                throw new RuntimeException("Resposta invalida ao trocar o code por token");
            }

            return new GoogleTokenResponseDTO(
                    response.get("access_token").toString(),
                    response.get("refresh_token") != null ? response.get("refresh_token").toString() : null,
                    response.get("token_type") != null ? response.get("token_type").toString() : null,
                    response.get("expires_in") instanceof Number number ? number.longValue() : null,
                    response.get("scope") != null ? response.get("scope").toString() : null
            );
        } catch (RestClientException e) {
            throw new RuntimeException("Falha ao trocar o code pelo token do Google", e);
        }
    }

    public boolean isConfigured() {
        return googleCalendarConfig.isEnabled()
                && googleCalendarConfig.getClientId() != null
                && !googleCalendarConfig.getClientId().isBlank()
                && googleCalendarConfig.getClientSecret() != null
                && !googleCalendarConfig.getClientSecret().isBlank()
                && googleCalendarConfig.getRedirectUri() != null
                && !googleCalendarConfig.getRedirectUri().isBlank();
    }

    public URI gerarRedirecionamentoFrontend(GoogleTokenResponseDTO tokenResponse) {
        return UriComponentsBuilder.fromUriString(googleCalendarConfig.getFrontendUrl())
                .fragment(UriComponentsBuilder.newInstance()
                        .queryParam("google_oauth", "success")
                        .queryParam("google_access_token", tokenResponse.accessToken())
                        .queryParamIfPresent("google_refresh_token", java.util.Optional.ofNullable(tokenResponse.refreshToken()))
                        .queryParamIfPresent("google_token_type", java.util.Optional.ofNullable(tokenResponse.tokenType()))
                        .queryParamIfPresent("google_expires_in", java.util.Optional.ofNullable(tokenResponse.expiresIn()))
                        .queryParamIfPresent("google_scope", java.util.Optional.ofNullable(tokenResponse.scope()))
                        .build()
                        .getQuery())
                .build(true)
                .toUri();
    }

    public URI gerarRedirecionamentoErro(String errorMessage) {
        return UriComponentsBuilder.fromUriString(googleCalendarConfig.getFrontendUrl())
                .fragment(UriComponentsBuilder.newInstance()
                        .queryParam("google_oauth", "error")
                        .queryParam("google_oauth_error", errorMessage)
                        .build()
                        .getQuery())
                .build(true)
                .toUri();
    }

    private void validarConfiguracao() {
        if (!isConfigured()) {
            throw new RuntimeException("Google Calendar OAuth nao configurado. Defina GOOGLE_CALENDAR_CLIENT_ID, GOOGLE_CALENDAR_CLIENT_SECRET e GOOGLE_CALENDAR_REDIRECT_URI.");
        }
    }
}
