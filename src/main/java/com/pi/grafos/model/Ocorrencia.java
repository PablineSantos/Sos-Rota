package com.pi.grafos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ocorrencias")
@Data
public class Ocorrencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOcorrencia;

    @ManyToOne
    @JoinColumn(name = "idLocal")
    private Bairro local;

    private int prioridadeOcorrência;

}
