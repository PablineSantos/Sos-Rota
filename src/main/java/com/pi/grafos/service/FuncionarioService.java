package com.pi.grafos.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository){
        this.repository = repository;
    }

    public void cadastrarFuncionario(String nome, Cargos cargo){
        Funcionario newFuncionario = new Funcionario();

        newFuncionario.setNomeFuncionario(nome);
        newFuncionario.setCargo(cargo);

        repository.save(newFuncionario);
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

}
