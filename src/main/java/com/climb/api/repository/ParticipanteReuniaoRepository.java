package com.climb.api.repository;

import com.climb.api.model.ParticipanteReuniao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipanteReuniaoRepository extends JpaRepository<ParticipanteReuniao, Long> {

    List<ParticipanteReuniao> findByReuniao_Id(Long reuniaoId);

}