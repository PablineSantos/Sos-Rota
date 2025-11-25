package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;

@Repository
public interface OcorrenciaRepository {
    
    Optional<Ocorrencia> findByIdOcorrencia(Long id);

    List<Ocorrencia> findByLocal(Localizacao local);

    List<Ocorrencia> findByTipoOcorrencia(TipoOcorrencia tipo);

}
