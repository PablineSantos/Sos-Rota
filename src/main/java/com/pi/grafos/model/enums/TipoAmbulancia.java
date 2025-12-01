package com.pi.grafos.model.enums;

public enum TipoAmbulancia {
    BASICA("Suporte Básico"),
    UTI("UTI Móvel");

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

    public static TipoAmbulancia fromDescricao(String descricao) {
        for (TipoAmbulancia t : values()) {
            if (t.getDescricao().equalsIgnoreCase(descricao)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Descrição inválida: " + descricao);
    }
}