package com.pi.grafos.model;

import java.util.ArrayList;
import java.util.List;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "equipes")
@Data
public class Equipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equipe;

    private String nomeEquipe;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL)
    private List<Funcionario> membros = new ArrayList<>();

    public Funcionario getMedico() {
        return getMembroPorCargos(Cargos.MEDICO);
    }

    public Funcionario getCondutor() {
        return getMembroPorCargos(Cargos.CONDUTOR);
    }

    public Funcionario getContato() {
        return getMembroPorCargos(Cargos.CONTATO);
    }

    public Funcionario getEnfermeiro() {
        return getMembroPorCargos(Cargos.ENFERMEIRO);
    }

    private Funcionario getMembroPorCargos(Cargos f) {
        if (membros == null) return null;
        return membros.stream()
                .filter(m -> m.getCargo() == f)
                .findFirst()
                .orElse(null);
    }
}
