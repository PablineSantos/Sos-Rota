package com.pi.grafos.model.enums;

public enum Turno {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");

    private final String turno;

    Turno(String turno) {
        this.turno = turno;
    }

    public String getTurno() {
        return turno;
    }

    @Override
    public String toString() {
        return turno;
    }
}