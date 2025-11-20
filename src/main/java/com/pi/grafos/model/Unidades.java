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
import lombok.Data;

@Entity
@Table(name  = "unidades")
@Data
public class Unidades {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidade;

    private String nomeUnidade;

    @OneToMany(mappedBy = "idAmbulancia", cascade = CascadeType.ALL)
    private List<Ambulancia> ambulancias = new ArrayList<>();

}