package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.model.enums.Turno;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByIdFuncionario(Long id);

    List<Funcionario> findByNomeFuncionario(String nome);


    // Busca na lista de equipes (Plural + Containing)
    List<Funcionario> findByEquipesContaining(Equipe equipe);

    List<Funcionario> findByCargo(Cargos cargo);

    // Busca quem não tem equipe (Lista Vazia)
    List<Funcionario> findByEquipesIsEmpty();

    // Query personalizada para validar turno
    @Query("SELECT f FROM Funcionario f WHERE f.idFuncionario NOT IN " +
            "(SELECT m.idFuncionario FROM Equipe e JOIN e.membros m WHERE e.turno = :turno)")
    List<Funcionario> findDisponiveisPorTurno(@Param("turno") Turno turno);
}