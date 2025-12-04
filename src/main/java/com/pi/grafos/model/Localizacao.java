package com.pi.grafos.model;

import com.pi.grafos.model.enums.TipoLocalizacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "localizacao")
@Getter
@Setter
public class Localizacao {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) -> O CSV já tem IDs fixos que as ruas usam
    private Long idLocal;

    private String nome;

    // Identificador para a cidade -- Caso for cadastrar mais de uma cidade/grafo
    @ManyToOne
    @JoinColumn(name = "id_cidade", nullable = false)
    private Cidade cidade;

    // Define se é BASE, HOSPITAL ou apenas BAIRRO
    @Enumerated(EnumType.STRING)
    private TipoLocalizacao tipo;

    // Ambulancias
    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Ambulancia> ambulancias = new ArrayList<>();

    // Localização de incidentes
    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Ocorrencia> ocorrencias = new ArrayList<>();

    // Conexões para as ruas

    @OneToMany(mappedBy = "origem", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Rua> ruasSaindo = new ArrayList<>();

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Rua> ruasChegando = new ArrayList<>();

    // Passar para o Services depois
    public List<Rua> getAdjacencias() {
        List<Rua> todas = new ArrayList<>();
        if(ruasSaindo != null) todas.addAll(ruasSaindo);
        if(ruasChegando != null) todas.addAll(ruasChegando);
        return todas;
    }

    // ToString para aparecer bonito no ComboBox
    @Override
    public String toString() {
        return nome;
    }
}