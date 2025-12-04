package com.pi.grafos.model;

import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ambulancias")
@Getter
@Setter
public class Ambulancia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAmbulancia;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private Boolean isAtivo;

    @Enumerated(EnumType.STRING)
    private TipoAmbulancia tipoAmbulancia;

    @Enumerated(EnumType.STRING)
    private AmbulanciaStatus statusAmbulancia;

    @ManyToOne
    @JoinColumn(name = "idUnidade")
    private Localizacao unidade;

    // --- RELAÇÃO 1:N (Uma Ambulância -> Muitas Equipes) ---
    // 'mappedBy' indica que quem manda na relação é a classe Equipe
    @OneToMany(mappedBy = "ambulancia", fetch = FetchType.EAGER) // EAGER para carregar as equipes na visualização
    @ToString.Exclude
    private List<Equipe> equipes = new ArrayList<>();

    // Método auxiliar para o visual
    public String getNomesEquipesFormatado() {
        if (equipes == null || equipes.isEmpty()) return "Nenhuma equipe vinculada";
        StringBuilder sb = new StringBuilder();
        for (Equipe e : equipes) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getNomeEquipe());
        }
        return sb.toString();
    }

    
}
