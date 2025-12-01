package com.pi.grafos.view.screens;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.event.Event;

import java.util.ArrayList;
import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

public class GestaoEquipesView {

    private VBox contentArea;
    private Button btnCadastrar, btnEditar, btnExcluir;

    // Componentes Globais
    private VBox containerMembros;
    private TextField txtNomeEquipe;

    // --- MOCK DE DADOS ---
    private static class MembroMock {
        String nome, funcao;
        public MembroMock(String nome, String funcao) { this.nome = nome; this.funcao = funcao; }
    }

    private static class EquipeMock {
        String id, nome;
        List<MembroMock> membros;
        public EquipeMock(String id, String nome) {
            this.id = id; this.nome = nome;
            this.membros = new ArrayList<>();
        }
    }

    private List<EquipeMock> listaEquipes = new ArrayList<>();

    public GestaoEquipesView() {
        // Dados de teste
        EquipeMock eq1 = new EquipeMock("1", "Equipe Alpha (UTI)");
        eq1.membros.add(new MembroMock("Carlos Pereira", "CONDUTOR"));
        eq1.membros.add(new MembroMock("Maria Souza", "ENFERMEIRO"));
        eq1.membros.add(new MembroMock("Dr. João Silva", "MÉDICO"));
        listaEquipes.add(eq1);

        EquipeMock eq2 = new EquipeMock("2", "Equipe Beta (Básica)");
        eq2.membros.add(new MembroMock("Pedro Santos", "CONDUTOR"));
        eq2.membros.add(new MembroMock("Ana Clara", "ENFERMEIRO"));
        listaEquipes.add(eq2);
    }

