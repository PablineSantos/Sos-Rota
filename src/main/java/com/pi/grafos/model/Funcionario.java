package com.pi.grafos.model;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "funcionarios",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"equipe_id", "cargo"})
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
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "idAmbulancia")
    private Ambulancia ambulancia;
}
