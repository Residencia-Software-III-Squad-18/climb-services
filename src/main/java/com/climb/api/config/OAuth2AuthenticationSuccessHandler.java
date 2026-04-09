package com.climb.api.config;

import com.climb.api.model.dto.ApiResponse;
import com.climb.api.model.dto.GoogleOAuthResolveResponseDTO;
import com.climb.api.service.GoogleOAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public static final String GOOGLE_LINK_USER_ID_SESSION_KEY = "GOOGLE_LINK_USER_ID";

    private final GoogleOAuthService googleOAuthService;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(GoogleOAuthService googleOAuthService, ObjectMapper objectMapper) {
        this.googleOAuthService = googleOAuthService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String providerUserId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String nome = oauth2User.getAttribute("name");
        String avatarUrl = oauth2User.getAttribute("picture");

        HttpSession session = request.getSession(false);
        Long linkUserId = extractLinkUserId(session);

        try {
            GoogleOAuthResolveResponseDTO result;
            if (linkUserId != null) {
                result = googleOAuthService.vincularConta(linkUserId, providerUserId, email, nome, avatarUrl);
                clearLinkSession(session);
                writeJson(response, HttpStatus.OK, ApiResponse.ok(result, result.getMessage()));
                return;
            }

            result = googleOAuthService.resolverLoginGoogle(providerUserId, email, nome, avatarUrl);
            HttpStatus status = GoogleOAuthService.STATUS_GOOGLE_NOT_LINKED.equals(result.getStatus())
                    ? HttpStatus.CONFLICT
                    : HttpStatus.OK;
            writeJson(response, status, ApiResponse.ok(result, result.getMessage()));
        } catch (RuntimeException ex) {
            clearLinkSession(session);
            writeJson(response, HttpStatus.UNAUTHORIZED, ApiResponse.error(ex.getMessage()));
        }
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }

    private Long extractLinkUserId(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(GOOGLE_LINK_USER_ID_SESSION_KEY);
        if (value instanceof Long longValue) {
            return longValue;
        }

        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }

        return null;
    }

    private void clearLinkSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute(GOOGLE_LINK_USER_ID_SESSION_KEY);
        }
    }
}
