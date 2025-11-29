package com.pi.grafos.view.screens;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.service.FuncionarioService;
import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.COR_VERMELHO_RESGATE;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class GestaoFuncionariosView {


    @Autowired
    private FuncionarioService funcionarioService;
            

    private VBox contentArea;
    private Button btnCadastrar;
    private Button btnEditar;
    private Button btnExcluir;

    // --- MOCK DE DADOS ---
    private static class FuncionarioMock {
        String id, nome, cargo, email, telefone;
        public FuncionarioMock(String id, String nome, String cargo, String email, String telefone) {
            this.id = id; this.nome = nome; this.cargo = cargo; this.email = email; this.telefone = telefone;
        }
    }

    private List<FuncionarioMock> listaFuncionarios = new ArrayList<>();

    public GestaoFuncionariosView() {
        listaFuncionarios.add(new FuncionarioMock("1", "Dr. João Silva", "MÉDICO", "joao@email.com", "62 9999-9999"));
        listaFuncionarios.add(new FuncionarioMock("2", "Maria Souza", "ENFERMEIRA", "maria@email.com", "62 8888-8888"));
    }

    public VBox criarView() {
        // --- CONTAINER PRINCIPAL ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // --- CABEÇALHO ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Gestão de Colaboradores");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Gerencie a equipe médica e operacional.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // --- BARRA DE FERRAMENTAS (CRUD) ---
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));

        // SOLUÇÃO DOS EMOJIS: Usando o novo método criarBotaoCrudComEmoji
        btnCadastrar = criarBotaoCrudComEmoji("CADASTRAR", "➕", "#10B981", "#059669");
        btnCadastrar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnCadastrar);
            mostrarFormularioCadastro(null);
        });

        btnEditar = criarBotaoCrudComEmoji("EDITAR", "✏️", "#F59E0B", "#D97706");
        btnEditar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnEditar);
            mostrarListaSelecao("EDITAR");
        });

        btnExcluir = criarBotaoCrudComEmoji("EXCLUIR", "🗑️", "#EF4444", "#B91C1C");
        btnExcluir.setOnAction(e -> {
            atualizarSelecaoBotoes(btnExcluir);
            mostrarListaSelecao("EXCLUIR");
        });

        toolBar.getChildren().addAll(btnCadastrar, btnEditar, btnExcluir);

        // --- ÁREA DE CONTEÚDO ---
        contentArea = new VBox(20);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Inicia na tela de cadastro
        btnCadastrar.fire();

        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =============================================================================================
    // TELAS
    // =============================================================================================

    private void mostrarFormularioCadastro(FuncionarioMock funcionario) {
        contentArea.getChildren().clear();

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(800);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblAcao = new Label(funcionario == null ? "Novo Colaborador" : "Editando: " + funcionario.nome);
        lblAcao.setFont(FONTE_SUBTITULO);
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // 1. Nome
        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome Completo");
        if(funcionario != null) txtNome.setText(funcionario.nome);
        VBox boxNome = criarCampoInput("Nome Completo", txtNome);

        // 2. Cargo
        ComboBox<String> comboCargo = new ComboBox<>();
        comboCargo.setItems(FXCollections.observableArrayList("MEDICO", "ENFERMEIRO", "CONDUTOR"));
        comboCargo.setPromptText("Selecione...");
        if(funcionario != null) comboCargo.setValue(funcionario.cargo);
        VBox boxCargo = criarCampoInput("Cargo / Função", comboCargo);

        // 3. Email e Telefone (LADO A LADO - HBox)
        HBox rowContato = new HBox(20);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("email@exemplo.com");
        if(funcionario != null) txtEmail.setText(funcionario.email);
        VBox boxEmail = criarCampoInput("E-mail", txtEmail);

        TextField txtTel = new TextField();
        txtTel.setPromptText("(62) 99999-9999");
        if(funcionario != null) txtTel.setText(funcionario.telefone);
        VBox boxTel = criarCampoInput("Telefone", txtTel);

        // Faz os dois crescerem igualmente
        HBox.setHgrow(boxEmail, Priority.ALWAYS);
        HBox.setHgrow(boxTel, Priority.ALWAYS);

        rowContato.getChildren().addAll(boxEmail, boxTel);

        // 4. Botão Salvar (Com Correção de Hover e Fonte)
        Button btnSalvar = new Button(funcionario == null ? "SALVAR CADASTRO" : "ATUALIZAR DADOS");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50); // Altura confortável
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        String styleNormal = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 18px; -fx-font-family: 'Poppins';";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 18px; -fx-font-family: 'Poppins';";

        btnSalvar.setStyle(styleNormal);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleNormal));

        btnSalvar.setOnAction(e -> {

            String nomeFuncionario = txtNome.getText();
            String funcaoFunconario = comboCargo.getValue();
            Cargos cargoEnum = Cargos.valueOf(funcaoFunconario);

            String emailFuncionario = txtEmail.getText();
            String telFuncionario = txtTel.getText();

            if(nomeFuncionario.isEmpty() || funcaoFunconario == null || emailFuncionario.isEmpty() || telFuncionario.isEmpty()){
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Campos vazios");
                alert.setHeaderText(null);
                alert.setContentText("Preencha todos os campos antes de salvar!");
                alert.showAndWait();
                return;
            }

            try {
            funcionarioService.cadastrarFuncionario(nomeFuncionario, cargoEnum);

            } catch (Exception error) {
                error.printStackTrace();
            }
            
        });

        formCard.getChildren().addAll(lblAcao, boxNome, boxCargo, rowContato, btnSalvar);
        contentArea.getChildren().add(formCard);
    }

    private void mostrarListaSelecao(String modo) {
        contentArea.getChildren().clear();
        Label lblInstrucao = new Label(modo.equals("EDITAR") ? "Selecione para editar:" : "Selecione para excluir:");
        lblInstrucao.setFont(FONTE_CORPO);
        lblInstrucao.setTextFill(COR_TEXTO_CLARO);
        contentArea.getChildren().add(lblInstrucao);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox listaContainer = new VBox(10);
        listaContainer.setPadding(new Insets(5));

        for (FuncionarioMock func : listaFuncionarios) {
            listaContainer.getChildren().add(criarCardFuncionario(func, modo));
        }
        scroll.setContent(listaContainer);
        contentArea.getChildren().add(scroll);
    }

    // =============================================================================================
    // COMPONENTES (CORRIGIDOS)
    // =============================================================================================

    /**
     * Versão Final: Corrige espaçamento fantasma e alinhamento visual
     */
    private Button criarBotaoCrudComEmoji(String texto, String emoji, String corNormal, String corEscura) {
        Button btn = new Button();
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        btn.setUserData(new String[]{corNormal, corEscura});

        // 1. O Emoji (Ícone)
        Text txtEmoji = new Text(emoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 20)); // Aumentei para 20px
        txtEmoji.setFill(Color.WHITE);
        // O SEGREDO: Remove as bordas transparentes da fonte do emoji
        txtEmoji.setBoundsType(TextBoundsType.VISUAL);

        // 2. O Texto (Rótulo)
        Label lblTexto = new Label(texto);
        // Aumentei para 18px para ficar legível e proporcional
        lblTexto.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        lblTexto.setTextFill(Color.WHITE);
        // Garante que o texto não tenha padding extra atrapalhando
        lblTexto.setPadding(Insets.EMPTY);

        // 3. O Container
        HBox container = new HBox(8); // Espaçamento de 8px (Fica elegante, nem grudado nem longe)
        container.setAlignment(Pos.CENTER); // Centraliza o conjunto dentro do botão

        // Adiciona os itens
        container.getChildren().addAll(txtEmoji, lblTexto);

        btn.setGraphic(container);

        // Estilo Base
        String style = "-fx-background-color: " + corNormal + "; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(style);

        return btn;
    }

    private HBox criarCardFuncionario(FuncionarioMock func, String modo) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

        Color corIcone = func.cargo.equals("MÉDICO") ? COR_VERMELHO_RESGATE : (func.cargo.equals("CONDUTOR") ? Color.web("#F59E0B") : Color.web("#10B981"));
        Circle icone = new Circle(20, corIcone);
        Label letra = new Label(func.cargo.substring(0,1));
        letra.setTextFill(Color.WHITE);
        letra.setFont(FONTE_BOTAO2);
        StackPane iconStack = new StackPane(icone, letra);

        VBox info = new VBox(2);
        Label lblNome = new Label(func.nome);
        lblNome.setFont(FONTE_SUBTITULO);
        lblNome.setTextFill(COR_AZUL_NOTURNO);
        Label lblCargo = new Label(func.cargo);
        lblCargo.setFont(FONTE_PEQUENA);
        lblCargo.setTextFill(COR_TEXTO_CLARO);
        info.getChildren().addAll(lblNome, lblCargo);

        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnAcao = new Button(modo.equals("EDITAR") ? "EDITAR" : "EXCLUIR");
        String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
        btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        card.getChildren().addAll(iconStack, info, btnAcao);

        card.setOnMouseClicked(e -> {
            if (modo.equals("EDITAR")) mostrarFormularioCadastro(func);
            else {
                contentArea.getChildren().remove(card); // Mock delete
            }
        });
        return card;
    }

    private void atualizarSelecaoBotoes(Button btnAtivo) {
        Button[] todos = {btnCadastrar, btnEditar, btnExcluir};
        for (Button b : todos) {
            String[] cores = (String[]) b.getUserData();
            if (b == btnAtivo) {
                b.setStyle("-fx-background-color: " + cores[1] + "; -fx-background-radius: 8; -fx-effect: innerShadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
            } else {
                b.setStyle("-fx-background-color: " + cores[0] + "; -fx-background-radius: 8;");
            }
        }
    }

    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));
        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        v.getChildren().addAll(l, input);
        return v;
    }
}