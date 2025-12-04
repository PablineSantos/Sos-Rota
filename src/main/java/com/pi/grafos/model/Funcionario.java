package com.pi.grafos.model;

import com.pi.grafos.model.enums.Cargos;
import jakarta.persistence.*;
import lombok.Data; // @Data gera Getters, Setters, Equals, HashCode...

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
@Data
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFuncionario;

    private String nomeFuncionario;

    @Enumerated(EnumType.STRING)
    private Cargos cargo;

    private String email;
    private String telefone;

    // O "mappedBy" diz que quem manda na relação é a variável "membros" lá na classe Equipe
    @ManyToMany(mappedBy = "membros")
    private List<Equipe> equipes = new ArrayList<>();
}