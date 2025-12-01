package com.pi.grafos.model.enums;

public enum TipoAmbulancia {
    BASICA("Básica"),
    UTI("UTI");

    private final String descricao;

    TipoAmbulancia(String descricao) {
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