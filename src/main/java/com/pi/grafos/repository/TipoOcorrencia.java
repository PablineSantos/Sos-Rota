package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface TipoOcorrencia {
    
    Optional<TipoOcorrencia> findByIdTipoOcorrencia(Long id);

    List<TipoOcorrencia> findByNomeTipoOcorrenciaIgnoreCaseLike(String nome);

}
