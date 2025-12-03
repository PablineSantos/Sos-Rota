package com.pi.grafos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.repository.EquipeRepository;
import com.pi.grafos.repository.FuncionarioRepository;

@Service
public class EquipeService {

    private final EquipeRepository repository;
    private final FuncionarioRepository fRepository;

    public EquipeService(EquipeRepository repository, FuncionarioRepository fRepository){
        this.repository = repository;
        this.fRepository = fRepository;
    }

    public Equipe cadastrarEquipe(String nome, List<Funcionario> funcionarios) {
    // 1. Cria a nova equipe
    Equipe newEquipe = new Equipe();
        newEquipe.setNomeEquipe(nome);
        
        // 2. Salva a equipe primeiro (para gerar o ID)
        newEquipe = repository.save(newEquipe);
        
        // 3. Atualiza cada funcionário para referenciar esta equipe
        for (Funcionario funcionario : funcionarios) {
            funcionario.setEquipe(newEquipe);
        }
        
        // 4. Salva os funcionários atualizados
        fRepository.saveAll(funcionarios);
        
        // 5. Atualiza a lista de membros da equipe
        newEquipe.setMembros(funcionarios);
        
        return newEquipe;
    }

    public void addMembro(Equipe equipe, Funcionario funcionario) {
        
        if (equipe.getMembros().size() >= equipe.getMaxMembros()) {
            throw new IllegalStateException(
                "Equipe atingiu o número máximo de membros: " + equipe.getMaxMembros()
            );
        }
    
        funcionario.setEquipe(equipe);
        equipe.getMembros().add(funcionario);
    }
}