    public VBox criarView() {
        // --- BASE ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // --- CABEÇALHO ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(Region.USE_PREF_SIZE); // Garante que não encolha

        Label lblTitulo = new Label("Gestão de Equipes");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Monte as tripulações e gerencie escalas.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // --- TOOLBAR ---
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));

        // Impede que a barra de botões seja espremida
        toolBar.setMinHeight(80);
        VBox.setVgrow(toolBar, Priority.NEVER);

        btnCadastrar = criarBotaoCrudComEmoji("CADASTRAR", "➕", "#10B981", "#059669");
        btnCadastrar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnCadastrar);
            mostrarFormulario(null);
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

        // --- CONTEÚDO ---
        contentArea = new VBox(15);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Inicia no cadastro
        btnCadastrar.fire();

        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =============================================================================================
    // LÓGICA DE TELAS
    // =============================================================================================

    private void mostrarFormulario(EquipeMock equipe) {
        contentArea.getChildren().clear();

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(850);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label lblAcao = new Label(equipe == null ? "Nova Equipe" : "Editando: " + equipe.nome);
        lblAcao.setFont(FONTE_SUBTITULO);
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // 1. Nome da Equipe
        txtNomeEquipe = new TextField();
        txtNomeEquipe.setPromptText("Ex: Equipe Beta - Básica");
        if(equipe != null) txtNomeEquipe.setText(equipe.nome);
        VBox boxNome = criarCampoInput("Identificação da Equipe", txtNomeEquipe);


        // 2. Container de Membros
        Label lblMembros = new Label("Membros");
        lblMembros.setFont(FONTE_CORPO);
        lblMembros.setTextFill(Color.web("#64748B")); // cinza igual ao input

        containerMembros = new VBox(10);
        ScrollPane scrollMembros = new ScrollPane(containerMembros);
        scrollMembros.setFitToWidth(true);
        scrollMembros.setPrefHeight(200);
        scrollMembros.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        // Lógica de Preenchimento da Lista
        if (equipe != null) {
            for (MembroMock m : equipe.membros) {
                adicionarLinhaMembro(m.funcao, m.nome, true);
            }
        } else {
            adicionarLinhaMembro("CONDUTOR", null, false);
            adicionarLinhaMembro("MÉDICO", null, false);
            adicionarLinhaMembro("ENFERMEIRO", null, false);
        }

        // Botão "Adicionar Novo Membro"
        Button btnAddMembro = new Button("+ Adicionar Membro");
        btnAddMembro.setFont(FONTE_CORPO);
        btnAddMembro.setMaxWidth(Double.MAX_VALUE);
        btnAddMembro.setStyle("-fx-background-color: white; -fx-text-fill: " + HEX_VERMELHO + "; -fx-border-color: " + HEX_VERMELHO + "; -fx-border-radius: 5; -fx-cursor: hand; -fx-border-style: dashed;");
        btnAddMembro.setOnAction(e -> adicionarLinhaMembro(null, null, true));

        // Botão Salvar
        Button btnSalvar = new Button("SALVAR EQUIPE");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        String styleBase = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";

        btnSalvar.setStyle(styleBase);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleBase));

        btnSalvar.setOnAction(e -> {
            btnSalvar.setText("SALVO COM SUCESSO!");
            btnSalvar.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;");
        });

        formCard.getChildren().addAll(lblAcao, boxNome, lblMembros, scrollMembros, btnAddMembro, btnSalvar);
        contentArea.getChildren().add(formCard);
    }

    /**
     * TELA 2: Lista de Equipes (Com CARD VISUAL)
     */
    private void mostrarListaSelecao(String modo) {
        contentArea.getChildren().clear();

        Label lblInstrucao = new Label(modo.equals("EDITAR") ? "Selecione uma equipe para editar:" : "Selecione uma equipe para excluir:");
        lblInstrucao.setFont(FONTE_CORPO);
        lblInstrucao.setTextFill(COR_TEXTO_CLARO);
        contentArea.getChildren().add(lblInstrucao);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox lista = new VBox(10);
        lista.setPadding(new Insets(5));

        for (EquipeMock eq : listaEquipes) {
            // RESTAURAÇÃO DO CARD VISUAL COMPLETO
            HBox card = new HBox(15);
            card.setPadding(new Insets(20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

            // Ícone
            Circle icone = new Circle(25, COR_AZUL_NOTURNO);
            Label letra = new Label("E");
            letra.setTextFill(Color.WHITE);
            letra.setFont(FONTE_BOTAO2);
            StackPane iconStack = new StackPane(icone, letra);

            // Informações da Equipe
            VBox info = new VBox(5);
            Label lblNome = new Label(eq.nome);
            lblNome.setFont(FONTE_SUBTITULO);
            lblNome.setTextFill(COR_AZUL_NOTURNO);

            // Resumo dos membros (ex: 3 membros)
            Label lblQtd = new Label(eq.membros.size() + " Profissionais alocados");
            lblQtd.setFont(FONTE_PEQUENA);
            lblQtd.setTextFill(COR_TEXTO_CLARO);

            info.getChildren().addAll(lblNome, lblQtd);
            HBox.setHgrow(info, Priority.ALWAYS);

            // Botão Lateral de Ação
            Button btnAcao = new Button(modo);
            String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
            btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

            card.getChildren().addAll(iconStack, info, btnAcao);

            // Ação do Clique no Card
            card.setOnMouseClicked(e -> {
                if(modo.equals("EDITAR")) mostrarFormulario(eq);
                else mostrarConfirmacaoExclusao(eq);
            });

            lista.getChildren().add(card);
        }

        scroll.setContent(lista);
        contentArea.getChildren().add(scroll);
    }

    /**
     * TELA 3: Popup de Exclusão
     */
    private void mostrarConfirmacaoExclusao(EquipeMock equipe) {
        contentArea.getChildren().clear();

        VBox confirmCard = new VBox(20);
        confirmCard.setMaxWidth(600);
        confirmCard.setPadding(new Insets(30));
        confirmCard.setAlignment(Pos.CENTER);
        confirmCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 5); -fx-border-color: #EF4444; -fx-border-width: 2; -fx-border-radius: 12;");

        Label lblAlert = new Label("Confirmar Exclusão");
        lblAlert.setFont(FONTE_TITULO);
        lblAlert.setTextFill(Color.web("#EF4444"));

        Label lblMsg = new Label("Tem certeza que deseja remover esta equipe? Esta ação é irreversível.");
        lblMsg.setFont(FONTE_CORPO);

        // Detalhes da Equipe (Visualização)
        VBox detalhes = new VBox(10);
        detalhes.setStyle("-fx-background-color: #FEF2F2; -fx-padding: 15; -fx-background-radius: 8;");
        detalhes.getChildren().add(new Label("Equipe: " + equipe.nome));
        detalhes.getChildren().add(new Label("Integrantes:"));
        for(MembroMock m : equipe.membros) {
            detalhes.getChildren().add(new Label(" • " + m.nome + " (" + m.funcao + ")"));
        }

        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Cancelar");
        btnCancel.setPrefHeight(45);
        btnCancel.setPrefWidth(120);
        btnCancel.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-text-fill: #64748B; -fx-background-radius: 5; -fx-border-radius: 5; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> mostrarListaSelecao("EXCLUIR")); // Volta pra lista

        Button btnConfirm = new Button("EXCLUIR");
        btnConfirm.setPrefHeight(45);
        btnConfirm.setPrefWidth(150);
        btnConfirm.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnConfirm.setOnAction(e -> {
            listaEquipes.remove(equipe);
            mostrarListaSelecao("EXCLUIR");
        });

        boxBtn.getChildren().addAll(btnCancel, btnConfirm);
        confirmCard.getChildren().addAll(lblAlert, lblMsg, detalhes, boxBtn);

        contentArea.getChildren().add(confirmCard);
    }

    // =============================================================================================
    // COMPONENTES AUXILIARES
    // =============================================================================================

    private void adicionarLinhaMembro(String cargoFixo, String nomePreSelecionado, boolean podeExcluir) {
        HBox linha = new HBox(10);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8; -fx-background-radius: 5; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        // Combo de Função
        ComboBox<String> comboFuncao = new ComboBox<>();

        comboFuncao.setItems(FXCollections.observableArrayList("CONDUTOR", "ENFERMEIRO", "MÉDICO"));
        
        comboFuncao.setPrefWidth(150);
        comboFuncao.setPrefHeight(40);
        comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");

        if (cargoFixo != null) {
            comboFuncao.setValue(cargoFixo);
            // Trava visualmente mas mantendo legibilidade
            comboFuncao.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
            comboFuncao.addEventFilter(KeyEvent.KEY_PRESSED, Event::consume);
            comboFuncao.setFocusTraversable(false);
            comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px; -fx-opacity: 1; -fx-background-color: #E2E8F0; -fx-text-fill: black;");
        } else {
            comboFuncao.setPromptText("Selecione...");
        }

        // Combo de Profissional
        ComboBox<String> comboNome = new ComboBox<>();
        comboNome.setPromptText("Selecione o Profissional...");
        comboNome.setItems(FXCollections.observableArrayList(
                "Dr. João Silva (CRM 123)",
                "Enf. Maria Souza (COREN 456)",
                "Mot. Carlos Pereira (CNH B)",
                "Mot. Roberto Carlos",
                "Dra. Ana Julia"
        ));
        comboNome.setMaxWidth(Double.MAX_VALUE);
        comboNome.setPrefHeight(40);
        comboNome.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");
        if (nomePreSelecionado != null) comboNome.setValue(nomePreSelecionado);
        HBox.setHgrow(comboNome, Priority.ALWAYS);

        linha.getChildren().addAll(comboFuncao, comboNome);

        if (podeExcluir) {
            Button btnRemover = new Button("✕");
            btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
            btnRemover.setOnAction(e -> containerMembros.getChildren().remove(linha));
            linha.getChildren().add(btnRemover);
        }

        containerMembros.getChildren().add(linha);
    }

    private Button criarBotaoCrudComEmoji(String texto, String emoji, String corNormal, String corEscura) {
        Button btn = new Button();
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        btn.setMinHeight(50);
        btn.setUserData(new String[]{corNormal, corEscura});

        Text txtEmoji = new Text(emoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 20));
        txtEmoji.setFill(Color.WHITE);
        txtEmoji.setBoundsType(TextBoundsType.VISUAL);

        Label lblTexto = new Label(texto);
        lblTexto.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        lblTexto.setTextFill(Color.WHITE);
        lblTexto.setPadding(Insets.EMPTY);

        HBox container = new HBox(8);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(txtEmoji, lblTexto);

        btn.setGraphic(container);
        btn.setStyle("-fx-background-color: " + corNormal + "; -fx-background-radius: 8; -fx-cursor: hand;");
        return btn;
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
        VBox v = new VBox(5);
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