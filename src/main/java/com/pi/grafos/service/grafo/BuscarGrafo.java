package com.pi.grafos.service.grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class BuscarGrafo {

	 public static List<Integer> menorCaminho(ConstruirGrafo grafo, int origem, int destino) {

	        Map<Integer, Double> distancia = new HashMap<>();
	        Map<Integer, Integer> anterior = new HashMap<>();

	        PriorityQueue<int[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

	        for (int vertice : grafo.getCaminho().keySet()) {
	            distancia.put(vertice, Double.MAX_VALUE);
	        }

	        distancia.put(origem, 0.0);
	        fila.add(new int[]{origem, 0});

	        while (!fila.isEmpty()) {

	            int atual = fila.poll()[0];

	            for (Aresta ar : grafo.getCaminho().get(atual)) {
	                int vizinho = ar.getDestino();
	                double novaDist = distancia.get(atual) + ar.getDistancia();

	                if (novaDist < distancia.get(vizinho)) {
	                    distancia.put(vizinho, novaDist);
	                    anterior.put(vizinho, atual);
	                    fila.add(new int[]{vizinho, (int) novaDist});
	                }
	            }
	        }

	        LinkedList<Integer> caminho = new LinkedList<>();

	        Integer atual = destino;
	        while (atual != null) {
	            caminho.addFirst(atual);
	            atual = anterior.get(atual);
	        }

	        return caminho;
	    }
	 public static double calculaDistanciaTotal(ConstruirGrafo g, List<Integer> caminho) {
	        double soma = 0;

	        for (int i = 0; i < caminho.size() - 1; i++) {
	            int atual = caminho.get(i);
	            int prox = caminho.get(i + 1);

	            for (Aresta a : g.getCaminho().get(atual)) {
	                if (a.getDestino() == prox) {
	                    soma += a.getDistancia();
	                    break;
	                }
	            }
	        }
	        return soma;
	    }
}
