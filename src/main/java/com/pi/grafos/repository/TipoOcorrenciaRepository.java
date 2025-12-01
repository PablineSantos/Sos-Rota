package com.pi.grafos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.TipoOcorrencia;

@Repository
public interface TipoOcorrenciaRepository extends JpaRepository<TipoOcorrencia, Long> {
    
    List<TipoOcorrenciaRepository> findByNomeTipoOcorrenciaIgnoreCaseLike(String nome);

}
