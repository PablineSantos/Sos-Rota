package com.pi.grafos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ruas")
@Data
public class Rua {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRua;

    private String nomeRua;

    private Double distancia;

    @ManyToOne
    @JoinColumn(name = "idCidade", nullable = false)
    private Cidade cidade;
    
    @ManyToOne
    @JoinColumn(name = "origem_id", nullable = false)
    private Localizacao origem;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Localizacao destino;
}