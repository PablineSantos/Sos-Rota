package com.pi.grafos.controller;

import com.pi.grafos.service.AmbulanciaService;
import com.pi.grafos.service.OcorrenciaService;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DashboardController {

    // Updated method signature to accept AmbulanciaService and the extra Label
    public void iniciarServicoAtualizacao(
            OcorrenciaService ocorrenciaService, 
            AmbulanciaService ambulanciaService, 
            Label lblAlta, 
            Label lblMedia, 
            Label lblBaixa, 
            Label lblAmbulancias) { // <--- New Label parameter
        
        // 1. Create a record to hold all 4 values
        record DashboardDados(int alta, int media, int baixa, int ambulancias) {}

        ScheduledService<DashboardDados> updater = new ScheduledService<>() {
            @Override
            protected Task<DashboardDados> createTask() {
                return new Task<>() {
                    @Override
                    protected DashboardDados call() throws Exception {
                        // Safety check
                        if (ocorrenciaService == null || ambulanciaService == null) {
                            return new DashboardDados(0, 0, 0, 0);
                        }

                        // 2. Fetch data from BOTH services
                        // (These run in the background thread)
                        int a = ocorrenciaService.contarAlta();
                        int m = ocorrenciaService.contarMedia();
                        int b = ocorrenciaService.contarBaixa();
                        int amb = ambulanciaService.contarAmbulancias(); // Assuming this method exists
                        
                        return new DashboardDados(a, m, b, amb);
                    }
                };
            }
        };

        // 3. Update the UI when data arrives
        updater.setOnSucceeded(e -> {
            DashboardDados dados = updater.getValue();
            
            // Update Ocorrencia Cards
            lblAlta.setText(String.valueOf(dados.alta()));
            lblMedia.setText(String.valueOf(dados.media()));
            lblBaixa.setText(String.valueOf(dados.baixa()));

            // Update Ambulancia Panel
            lblAmbulancias.setText(String.valueOf(dados.ambulancias()));
        });

        updater.setPeriod(Duration.seconds(5));
        updater.start();
    }
}