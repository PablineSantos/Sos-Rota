package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.TipoLocalizacao;

@Repository
public interface LocalizacaoRepository {

    Optional<Localizacao> findByIdLocal(Long id);

    List<Localizacao> findByNome(String nome);

    List<Localizacao> findByTipoLocalizacao(TipoLocalizacao tipo);
    
}
