package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario,Long>{
    
    Optional<Funcionario> findByIdFuncionario(Long id);

    List<Funcionario> findByNomeFuncionario(String nome);

    List<Funcionario> findByCargoFuncionario(Cargos cargo);

    List<Funcionario> findByEquipeFuncionario(String equipe);

}
