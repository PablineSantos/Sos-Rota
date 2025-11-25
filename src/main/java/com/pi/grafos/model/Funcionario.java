package com.pi.grafos.model;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "funcionarios",

    // Forma de rejeitar membros de equipe com funções duplicadas
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"equipe_id", "funcao"})
    }
)
@Data
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFuncionario;

    private String nomeFuncionario;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo")
    private Cargos cargo;

    @ManyToOne
    @JoinColumn(name = "idEquipe")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "idAmbulancia")
    private Ambulancia ambulancia;
}
