package com.climb.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.climb.api.model.Reuniao;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    List<Reuniao> findByEmpresa_IdEmpresa(Long empresaId);

    List<Reuniao> findByEmpresa_IdEmpresaAndData(Long empresaId, LocalDate data);

    @Query("""
            select distinct p.reuniao
            from ParticipanteReuniao p
            where p.usuario.id = :usuarioId
            """)
    List<Reuniao> findByParticipanteUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("""
            select distinct p.reuniao
            from ParticipanteReuniao p
            where p.usuario.id = :usuarioId
              and p.reuniao.data = :data
            """)
    List<Reuniao> findByParticipanteUsuarioIdAndData(@Param("usuarioId") Long usuarioId,
                                                     @Param("data") LocalDate data);

    @Query("""
            select distinct p.reuniao
            from ParticipanteReuniao p
            where p.usuario.id = :usuarioId
              and p.reuniao.empresa.idEmpresa = :empresaId
            """)
    List<Reuniao> findByEmpresaIdAndParticipanteUsuarioId(@Param("empresaId") Long empresaId,
                                                          @Param("usuarioId") Long usuarioId);

}
