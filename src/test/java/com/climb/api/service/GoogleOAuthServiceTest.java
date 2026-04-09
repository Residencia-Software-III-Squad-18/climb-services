package com.climb.api.service;

import com.climb.api.model.OAuth2PendingRegistration;
import com.climb.api.model.OAuthProvider;
import com.climb.api.model.Usuario;
import com.climb.api.model.UsuarioOAuth;
import com.climb.api.model.dto.CompleteGoogleRegistrationRequestDTO;
import com.climb.api.model.dto.GoogleOAuthResolveResponseDTO;
import com.climb.api.model.dto.LoginResponseDTO;
import com.climb.api.repository.OAuth2PendingRegistrationRepository;
import com.climb.api.repository.UsuarioOAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTest {

    @Mock
    private UsuarioOAuthRepository usuarioOAuthRepository;

    @Mock
    private OAuth2PendingRegistrationRepository pendingRegistrationRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private GoogleOAuthService googleOAuthService;

    private Usuario usuario;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@teste.com");
        usuario.setSituacao("ATIVO");

        loginResponse = new LoginResponseDTO();
        loginResponse.setAccessToken("access-token");
        loginResponse.setRefreshToken("refresh-token");
        loginResponse.setExpiresIn(900000);
    }

    @Test
    void deveLogarQuandoJaExisteVinculoGoogle() {
        UsuarioOAuth vinculo = new UsuarioOAuth();
        vinculo.setUsuario(usuario);

        when(usuarioOAuthRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.of(vinculo));
        when(authenticationService.gerarRespostaLogin(usuario)).thenReturn(loginResponse);

        GoogleOAuthResolveResponseDTO response = googleOAuthService
                .resolverLoginGoogle("google-sub", "usuario@teste.com", "Usuario", "https://img");

        assertEquals(GoogleOAuthService.STATUS_LOGIN_SUCCESS, response.getStatus());
        assertEquals("access-token", response.getLogin().getAccessToken());
        verify(usuarioService, never()).buscarPorEmail(anyString());
    }

    @Test
    void deveRetornarCadastroPendenteQuandoNaoExisteConta() {
        when(usuarioOAuthRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.empty());
        when(usuarioService.buscarPorEmail("novo@teste.com")).thenReturn(null);
        when(pendingRegistrationRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, "google-sub"))
                .thenReturn(Optional.empty());

        GoogleOAuthResolveResponseDTO response = googleOAuthService
                .resolverLoginGoogle("google-sub", "novo@teste.com", "Novo Usuario", "https://img");

        assertEquals(GoogleOAuthService.STATUS_CADASTRO_PENDENTE, response.getStatus());
        assertNotNull(response.getPendingToken());

        ArgumentCaptor<OAuth2PendingRegistration> captor = ArgumentCaptor.forClass(OAuth2PendingRegistration.class);
        verify(pendingRegistrationRepository).save(captor.capture());
        assertEquals("novo@teste.com", captor.getValue().getEmail());
        assertEquals(OAuthProvider.GOOGLE, captor.getValue().getProvider());
    }

    @Test
    void deveConcluirCadastroGoogleCriandoUsuarioEVinculo() {
        CompleteGoogleRegistrationRequestDTO dto = new CompleteGoogleRegistrationRequestDTO();
        dto.setPendingToken("pending-token");
        dto.setCpf("12345678900");
        dto.setContato("85999999999");
        dto.setSenha("SenhaForte123!");
        dto.setCargoId(1L);

        OAuth2PendingRegistration pending = new OAuth2PendingRegistration();
        pending.setProvider(OAuthProvider.GOOGLE);
        pending.setProviderUserId("google-sub");
        pending.setEmail("novo@teste.com");
        pending.setNome("Novo Usuario");
        pending.setAvatarUrl("https://img");
        pending.setTokenUnico("pending-token");
        pending.setExpiraEm(LocalDateTime.now().plusMinutes(15));
        pending.setConsumido(false);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setId(2L);
        novoUsuario.setEmail("novo@teste.com");
        novoUsuario.setSituacao("ATIVO");

        when(pendingRegistrationRepository.findByTokenUnicoAndConsumidoFalse("pending-token"))
                .thenReturn(Optional.of(pending));
        when(usuarioService.buscarPorEmail("novo@teste.com")).thenReturn(null);
        when(usuarioService.criarViaGoogle("Novo Usuario", "novo@teste.com", "12345678900", "85999999999", "SenhaForte123!", 1L))
                .thenReturn(novoUsuario);
        when(authenticationService.gerarRespostaLogin(novoUsuario)).thenReturn(loginResponse);

        LoginResponseDTO response = googleOAuthService.concluirCadastro(dto);

        assertEquals("access-token", response.getAccessToken());
        verify(usuarioOAuthRepository).save(any(UsuarioOAuth.class));
        verify(pendingRegistrationRepository).save(any(OAuth2PendingRegistration.class));
    }

    @Test
    void deveFalharQuandoCadastroPendenteExpirou() {
        CompleteGoogleRegistrationRequestDTO dto = new CompleteGoogleRegistrationRequestDTO();
        dto.setPendingToken("pending-token");

        OAuth2PendingRegistration pending = new OAuth2PendingRegistration();
        pending.setExpiraEm(LocalDateTime.now().minusMinutes(1));
        pending.setConsumido(false);

        when(pendingRegistrationRepository.findByTokenUnicoAndConsumidoFalse("pending-token"))
                .thenReturn(Optional.of(pending));

        assertThrows(RuntimeException.class, () -> googleOAuthService.concluirCadastro(dto));
    }
}
