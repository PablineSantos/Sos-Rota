package com.pi.grafos.view.screens;

import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import com.pi.grafos.service.AmbulanciaService;
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

    private static final javafx.scene.text.Font FONTE_TITULO = new javafx.scene.text.Font("Arial", 20);
    private static final javafx.scene.text.Font FONTE_CORPO = new javafx.scene.text.Font("Arial", 14);
    private static final javafx.scene.text.Font FONTE_PEQUENA = new javafx.scene.text.Font("Arial", 12);
    private static final javafx.scene.text.Font FONTE_BOTAO2 = new javafx.scene.text.Font("Arial", 14);
    private static final Color COR_AZUL_NOTURNO = Color.web("#1E293B");

    private OcorrenciaService ocorrenciaService;
    private AmbulanciaService ambulanciaService;

    public void exibir(Stage dono, OcorrenciaService ocorrenciaService, AmbulanciaService ambulanciaService, OcorrenciaStatus statusFiltro) {
        this.ocorrenciaService = ocorrenciaService;
        this.ambulanciaService = ambulanciaService;
        
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- Layout Base ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 15;");
        root.setPrefWidth(550);
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
                String titulo = "Ocorrência";
                if (oc.getTipoOcorrencia() != null) {
                    titulo = oc.getTipoOcorrencia().toString(); 
                }

                String subtitulo = oc.getDescricao() != null ? oc.getDescricao() : "Sem descrição";

                String localTexto = "Local n/d";
                if (oc.getLocal() != null) {
                    localTexto = oc.getLocal().getNome() != null ? oc.getLocal().getNome() : "Endereço desconhecido";
                }

                String corStatus = "#3B82F6"; // Default: Blue
                String gravidadeStr = oc.getGravidade() != null ? oc.getGravidade().toString() : "N/A";

                if ("ALTA".equalsIgnoreCase(gravidadeStr) || "GRAVE".equalsIgnoreCase(gravidadeStr)) {
                    corStatus = "#EF4444"; // Red
                } else if ("MEDIA".equalsIgnoreCase(gravidadeStr)) {
                    corStatus = "#F59E0B"; // Orange
                }

                // Badge: Use the ID
                Label lblId = new Label("#" + oc.getIdOcorrencia());
                
                // --- Create Card ---
                HBox card = criarCardOcorrencia(
                    titulo,          
                    subtitulo,       
                    corStatus,       
                    localTexto,      
                    gravidadeStr,    
                    lblId,           
                    oc               
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
        
        modal.centerOnScreen();
        modal.showAndWait();
    }

    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus, String bairro, String gravidade, Label lblContador, Ocorrencia ocorrencia) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        String estiloNormal = "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-style: solid; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #F1F5F9; -fx-border-color: " + corStatus + "; -fx-border-width: 1; -fx-border-style: solid; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";

        card.setStyle(estiloNormal);
        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e -> card.setStyle(estiloNormal));

        // --- Layout for Text ---
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

        // --- Right Side Actions ---
        VBox rightSide = new VBox(5);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        // 1. ID Badge
        lblContador.setFont(FONTE_PEQUENA);
        lblContador.setMinWidth(Region.USE_PREF_SIZE); 
        lblContador.setMinHeight(Region.USE_PREF_SIZE);
        lblContador.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 12; -fx-padding: 2 8 2 8; -fx-font-weight: bold;");
        
        rightSide.getChildren().add(lblContador);

        // 2. Dispatch Logic
        if (ocorrencia.getAmbulancia() == null) {
            Button btnDespachar = new Button("Despachar");
            btnDespachar.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;");
            
            btnDespachar.setOnAction(e -> {
                 new ModalSelecaoAmbulancia().exibir(
                     (Stage) card.getScene().getWindow(), 
                     ocorrencia, 
                     ambulanciaService, 
                     ocorrenciaService
                 );
            });
            
            rightSide.getChildren().add(btnDespachar);
        } else {
            Label lblAmb = new Label("🚑 " + ocorrencia.getAmbulancia().getPlaca());
            lblAmb.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
            rightSide.getChildren().add(lblAmb);
        }

        card.getChildren().addAll(statusDot, textos, spacer, rightSide);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 8; -fx-border-color: #FECACA; -fx-border-radius: 8;");

        Label lblIcon = new Label("✓"); 
        lblIcon.setStyle("-fx-font-size: 30px;");
        Label lblMsg = new Label("Nenhuma ocorrência encontrada.");
        lblMsg.setFont(FONTE_BOTAO2);
        lblMsg.setTextFill(Color.web("#64748B"));

        box.getChildren().addAll(lblIcon, lblMsg);
        return box;
    }
}