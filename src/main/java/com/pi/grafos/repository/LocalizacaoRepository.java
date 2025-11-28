package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Localizacao;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long>{

    Optional<Localizacao> findByIdLocal(Long id);

    List<Localizacao> findByNome(String nome);
    
}
