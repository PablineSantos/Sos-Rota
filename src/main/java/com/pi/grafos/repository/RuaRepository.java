package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Rua;

@Repository
public interface RuaRepository {

    Optional<Rua> findByIdRua(Long id);

    List<Rua> findByNomeRuaIgnoreCase(String nome);

    List<Rua> findByCidade(Cidade cidade);

}
