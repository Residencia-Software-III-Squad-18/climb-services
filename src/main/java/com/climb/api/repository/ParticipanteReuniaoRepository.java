package com.climb.api.repository;

import com.climb.api.model.ParticipanteReuniao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipanteReuniaoRepository extends JpaRepository<ParticipanteReuniao, Long> {

    List<ParticipanteReuniao> findByReuniao_IdReuniao(Long reuniaoId);

    List<ParticipanteReuniao> findByUsuario_Id(Long usuarioId);

    boolean existsByReuniao_IdReuniaoAndUsuario_Id(Long reuniaoId, Long usuarioId);

    void deleteByReuniao_IdReuniao(Long reuniaoId);

}
