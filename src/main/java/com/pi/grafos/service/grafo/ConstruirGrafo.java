package com.pi.grafos.service.grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ConstruirGrafo {

	private Map<Integer, List<Aresta>> caminho = new HashMap<>();

	public void addAresta(int origem, int destino, double distancia) {
		if (!caminho.containsKey(origem)) {
			caminho.put(origem, new ArrayList<Aresta>());
		}

		caminho.get(origem).add(new Aresta(destino, distancia));
		
		if (!caminho.containsKey(destino)) {
			caminho.put(destino, new ArrayList<Aresta>());
		}
		caminho.get(destino).add(new Aresta(origem, distancia));
	}

	public Map<Integer, List<Aresta>> getCaminho() {

		return caminho;
	}
}
