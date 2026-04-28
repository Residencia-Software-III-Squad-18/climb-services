package com.climb.api.service;

import com.climb.api.config.GoogleCalendarConfig;
import com.climb.api.model.Cargo;
import com.climb.api.model.OAuth2PendingRegistration;
import com.climb.api.model.OAuthProvider;
import com.climb.api.model.Usuario;
import com.climb.api.model.UsuarioOAuth;
import com.climb.api.model.dto.CompleteGoogleRegistrationRequestDTO;
import com.climb.api.model.dto.GoogleAuthorizationUrlResponseDTO;
import com.climb.api.model.dto.GoogleOAuthResolveResponseDTO;
import com.climb.api.model.dto.GoogleTokenResponseDTO;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.model.dto.UsuarioResponseDTO;
import com.climb.api.repository.CargoRepository;
import com.climb.api.repository.OAuth2PendingRegistrationRepository;
import com.climb.api.repository.UsuarioOAuthRepository;
import com.climb.api.repository.UsuarioRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleOAuthService {

    // Status constants for OAuth2 login flow
    public static final String STATUS_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String STATUS_CADASTRO_PENDENTE = "CADASTRO_PENDENTE";
    public static final String STATUS_GOOGLE_NOT_LINKED = "GOOGLE_NOT_LINKED";
    public static final String STATUS_LINK_SUCCESS = "LINK_SUCCESS";

    // Google API endpoints
    private static final String GOOGLE_AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GOOGLE_SCOPE = "openid email profile https://www.googleapis.com/auth/calendar";

    @org.springframework.beans.factory.annotation.Value("${google.calendar.allowed-domain:}")
    private String googleAllowedDomain;

    private final GoogleCalendarConfig googleCalendarConfig;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;
    private final JwtUtil jwtUtil;
    private final RestClient restClient;
    private final UsuarioOAuthRepository usuarioOAuthRepository;
    private final OAuth2PendingRegistrationRepository pendingRegistrationRepository;
    private final AuthenticationService authenticationService;

    public GoogleOAuthService(
            GoogleCalendarConfig googleCalendarConfig,
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            CargoRepository cargoRepository,
            JwtUtil jwtUtil,
            UsuarioOAuthRepository usuarioOAuthRepository,
            OAuth2PendingRegistrationRepository pendingRegistrationRepository,
            AuthenticationService authenticationService
    ) {
        this.googleCalendarConfig = googleCalendarConfig;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.cargoRepository = cargoRepository;
        this.jwtUtil = jwtUtil;
        this.restClient = RestClient.builder().build();
        this.usuarioOAuthRepository = usuarioOAuthRepository;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.authenticationService = authenticationService;
    }

    // ==================== Google Calendar API Integration ====================

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
                .encode(StandardCharsets.UTF_8)
                .build()
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

            String googleAccessToken = response.get("access_token").toString();
            UsuarioResponseDTO usuario = autenticarUsuarioGoogle(googleAccessToken);

            return new GoogleTokenResponseDTO(
                    googleAccessToken,
                    response.get("refresh_token") != null ? response.get("refresh_token").toString() : null,
                    response.get("token_type") != null ? response.get("token_type").toString() : null,
                    response.get("expires_in") instanceof Number number ? number.longValue() : null,
                    response.get("scope") != null ? response.get("scope").toString() : null,
                    jwtUtil.generateAccessToken(usuario.getId(), usuario.getEmail()),
                    jwtUtil.generateRefreshToken(usuario.getId(), usuario.getEmail()),
                    jwtUtil.getAccessTokenExpirationTime(),
                    usuario
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(googleCalendarConfig.getFrontendUrl())
                .queryParam("google_oauth", "success")
                .queryParam("google_access_token", tokenResponse.accessToken());

        if (tokenResponse.refreshToken() != null) builder.queryParam("google_refresh_token", tokenResponse.refreshToken());
        if (tokenResponse.tokenType() != null) builder.queryParam("google_token_type", tokenResponse.tokenType());
        if (tokenResponse.expiresIn() != null) builder.queryParam("google_expires_in", tokenResponse.expiresIn());
        if (tokenResponse.scope() != null) builder.queryParam("google_scope", tokenResponse.scope());
        if (tokenResponse.appAccessToken() != null) builder.queryParam("app_access_token", tokenResponse.appAccessToken());
        if (tokenResponse.appRefreshToken() != null) builder.queryParam("app_refresh_token", tokenResponse.appRefreshToken());
        if (tokenResponse.appExpiresIn() != null) builder.queryParam("app_expires_in", tokenResponse.appExpiresIn());

        if (tokenResponse.usuario() != null) {
            builder.queryParam("app_user_id", tokenResponse.usuario().getId());
            builder.queryParam("app_user_name", tokenResponse.usuario().getNomeCompleto());
            builder.queryParam("app_user_email", tokenResponse.usuario().getEmail());
            builder.queryParam("app_user_status", tokenResponse.usuario().getSituacao());

            if (tokenResponse.usuario().getCargoNome() != null) {
                builder.queryParam("app_user_role", tokenResponse.usuario().getCargoNome());
            }
        }

        return builder.build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }

    public URI gerarRedirecionamentoErro(String errorMessage) {
        return UriComponentsBuilder.fromUriString(googleCalendarConfig.getFrontendUrl())
                .queryParam("google_oauth", "error")
                .queryParam("google_oauth_error", errorMessage)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }

    // ==================== OAuth2 Login/Registration Flow ====================

    @Transactional
    public GoogleOAuthResolveResponseDTO resolverLoginGoogle(String providerUserId,
                                                             String email,
                                                             String nome,
                                                             String avatarUrl) {
        validarDadosGoogle(providerUserId, email);
        limparPendenciasExpiradas();

        UsuarioOAuth vinculo = usuarioOAuthRepository
                .findByProviderAndProviderUserId(OAuthProvider.GOOGLE, providerUserId)
                .orElse(null);

        if (vinculo != null) {
            LoginResponseDTO login = authenticationService.gerarRespostaLogin(vinculo.getUsuario());
            GoogleOAuthResolveResponseDTO response = new GoogleOAuthResolveResponseDTO();
            response.setStatus(STATUS_LOGIN_SUCCESS);
            response.setLogin(login);
            response.setMessage("Login Google realizado com sucesso");
            return response;
        }

        Usuario usuarioExistente = usuarioService.buscarPorEmail(email);
        if (usuarioExistente != null) {
            GoogleOAuthResolveResponseDTO response = new GoogleOAuthResolveResponseDTO();
            response.setStatus(STATUS_GOOGLE_NOT_LINKED);
            response.setEmail(email);
            response.setNome(nome);
            response.setAvatarUrl(avatarUrl);
            response.setMessage("Ja existe um usuario com esse e-mail. Faca login normal para vincular a conta Google.");
            return response;
        }

        OAuth2PendingRegistration pending = pendingRegistrationRepository
                .findByProviderAndProviderUserId(OAuthProvider.GOOGLE, providerUserId)
                .orElseGet(OAuth2PendingRegistration::new);

        pending.setProvider(OAuthProvider.GOOGLE);
        pending.setProviderUserId(providerUserId);
        pending.setEmail(email);
        pending.setNome(nome);
        pending.setAvatarUrl(avatarUrl);
        pending.setTokenUnico(UUID.randomUUID().toString());
        pending.setExpiraEm(LocalDateTime.now().plusMinutes(30));
        pending.setConsumido(false);
        if (pending.getCriadoEm() == null) {
            pending.setCriadoEm(LocalDateTime.now());
        }

        pendingRegistrationRepository.save(pending);

        GoogleOAuthResolveResponseDTO response = new GoogleOAuthResolveResponseDTO();
        response.setStatus(STATUS_CADASTRO_PENDENTE);
        response.setPendingToken(pending.getTokenUnico());
        response.setEmail(email);
        response.setNome(nome);
        response.setAvatarUrl(avatarUrl);
        response.setMessage("Complete seu cadastro para finalizar o login com Google.");
        return response;
    }

    @Transactional
    public LoginResponseDTO concluirCadastro(CompleteGoogleRegistrationRequestDTO dto) {
        if (dto.getPendingToken() == null || dto.getPendingToken().isBlank()) {
            throw new RuntimeException("Pending token obrigatorio");
        }

        limparPendenciasExpiradas();

        OAuth2PendingRegistration pending = pendingRegistrationRepository
                .findByTokenUnicoAndConsumidoFalse(dto.getPendingToken())
                .orElseThrow(() -> new RuntimeException("Cadastro Google pendente nao encontrado ou expirado"));

        if (pending.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cadastro Google pendente expirado");
        }

        Usuario usuarioExistente = usuarioService.buscarPorEmail(pending.getEmail());
        if (usuarioExistente != null) {
            throw new RuntimeException("Ja existe um usuario cadastrado com esse e-mail");
        }

        Usuario usuario = usuarioService.criarViaGoogle(
                pending.getNome(),
                pending.getEmail(),
                dto.getCpf(),
                dto.getContato(),
                dto.getSenha(),
                dto.getCargoId()
        );

        UsuarioOAuth vinculo = new UsuarioOAuth();
        vinculo.setUsuario(usuario);
        vinculo.setProvider(OAuthProvider.GOOGLE);
        vinculo.setProviderUserId(pending.getProviderUserId());
        vinculo.setEmailProvider(pending.getEmail());
        vinculo.setNomeProvider(pending.getNome());
        vinculo.setAvatarUrl(pending.getAvatarUrl());
        vinculo.setVinculadoEm(LocalDateTime.now());
        usuarioOAuthRepository.save(vinculo);

        pending.setConsumido(true);
        pendingRegistrationRepository.save(pending);

        return authenticationService.gerarRespostaLogin(usuario);
    }

    @Transactional
    public GoogleOAuthResolveResponseDTO vincularConta(Long usuarioId,
                                                       String providerUserId,
                                                       String email,
                                                       String nome,
                                                       String avatarUrl) {
        validarDadosGoogle(providerUserId, email);

        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        authenticationService.validarUsuarioAtivo(usuario, "Usuario nao encontrado");

        UsuarioOAuth vinculoExistente = usuarioOAuthRepository
                .findByProviderAndProviderUserId(OAuthProvider.GOOGLE, providerUserId)
                .orElse(null);

        if (vinculoExistente != null && !vinculoExistente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Esta conta Google ja esta vinculada a outro usuario");
        }

        UsuarioOAuth vinculo = usuarioOAuthRepository
                .findByUsuarioIdAndProvider(usuarioId, OAuthProvider.GOOGLE)
                .orElseGet(UsuarioOAuth::new);

        if (vinculo.getId() != null && !vinculo.getProviderUserId().equals(providerUserId)) {
            throw new RuntimeException("Este usuario ja possui outra conta Google vinculada");
        }

        vinculo.setUsuario(usuario);
        vinculo.setProvider(OAuthProvider.GOOGLE);
        vinculo.setProviderUserId(providerUserId);
        vinculo.setEmailProvider(email);
        vinculo.setNomeProvider(nome);
        vinculo.setAvatarUrl(avatarUrl);
        if (vinculo.getVinculadoEm() == null) {
            vinculo.setVinculadoEm(LocalDateTime.now());
        }
        usuarioOAuthRepository.save(vinculo);

        GoogleOAuthResolveResponseDTO response = new GoogleOAuthResolveResponseDTO();
        response.setStatus(STATUS_LINK_SUCCESS);
        response.setEmail(email);
        response.setNome(nome);
        response.setAvatarUrl(avatarUrl);
        response.setMessage("Conta Google vinculada com sucesso");
        return response;
    }

    // ==================== Private Helper Methods ====================

    private void validarConfiguracao() {
        if (!isConfigured()) {
            throw new RuntimeException("Google Calendar OAuth nao configurado. Defina GOOGLE_CALENDAR_CLIENT_ID, GOOGLE_CALENDAR_CLIENT_SECRET e GOOGLE_CALENDAR_REDIRECT_URI.");
        }
    }

    private void validarDadosGoogle(String providerUserId, String email) {
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new RuntimeException("O Google nao retornou um identificador valido");
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException("O Google nao retornou um e-mail valido");
        }
    }

    private void limparPendenciasExpiradas() {
        pendingRegistrationRepository.deleteByExpiraEmBefore(LocalDateTime.now());
    }

    private UsuarioResponseDTO autenticarUsuarioGoogle(String googleAccessToken) {
        Map<String, Object> userInfo = restClient.get()
                .uri(GOOGLE_USERINFO_URI)
                .header("Authorization", "Bearer " + googleAccessToken)
                .retrieve()
                .body(Map.class);

        String email = userInfo != null && userInfo.get("email") != null ? userInfo.get("email").toString() : null;

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Google nao retornou e-mail para autenticar no sistema.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            try {
                usuario = criarUsuarioGoogle(email, userInfo);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao cadastrar novo usuario. Verifique as permissoes do banco de dados.");
            }
        }

        if (!"ATIVO".equals(usuario.getSituacao())) {
            throw new RuntimeException("Usuario do sistema esta inativo.");
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setCpf(usuario.getCpf());
        dto.setEmail(usuario.getEmail());
        dto.setContato(usuario.getContato());
        dto.setSituacao(usuario.getSituacao());

        if (usuario.getCargo() != null) {
            dto.setCargoNome(usuario.getCargo().getNome());
        }

        return dto;
    }

    private Usuario criarUsuarioGoogle(String email, Map<String, Object> userInfo) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (googleAllowedDomain != null && !googleAllowedDomain.isBlank() && !normalizedEmail.endsWith(googleAllowedDomain)) {
            throw new RuntimeException("E-mail Google " + email + " nao pertence ao dominio permitido (" + googleAllowedDomain + ").");
        }

        Cargo cargo = cargoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cargo padrao nao encontrado para criar usuario Google."));

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(obterNomeGoogle(normalizedEmail, userInfo));
        usuario.setCpf(gerarCpfGoogle(normalizedEmail));
        usuario.setEmail(normalizedEmail);
        usuario.setContato("");
        usuario.setSenhaHash("GOOGLE_OAUTH_" + UUID.randomUUID());
        usuario.setSituacao("ATIVO");
        usuario.setCargo(cargo);

        return usuarioRepository.save(usuario);
    }

    private String obterNomeGoogle(String email, Map<String, Object> userInfo) {
        if (userInfo != null && userInfo.get("name") != null && !userInfo.get("name").toString().isBlank()) {
            return userInfo.get("name").toString();
        }

        return email.substring(0, email.indexOf('@'));
    }

    private String gerarCpfGoogle(String email) {
        long base = Integer.toUnsignedLong(email.hashCode());

        for (int offset = 0; offset < 100; offset++) {
            String cpf = String.format("%011d", (base + offset) % 100_000_000_000L);

            if (usuarioRepository.findByCpf(cpf).isEmpty()) {
                return cpf;
            }
        }

        throw new RuntimeException("Nao foi possivel gerar CPF tecnico para usuario Google.");
    }
}
