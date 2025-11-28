package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Rua;

@Repository
public interface RuaRepository extends JpaRepository<Rua, Long>{

    Optional<Rua> findByIdRua(Long id);

    List<Rua> findByNomeRuaIgnoreCase(String nome);

    List<Rua> findByCidade(Cidade cidade);

}
