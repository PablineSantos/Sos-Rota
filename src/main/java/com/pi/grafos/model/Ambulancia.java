package com.pi.grafos.model;

import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ambulancias")
@Data
public class Ambulancia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAmbulancia;

    private String placa;

    @Enumerated(EnumType.STRING)
    private TipoAmbulancia tipoAmbulancia;

    @Enumerated(EnumType.STRING)
    private AmbulanciaStatus statusambulancia;

    @ManyToOne
    @JoinColumn(name = "idUnidade")
    private Unidades unidade;
}
