package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long>{

    Optional<Cidade> findById(Long id);
    
    List<Cidade> findByNomeCidadeIgnoreCaseLike(String nome);

}