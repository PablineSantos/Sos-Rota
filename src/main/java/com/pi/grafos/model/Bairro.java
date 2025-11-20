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
import lombok.ToString;

@Entity
@Table(name = "bairro")
@Data
public class Bairro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBairro;

    private String nomeBairro;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Ocorrencia> ocorrencias = new ArrayList<>();
    

}
