package com.pi.grafos.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.repository.CidadeRepository;

@Service
public class grafosService {

    @Autowired
    private CidadeRepository cidadeRepository;

    // =========================
    // CLASSE ARESTA
    // =========================
    public static class Aresta {
        private int destino;
        private double distancia;

        public Aresta(int destino, double distancia) {
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

    // =========================
    // CLASSE CONSTRUIR GRAFO
    // (ANTES ESTAVA EXTERNA – AGORA ESTÁ AQUI)
    // =========================
    public static class ConstruirGrafo {

        private Map<Integer, List<Aresta>> caminho = new HashMap<>();

        public void addAresta(Long origem, Long destino, double distancia) {
        caminho.putIfAbsent(origem.intValue(), new ArrayList<>()); 
        caminho.putIfAbsent(destino.intValue(), new ArrayList<>());

        caminho.get(origem.intValue()).add(new Aresta(destino.intValue(), distancia));
        caminho.get(destino.intValue()).add(new Aresta(origem.intValue(), distancia));
    }

        public Map<Integer, List<Aresta>> getCaminho() {
            return caminho;
        }
    }

    // =========================
    // DIJKSTRA – MENOR CAMINHO
    // =========================
    public static List<Integer> menorCaminho(ConstruirGrafo grafo, int origem, int destino) {

        Map<Integer, Double> distancia = new HashMap<>();
        Map<Integer, Integer> anterior = new HashMap<>();

        PriorityQueue<double[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        for (int v : grafo.getCaminho().keySet()) {
            distancia.put(v, Double.MAX_VALUE);
        }

        distancia.put(origem, 0.0);
        fila.add(new double[]{origem, 0.0});

        while (!fila.isEmpty()) {

            int atual = (int) fila.poll()[0];

            for (Aresta ar : grafo.getCaminho().get(atual)) {

                int vizinho = ar.getDestino();
                double novaDist = distancia.get(atual) + ar.getDistancia();

                if (novaDist < distancia.get(vizinho)) {
                    distancia.put(vizinho, novaDist);
                    anterior.put(vizinho, atual);

                    fila.add(new double[]{vizinho, novaDist});
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

    // =========================
    // CALCULAR DISTÂNCIA TOTAL DO CAMINHO
    // =========================
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

    // =========================
    // CARREGAR GRAFO DO CSV
    // =========================
    @Value("classpath:ruas_conexoes.csv")
    private Resource recurso;

public ConstruirGrafo carregarGrafo(Long idCidade) {

    // Busca a cidade no banco
    Cidade cidade = cidadeRepository.findByIdCidade(idCidade)
            .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

    // Cria o grafo
    ConstruirGrafo grafo = new ConstruirGrafo();

    // Adiciona todas as ruas ao grafo
    cidade.getRuas().forEach(rua -> {
        grafo.addAresta(
            rua.getOrigem().getIdLocal(),       // Long
            rua.getDestino().getIdLocal(),      // Long
            rua.getDistancia().doubleValue()    // double
        );
    });

    return grafo;
}


    public ConstruirGrafo carregarGrafoDeTexto(String csv) {

    ConstruirGrafo grafo = new ConstruirGrafo();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(
            new java.io.ByteArrayInputStream(csv.getBytes())))) {

        String line = br.readLine(); 
        line = br.readLine();        

        while (line != null) {
            String[] vet = line.split(",");

            int origem = Integer.parseInt(vet[1]);
            int destino = Integer.parseInt(vet[2]);
            double distancia = Double.parseDouble(vet[3]);

            grafo.addAresta(origem, destino, distancia);

            line = br.readLine();
        }

    } catch (Exception e) {
        throw new RuntimeException("Erro ao ler conteúdo CSV", e);
    }

    return grafo;
}

}
