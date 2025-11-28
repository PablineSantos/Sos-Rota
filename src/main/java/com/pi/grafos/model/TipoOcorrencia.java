package com.pi.grafos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "tipo_ocorrencia")
@Getter
@Setter
public class TipoOcorrencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoOcorrencia;

    private String nomeTipoOcorrencia;

    @OneToMany(mappedBy = "tipoOcorrencia", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Ocorrencia> ocorrencias = new ArrayList<>();

}
