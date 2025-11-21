package com.pi.grafos.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class LeituraGrafo {

    @Value("classpath:ruas_conexoes.csv")
    private Resource recurso;

    public ConstruirGrafo carregarGrafo() {
        ConstruirGrafo grafo = new ConstruirGrafo();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(recurso.getInputStream()))) {

            String line = br.readLine(); 
            line = br.readLine();        

            while (line != null) {
                String[] vet = line.split(",");
                grafo.addAresta(
                    Integer.parseInt(vet[1]), // origem
                    Integer.parseInt(vet[2]), // destino
                    Double.parseDouble(vet[3]) // distância
                );
                line = br.readLine();
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo CSV", e);
        }

        return grafo;
    }
}