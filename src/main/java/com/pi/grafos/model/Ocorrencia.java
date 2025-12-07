package com.pi.grafos.model;

import com.pi.grafos.model.enums.OcorrenciaStatus;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ocorrencias")
@Getter
@Setter
public class Ocorrencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOcorrencia;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "idLocal")
    private Localizacao local;

    @ManyToOne
    @JoinColumn(name = "idTipoOcorrencia")
    private TipoOcorrencia tipoOcorrencia;

    @Enumerated(EnumType.STRING)
    private OcorrenciaStatus gravidade;

    @ManyToOne
    @JoinColumn(name = "id_ambulancia_despachada")
    private Ambulancia ambulancia;

    public Ambulancia getAmbulancia() 
    { return ambulancia; }
    public void setAmbulancia(Ambulancia ambulancia) { this.ambulancia = ambulancia; }

    private LocalDateTime dataHoraChamado; 
    
    private LocalDateTime dataHoraChegada; 
    
    private LocalDateTime dataHoraFinalizacao;

    public Ocorrencia() {
        this.dataHoraChamado = LocalDateTime.now();
    }
}
