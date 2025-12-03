package com.pi.grafos.view.screens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pi.grafos.controller.MainController;
import static com.pi.grafos.view.styles.AppStyles.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent; // IMPORTANTE
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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
public class TelaCadastro {

    @Autowired
    @Lazy
    private TelaLogin telaLogin;

    @Autowired
    @Lazy
    private TelaDashboard telaDashboard;

    @Autowired
    private MainController mainController;

    // MUDANÇA: Retorna Parent, não Scene
    public Parent criarConteudo(Stage stage) {

        // --- 1. PAINEL ESQUERDO (Formulário) ---
        VBox cadastroPanel = new VBox(20);
        cadastroPanel.setPadding(new Insets(40));
        cadastroPanel.setAlignment(Pos.CENTER);
        cadastroPanel.setStyle("-fx-background-color: white");
        cadastroPanel.setMaxWidth(500);
        cadastroPanel.setMinWidth(400);

        // Logo
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { }

        Label lblTitulo = new Label("Cadastrar");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        // --- CAMPOS ---
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMaxWidth(Double.MAX_VALUE);

        Label lblUser = new Label("Nome de Usuário");
        lblUser.setFont(FONTE_SUBTITULO);
        lblUser.setTextFill(COR_TEXTO_PRETO);

        TextField txtUser = new TextField();
        txtUser.setPrefHeight(40);
        estilizarCampo(txtUser);

        Label lblPass = new Label("Senha");
        lblPass.setFont(FONTE_SUBTITULO);
        lblPass.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtPass = new PasswordField();
        txtPass.setPrefHeight(40);
        txtPass.setPromptText("••••••••");
        estilizarCampo(txtPass);

        Label lblConfirm = new Label("Confirmar Senha");
        lblConfirm.setFont(FONTE_SUBTITULO);
        lblConfirm.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setPrefHeight(40);
        txtConfirm.setPromptText("••••••••");
        estilizarCampo(txtConfirm);

        formContainer.getChildren().addAll(lblUser, txtUser, lblPass, txtPass, lblConfirm, txtConfirm);

        // --- BOTÃO REGISTRAR ---
        Button btnCadastrar = new Button("Registrar");
        btnCadastrar.setFont(FONTE_BOTAO);
        btnCadastrar.setPrefHeight(45);
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);
        btnCadastrar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");

        // --- LINK VOLTAR ---
        Hyperlink linkLogin = new Hyperlink("Já tem uma conta? Faça Login");
        linkLogin.setFont(FONTE_PEQUENA);
        linkLogin.setTextFill(Color.web("#666666"));
        linkLogin.setBorder(Border.EMPTY);
        linkLogin.setStyle("-fx-underline: true; -fx-cursor: hand;");

        // === AÇÃO DE NAVEGAÇÃO SEGURA ===
        linkLogin.setOnAction(e -> {
            // Volta para o Login trocando o root da scene atual
            stage.getScene().setRoot(telaLogin.criarConteudo(stage));
        });

        // Copyright
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        cadastroPanel.getChildren().addAll(logoView, lblTitulo, formContainer, btnCadastrar, linkLogin, spacer, lblCopyright);

        // --- 2. PAINEL DIREITO (Imagem) ---
        Region backgroundRegion = criarBackgroundImagem();

        // --- LÓGICA DE REGISTRO ---
        btnCadastrar.setOnAction(e -> {
            String nomeUsuario = txtUser.getText();
            String senhaUsuario = txtPass.getText();
            String confirmSenha = txtConfirm.getText();

            if (nomeUsuario.isEmpty() || senhaUsuario.isEmpty()) {
                mostrarAlerta("Campos vazios", "Preencha todos os campos antes de salvar!");
                return;
            }

            // Nota: Lógica original do usuario mantida (verificando se loga para ver se existe)
            // Idealmente deveria ter um metodo existeUsuario(nome)
            if (mainController.logar(nomeUsuario, senhaUsuario)) {
                mostrarAlerta("Usuário existente", "Este usuário já possui cadastro.");
            } else {
                if (senhaUsuario.equals(confirmSenha)) {
                    // Cadastra
                    mainController.cadastrarUsuario(nomeUsuario, senhaUsuario);
                    mostrarAlerta("Sucesso", "Usuário cadastrado com sucesso! Entrando...");

                    // === VAI PARA DASHBOARD SEM PISCAR A TELA ===
                    stage.getScene().setRoot(telaDashboard.criarConteudo(stage));
                } else {
                    mostrarAlerta("Erro de Senha", "As senhas não conferem.");
                }
            }
        });

        // MONTAGEM ROOT
        HBox root = new HBox();
        root.getChildren().addAll(cadastroPanel, backgroundRegion);
        HBox.setHgrow(cadastroPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);

        return root;
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void estilizarCampo(Control campo) {
        campo.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");
    }

    private Region criarBackgroundImagem() {
        Region region = new Region();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize size = new BackgroundSize(1.0, 1.0, true, true, false, true);
            BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
            region.setBackground(new Background(bgImg));
        } catch (Exception e) {
            region.setStyle("-fx-background-color: #1E293B;");
        }
        return region;
    }
}