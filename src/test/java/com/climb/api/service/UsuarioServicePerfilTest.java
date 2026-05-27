package com.climb.api.service;

import com.climb.api.model.Cargo;
import com.climb.api.model.Usuario;
import com.climb.api.model.enums.PerfilUsuario;
import com.climb.api.model.dto.UsuarioRequestDTO;
import com.climb.api.model.dto.UsuarioResponseDTO;
import com.climb.api.repository.CargoRepository;
import com.climb.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServicePerfilTest {

    @Mock private UsuarioRepository repository;
    @Mock private CargoRepository cargoRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private UsuarioService service;

    private Cargo cargo;

    @BeforeEach
    void setUp() {
        cargo = new Cargo();
        cargo.setId(1L);
        cargo.setNome("Analista de BPO Financeiro");
    }

    private UsuarioRequestDTO buildRequest(String perfil) {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("João Silva");
        dto.setCpf("12345678900");
        dto.setEmail("joao@climb.com");
        dto.setContato("11999999999");
        dto.setSenha("senha123");
        dto.setCargoId(1L);
        dto.setPerfil(perfil);
        return dto;
    }

    private Usuario savedUsuario(PerfilUsuario perfil) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNomeCompleto("João Silva");
        u.setCpf("12345678900");
        u.setEmail("joao@climb.com");
        u.setContato("11999999999");
        u.setSituacao("ATIVO");
        u.setCargo(cargo);
        u.setPerfil(perfil);
        u.setSenhaHash("hash");
        return u;
    }

    @Test
    void criar_comPerfilGestor_salvaPerfil() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(repository.save(any())).thenReturn(savedUsuario(PerfilUsuario.GESTOR));

        UsuarioResponseDTO result = service.criar(buildRequest("GESTOR"));

        assertThat(result.getPerfil()).isEqualTo("GESTOR");
    }

    @Test
    void criar_semPerfil_aplicaAnalistaDefault() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(repository.save(any())).thenReturn(savedUsuario(PerfilUsuario.ANALISTA));

        UsuarioResponseDTO result = service.criar(buildRequest(null));

        assertThat(result.getPerfil()).isEqualTo("ANALISTA");
    }

    @Test
    void criar_comPerfilInvalido_lancaExcecao() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        assertThatThrownBy(() -> service.criar(buildRequest("SUPERADMIN")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Perfil inválido")
                .hasMessageContaining("ADMIN, GESTOR, ANALISTA");
    }

    @Test
    void atualizar_comPerfil_atualizaPerfil() {
        Usuario existente = savedUsuario(PerfilUsuario.ANALISTA);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByCpf(anyString())).thenReturn(Optional.of(existente));
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(existente));
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(repository.save(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            return u;
        });

        UsuarioRequestDTO dto = buildRequest("ADMIN");
        UsuarioResponseDTO result = service.atualizar(1L, dto);

        assertThat(result.getPerfil()).isEqualTo("ADMIN");
    }

    @Test
    void atualizar_semPerfil_mantemPerfilAtual() {
        Usuario existente = savedUsuario(PerfilUsuario.GESTOR);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByCpf(anyString())).thenReturn(Optional.of(existente));
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(existente));
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioRequestDTO dto = buildRequest(null);
        UsuarioResponseDTO result = service.atualizar(1L, dto);

        assertThat(result.getPerfil()).isEqualTo("GESTOR");
    }

    @Test
    void listar_retornaPerfilEmCadaItem() {
        Usuario u1 = savedUsuario(PerfilUsuario.ADMIN);
        Usuario u2 = savedUsuario(PerfilUsuario.ANALISTA);
        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<UsuarioResponseDTO> result = service.listar();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPerfil()).isEqualTo("ADMIN");
        assertThat(result.get(1).getPerfil()).isEqualTo("ANALISTA");
    }
}
