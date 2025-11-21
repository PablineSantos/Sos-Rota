package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.grafos.model.Equipe;

public interface EquipeRepository extends JpaRepository<Equipe,Long>{
    
    Optional<Equipe> findByIdEquipe(Long id);

    List<Equipe> findByNomeEquipe(String nome);
    
}
