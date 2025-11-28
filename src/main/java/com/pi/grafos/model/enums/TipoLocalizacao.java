package com.pi.grafos.model.enums;

// Unused
public enum TipoLocalizacao {
    BAIRRO("Bairro"),
    BASE_AMBULANCIA("Unidade");

    private final String descricao;

    TipoLocalizacao(String descricao) {
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