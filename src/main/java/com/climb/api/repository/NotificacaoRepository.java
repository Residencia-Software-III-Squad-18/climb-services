package com.climb.api.repository;

import com.climb.api.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuario_IdOrderByDataEnvioDesc(Long usuarioId);

    Optional<Notificacao> findByIdNotificacaoAndUsuario_Id(Long idNotificacao, Long usuarioId);

}