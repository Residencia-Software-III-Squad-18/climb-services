package com.climb.api.service;

import com.climb.api.model.ParticipanteReuniao;
import com.climb.api.model.Reuniao;
import com.climb.api.model.Usuario;
import com.climb.api.model.Empresa;
import com.climb.api.model.dto.ReuniaoAgendamentoRequestDTO;
import com.climb.api.model.dto.ReuniaoDisponibilidadeResponseDTO;
import com.climb.api.model.dto.ReuniaoListItemDTO;
import com.climb.api.repository.EmpresaRepository;
import com.climb.api.repository.ParticipanteReuniaoRepository;
import com.climb.api.repository.ReuniaoRepository;
import com.climb.api.repository.UsuarioRepository;
import com.google.api.services.calendar.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReuniaoService {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoService.class);
    private static final int DURACAO_PADRAO_MINUTOS = 60;
    private static final ZoneId ZONE = ZoneId.of("America/Fortaleza");

    private final ReuniaoRepository repository;
    private final ParticipanteReuniaoRepository participanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final GoogleCalendarService googleCalendarService;

    public ReuniaoService(ReuniaoRepository repository,
                          ParticipanteReuniaoRepository participanteRepository,
                          UsuarioRepository usuarioRepository,
                          EmpresaRepository empresaRepository,
                          GoogleCalendarService googleCalendarService) {
        this.repository = repository;
        this.participanteRepository = participanteRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.googleCalendarService = googleCalendarService;
    }

    @Transactional
    public List<ReuniaoListItemDTO> listar(Long usuarioId, String googleAccessToken) {
        List<Reuniao> reunioes = repository.findByParticipanteUsuarioId(usuarioId);
        log.info("ReuniaoService.listar - usuario={}, linhas do banco={}", usuarioId, reunioes.size());

        if (googleAccessToken == null || googleAccessToken.isBlank()) {
            List<ReuniaoListItemDTO> soBanco = reunioes.stream().map(ReuniaoListItemDTO::fromEntity).toList();
            log.info("ReuniaoService.listar - sem token Google; banco={} DTOs", soBanco.size());
            return soBanco;
        }

        List<Reuniao> filtradas = reunioes.stream()
                .filter(reuniao -> sincronizarEventoGoogle(reuniao, googleAccessToken))
                .toList();
        log.info("ReuniaoService.listar - apos sync Google com banco: {} reunioes", filtradas.size());

        Set<String> idsGoogleJaNoClimb = filtradas.stream()
                .map(Reuniao::getGoogleEventId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toCollection(HashSet::new));

        List<ReuniaoListItemDTO> resultado = new ArrayList<>(filtradas.stream()
                .map(ReuniaoListItemDTO::fromEntity)
                .toList());
        int antesExternos = resultado.size();

        try {
            Instant min = Instant.now().minus(90, ChronoUnit.DAYS);
            Instant max = Instant.now().plus(365, ChronoUnit.DAYS);
            List<Event> externos = googleCalendarService.listarEventosPrimarios(googleAccessToken, min, max);
            int add = 0;
            int skipCancel = 0;
            int skipDup = 0;

            for (Event ev : externos) {
                if (ev == null || "cancelled".equalsIgnoreCase(ev.getStatus())) {
                    skipCancel++;
                    continue;
                }
                String gid = ev.getId();
                if (gid == null || idsGoogleJaNoClimb.contains(gid)) {
                    skipDup++;
                    continue;
                }
                resultado.add(ReuniaoListItemDTO.fromGoogleEventExterno(ev));
                add++;
            }

            log.info("ReuniaoService.listar - Google: {} eventos; skip cancelados={}; skip dup/id vazio={}; adicionados={}",
                    externos.size(), skipCancel, skipDup, add);
        } catch (Exception e) {
            log.warn("ReuniaoService.listar - falha mescla Calendar: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }

        resultado.sort(Comparator
                .comparing(ReuniaoListItemDTO::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ReuniaoListItemDTO::getHora, Comparator.nullsLast(Comparator.naturalOrder())));
        log.info("ReuniaoService.listar - total resposta: {} ({} do banco + externos)", resultado.size(), antesExternos);
        return resultado;
    }

    public Reuniao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reuniao nao encontrada"));
    }

    public Reuniao buscarPorIdDoUsuario(Long id, Long usuarioId) {
        Reuniao reuniao = buscarPorId(id);
        validarParticipante(reuniao.getIdReuniao(), usuarioId);
        return reuniao;
    }

    public List<Reuniao> listarPorEmpresa(Long empresaId) {
        return repository.findByEmpresa_IdEmpresa(empresaId);
    }

    public List<Reuniao> listarPorEmpresaDoUsuario(Long empresaId, Long usuarioId) {
        return repository.findByEmpresaIdAndParticipanteUsuarioId(empresaId, usuarioId);
    }

    @Transactional
    public Reuniao criar(Reuniao reuniao, Long usuarioId, String accessToken) throws Exception {
        validarReuniao(reuniao);
        Usuario criador = buscarUsuario(usuarioId);
        reuniao.setCriador(criador);
        validarDisponibilidadeOuFalhar(
                usuarioId,
                reuniao.getData(),
                reuniao.getHora(),
                DURACAO_PADRAO_MINUTOS,
                reuniao.getEmpresa().getIdEmpresa(),
                null,
                accessToken);

        Reuniao salva = repository.save(reuniao);
        vincularParticipante(salva, usuarioId);

        if (accessToken == null || accessToken.isBlank()) {
            log.info("Reuniao {} criada sem integracao com Google Calendar por ausencia de token", salva.getIdReuniao());
            return salva;
        }

        try {
            String googleEventId = googleCalendarService.criarEvento(salva, accessToken);
            salva.setGoogleEventId(googleEventId);
            repository.save(salva);
        } catch (Exception e) {
            log.warn("Falha ao criar evento no Google Calendar para reuniao {}: {}", salva.getIdReuniao(), e.getMessage());
        }

        return salva;
    }

    @Transactional
    public Reuniao criarAgendamento(ReuniaoAgendamentoRequestDTO dto, Long usuarioId, String accessToken) throws Exception {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa nao encontrada"));

        Reuniao reuniao = new Reuniao();
        reuniao.setTitulo(dto.getTitulo());
        reuniao.setPauta(dto.getPauta());
        reuniao.setData(dto.getData());
        reuniao.setHora(dto.getHora());
        reuniao.setPresencial(dto.getPresencial());
        reuniao.setLocal(dto.getLocal());
        reuniao.setEmpresa(empresa);
        reuniao.setStatus(dto.getStatus());

        validarReuniao(reuniao);
        validarDisponibilidadeOuFalhar(
                usuarioId,
                dto.getData(),
                dto.getHora(),
                duracaoMinutos(dto.getDuracaoMinutos()),
                dto.getEmpresaId(),
                null,
                accessToken);

        return criar(reuniao, usuarioId, accessToken);
    }

    @Transactional
    public Reuniao atualizar(Long id, Reuniao atualizada, Long usuarioId, String accessToken) {
        Reuniao reuniao = buscarPorId(id);
        validarCriador(reuniao, usuarioId);

        reuniao.setTitulo(atualizada.getTitulo());
        reuniao.setEmpresa(atualizada.getEmpresa());
        reuniao.setData(atualizada.getData());
        reuniao.setHora(atualizada.getHora());
        reuniao.setPresencial(atualizada.getPresencial());
        reuniao.setLocal(atualizada.getLocal());
        reuniao.setPauta(atualizada.getPauta());
        reuniao.setStatus(atualizada.getStatus());
        validarReuniao(reuniao);
        validarDisponibilidadeOuFalhar(
                usuarioId,
                reuniao.getData(),
                reuniao.getHora(),
                DURACAO_PADRAO_MINUTOS,
                reuniao.getEmpresa().getIdEmpresa(),
                reuniao.getIdReuniao(),
                accessToken);

        Reuniao salva = repository.save(reuniao);

        if (accessToken != null && !accessToken.isBlank() && salva.getGoogleEventId() != null) {
            try {
                googleCalendarService.atualizarEvento(salva, accessToken);
            } catch (Exception e) {
                log.warn("Falha ao atualizar evento no Google Calendar para reuniao {}: {}", salva.getIdReuniao(), e.getMessage());
            }
        }

        return salva;
    }

    @Transactional
    public void deletar(Long id, Long usuarioId, String accessToken) {
        Reuniao reuniao = buscarPorId(id);
        validarCriador(reuniao, usuarioId);
        if (
                accessToken != null &&
                !accessToken.isBlank() &&
                reuniao.getGoogleEventId() != null &&
                !reuniao.getGoogleEventId().isBlank()
        ) {
            try {
                googleCalendarService.deletarEvento(reuniao.getGoogleEventId(), accessToken);
            } catch (Exception e) {
                log.warn("Falha ao excluir evento no Google Calendar para reuniao {}: {}", reuniao.getIdReuniao(), e.getMessage());
            }
        }
        participanteRepository.deleteByReuniao_IdReuniao(reuniao.getIdReuniao());
        repository.delete(reuniao);
    }

    public ReuniaoDisponibilidadeResponseDTO verificarDisponibilidade(Long usuarioId,
                                                                       LocalDate data,
                                                                       LocalTime hora,
                                                                       Integer duracaoMinutos,
                                                                       Long empresaId,
                                                                       Long ignorarReuniaoId,
                                                                       String googleAccessToken) {
        int duracao = duracaoMinutos(duracaoMinutos);
        LocalTime fim = hora.plusMinutes(duracao);

        ReuniaoDisponibilidadeResponseDTO response = new ReuniaoDisponibilidadeResponseDTO();
        response.setData(data);
        response.setHoraInicio(hora);
        response.setHoraFim(fim);
        response.setDuracaoMinutos(duracao);

        List<ReuniaoDisponibilidadeResponseDTO.ConflitoDTO> conflitos = new ArrayList<>();
        Set<String> chaves = new HashSet<>();

        for (Reuniao reuniao : repository.findByParticipanteUsuarioIdAndData(usuarioId, data)) {
            adicionarConflitoBancoSeNecessario(conflitos, chaves, reuniao, "USUARIO", hora, fim, ignorarReuniaoId);
        }

        if (empresaId != null) {
            for (Reuniao reuniao : repository.findByEmpresa_IdEmpresaAndData(empresaId, data)) {
                adicionarConflitoBancoSeNecessario(conflitos, chaves, reuniao, "EMPRESA", hora, fim, ignorarReuniaoId);
            }
        }

        adicionarConflitosGoogle(conflitos, chaves, data, hora, fim, ignorarReuniaoId, googleAccessToken);

        response.setConflitos(conflitos);
        response.setDisponivel(conflitos.isEmpty());
        return response;
    }

    private void validarDisponibilidadeOuFalhar(Long usuarioId,
                                                LocalDate data,
                                                LocalTime hora,
                                                Integer duracaoMinutos,
                                                Long empresaId,
                                                Long ignorarReuniaoId,
                                                String googleAccessToken) {
        ReuniaoDisponibilidadeResponseDTO disponibilidade = verificarDisponibilidade(
                usuarioId,
                data,
                hora,
                duracaoMinutos,
                empresaId,
                ignorarReuniaoId,
                googleAccessToken);

        if (Boolean.FALSE.equals(disponibilidade.getDisponivel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario indisponivel para agendamento");
        }
    }

    private void validarReuniao(Reuniao reuniao) {
        if (reuniao.getTitulo() == null || reuniao.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titulo e obrigatorio");
        }
        if (reuniao.getEmpresa() == null || reuniao.getEmpresa().getIdEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa e obrigatoria");
        }
        if (reuniao.getData() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data e obrigatoria");
        }
        if (reuniao.getHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horario e obrigatorio");
        }
        if (reuniao.getStatus() == null || reuniao.getStatus().isBlank()) {
            reuniao.setStatus("AGENDADA");
        }
    }

    private void vincularParticipante(Reuniao reuniao, Long usuarioId) {
        if (participanteRepository.existsByReuniao_IdReuniaoAndUsuario_Id(reuniao.getIdReuniao(), usuarioId)) {
            return;
        }

        Usuario usuario = buscarUsuario(usuarioId);

        ParticipanteReuniao participante = new ParticipanteReuniao();
        participante.setReuniao(reuniao);
        participante.setUsuario(usuario);
        participanteRepository.save(participante);
    }

    private void validarParticipante(Long reuniaoId, Long usuarioId) {
        if (!participanteRepository.existsByReuniao_IdReuniaoAndUsuario_Id(reuniaoId, usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reuniao nao pertence ao usuario autenticado");
        }
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    private void validarCriador(Reuniao reuniao, Long usuarioId) {
        if (reuniao.getCriador() == null || !Objects.equals(reuniao.getCriador().getId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente o criador pode alterar esta reuniao");
        }
    }

    private boolean sincronizarEventoGoogle(Reuniao reuniao, String accessToken) {
        if (reuniao.getGoogleEventId() == null || reuniao.getGoogleEventId().isBlank()) {
            return true;
        }

        try {
            if (googleCalendarService.eventoExiste(reuniao.getGoogleEventId(), accessToken)) {
                return true;
            }

            participanteRepository.deleteByReuniao_IdReuniao(reuniao.getIdReuniao());
            repository.delete(reuniao);
            log.info("Reuniao {} removida localmente porque o evento Google foi excluido", reuniao.getIdReuniao());
            return false;
        } catch (Exception e) {
            log.warn("Falha ao sincronizar evento Google da reuniao {}: {}", reuniao.getIdReuniao(), e.getMessage());
            return true;
        }
    }

    private void adicionarConflitoBancoSeNecessario(List<ReuniaoDisponibilidadeResponseDTO.ConflitoDTO> conflitos,
                                                    Set<String> chaves,
                                                    Reuniao reuniao,
                                                    String tipo,
                                                    LocalTime inicio,
                                                    LocalTime fim,
                                                    Long ignorarReuniaoId) {
        if (Objects.equals(reuniao.getIdReuniao(), ignorarReuniaoId) || reuniao.getHora() == null) {
            return;
        }

        LocalTime inicioExistente = reuniao.getHora();
        LocalTime fimExistente = inicioExistente.plusMinutes(DURACAO_PADRAO_MINUTOS);
        if (!horariosSobrepostos(inicio, fim, inicioExistente, fimExistente)) {
            return;
        }

        String chave = "CLIMB:" + reuniao.getIdReuniao() + ":" + tipo;
        if (!chaves.add(chave)) {
            return;
        }

        ReuniaoDisponibilidadeResponseDTO.ConflitoDTO conflito = new ReuniaoDisponibilidadeResponseDTO.ConflitoDTO();
        conflito.setOrigem("CLIMB");
        conflito.setTipo(tipo);
        conflito.setReuniaoId(reuniao.getIdReuniao());
        conflito.setGoogleEventId(reuniao.getGoogleEventId());
        conflito.setTitulo(reuniao.getTitulo());
        conflito.setHoraInicio(inicioExistente);
        conflito.setHoraFim(fimExistente);
        conflitos.add(conflito);
    }

    private void adicionarConflitosGoogle(List<ReuniaoDisponibilidadeResponseDTO.ConflitoDTO> conflitos,
                                          Set<String> chaves,
                                          LocalDate data,
                                          LocalTime inicio,
                                          LocalTime fim,
                                          Long ignorarReuniaoId,
                                          String googleAccessToken) {
        if (googleAccessToken == null || googleAccessToken.isBlank()) {
            return;
        }

        String googleEventIdIgnorado = ignorarReuniaoId != null
                ? repository.findById(ignorarReuniaoId).map(Reuniao::getGoogleEventId).orElse(null)
                : null;

        Instant timeMin = ZonedDateTime.of(data, inicio, ZONE).toInstant();
        Instant timeMax = ZonedDateTime.of(data, fim, ZONE).toInstant();

        try {
            for (Event event : googleCalendarService.listarEventosPrimarios(googleAccessToken, timeMin, timeMax)) {
                if (event == null || "cancelled".equalsIgnoreCase(event.getStatus())) {
                    continue;
                }
                if (googleEventIdIgnorado != null && googleEventIdIgnorado.equals(event.getId())) {
                    continue;
                }

                ZonedDateTime inicioEvento = inicioEventoGoogle(event);
                ZonedDateTime fimEvento = fimEventoGoogle(event, inicioEvento);
                if (inicioEvento == null || fimEvento == null || !inicioEvento.toLocalDate().equals(data)) {
                    continue;
                }

                LocalTime inicioGoogle = inicioEvento.toLocalTime().withNano(0);
                LocalTime fimGoogle = fimEvento.toLocalTime().withNano(0);
                if (!horariosSobrepostos(inicio, fim, inicioGoogle, fimGoogle)) {
                    continue;
                }

                String chave = "GOOGLE:" + event.getId();
                if (!chaves.add(chave)) {
                    continue;
                }

                ReuniaoDisponibilidadeResponseDTO.ConflitoDTO conflito = new ReuniaoDisponibilidadeResponseDTO.ConflitoDTO();
                conflito.setOrigem("GOOGLE_CALENDAR");
                conflito.setTipo("USUARIO");
                conflito.setGoogleEventId(event.getId());
                conflito.setTitulo(event.getSummary() != null ? event.getSummary() : "(Sem titulo)");
                conflito.setHoraInicio(inicioGoogle);
                conflito.setHoraFim(fimGoogle);
                conflitos.add(conflito);
            }
        } catch (Exception e) {
            log.warn("Falha ao validar disponibilidade no Google Calendar: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private boolean horariosSobrepostos(LocalTime inicioA, LocalTime fimA, LocalTime inicioB, LocalTime fimB) {
        return inicioA.isBefore(fimB) && fimA.isAfter(inicioB);
    }

    private int duracaoMinutos(Integer duracaoMinutos) {
        if (duracaoMinutos == null) {
            return DURACAO_PADRAO_MINUTOS;
        }
        if (duracaoMinutos < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duracao deve ser maior que zero");
        }
        return duracaoMinutos;
    }

    private ZonedDateTime inicioEventoGoogle(Event event) {
        if (event.getStart() == null) {
            return null;
        }
        if (event.getStart().getDateTime() != null) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.getStart().getDateTime().getValue()), ZONE);
        }
        if (event.getStart().getDate() != null) {
            String raw = event.getStart().getDate().toString();
            if (raw != null && raw.length() >= 10) {
                return LocalDate.parse(raw.substring(0, 10)).atStartOfDay(ZONE);
            }
        }
        return null;
    }

    private ZonedDateTime fimEventoGoogle(Event event, ZonedDateTime inicio) {
        if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()), ZONE);
        }
        if (event.getEnd() != null && event.getEnd().getDate() != null) {
            String raw = event.getEnd().getDate().toString();
            if (raw != null && raw.length() >= 10) {
                return LocalDate.parse(raw.substring(0, 10)).atStartOfDay(ZONE);
            }
        }
        return inicio != null ? inicio.plusMinutes(DURACAO_PADRAO_MINUTOS) : null;
    }
}
