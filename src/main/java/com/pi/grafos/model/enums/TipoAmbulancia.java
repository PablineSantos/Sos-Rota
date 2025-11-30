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

    // ✅ Adicionado método para converter String em TipoAmbulancia
    public static TipoAmbulancia fromDescricao(String descricao) {
        for (TipoAmbulancia t : TipoAmbulancia.values()) {
            if (t.getDescricao().equalsIgnoreCase(descricao) ||
                descricao.toUpperCase().contains(t.name())) {
                return t;
            }
        }
        throw new IllegalArgumentException("TipoAmbulancia inválido: " + descricao);
    }
}
