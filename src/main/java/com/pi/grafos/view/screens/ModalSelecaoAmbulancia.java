package com.pi.grafos.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

// EM CONSTRUÇÃO
public class ModalSelecaoAmbulancia {

    // Classe interna simples para representar os dados na lista (DTO)
    public static class SugestaoAmbulancia {
        String placa;
        String base;
        String tipo; // UTI ou BASICA
        double distanciaKm;
        int tempoMinutos;
        boolean dentroDoSla;

        public SugestaoAmbulancia(String placa, String base, String tipo, double distanciaKm, int tempoMinutos, boolean dentroDoSla) {
            this.placa = placa;
            this.base = base;
            this.tipo = tipo;
            this.distanciaKm = distanciaKm;
            this.tempoMinutos = tempoMinutos;
            this.dentroDoSla = dentroDoSla;
        }
    }

    public void exibir(Stage dono, String bairroOcorrencia, String gravidade) {
        // Configuração da Janela Modal
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela de trás
        modal.initStyle(StageStyle.TRANSPARENT); // Sem barra de título padrão

        // --- CONTEÚDO ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 15;");
        root.setPrefWidth(500);
        root.setPrefHeight(600);

        // Cabeçalho
        Label lblTitulo = new Label("Despacho de Ambulância");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblSub = new Label("Local: " + bairroOcorrencia + " | Gravidade: " + gravidade);
        lblSub.setFont(FONTE_CORPO);
        lblSub.setTextFill(Color.web("#64748B"));

        // --- SIMULAÇÃO DA LÓGICA ---
        // Aqui chamara o Service: List<Sugestao> lista = service.buscarMelhores(bairro, gravidade);
        List<SugestaoAmbulancia> listaSugestoes = simularBuscaInteligente(gravidade);

        VBox containerLista = new VBox(10);

        if (listaSugestoes.isEmpty()) {
            // CENÁRIO: Nenhuma ambulância disponível
            containerLista.getChildren().add(criarAlertaVazio());
        } else {
            // CENÁRIO: Existem ambulâncias
            for (SugestaoAmbulancia amb : listaSugestoes) {
                containerLista.getChildren().add(criarItemLista(amb, modal));
            }
        }

        ScrollPane scroll = new ScrollPane(containerLista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Botão Fechar
        Button btnFechar = new Button("Fechar / Colocar em Espera");
        btnFechar.setFont(FONTE_BOTAO2);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setPrefHeight(45);
        btnFechar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> modal.close());

        root.getChildren().addAll(lblTitulo, lblSub, scroll, btnFechar);

        // Cria a cena transparente
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        // Centraliza no pai
        modal.setX(dono.getX() + (dono.getWidth() - 500) / 2);
        modal.setY(dono.getY() + (dono.getHeight() - 600) / 2);

        modal.showAndWait();
    }

    // --- COMPONENTES VISUAIS ---

    private HBox criarItemLista(SugestaoAmbulancia amb, Stage modal) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        // Estilo muda se estiver dentro do SLA ou não
        String bordaColor = amb.dentroDoSla ? "#10B981" : "#EF4444"; // Verde ou Vermelho
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: " + bordaColor + "; -fx-border-radius: 8; -fx-border-width: 1;");

        // Ícone
        Label icon = new Label("🚑");
        icon.setStyle("-fx-font-size: 24px;");

        // Infos
        VBox info = new VBox(3);
        Label lblPlaca = new Label(amb.tipo + " - " + amb.placa);
        lblPlaca.setFont(FONTE_BOTAO2);
        lblPlaca.setTextFill(COR_AZUL_NOTURNO);

        Label lblBase = new Label("Base: " + amb.base);
        lblBase.setFont(FONTE_PEQUENA);

        Label lblTempo = new Label(amb.tempoMinutos + " min (" + amb.distanciaKm + " km)");
        lblTempo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + bordaColor + ";");

        info.getChildren().addAll(lblPlaca, lblBase, lblTempo);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Botão Selecionar
        Button btnSelect = new Button("DESPACHAR");
        btnSelect.setStyle("-fx-background-color: " + bordaColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSelect.setOnAction(e -> {
            System.out.println("Despachando ambulância: " + amb.placa);
            modal.close();
            // TODO: Chamar método de salvar no banco e mudar status
        });

        card.getChildren().addAll(icon, info, btnSelect);
        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 8; -fx-border-color: #FECACA; -fx-border-radius: 8;"); // Vermelho claro

        Label lblIcon = new Label("⚠️");
        lblIcon.setStyle("-fx-font-size: 30px;");

        Label lblMsg = new Label("Nenhuma ambulância disponível no momento.");
        lblMsg.setFont(FONTE_BOTAO2);
        lblMsg.setTextFill(COR_VERMELHO_RESGATE);

        Label lblSugestao = new Label("A ocorrência será salva como 'PENDENTE' na fila de espera.");
        lblSugestao.setWrapText(true);

        box.getChildren().addAll(lblIcon, lblMsg, lblSugestao);
        return box;
    }

    // --- SIMULAÇÃO ---
    private List<SugestaoAmbulancia> simularBuscaInteligente(String gravidade) {
        List<SugestaoAmbulancia> lista = new ArrayList<>();

        // Regra do PDF: Alta = 8 min. Média = 15 min.
        int slaMax = gravidade.equals("ALTA") ? 8 : 15;

        // Simulando dados que viriam do Dijkstra
        // Adicionei uma longe (12km) para testar o visual vermelho
        lista.add(new SugestaoAmbulancia("ABC-1234", "Centro", "UTI", 3.5, 4, 4 <= slaMax));
        lista.add(new SugestaoAmbulancia("XYZ-9876", "Jardim América", "UTI", 5.0, 5, 5 <= slaMax));
        lista.add(new SugestaoAmbulancia("DEF-5678", "Vila Nova", "BÁSICA", 12.0, 12, 12 <= slaMax));

        // Ordena por tempo (Menor para o Maior)
        lista.sort(Comparator.comparingInt(a -> a.tempoMinutos));

        return lista;
        // Retorne "new ArrayList<>()" para testar a tela vazia
    }
}