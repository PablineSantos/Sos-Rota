package com.pi.grafos.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Importando seus estilos globais
import static com.pi.grafos.view.styles.AppStyles.*;

public class AlertaConfirmacao {

    // Variável para capturar a resposta do usuário
    private boolean confirmado = false;

    /**
     * Exibe o modal e espera a resposta do usuário.
     * @param titulo Título do alerta (Ex: "Confirmar Remoção")
     * @param mensagem Texto descritivo (Ex: "Tem certeza que deseja remover...?")
     * @return true se clicou em CONFIRMAR, false caso contrário.
     */
    public boolean mostrar(String titulo, String mensagem) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela de trás
        modal.initStyle(StageStyle.TRANSPARENT); // Remove a barra do Windows

        // --- ESTRUTURA DO CARD ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setPrefWidth(450);

        // Estilo IDÊNTICO à sua imagem de referência
        root.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #EF4444; " + // Borda Vermelha
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );

        // 1. Título (Vermelho e Grande)
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(FONTE_TITULO); // Usa sua Poppins Bold Grande
        lblTitulo.setTextFill(Color.web("#EF4444")); // Vermelho

        // 2. Mensagem (Cinza e Centralizada)
        Label lblMsg = new Label(mensagem);
        lblMsg.setFont(FONTE_CORPO);
        lblMsg.setTextFill(COR_TEXTO_CLARO); // Cinza
        lblMsg.setWrapText(true);
        lblMsg.setTextAlignment(TextAlignment.CENTER);

        // 3. Botões (Lado a Lado)
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER);

        // Botão Cancelar (Branco com borda cinza)
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefHeight(45);
        btnCancelar.setPrefWidth(120);
        btnCancelar.setFont(FONTE_BOTAO2);
        btnCancelar.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #CBD5E1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-text-fill: #64748B; " +
                        "-fx-cursor: hand;"
        );
        btnCancelar.setOnAction(e -> {
            confirmado = false;
            modal.close();
        });

        // Botão Confirmar (Vermelho Sólido)
        Button btnConfirmar = new Button("CONFIRMAR");
        btnConfirmar.setPrefHeight(45);
        btnConfirmar.setPrefWidth(140);
        btnConfirmar.setFont(FONTE_BOTAO2);
        btnConfirmar.setStyle(
                "-fx-background-color: #EF4444; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"
        );

        // Efeito Hover simples no confirmar
        btnConfirmar.setOnMouseEntered(e -> btnConfirmar.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;"));
        btnConfirmar.setOnMouseExited(e -> btnConfirmar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;"));

        btnConfirmar.setOnAction(e -> {
            confirmado = true;
            modal.close();
        });

        boxBtn.getChildren().addAll(btnCancelar, btnConfirmar);

        // Adiciona tudo ao layout
        root.getChildren().addAll(lblTitulo, lblMsg, boxBtn);

        // --- CENA ---
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Fundo transparente para o arredondamento
        modal.setScene(scene);
        modal.centerOnScreen();

        // Mostra e ESPERA o usuário clicar (Bloqueia o código aqui)
        modal.showAndWait();

        return confirmado;
    }
}