package com.pi.grafos.model.enums;

public enum Cargos {
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro"),
    CONDUTOR("Condutor");

    private final String cargo;

    Cargos(String cargo) {
        this.cargo = cargo;
    }

    public String getcargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return cargo;
    }
}
