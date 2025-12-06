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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.RuaRepository;

@Service
public class grafosService {

    private final RuaRepository ruaRepository;

    public grafosService(RuaRepository ruaRepository) {
        this.ruaRepository = ruaRepository;
    }

    // =========================
    // ESTRUTURAS DO GRAFO
    // =========================
    public static class Aresta {
        private int destino;
        private double distancia;

        public Aresta(int destino, double distancia) {
            this.destino = destino;
            this.distancia = distancia;
        }
        public int getDestino() { return destino; }
        public double getDistancia() { return distancia; }
    }

    public static class ConstruirGrafo {
        private Map<Integer, List<Aresta>> caminho = new HashMap<>();

        public void addAresta(Long origem, Long destino, double distancia) {
            caminho.putIfAbsent(origem.intValue(), new ArrayList<>()); 
            caminho.putIfAbsent(destino.intValue(), new ArrayList<>());
            // Grafo não-direcionado (vai e volta)
            caminho.get(origem.intValue()).add(new Aresta(destino.intValue(), distancia));
            caminho.get(destino.intValue()).add(new Aresta(origem.intValue(), distancia));
        }
        public Map<Integer, List<Aresta>> getCaminho() { return caminho; }
    }

    public static class SugestaoAmbulancia {
        private Ambulancia ambulancia;
        private double distanciaKm;

        public SugestaoAmbulancia(Ambulancia ambulancia, double distanciaKm) {
            this.ambulancia = ambulancia;
            this.distanciaKm = distanciaKm;
        }
        public Ambulancia getAmbulancia() { return ambulancia; }
        public double getDistanciaKm() { return distanciaKm; }
    }

    // =========================
    // DIJKSTRA
    // =========================
    public static List<Integer> menorCaminho(ConstruirGrafo grafo, int origem, int destino) {
        Map<Integer, Double> distancia = new HashMap<>();
        Map<Integer, Integer> anterior = new HashMap<>();
        PriorityQueue<double[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        // Inicializa distâncias
        for (int v : grafo.getCaminho().keySet()) {
            distancia.put(v, Double.MAX_VALUE);
        }
        
        // Verifica se origem e destino existem no grafo para evitar loop infinito ou erro
        if (!distancia.containsKey(origem) || !distancia.containsKey(destino)) {
            System.err.println("Dijkstra falhou: Origem (" + origem + ") ou Destino (" + destino + ") não estão no grafo de conexões.");
            return new LinkedList<>();
        }

        distancia.put(origem, 0.0);
        fila.add(new double[]{origem, 0.0});

        while (!fila.isEmpty()) {
            double[] top = fila.poll();
            int atual = (int) top[0];
            double distAtual = top[1];

            if (distAtual > distancia.get(atual)) continue;

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
        if (distancia.get(destino) == Double.MAX_VALUE) return caminho; // Sem rota

        Integer passo = destino;
        while (passo != null) {
            caminho.addFirst(passo);
            passo = anterior.get(passo);
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

    // =========================
    // CARREGAMENTO DO GRAFO (AGORA ROBUSTO)
    // =========================
    @Transactional(readOnly = true) 
    public ConstruirGrafo carregarGrafoDiretoDoBanco() {
        ConstruirGrafo grafo = new ConstruirGrafo();
        
        // CORREÇÃO: Pega todas as ruas diretamente do repositório de Ruas
        List<Rua> todasRuas = ruaRepository.findAll();
        
        System.out.println(">>> Carregando Grafo: Encontradas " + todasRuas.size() + " conexões de ruas no banco.");

        for (Rua rua : todasRuas) {
            if (rua.getOrigem() != null && rua.getDestino() != null) {
                grafo.addAresta(
                    rua.getOrigem().getIdLocal(),       
                    rua.getDestino().getIdLocal(),      
                    rua.getDistancia()   
                );
            }
        }
        return grafo;
    }

    // =========================
    // SUGERIR AMBULÂNCIAS
    // =========================
    @Transactional(readOnly = true)
    public List<SugestaoAmbulancia> sugerirAmbulancias(Long idBairroOcorrencia, List<Ambulancia> frotaAtiva) {
        
        // 1. Carrega o grafo garantido
        ConstruirGrafo grafo = carregarGrafoDiretoDoBanco();
        
        List<SugestaoAmbulancia> ranking = new ArrayList<>();
        int destino = idBairroOcorrencia.intValue();

        System.out.println(">>> Calculando rotas para destino ID: " + destino);

        for (Ambulancia amb : frotaAtiva) {
            // Filtro extra de segurança (caso o filtro da View tenha deixado passar algo)
            if (amb.getUnidade() != null && Boolean.TRUE.equals(amb.getIsAtivo())) {
                
                int origem = amb.getUnidade().getIdLocal().intValue();
                System.out.println("   > Testando Ambulância " + amb.getPlaca() + " na base ID: " + origem);

                // Se a ambulância já está no local da ocorrência
                if (origem == destino) {
                    ranking.add(new SugestaoAmbulancia(amb, 0.0));
                    System.out.println("     -> Está no local! Distância 0.");
                    continue;
                }

                // Calcula Dijkstra
                List<Integer> rota = menorCaminho(grafo, origem, destino);
                
                if (!rota.isEmpty()) {
                    double distancia = calculaDistanciaTotal(grafo, rota);
                    ranking.add(new SugestaoAmbulancia(amb, distancia));
                    System.out.println("     -> Rota encontrada: " + distancia + "km");
                } else {
                    System.out.println("     -> SEM ROTA POSSÍVEL (Verifique conectividade do grafo).");
                }
            }
        }

        ranking.sort(Comparator.comparingDouble(SugestaoAmbulancia::getDistanciaKm));
        return ranking;
    }

    // Método auxiliar legado para teste de texto (pode manter se usar em testes unitários)
    public ConstruirGrafo carregarGrafoDeTexto(String csv) {
        ConstruirGrafo grafo = new ConstruirGrafo();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new java.io.ByteArrayInputStream(csv.getBytes())))) {
            String line = br.readLine(); 
            line = br.readLine();        
            while (line != null) { line = br.readLine(); }
        } catch (Exception e) {}
        return grafo;
    }
}