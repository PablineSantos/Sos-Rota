package com.pi.grafos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cidades")
@Getter
@Setter
public class Cidade {

    // Construtores
    public Cidade() {} // Construtor vazio obrigatório pro JPA

    public Cidade(String nome) { // O construtor que você quer usar
        this.nomeCidade = nome;
    }

    // Relacionamento com todos os bairros da cidade
    // Relacionamento com todas as ruas da cidade
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCidade;

    private String nomeCidade;

    @OneToMany(mappedBy = "cidade")
    @ToString.Exclude
    private List<Localizacao> locais = new ArrayList<>();

    @OneToMany(mappedBy = "cidade")
    @ToString.Exclude
    private List<Rua> ruas = new ArrayList<>();

}
