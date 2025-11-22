package com.pi.grafos.service;

import org.springframework.stereotype.Service;

// Comentei se não o programa não roda
// Essa classe não precisa ser um "@Service", pois ela é apenas um molde de dados (minha opinião)
//@Service
public class Aresta {

	private int destino;
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
