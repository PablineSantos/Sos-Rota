package com.pi.grafos.view.screens;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.service.EquipeService;
import com.pi.grafos.service.FuncionarioService;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
@Scope("prototype")
public class GestaoEquipesView {

    private VBox contentArea;
    private Button btnCadastrar, btnEditar, btnExcluir;

    // Componentes Globais
    private VBox containerMembros;
    private TextField txtNomeEquipe;
    
    private final FuncionarioService fService;
    private final EquipeService eService;

    // --- MOCK DE DADOS ---
    private static class MembroMock {
        String nome;
        Cargos funcao;
        public MembroMock(String nome, Cargos funcao) { this.nome = nome; this.funcao = funcao; }
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

    public GestaoEquipesView(EquipeService eService, FuncionarioService fService) {
        // Dados de teste
        EquipeMock eq1 = new EquipeMock("1", "Equipe Alpha (UTI)");
        eq1.membros.add(new MembroMock("Carlos Pereira", Cargos.CONDUTOR));
        eq1.membros.add(new MembroMock("Maria Souza", Cargos.ENFERMEIRO));
        eq1.membros.add(new MembroMock("Dr. João Silva", Cargos.MEDICO));
        listaEquipes.add(eq1);

        EquipeMock eq2 = new EquipeMock("2", "Equipe Beta (Básica)");
        eq2.membros.add(new MembroMock("Pedro Santos", Cargos.CONDUTOR));
        eq2.membros.add(new MembroMock("Ana Clara", Cargos.ENFERMEIRO));
        listaEquipes.add(eq2);

        this.fService = fService;
        this.eService = eService;
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
        Button btnSalvar = new Button("SALVAR EQUIPE");
        btnSalvar.setFont(FONTE_BOTAO2);

        btnSalvar.setDisable(true);
        
        // Botão "Adicionar Novo Membro"
        Button btnAddMembro = new Button("+ Adicionar Membro");
        btnAddMembro.setFont(FONTE_CORPO);
        btnAddMembro.setMaxWidth(Double.MAX_VALUE);
        btnAddMembro.setStyle("-fx-background-color: white; -fx-text-fill: " + HEX_VERMELHO + "; -fx-border-color: " + HEX_VERMELHO + "; -fx-border-radius: 5; -fx-cursor: hand; -fx-border-style: dashed;");
        btnAddMembro.setOnAction(e -> adicionarLinhaMembro(null, false, btnSalvar));
        
        // Botão Salvar
        if (equipe != null) {
            for (MembroMock m : equipe.membros) {
                adicionarLinhaMembro(m.funcao, false, btnSalvar);
            }
        } else {
            adicionarLinhaMembro(Cargos.CONDUTOR, false, btnSalvar);
            adicionarLinhaMembro(Cargos.MEDICO, false, btnSalvar);
            adicionarLinhaMembro(Cargos.ENFERMEIRO, false, btnSalvar);

            // private void adicionarLinhaMembro(Cargos cargoFixo, String nomePreSelecionado, boolean podeExcluir, Button btnSalvar)
        }
        btnSalvar.setPrefHeight(50);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        String styleBase = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";

        btnSalvar.setStyle(styleBase);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleBase));

