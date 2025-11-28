package com.pi.grafos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipes")
@Getter
@Setter
public class Equipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipe;

    private String nomeEquipe;

    private int maxMembros = 10;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Funcionario> membros = new ArrayList<>();

    public List<Funcionario> getMedico() { return getMembrosPorCargo(Cargos.MEDICO); }
    public List<Funcionario> getCondutor() { return getMembrosPorCargo(Cargos.CONDUTOR); }
    public List<Funcionario> getContato() { return getMembrosPorCargo(Cargos.CONTATO); }
    public List<Funcionario> getEnfermeiro() { return getMembrosPorCargo(Cargos.ENFERMEIRO); }

    private List<Funcionario> getMembrosPorCargo(Cargos cargo) {
        if (membros == null) return List.of();
    
        return membros.stream()
                .filter(m -> m.getCargo() == cargo)
                .collect(Collectors.toList());
    }

    public void addMembro(Funcionario funcionario) {
        if (membros.size() >= maxMembros) {
            throw new IllegalStateException(
                "Equipe atingiu o número máximo de membros: " + maxMembros
            );
        }
    
        funcionario.setEquipe(this);
        membros.add(funcionario);
    }
    
}
