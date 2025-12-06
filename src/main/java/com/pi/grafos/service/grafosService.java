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
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTANTE

import com.pi.grafos.model.Ambulancia;
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
    // NOVA CLASSE PARA RETORNAR A SUGESTÃO
    // =========================
    public static class SugestaoAmbulancia {
        private Ambulancia ambulancia;
        private double distanciaKm;

        public SugestaoAmbulancia(Ambulancia ambulancia, double distanciaKm) {
            this.ambulancia = ambulancia;
            this.distanciaKm = distanciaKm;
        }

        public Ambulancia getAmbulancia() { return ambulancia; }
        public double getDistanciaKm() { return distanciaKm; }
        
        @Override
        public String toString() {
            return String.format("%s - %.1f Km", ambulancia.getPlaca(), distanciaKm);
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

            // Verifica se o nó atual tem vizinhos antes de iterar
            if (grafo.getCaminho().containsKey(atual)) {
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
        }

        LinkedList<Integer> caminho = new LinkedList<>();
        Integer atual = destino;

        // Se não houver caminho até o destino (distancia ainda é infinita ou não tem anterior), retorna vazio ou trata
        if (distancia.get(destino) == Double.MAX_VALUE) {
            return caminho; // Caminho vazio
        }

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

            if (g.getCaminho().containsKey(atual)) {
                for (Aresta a : g.getCaminho().get(atual)) {
                    if (a.getDestino() == prox) {
                        soma += a.getDistancia();
                        break;
                    }
                }
            }
        }
        return soma;
    }

    // =========================
    // CARREGAR GRAFO DO CSV (BANCO DE DADOS)
    // =========================
    @Value("classpath:ruas_conexoes.csv")
    private Resource recurso;

    // ADICIONADO @Transactional para evitar erro de LazyInitializationException ao pegar as ruas
    @Transactional(readOnly = true) 
    public ConstruirGrafo carregarGrafo(Long idCidade) {

        // Busca a cidade no banco
        Cidade cidade = cidadeRepository.findByIdCidade(idCidade)
                .orElseThrow(() -> new RuntimeException("Cidade não encontrada"));

        // Cria o grafo
        ConstruirGrafo grafo = new ConstruirGrafo();

        // Adiciona todas as ruas ao grafo
        // O @Transactional mantem a sessão aberta aqui
        cidade.getRuas().forEach(rua -> {
            if (rua.getOrigem() != null && rua.getDestino() != null) {
                grafo.addAresta(
                    rua.getOrigem().getIdLocal(),       
                    rua.getDestino().getIdLocal(),      
                    rua.getDistancia().doubleValue()    
                );
            }
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
                // Lógica antiga simplificada...
                line = br.readLine();
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler conteúdo CSV", e);
        }

        return grafo;
    }

    // =========================
    // MÉTODO DE SUGESTÃO (IMPLEMENTAÇÃO MINIMA)
    // =========================
    @Transactional(readOnly = true)
    public List<SugestaoAmbulancia> sugerirAmbulancias(Long idBairroOcorrencia, List<Ambulancia> frotaAtiva) {
        // 1. Carrega o grafo da cidade (Fixando ID 1 - Cidália)
        ConstruirGrafo grafo = carregarGrafo(1L); 

        List<SugestaoAmbulancia> ranking = new ArrayList<>();
        int destino = idBairroOcorrencia.intValue();

        for (Ambulancia amb : frotaAtiva) {
            // Verifica se a ambulância tem uma base/unidade definida
            if (amb.getUnidade() != null && Boolean.TRUE.equals(amb.getIsAtivo())) {
                
                int origem = amb.getUnidade().getIdLocal().intValue();

                // Calcula o caminho
                List<Integer> rota = menorCaminho(grafo, origem, destino);
                
                // Se a lista rota não estiver vazia, calcula a distância
                if (!rota.isEmpty()) {
                    double distancia = calculaDistanciaTotal(grafo, rota);
                    
                    // Adiciona na lista se achou caminho válido
                    if (distancia > 0 || origem == destino) {
                        ranking.add(new SugestaoAmbulancia(amb, distancia));
                    }
                }
            }
        }

        // Ordena: Menor distância primeiro
        ranking.sort(Comparator.comparingDouble(SugestaoAmbulancia::getDistanciaKm));

        return ranking;
    }
}