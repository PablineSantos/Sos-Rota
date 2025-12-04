package com.pi.grafos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String nomeEquipe;

    @Transient // Indica que esse campo não precisa virar coluna no banco
    private int maxMembros = 10;

    @Column(nullable = false)
    private String turno; // MANHÃ, TARDE, NOITE

    // Relacionamento Muitos-para-Muitos (O Dono da Relação)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "equipe_profissional",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "profissional_id")
    )
    private List<Funcionario> membros = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_ambulancia")
    private Ambulancia ambulancia;

    // --- MÉTODOS AUXILIARES DE LEITURA (Podem ficar) ---

    public List<Funcionario> getMedico() { return getMembrosPorCargo(Cargos.MEDICO); }
    public List<Funcionario> getCondutor() { return getMembrosPorCargo(Cargos.CONDUTOR); }
    public List<Funcionario> getEnfermeiro() { return getMembrosPorCargo(Cargos.ENFERMEIRO); }

    private List<Funcionario> getMembrosPorCargo(Cargos cargo) {
        if (membros == null) return List.of();

        return membros.stream()
                .filter(m -> m.getCargo() == cargo)
                .collect(Collectors.toList());
    }

    // toString para aparecer corretamente no ComboBox
    @Override
    public String toString() {
        return nomeEquipe;
    }

    // REMOVIDO: public void addMembro(...)
    // Motivo: Esse método manual causava o erro de tipagem e conflitava com a lógica do Service.
}