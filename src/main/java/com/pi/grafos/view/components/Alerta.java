package com.pi.grafos.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Importe seus estilos globais
import static com.pi.grafos.view.styles.AppStyles.*;

public class Alerta {

    public enum Tipo {
        SUCESSO("#10B981", "✅"),
        ERRO("#EF4444", "❌"),
        AVISO("#F59E0B", "⚠️");

        final String corHex;
        final String emoji;

        Tipo(String corHex, String emoji) {
            this.corHex = corHex;
            this.emoji = emoji;
        }
    }

    public void mostrar(String titulo, String mensagem, Tipo tipo) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela de trás
        modal.initStyle(StageStyle.TRANSPARENT); // Remove a barra feia do Windows

        // --- CONTEÚDO ---
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        // Estilo Card Branco com Borda Colorida baseada no Tipo
        root.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: " + tipo.corHex + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );
        root.setPrefWidth(400);

        // 1. Ícone Grande
        Label icon = new Label(tipo.emoji);
        icon.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 50px;");
        icon.setTextFill(Color.web(tipo.corHex));

        // 2. Título
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        // 3. Mensagem
        Label lblMsg = new Label(mensagem);
        lblMsg.setFont(FONTE_CORPO);
        lblMsg.setTextFill(COR_TEXTO_CLARO);
        lblMsg.setWrapText(true); // Quebra linha se for texto grande
        lblMsg.setTextAlignment(TextAlignment.CENTER);

        // 4. Botão OK
        Button btnOk = new Button("OK");
        btnOk.setPrefHeight(45);
        btnOk.setPrefWidth(150);
        btnOk.setFont(FONTE_BOTAO2); // Sua fonte Poppins Bold

        // Estilo do botão combinando com o tipo do alerta
        String estiloBtn = "-fx-background-color: " + tipo.corHex + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand";
        btnOk.setStyle(estiloBtn);

        // Efeito Hover simples
        btnOk.setOnMouseEntered(e -> btnOk.setOpacity(0.9));
        btnOk.setOnMouseExited(e -> btnOk.setOpacity(1.0));

        btnOk.setOnAction(e -> modal.close());

        root.getChildren().addAll(icon, lblTitulo, lblMsg, btnOk);

        // --- CENA ---
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Fundo transparente para o arredondamento funcionar
        modal.setScene(scene);
        modal.centerOnScreen(); // Centraliza no monitor
        modal.showAndWait(); // Espera o usuário fechar
    }
}