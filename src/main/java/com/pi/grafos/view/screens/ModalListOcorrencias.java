package com.pi.grafos.view.screens;

import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import com.pi.grafos.service.OcorrenciaService;

import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.util.List;

public class ModalListOcorrencias {

    // Assuming these constants exist in your class context
    private static final javafx.scene.text.Font FONTE_TITULO = new javafx.scene.text.Font("Arial", 20);
    private static final javafx.scene.text.Font FONTE_CORPO = new javafx.scene.text.Font("Arial", 14);
    private static final javafx.scene.text.Font FONTE_PEQUENA = new javafx.scene.text.Font("Arial", 12);
    private static final javafx.scene.text.Font FONTE_BOTAO2 = new javafx.scene.text.Font("Arial", 14);
    private static final Color COR_AZUL_NOTURNO = Color.web("#1E293B");

    private OcorrenciaService ocorrenciaService;

    // Call this method to open the window
    public void exibir(Stage dono, OcorrenciaService ocorrenciaService, OcorrenciaStatus statusFiltro) {
        this.ocorrenciaService = ocorrenciaService;
        
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL); // Or NONE if you want it to stay open alongside others
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- Layout Base ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 15;");
        root.setPrefWidth(550); // Slightly wider for the list
        root.setPrefHeight(700);

        // Header
        Label lblTitulo = new Label("Ocorrências Pendentes");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblSub = new Label("Filtrando por status: " + statusFiltro.toString());
        lblSub.setFont(FONTE_CORPO);
        lblSub.setTextFill(Color.web("#64748B"));

        // --- Container for the List ---
        VBox containerLista = new VBox(10);

        // 1. Fetch from Database
        List<Ocorrencia> listaOcorrencias = ocorrenciaService.findByGravidade(statusFiltro);

        if (listaOcorrencias == null || listaOcorrencias.isEmpty()) {
            containerLista.getChildren().add(criarAlertaVazio());
        } else {
            // 2. Loop through entities and create cards
            for (Ocorrencia oc : listaOcorrencias) {
                
                // --- Data Extraction ---
                
                // 1. Title: Use TipoOcorrencia (e.g., "Cardiac Arrest") or fallback to generic
                String titulo = "Ocorrência";
                if (oc.getTipoOcorrencia() != null) {
                    // Change .getNome() or .getDescricao() to match your TipoOcorrencia class fields
                    titulo = oc.getTipoOcorrencia().toString(); 
                }

                // 2. Subtitle: The specific description of this event
                String subtitulo = oc.getDescricao() != null ? oc.getDescricao() : "Sem descrição";

                // 3. Location: Extract "Bairro" or address from Localizacao entity
                String localTexto = "Local n/d";
                if (oc.getLocal() != null) {
                    // Change .getBairro() to match your Localizacao class fields
                    localTexto = oc.getLocal().getNome() != null ? oc.getLocal().getNome() : "Endereço desconhecido";
                }

                // 4. Color Logic based on 'gravidade' (OcorrenciaStatus Enum)
                String corStatus = "#3B82F6"; // Default: Blue
                String gravidadeStr = oc.getGravidade() != null ? oc.getGravidade().toString() : "N/A";

                // Adjust strings below to match your exact Enum names (e.g., "ALTA", "GRAVE", "CRITICA")
                if ("ALTA".equalsIgnoreCase(gravidadeStr) || "GRAVE".equalsIgnoreCase(gravidadeStr)) {
                    corStatus = "#EF4444"; // Red
                } else if ("MEDIA".equalsIgnoreCase(gravidadeStr)) {
                    corStatus = "#F59E0B"; // Orange
                }

                // 5. Badge: Use the ID
                Label lblId = new Label("#" + oc.getIdOcorrencia());
                
                // --- Create Card ---
                HBox card = criarCardOcorrencia(
                    titulo,          // Title -> TipoOcorrencia
                    subtitulo,       // Subtitle -> Descricao
                    corStatus,       // Color -> Gravidade Logic
                    localTexto,      // Location -> Localizacao
                    gravidadeStr,    // Gravity Label
                    lblId,           // Counter/Badge
                    oc               // The full entity for the click event
                );
                
                containerLista.getChildren().add(card);
            }
        }

        ScrollPane scroll = new ScrollPane(containerLista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Close Button
        Button btnFechar = new Button("Fechar Lista");
        btnFechar.setFont(FONTE_BOTAO2);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setPrefHeight(45);
        btnFechar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> modal.close());

        root.getChildren().addAll(lblTitulo, lblSub, scroll, btnFechar);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        
        // Center on screen
        modal.centerOnScreen();
        
        modal.showAndWait();
    }

    // --- Updated Card Method to handle the Click Logic ---
    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus, String bairro, String gravidade, Label lblContador, Ocorrencia ocorrencia) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        String estiloNormal = "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-style: solid; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #F1F5F9; -fx-border-color: " + corStatus + "; -fx-border-width: 1; -fx-border-style: solid; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";

        card.setStyle(estiloNormal);
        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e -> card.setStyle(estiloNormal));

        // --- CLICK ACTION ---
        card.setOnMouseClicked(e -> {
            System.out.println("Opening dispatch for ID: " + ocorrencia.getIdOcorrencia());
            Stage stageAtual = (Stage) card.getScene().getWindow();
            
            // HERE IS THE LINK: We open your PREVIOUS window (ModalSelecaoAmbulancia)
            // You might need to generate the logic to find suggestions here or pass null to let the next window handle it
            // Assuming ModalSelecaoAmbulancia is the class handling the specific dispatch
            // new ModalSelecaoAmbulancia().exibir(stageAtual, ocorrencia.getId(), bairro, gravidade, null, ocorrenciaService);
            
            // TIP: It's cleaner if ModalSelecaoAmbulancia fetches its own suggestions based on ID, 
            // but if you have them ready, pass them here.
        });

        Circle statusDot = new Circle(5, Color.web(corStatus));

        VBox textos = new VBox(4);
        Label lblTit = new Label(titulo);
        lblTit.setFont(FONTE_CORPO);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblSub = new Label(subtitulo);
        lblSub.setFont(FONTE_PEQUENA);
        lblSub.setStyle("-fx-text-fill: #64748B;");

        textos.getChildren().addAll(lblTit, lblSub);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblContador.setFont(FONTE_PEQUENA);
        lblContador.setMinWidth(Region.USE_PREF_SIZE); 
        lblContador.setMinHeight(Region.USE_PREF_SIZE);
        lblContador.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 12; -fx-padding: 2 8 2 8; -fx-font-weight: bold;");

        card.getChildren().addAll(statusDot, textos, spacer, lblContador);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 8; -fx-border-color: #FECACA; -fx-border-radius: 8;");

        Label lblIcon = new Label("✓"); // Checkmark instead of warning?
        lblIcon.setStyle("-fx-font-size: 30px;");
        Label lblMsg = new Label("Nenhuma ocorrência encontrada.");
        lblMsg.setFont(FONTE_BOTAO2);
        lblMsg.setTextFill(Color.web("#64748B"));

        box.getChildren().addAll(lblIcon, lblMsg);
        return box;
    }
}