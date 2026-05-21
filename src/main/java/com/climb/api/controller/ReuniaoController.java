package com.climb.api.controller;

import com.climb.api.model.Reuniao;
import com.climb.api.model.dto.ReuniaoAgendamentoRequestDTO;
import com.climb.api.model.dto.ReuniaoDisponibilidadeResponseDTO;
import com.climb.api.model.dto.ReuniaoListItemDTO;
import com.climb.api.service.ReuniaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reunioes")
public class ReuniaoController {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoController.class);

    private final ReuniaoService service;

    public ReuniaoController(ReuniaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReuniaoListItemDTO> listar(
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) {
        boolean comGoogle = googleAccessToken != null && !googleAccessToken.isBlank();
        log.info("GET /reunioes - header Google: presente={}, tamanho={}",
                comGoogle, comGoogle ? googleAccessToken.length() : 0);
        List<ReuniaoListItemDTO> out = service.listar(usuarioIdAutenticado(authentication), googleAccessToken);
        log.info("GET /reunioes - retornando {} itens", out.size());
        return out;
    }

    @GetMapping("/minhas")
    public List<ReuniaoListItemDTO> listarMinhas(
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) {
        return service.listar(usuarioIdAutenticado(authentication), googleAccessToken);
    }

    @GetMapping("/disponibilidade")
    public ReuniaoDisponibilidadeResponseDTO verificarDisponibilidade(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam(required = false) Integer duracaoMinutos,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long ignorarReuniaoId,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) {
        return service.verificarDisponibilidade(
                usuarioIdAutenticado(authentication),
                data,
                hora,
                duracaoMinutos,
                empresaId,
                ignorarReuniaoId,
                googleAccessToken);
    }

    @GetMapping("/{id}")
    public Reuniao buscarPorId(@PathVariable Long id, Authentication authentication) {
        return service.buscarPorIdDoUsuario(id, usuarioIdAutenticado(authentication));
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Reuniao> listarPorEmpresa(@PathVariable Long empresaId, Authentication authentication) {
        return service.listarPorEmpresaDoUsuario(empresaId, usuarioIdAutenticado(authentication));
    }

    @PostMapping
    public Reuniao criar(
            @RequestBody Reuniao reuniao,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) throws Exception {
        return service.criar(reuniao, usuarioIdAutenticado(authentication), googleAccessToken);
    }

    @PostMapping("/agendamento")
    public Reuniao criarAgendamento(
            @Valid @RequestBody ReuniaoAgendamentoRequestDTO agendamento,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) throws Exception {
        return service.criarAgendamento(agendamento, usuarioIdAutenticado(authentication), googleAccessToken);
    }

    @PutMapping("/{id}")
    public Reuniao atualizar(
            @PathVariable Long id,
            @RequestBody Reuniao atualizada,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) {
        return service.atualizar(id, atualizada, usuarioIdAutenticado(authentication), googleAccessToken);
    }

    @DeleteMapping("/{id}")
    public void deletar(
            @PathVariable Long id,
            @RequestHeader(value = "X-Google-Access-Token", required = false) String googleAccessToken,
            Authentication authentication) {
        service.deletar(id, usuarioIdAutenticado(authentication), googleAccessToken);
    }

    private Long usuarioIdAutenticado(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Long usuarioId)) {
            throw new RuntimeException("Usuario autenticado nao identificado");
        }
        return usuarioId;
    }
}