        btnSalvar.setOnAction(e -> {

            List<Funcionario> j = extrairFuncionariosSelecionados();

            eService.cadastrarEquipe(txtNomeEquipe.getText(), j);
        
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
    
    private void adicionarLinhaMembro(Cargos cargoFixo, boolean podeExcluir, Button btnSalvar) {
        HBox linha = new HBox(10);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8; -fx-background-radius: 5; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        // Combo de Função
        ComboBox<Cargos> comboFuncao = new ComboBox<>();
        comboFuncao.setItems(FXCollections.observableArrayList(Cargos.values()));
        comboFuncao.setPrefWidth(150);
        comboFuncao.setPrefHeight(40);
        comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");

        // Combo de Profissional
        ComboBox<String> comboNome = new ComboBox<>();
        comboNome.setPromptText("Selecione o Profissional...");
        comboNome.setMaxWidth(Double.MAX_VALUE);
        comboNome.setPrefHeight(40);
        comboNome.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");
        HBox.setHgrow(comboNome, Priority.ALWAYS);

        if (cargoFixo != null) {
            // Cargo pré-definido (modo edição ou template inicial)
            comboFuncao.setValue(cargoFixo);
            comboFuncao.setDisable(true);
            comboFuncao.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
            comboFuncao.addEventFilter(KeyEvent.KEY_PRESSED, Event::consume);
            comboFuncao.setFocusTraversable(false);
            comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px; -fx-opacity: 1; -fx-background-color: #E2E8F0; -fx-text-fill: black;");
            
            // Carrega funcionários do cargo específico
            carregarFuncionariosPorCargo(cargoFixo, comboNome);
                // Modo "adicionar novo" - deixa combo vazio até selecionar função
                comboFuncao.setPromptText("Selecione...");
                
                // Listener: quando selecionar uma função, carrega os funcionários
                comboFuncao.setOnAction(e -> {
                    Cargos cargoSelecionado = comboFuncao.getValue();
                    if (cargoSelecionado != null) {
                        carregarFuncionariosPorCargo(cargoSelecionado, comboNome);
                    } else {
                        comboNome.getItems().clear();
                        comboNome.setValue(null);
                    }
                    validarFormulario(btnSalvar, null, null, null);
                });
            
            }
            // Listener para validar quando selecionar um profissional
            comboNome.setOnAction(e -> validarFormulario(btnSalvar, null, null, null));

            linha.getChildren().addAll(comboFuncao, comboNome);

            if (podeExcluir) {
                Button btnRemover = new Button("✕");
                btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
                btnRemover.setOnAction(e -> {
                    containerMembros.getChildren().remove(linha);
                    validarFormulario(btnSalvar, null, null, null);
                });
                linha.getChildren().add(btnRemover);
            }

            containerMembros.getChildren().add(linha);
    }

    // Método auxiliar para carregar funcionários
    private void carregarFuncionariosPorCargo(Cargos cargo, ComboBox<String> comboNome) {
        try {
            List<Funcionario> funcionarios = fService.findByCargos(cargo);
            List<String> nomes = funcionarios.stream()
                    .map(Funcionario::getNomeFuncionario)
                    .toList();
            
            comboNome.setItems(FXCollections.observableArrayList(nomes));
            comboNome.setPromptText("Selecione o Profissional...");
            
        } catch (RuntimeException e) {
            // Se não encontrar funcionários, trata como lista vazia (não é erro!)
            if (e.getMessage() != null && e.getMessage().contains("Nenhum Funcionário encontrado")) {
                comboNome.setItems(FXCollections.observableArrayList());
                comboNome.setPromptText("Nenhum funcionário cadastrado");
            } else {
                // Outros erros reais (conexão, etc)
                System.err.println("Erro ao carregar funcionários: " + e.getMessage());
                comboNome.setItems(FXCollections.observableArrayList());
                comboNome.setPromptText("Erro ao carregar");
            }
        }
    }

    // Método de validação do formulário
    private boolean validarFormulario(Button btnSalvar, String styleBase, String styleHover, String styleDisabled) {
        // Verifica se os 3 cargos obrigatórios estão preenchidos
        boolean temCondutor = false;
        boolean temMedico = false;
        boolean temEnfermeiro = false;

        for (Node node : containerMembros.getChildren()) {
            if (node instanceof HBox linha) {
                ComboBox<Cargos> comboFuncao = null;
                ComboBox<String> comboNome = null;

                // Pega os combos da linha
                for (Node child : linha.getChildren()) {
                    if (child instanceof ComboBox<?>) {
                        if (comboFuncao == null) {
                            comboFuncao = (ComboBox<Cargos>) child;
                        } else {
                            comboNome = (ComboBox<String>) child;
                        }
                    }
                }

                // Verifica se ambos estão preenchidos
                if (comboFuncao != null && comboNome != null) {
                    Cargos cargo = comboFuncao.getValue();
                    String nome = comboNome.getValue();

                    if (cargo != null && nome != null && !nome.isEmpty()) {
                        if (cargo == Cargos.CONDUTOR) temCondutor = true;
                        if (cargo == Cargos.MEDICO) temMedico = true;
                        if (cargo == Cargos.ENFERMEIRO) temEnfermeiro = true;
                    }
                }
            }
        }

        boolean formularioValido = temCondutor && temMedico && temEnfermeiro;

        // Atualiza o estado do botão
        if (formularioValido) {
            btnSalvar.setDisable(false);
            if (styleBase != null) {
                btnSalvar.setStyle(styleBase);
                btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
                btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleBase));
            }
        } else {
            btnSalvar.setDisable(true);
            if (styleDisabled != null) {
                btnSalvar.setStyle(styleDisabled);
                btnSalvar.setOnMouseEntered(null);
                btnSalvar.setOnMouseExited(null);
            }
        }

        return formularioValido;
    }

    private List<Funcionario> extrairFuncionariosSelecionados() {
        List<Funcionario> funcionariosSelecionados = new ArrayList<>();
        
        // Percorre todas as linhas de membros no container
        for (Node node : containerMembros.getChildren()) {
            if (node instanceof HBox linha) {
                ComboBox<Cargos> comboFuncao = null;
                ComboBox<String> comboNome = null;
                
                // Extrai os combos da linha
                for (Node child : linha.getChildren()) {
                    if (child instanceof ComboBox<?>) {
                        if (comboFuncao == null) {
                            comboFuncao = (ComboBox<Cargos>) child;
                        } else {
                            comboNome = (ComboBox<String>) child;
                        }
                    }
                }
                
                // Se ambos os combos estão preenchidos, busca o funcionário
                if (comboFuncao != null && comboNome != null) {
                    Cargos cargo = comboFuncao.getValue();
                    String nomeSelecionado = comboNome.getValue();
                    
                    if (cargo != null && nomeSelecionado != null && !nomeSelecionado.isEmpty()) {
                        try {
                            // Busca todos os funcionários desse cargo
                            List<Funcionario> funcionariosDoCargo = fService.findByCargos(cargo);
                            
                            // Encontra o funcionário específico pelo nome
                            funcionariosDoCargo.stream()
                                .filter(f -> f.getNomeFuncionario().equals(nomeSelecionado))
                                .findFirst()
                                .ifPresent(funcionariosSelecionados::add);
                                
                        } catch (Exception e) {
                            System.err.println("Erro ao buscar funcionário: " + nomeSelecionado);
                        }
                    }
                }
            }
        }
        
        return funcionariosSelecionados;
    }

}