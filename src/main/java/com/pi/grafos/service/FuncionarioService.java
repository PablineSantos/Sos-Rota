package com.pi.grafos.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    @Autowired
    public FuncionarioService(FuncionarioRepository repository){
        this.repository = repository;
    }

    public List<Funcionario> findAll(){
        return repository.findAll();
    }

    // Metodo único para Salvar (Novo) ou Atualizar (Editar)
    @Transactional
    public void salvarOuAtualizar(Long id, String nome, Cargos cargo, String email, String telefone) {
        // 1. Validações de Regra de Negócio
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do funcionário é obrigatório.");
        }

        // 2. Validação de Cargo
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo é obrigatório.");
        }

        // 3. Validação de Contato (CORREÇÃO AQUI)
        // Verificamos se ESTÁ vazio (null ou string em branco)
        boolean emailVazio = (email == null || email.trim().isEmpty());
        boolean telefoneVazio = (telefone == null || telefone.trim().isEmpty());

        // A regra é: Se AMBOS estiverem vazios, lança erro.
        // Se pelo menos um tiver valor, passa.
        if (emailVazio && telefoneVazio) {
            throw new IllegalArgumentException("É obrigatório informar pelo menos um contato (E-mail ou Telefone).");
        }

        // Aqui entra a lógica de validar formato de email ou telefone se quisesse)

        Funcionario funcionario;

        if (id == null) {
            // Novo Cadastro
            funcionario = new Funcionario();
        } else {
            // Edição: Busca o existente
            funcionario = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado para edição."));
        }

        // Atualiza dados
        funcionario.setNomeFuncionario(nome);
        funcionario.setCargo(cargo);
        funcionario.setEmail(email);
        funcionario.setTelefone(telefone);

        repository.save(funcionario);
    }

    @Transactional
    public void deleteFuncionario(Long id){
        Objects.requireNonNull(id, "ID não pode ser nulo");
        if (!repository.existsById(id)) {
            throw new RuntimeException("Funcionário não encontrado para exclusão.");
        }
        repository.deleteById(id);
    }

    public List<Funcionario> findAllNoTeams() {
        return repository.findByEquipeIsNull(); // Supondo que você criou esse método no repo
    }
}