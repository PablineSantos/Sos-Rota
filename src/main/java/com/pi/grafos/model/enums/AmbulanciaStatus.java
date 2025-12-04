package com.pi.grafos.model.enums;

public enum AmbulanciaStatus {
    DISPONIVEL("Disponível"),
    EM_ATENDIMENTO("Em Atendimento"),
    EM_MANUTENCAO("Em Manutenção"),
    INDISPONIVEL("Indisponível");

    private final String descricao;

    AmbulanciaStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}