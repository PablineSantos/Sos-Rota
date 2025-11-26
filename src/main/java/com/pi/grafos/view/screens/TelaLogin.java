package com.pi.grafos.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*; // Importei Hyperlink e outros controles
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
public class TelaLogin {

    // Injetamos a tela de Cadastro
    @Autowired
    @Lazy // Lazy é essencial aqui para o Spring carregar sob demanda e não travar no boot
    private TelaCadastro telaCadastro;

    // @Autowired
    // private UsuarioService usuarioService;

    public Scene criarCena(Stage stage) {

        // --- 1. PAINEL PRINCIPAL ---
        VBox loginPanel = new VBox(20);
        loginPanel.setPadding(new Insets(40));
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color: white");

        // --- BLOCO 1: LOGO E TÍTULO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { }

        Label lblLogin = new Label("Login");
        lblLogin.setFont(FONTE_TITULO);
        lblLogin.setTextFill(COR_AZUL_NOTURNO);

        // --- BLOCO 2: FORMULÁRIO ---
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

        Label lblMensagemErro = new Label();
        lblMensagemErro.setTextFill(Color.RED);
        lblMensagemErro.setFont(FONTE_PEQUENA);

        formContainer.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, lblMensagemErro);

        // --- BLOCO 3: AÇÕES ---

        Button btnLogar = new Button("Log In");
        btnLogar.setFont(FONTE_BOTAO);
        btnLogar.setPrefHeight(45);
        btnLogar.setMaxWidth(Double.MAX_VALUE);
        btnLogar.setStyle(
                "-fx-background-color: " + HEX_VERMELHO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;"
        );

        // --- HIPERLINK PARA CADASTRO ---
        Hyperlink linkCadastro = new Hyperlink("Não tem uma conta? Registre-se agora");
        linkCadastro.setFont(FONTE_PEQUENA);
        // Usando a cor vermelha ou azul para destacar, ou cinza para ser sutil.
        // Vou usar o vermelho da marca para combinar com a imagem de referência do SeedProd
        linkCadastro.setTextFill(COR_VERMELHO_RESGATE);
        linkCadastro.setBorder(Border.EMPTY);
        // Remove o sublinhado padrão e adiciona só no hover se quiser, ou deixa fixo
        linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;");

        linkCadastro.setOnMouseEntered(e -> linkCadastro.setStyle("-fx-underline: true; -fx-cursor: hand;"));
        linkCadastro.setOnMouseExited(e -> linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;"));

        // AÇÃO DE NAVEGAÇÃO
        linkCadastro.setOnAction(e -> {
            // Troca a cena atual pela cena de cadastro
            stage.setScene(telaCadastro.criarCena(stage));
        });

        // Espaçador
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        // --- MONTAGEM ---
        loginPanel.getChildren().addAll(
                logoView,
                lblLogin,
                formContainer,
                btnLogar,
                linkCadastro, // Adicionado aqui!
                spacer,
                lblCopyright
        );

        // --- LADO DIREITO (Imagem) ---
        Region backgroundRegion = new Region();
        try {
            Image imgBackground = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            javafx.scene.layout.BackgroundSize bgSize = new javafx.scene.layout.BackgroundSize(
                    1.0, 1.0, true, true, false, true
            );
            javafx.scene.layout.BackgroundImage bgImage = new javafx.scene.layout.BackgroundImage(
                    imgBackground,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundPosition.CENTER,
                    bgSize
            );
            backgroundRegion.setBackground(new javafx.scene.layout.Background(bgImage));
        } catch (Exception e) {
            backgroundRegion.setStyle("-fx-background-color: #1E293B;");
        }

        // --- RAIZ ---
        HBox root = new HBox();
        root.getChildren().addAll(loginPanel, backgroundRegion);
        HBox.setHgrow(loginPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);
        loginPanel.setMaxWidth(500);
        loginPanel.setMinWidth(400);

        // --- Lógica do Botão (Mock) ---
        btnLogar.setOnAction(event -> {
            String email = txtEmail.getText();
            String senha = txtPassword.getText();
            if (email.equals("teste@email.com") && senha.equals("123")) {
                lblMensagemErro.setText("Sucesso!");
                lblMensagemErro.setTextFill(Color.GREEN);
            } else {
                lblMensagemErro.setText("Dados inválidos.");
                lblMensagemErro.setTextFill(Color.RED);
            }
        });

        return new Scene(root, 1000, 700);
    }
}