package com.climb.api.service;

import com.climb.api.model.Empresa;
import com.climb.api.model.PermissaoCodigo;
import com.climb.api.model.Proposta;
import com.climb.api.model.Usuario;
import com.climb.api.model.dto.PropostaRequestDTO;
import com.climb.api.model.dto.PropostaResponseDTO;
import com.climb.api.repository.EmpresaRepository;
import com.climb.api.repository.PropostaRepository;
import com.climb.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropostaServiceTest {

    @Mock
    private PropostaRepository repository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RbacService rbacService;

    @InjectMocks
    private PropostaService propostaService;

    private Empresa empresa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setIdEmpresa(10L);

        usuario = new Usuario();
        usuario.setId(20L);
    }

    @Test
    void deveCriarPropostaComUrl() {
        PropostaRequestDTO dto = new PropostaRequestDTO(10L, 20L, "ABERTA", "https://exemplo.com/proposta/1", LocalDate.of(2026, 5, 13));

        when(rbacService.temPermissao(20L, PermissaoCodigo.PROPOSTA_CRUD)).thenReturn(true);
        when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Proposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropostaResponseDTO response = propostaService.criar(dto);

        assertEquals("https://exemplo.com/proposta/1", response.url());
        assertEquals(LocalDate.of(2026, 5, 13), response.dataCriacao());
    }

    @Test
    void deveAtualizarPropostaComUrl() {
        Proposta existente = new Proposta();
        existente.setIdProposta(1L);
        existente.setEmpresa(empresa);
        existente.setUsuario(usuario);
        existente.setStatus("ABERTA");
        existente.setUrl(null);
        existente.setDataCriacao(LocalDate.of(2026, 1, 1));

        PropostaRequestDTO dto = new PropostaRequestDTO(10L, 20L, "EM_ANALISE", "https://exemplo.com/proposta/atualizada", null);

        when(rbacService.temPermissao(20L, PermissaoCodigo.PROPOSTA_CRUD)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Proposta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropostaResponseDTO response = propostaService.atualizar(1L, dto);

        assertEquals("https://exemplo.com/proposta/atualizada", response.url());
        assertEquals(LocalDate.of(2026, 1, 1), response.dataCriacao());
    }

    @Test
    void deveRetornarUrlNulaQuandoNaoInformada() {
        Proposta proposta = new Proposta();
        proposta.setIdProposta(1L);
        proposta.setEmpresa(empresa);
        proposta.setUsuario(usuario);
        proposta.setStatus("ABERTA");
        proposta.setUrl(null);

        when(repository.findById(1L)).thenReturn(Optional.of(proposta));

        PropostaResponseDTO response = propostaService.buscarPorId(1L);

        assertNull(response.url());
    }
}