package com.pi.grafos.view.screens;

import java.util.List;

import com.pi.grafos.service.grafosService.SugestaoAmbulancia;
import com.pi.grafos.service.OcorrenciaService; // Importante
import com.pi.grafos.view.components.Alerta; // Importante

import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_VERMELHO_RESGATE;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModalSelecaoAmbulancia {

    /**
     * Exibe o modal para selecionar uma ambulância.
     * Agora EXIGE o OcorrenciaService e o ID da Ocorrência para poder salvar no banco.
     */
    public void exibir(Stage dono, Long idOcorrencia, String bairroOcorrencia, String gravidade, 
                       List<SugestaoAmbulancia> listaSugestoes, OcorrenciaService ocorrenciaService) {
        
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- Layout Base ---
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

        // --- Lista de Sugestões ---
        VBox containerLista = new VBox(10);

        if (listaSugestoes == null || listaSugestoes.isEmpty()) {
            containerLista.getChildren().add(criarAlertaVazio());
        } else {
            // Regra simples de SLA para cor (8 min para ALTA, 15 para MÉDIA)
            int slaMax = gravidade.equalsIgnoreCase("ALTA") ? 8 : 15;

            for (SugestaoAmbulancia sugestao : listaSugestoes) {
                // Calcula tempo estimado (Ex: 60km/h = 1km por min)
                // *1.5 considerando trânsito/curvas como margem de segurança
                int tempoMin = (int) Math.ceil(sugestao.getDistanciaKm() * 1.5); 
                boolean dentroSla = tempoMin <= slaMax;

                // Passamos o ID e o Service para o método de criação do item
                containerLista.getChildren().add(
                    criarItemLista(sugestao, tempoMin, dentroSla, modal, idOcorrencia, ocorrenciaService)
                );
            }
        }

        ScrollPane scroll = new ScrollPane(containerLista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Botão Fechar
        Button btnFechar = new Button("Fechar");
        btnFechar.setFont(FONTE_BOTAO2);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setPrefHeight(45);
        btnFechar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> modal.close());

        root.getChildren().addAll(lblTitulo, lblSub, scroll, btnFechar);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        
        // Centraliza em relação ao dono
        if (dono != null) {
            modal.setX(dono.getX() + (dono.getWidth() - 500) / 2);
            modal.setY(dono.getY() + (dono.getHeight() - 600) / 2);
        }
        
        modal.showAndWait();
    }

    // Método auxiliar para criar o card visual de cada ambulância
    private HBox criarItemLista(SugestaoAmbulancia sugestao, int tempoMin, boolean dentroSla, 
                                Stage modal, Long idOcorrencia, OcorrenciaService service) {
        
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        String bordaColor = dentroSla ? "#10B981" : "#EF4444"; 
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: " + bordaColor + "; -fx-border-radius: 8; -fx-border-width: 1;");

        Label icon = new Label("🚑");
        icon.setStyle("-fx-font-size: 24px;");

        VBox info = new VBox(3);
        // Pega dados reais do objeto Ambulancia
        String tipo = sugestao.getAmbulancia().getTipoAmbulancia() != null ? sugestao.getAmbulancia().getTipoAmbulancia().toString() : "N/A";
        String placa = sugestao.getAmbulancia().getPlaca();
        String base = sugestao.getAmbulancia().getUnidade() != null ? sugestao.getAmbulancia().getUnidade().getNome() : "Sem Base";

        Label lblPlaca = new Label(tipo + " - " + placa);
        lblPlaca.setFont(FONTE_BOTAO2);
        lblPlaca.setTextFill(COR_AZUL_NOTURNO);

        Label lblBase = new Label("Base Atual: " + base);
        lblBase.setFont(FONTE_PEQUENA);

        Label lblTempo = new Label(String.format("~%d min (%.1f km)", tempoMin, sugestao.getDistanciaKm()));
        lblTempo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + bordaColor + ";");

        info.getChildren().addAll(lblPlaca, lblBase, lblTempo);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnSelect = new Button("DESPACHAR");
        btnSelect.setStyle("-fx-background-color: " + bordaColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        
        // --- AÇÃO DE PERSISTÊNCIA ---
        btnSelect.setOnAction(e -> {
            try {
                if (service != null && idOcorrencia != null) {
                    // Chama o serviço para realizar o despacho no banco
                    service.despacharAmbulancia(idOcorrencia, sugestao.getAmbulancia().getIdAmbulancia());
                    
                    new Alerta().mostrar("Sucesso", "Ambulância " + placa + " despachada com sucesso!", Alerta.Tipo.SUCESSO);
                    modal.close();
                } else {
                    new Alerta().mostrar("Erro", "Serviço de ocorrência não inicializado.", Alerta.Tipo.ERRO);
                }
            } catch (Exception ex) {
                new Alerta().mostrar("Erro ao Despachar", ex.getMessage(), Alerta.Tipo.ERRO);
            }
        });

        card.getChildren().addAll(icon, info, btnSelect);
        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 8; -fx-border-color: #FECACA; -fx-border-radius: 8;");

        Label lblIcon = new Label("⚠️");
        lblIcon.setStyle("-fx-font-size: 30px;");
        Label lblMsg = new Label("Nenhuma ambulância encontrada na rede.");
        lblMsg.setFont(FONTE_BOTAO2);
        lblMsg.setTextFill(COR_VERMELHO_RESGATE);

        box.getChildren().addAll(lblIcon, lblMsg);
        return box;
    }
}