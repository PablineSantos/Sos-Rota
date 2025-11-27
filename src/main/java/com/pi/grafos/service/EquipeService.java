package com.pi.grafos.service;

import org.springframework.stereotype.Service;

import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.repository.EquipeRepository;

@Service
public class EquipeService {

    private final EquipeRepository repository;

    public EquipeService(EquipeRepository repository){
        this.repository = repository;
    }

    public Equipe cadastrarEquipe(String nome){
        Equipe newEquipe = new Equipe();

        newEquipe.setNomeEquipe(nome);

        return repository.save(newEquipe);
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