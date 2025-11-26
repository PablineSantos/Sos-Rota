package com.pi.grafos.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository){
        this.repository = repository;
    }

    public Funcionario cadastrarFuncionario(String nome, Cargos cargo){
        Funcionario newFuncionario = new Funcionario();

        newFuncionario.setNomeFuncionario(nome);
        newFuncionario.setCargo(cargo);

        return repository.save(newFuncionario);
    }

    public void deleteFuncionario(Long id){
        Objects.requireNonNull(id, "id cannot be null");
        repository.deleteById(id);
    }

    public void editFuncionario(Long id, String nome, Cargos cargo){
        Funcionario func = repository.findByIdFuncionario(id)
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        func.setNomeFuncionario(nome);
        func.setCargo(cargo);

        repository.save(func);
    }

    public List<Funcionario> findByCargos(Cargos cargo){
        List<Funcionario> lista = repository.findByCargo(cargo);
        if (lista.isEmpty()) {
            throw new RuntimeException("Nenhum Funcionário encontrado.");
        }
        return lista;
    }

    public List<Funcionario> findAll(){
        return repository.findAll();
    }

    public List<Funcionario> findAllNoTeams(){
        return repository.findByEquipeIsNull();
    }

}