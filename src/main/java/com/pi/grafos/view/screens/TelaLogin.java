package com.pi.grafos.view.screens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pi.grafos.controller.MainController;
import static com.pi.grafos.view.styles.AppStyles.*; // Seus imports estáticos de estilo

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent; // <--- IMPORTANTE: Usamos Parent, não Scene
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

@Component
public class TelaLogin {

    @Autowired
    private MainController controller;

    @Autowired
    @Lazy // Evita ciclo de dependência: Login precisa de Cadastro, Cadastro precisa de Login
    private TelaCadastro telaCadastro;

    @Autowired
    @Lazy // Evita ciclo de dependência
    private TelaDashboard telaDashboard;

    // MUDANÇA: O método retorna o LEIAUTE (Parent), não a JANELA (Scene)
    public Parent criarConteudo(Stage stage) {

        // --- 1. PAINEL PRINCIPAL (Esquerdo) ---
        VBox loginPanel = new VBox(20);
        loginPanel.setPadding(new Insets(40));
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color: white");

        // --- LOGO E TÍTULO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* Ignora erro de imagem */ }

        Label lblLogin = new Label("Login");
        lblLogin.setFont(FONTE_TITULO);
        lblLogin.setTextFill(COR_AZUL_NOTURNO);

        // --- FORMULÁRIO ---
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMaxWidth(Double.MAX_VALUE);

        Label lblEmail = new Label("Nome de usuário");
        lblEmail.setFont(FONTE_SUBTITULO);
        lblEmail.setTextFill(COR_TEXTO_PRETO);

        TextField txtEmail = new TextField();
        txtEmail.setFont(FONTE_PEQUENA);
        txtEmail.setPrefHeight(35);
        txtEmail.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblPassword = new Label("Senha");
        lblPassword.setFont(FONTE_SUBTITULO);
        lblPassword.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");
        txtPassword.setPrefHeight(35);
        txtPassword.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");

        formContainer.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword);

        // --- AÇÕES ---
        Button btnLogar = new Button("Log In");
        btnLogar.setFont(FONTE_BOTAO);
        btnLogar.setPrefHeight(45);
        btnLogar.setMaxWidth(Double.MAX_VALUE);
        btnLogar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");

        // Link Cadastro
        Hyperlink linkCadastro = new Hyperlink("Não tem uma conta? Registre-se agora");
        linkCadastro.setFont(FONTE_PEQUENA);
        linkCadastro.setTextFill(COR_AZUL_NOTURNO);
        linkCadastro.setBorder(Border.EMPTY);
        linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;");

        linkCadastro.setOnMouseEntered(e -> linkCadastro.setStyle("-fx-underline: true; -fx-cursor: hand;"));
        linkCadastro.setOnMouseExited(e -> linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;"));

        // === AÇÃO DE NAVEGAÇÃO SEGURA (SEM PISCAR) ===
        linkCadastro.setOnAction(e -> {
            // Troca apenas o conteúdo da janela atual
            stage.getScene().setRoot(telaCadastro.criarConteudo(stage));
        });

        // Copyright
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        loginPanel.getChildren().addAll(logoView, lblLogin, formContainer, btnLogar, linkCadastro, spacer, lblCopyright);
        loginPanel.setMaxWidth(500);
        loginPanel.setMinWidth(400);

        // --- 2. PAINEL DIREITO (Imagem) ---
        Region backgroundRegion = new Region();
        try {
            Image imgBackground = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            javafx.scene.layout.BackgroundSize bgSize = new javafx.scene.layout.BackgroundSize(1.0, 1.0, true, true, false, true);
            javafx.scene.layout.BackgroundImage bgImage = new javafx.scene.layout.BackgroundImage(imgBackground, javafx.scene.layout.BackgroundRepeat.NO_REPEAT, javafx.scene.layout.BackgroundRepeat.NO_REPEAT, javafx.scene.layout.BackgroundPosition.CENTER, bgSize);
            backgroundRegion.setBackground(new javafx.scene.layout.Background(bgImage));
        } catch (Exception e) {
            backgroundRegion.setStyle("-fx-background-color: #1E293B;");
        }

        // --- LÓGICA DE LOGIN ---
        btnLogar.setOnAction(event -> {
            String nomeUsuario = txtEmail.getText();
            String senhaUsuario = txtPassword.getText();

            if (nomeUsuario.isEmpty() || senhaUsuario.isEmpty()) {
                mostrarAlerta("Campos vazios", "Preencha todos os campos do login!");
                return;
            }

            try {
                boolean loginSucesso = controller.logar(nomeUsuario, senhaUsuario);

                if (loginSucesso) {
                    // === NAVEGAÇÃO SEGURA PARA DASHBOARD ===
                    // Injeta o layout do Dashboard na cena atual
                    stage.getScene().setRoot(telaDashboard.criarConteudo(stage));
                } else {
                    mostrarAlerta("Login Falhou", "Usuário ou senha inválidos.");
                }
            } catch (Exception e) {
                mostrarAlerta("Erro Crítico", "Erro de conexão: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // MONTAGEM DO ROOT
        HBox root = new HBox();
        root.getChildren().addAll(loginPanel, backgroundRegion);
        HBox.setHgrow(loginPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);

        return root; // Retorna o HBox (Parent), não a Scene
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}