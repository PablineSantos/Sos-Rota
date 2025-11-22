package com.pi.grafos.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
public class TelaDashboard {

    // --- CONFIGURAÇÕES VISUAIS ---
    private static final double LARGURA_SIDEBAR = 280; // Cabe fonte 18px
    private static final double LARGURA_RESUMO = 320;

    public Scene criarCena(Stage stage) {

        // =============================================================================================
        // 1. COLUNA ESQUERDA: MENU LATERAL
        // =============================================================================================
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(LARGURA_SIDEBAR);
        sidebar.setMinWidth(LARGURA_SIDEBAR);
        sidebar.setStyle("-fx-background-color: " + HEX_SIDEBAR_BG + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // --- LOGO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo2.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { System.err.println("Erro logo dashboard"); }

        // --- TÍTULO DO PAINEL ---
        Label lblTituloPainel = new Label("PAINEL");
        lblTituloPainel.setFont(FONTE_SUBTITULO);
        lblTituloPainel.setTextFill(COR_TEXTO_BRANCO);

        // --- BOTÕES DE NAVEGAÇÃO ---
        Button btnDashboard = criarBotaoMenu("Dashboard", "🏠");
        // Destaque para o botão ativo
        btnDashboard.setStyle("-fx-background-color: " + HEX_SIDEBAR_HOVER + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;");

        Button btnNovaOcorrencia = criarBotaoMenu("Nova Ocorrência", "➕");
        Button btnFrota = criarBotaoMenu("Frota (Ambulâncias)", "🚑");
        Button btnEquipe = criarBotaoMenu("Equipe Médica", "👨‍⚕️");

        // Espacador
        Region spacerMenu = new Region();
        VBox.setVgrow(spacerMenu, Priority.ALWAYS);

        Button btnSair = criarBotaoMenu("Sair do Sistema", "🚪");
        btnSair.setOnAction(e -> {
            System.out.println("Saindo...");
            stage.close();
        });

        sidebar.getChildren().addAll(logoView, lblTituloPainel, btnDashboard, btnNovaOcorrencia, btnFrota, btnEquipe, spacerMenu, btnSair);


        // =============================================================================================
        // 2. COLUNA CENTRAL: MAPA DA CIDADE QUE IREI COLOCAR AINDA (MEU PROJETO É DESENHAR)
        // =============================================================================================
        Region centerMap = new Region();

        try {
            Image imgMap = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, true); // COVER
            BackgroundImage bgImage = new BackgroundImage(
                    imgMap,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );
            centerMap.setBackground(new Background(bgImage));
        } catch (Exception e) {
            centerMap.setStyle("-fx-background-color: #CBD5E1;");
        }


        // =============================================================================================
        // 3. COLUNA DIREITA: RESUMO
        // =============================================================================================
        VBox rightPanel = new VBox(25);
        rightPanel.setPadding(new Insets(30));
        rightPanel.setPrefWidth(LARGURA_RESUMO);
        rightPanel.setMinWidth(LARGURA_RESUMO);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, -5, 0);");

        // --- Título Resumo ---
        Label lblResumo = new Label("Resumo Operacional");
        lblResumo.setFont(FONTE_SUBTITULO);
        lblResumo.setTextFill(COR_AZUL_NOTURNO);

        // --- SEÇÃO 1: LISTA DE OCORRÊNCIAS ---
        Label lblPendentes = new Label("Ocorrências Pendentes");
        lblPendentes.setFont(FONTE_CORPO);
        lblPendentes.setTextFill(COR_TEXTO_CLARO);

        VBox listaContainer = new VBox(10);

        // Adicionando ocorrências na força bruta para visualisacao
        listaContainer.getChildren().add(criarCardOcorrencia("Acidente Centro", "Alta Prioridade", HEX_VERMELHO));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito - Jd. América", "Média Prioridade", "#F59E0B"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade", "#10B981"));
        listaContainer.getChildren().add(criarCardOcorrencia("Colisão Leve", "Baixa Prioridade", "#10B981"));        listaContainer.getChildren().add(criarCardOcorrencia("Acidente Centro", "Alta Prioridade", HEX_VERMELHO));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito - Jd. América", "Média Prioridade", "#F59E0B"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade", "#10B981"));
        listaContainer.getChildren().add(criarCardOcorrencia("Colisão Leve", "Baixa Prioridade", "#10B981"));
        // ... outros itens ...

        ScrollPane scrollPane = new ScrollPane(listaContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // --- SEÇÃO 2: CONTADOR DE FROTA ---
        VBox painelFrota = new VBox(5);
        painelFrota.setAlignment(Pos.CENTER);
        painelFrota.setPadding(new Insets(20));
        painelFrota.setStyle("-fx-background-color: " + HEX_CINZA_FUNDO + "; -fx-background-radius: 8;");

        Label lblFrotaTitulo = new Label("Ambulâncias Disponíveis");
        lblFrotaTitulo.setFont(FONTE_CORPO);
        lblFrotaTitulo.setTextFill(COR_TEXTO_BRANCO);

        Label lblFrotaNumero = new Label("5");
        // Se possível, use uma fonte constante aqui também, ex: FONTE_TITULO aumentanda
        lblFrotaNumero.setFont(FONTE_TITULO);
        lblFrotaNumero.setTextFill(COR_TEXTO_BRANCO); //

        Label lblFrotaTotal = new Label("de 12 viaturas totais");
        lblFrotaTotal.setFont(FONTE_PEQUENA);
        lblFrotaTotal.setTextFill(COR_TEXTO_BRANCO);

        painelFrota.getChildren().addAll(lblFrotaTitulo, lblFrotaNumero, lblFrotaTotal);

        rightPanel.getChildren().addAll(lblResumo, lblPendentes, scrollPane, painelFrota);


        // =============================================================================================
        // MONTAGEM FINAL
        // =============================================================================================
        HBox root = new HBox();
        root.getChildren().addAll(sidebar, centerMap, rightPanel);

        HBox.setHgrow(sidebar, Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.NEVER);
        HBox.setHgrow(centerMap, Priority.ALWAYS);

        return new Scene(root, 1200, 700);
    }

    // =============================================================================================
    // MÉTODOS AUXILIARESw (Botao com emoji de uma fonte, e escrita de outra)
    // =============================================================================================

    /**
     * Cria um botão estilizado
     */
    private Button criarBotaoMenu(String texto, String iconeEmoji) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        // Sequência: v cima, v1 direita, v2 baixo, v3 esquerda
        btn.setPadding(new Insets(12, 15, 12, 15));

        // Altura controlada (Evita que fique gigante)
        btn.setMinHeight(45);
        btn.setMaxHeight(45);

        // --- 1. EMOJI ---
        javafx.scene.text.Text txtEmoji = new javafx.scene.text.Text(iconeEmoji);
        // Reduz o emoji para 16px
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.web("#E2E8F0"));

