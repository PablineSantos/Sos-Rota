package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.OcorrenciaStatus;


@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    
    Optional<Ocorrencia> findByIdOcorrencia(Long id);

    List<Ocorrencia> findByLocal(Localizacao local);

    List<Ocorrencia> findByTipoOcorrencia(TipoOcorrencia tipo);

    List<Ocorrencia> findByGravidade(OcorrenciaStatus gravidade);

}