package com.pi.grafos.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Usar PasswordField para senhas
import javafx.scene.control.TextField;
import javafx.scene.image.Image; // Para carregar a imagem
import javafx.scene.image.ImageView; // Para exibir a imagem
import javafx.scene.layout.HBox; // Para dividir em duas colunas (Login e Imagem)
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox; // Para empilhar elementos na coluna de login
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import static com.pi.grafos.view.Styles.AppStyles.*;


@Component
class TelaLogin {

    // Aqui você injetaria seu serviço de usuário para validar o login
    // @Autowired
    // private UsuarioService usuarioService;

    public Scene criarCena(Stage stage) {

        // --- 1. PAINEL PRINCIPAL (Alinhamento CENTRALIZADO) ---
        // Esse VBox vai cuidar de tudo que precisa ficar no meio (Logo, Título, Copyright)
        VBox loginPanel = new VBox(20); // Aumentei um pouco o espaçamento geral
        loginPanel.setPadding(new Insets(40));
        loginPanel.setAlignment(Pos.CENTER); // <--- O SEGREDO: Tudo aqui fica no centro por padrão
        loginPanel.setStyle("-fx-background-color: white");

        // --- BLOCO 1: LOGO E TÍTULO (Centralizados) ---

        // Logo
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* Trata erro se precisar */ }

        // Título "Login"
        Label lblLogin = new Label("Login");
        lblLogin.setFont(FONTE_TITULO);
        lblLogin.setTextFill(COR_AZUL_NOTURNO);

        // --- BLOCO 2: CONTAINER DO FORMULÁRIO (Alinhado à ESQUERDA) ---
        // Criamos uma "caixa dentro da caixa" só para os inputs
        VBox formContainer = new VBox(10); // Espaçamento menor entre label e campo
        formContainer.setAlignment(Pos.CENTER_LEFT); // <--- O SEGREDO: Aqui dentro tudo fica à esquerda
        formContainer.setMaxWidth(Double.MAX_VALUE); // Garante que o container ocupe a largura toda

        Label lblEmail = new Label("Endereço de email");
        lblEmail.setFont(FONTE_SUBTITULO);
        lblEmail.setTextFill(COR_TEXTO_PRETO);

        TextField txtEmail = new TextField();
        txtEmail.setFont(FONTE_PEQUENA);
        txtEmail.setPromptText("seu.email@exemplo.com");
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

        // Adicionamos os campos dentro deste container ESQUERDO
        formContainer.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, lblMensagemErro);

        // --- BLOCO 3: BOTÃO E RODAPÉ (Centralizados) ---

        Button btnLogar = new Button("Log In");
        // Selecionando a fonte
        btnLogar.setFont(FONTE_BOTAO);

        // 2. Ajuste de altura (Botão de 30px de fonte precisa ser mais alto que 45px)
        btnLogar.setPrefHeight(45);
        btnLogar.setMaxWidth(Double.MAX_VALUE);

        // 3. CSS (Removi o '-fx-font-size' daqui, pois já setamos no Java acima)
        btnLogar.setStyle(
                "-fx-background-color: " + HEX_VERMELHO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;" +
                        "-fx-border-radius: 4;"
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Empurra o copyright para o fundo

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));
        // Como lblCopyright está no loginPanel (Pai), ele ficará centralizado automaticamente.

        // --- MONTAGEM DO PAINEL ESQUERDO ---
        // Observe a ordem: Logo(Centro), Título(Centro), Form(Esquerda), Botão, Spacer, Copy(Centro)
        loginPanel.getChildren().addAll(
                logoView,
                lblLogin,
                formContainer, // Adicionamos a caixa de formulário aqui no meio
                btnLogar,
                spacer,
                lblCopyright
        );

        // --- LADO DIREITO (Imagem Blindada e Proporcional) ---
        Region backgroundRegion = new Region();
        try {
            Image imgBackground = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            //Image imgBackground = new Image(getClass().getResourceAsStream("/images/cidade2.png"));
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







        // --- 3. PAINEL PRINCIPAL: HBox (Login + Imagem) ---
        HBox root = new HBox();
        root.getChildren().addAll(loginPanel, backgroundRegion);

        HBox.setHgrow(loginPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);

        // Restrições de largura do painel de login
        loginPanel.setMaxWidth(500);
        loginPanel.setMinWidth(400);

        // --- Lógica do Botão ---
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