package com.pi.grafos.model;

import com.pi.grafos.model.enums.Cargos;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    private Cargos cargo;

    @ManyToOne
    @JoinColumn(name = "idEquipe")
    private Equipe equipe;

}
