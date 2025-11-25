package com.pi.grafos.service;

import org.springframework.stereotype.Service;

//@Service
public class Aresta {

	int destino;
	private double distancia;

	public Aresta(int destino, double distancia) {
		super();
		this.destino = destino;
		this.distancia = distancia;
	}

	public int getDestino() {
		return destino;
	}

	public double getDistancia() {
		return distancia;
	}

}