        // --- 2. TEXTO POPPINS ---
        javafx.scene.text.Text txtLabel = new javafx.scene.text.Text("  " + texto); // Espaço aqui no texto
        txtLabel.setFont(FONTE_BOTAO2);
        txtLabel.setFill(Color.web("#E2E8F0"));

        // --- 3. MISTA DOS DOIS ---
        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(txtEmoji, txtLabel);
        // Alinha o testo dos botoes
        flow.setTextAlignment(TextAlignment.LEFT);

        btn.setGraphic(flow);

        // --- ESTILIZAÇÃO (CSS) ---
        String estiloNormal = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloHover = "-fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        // Estilo ATIVO (Vermelho) - será usado para botão 'Dashboard' manualmente lá em cima
        // String estiloAtivo = "-fx-background-color: #D92027; -fx-background-radius: 8;";

        btn.setStyle(estiloNormal);

        btn.setOnMouseEntered(e -> {
            // Só muda a cor se não tiver um estilo fixo (como o vermelho do dashboard)
            if (!btn.getStyle().contains("#D92027")) {
                btn.setStyle(estiloHover);
            }
            txtEmoji.setFill(Color.WHITE);
            txtLabel.setFill(Color.WHITE);
        });

        btn.setOnMouseExited(e -> {
            // Só volta ao normal se não for o botão vermelho
            if (!btn.getStyle().contains("#D92027")) {
                btn.setStyle(estiloNormal);
            }
            txtEmoji.setFill(Color.web("#E2E8F0"));
            txtLabel.setFill(Color.web("#E2E8F0"));
        });

        return btn;
    }

    /**
     * Cria um quadrado visual para representar uma ocorrência na lista
     */
    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6;");
        card.setAlignment(Pos.CENTER_LEFT);

        Circle statusDot = new Circle(5, Color.web(corStatus));

        VBox textos = new VBox(2);
        Label lblTit = new Label(titulo);
        // Usando fonte do corpo para o card ficar mais limpo
        lblTit.setFont(FONTE_CORPO);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblSub = new Label(subtitulo);
        lblSub.setFont(FONTE_PEQUENA);
        lblSub.setTextFill(Color.web(corStatus));

        textos.getChildren().addAll(lblTit, lblSub);

        card.getChildren().addAll(statusDot, textos);
        return card;
    }
}