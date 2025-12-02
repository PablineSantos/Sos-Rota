package com.pi.grafos.view.screens;

import java.util.Comparator;
import java.util.List;

import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.service.FuncionarioService;
import com.pi.grafos.view.components.Alerta;
import com.pi.grafos.view.components.AlertaConfirmacao; // <--- NÃO ESQUEÇA DESTE IMPORT

import static com.pi.grafos.view.styles.AppStyles.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

public class GestaoFuncionariosView {

    private final FuncionarioService funcionarioService;

    private VBox contentArea;
    private Button btnCadastrar, btnEditar, btnExcluir;

    public GestaoFuncionariosView(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    public VBox criarView() {
        // --- BASE ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // --- HEADER ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Gestão de Colaboradores");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Gerencie a equipe médica e operacional.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // --- TOOLBAR ---
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));

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

        // --- CONTEÚDO ---
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

    private void mostrarFormularioCadastro(Funcionario funcionario) {
        contentArea.getChildren().clear();

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(800);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblAcao = new Label(funcionario == null ? "Novo Colaborador" : "Editando: " + funcionario.getNomeFuncionario());
        lblAcao.setFont(FONTE_SUBTITULO);
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // 1. Nome
        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome Completo");
        if(funcionario != null) txtNome.setText(funcionario.getNomeFuncionario());
        VBox boxNome = criarCampoInput("Nome Completo", txtNome);

        // 2. Cargo
        ComboBox<String> comboCargo = new ComboBox<>();
        comboCargo.setItems(FXCollections.observableArrayList("MEDICO", "ENFERMEIRO", "CONDUTOR"));
        comboCargo.setPromptText("Selecione...");
        if(funcionario != null && funcionario.getCargo() != null) {
            comboCargo.setValue(funcionario.getCargo().name());
        }
        VBox boxCargo = criarCampoInput("Cargo / Função", comboCargo);

        // 3. Email e Telefone
        HBox rowContato = new HBox(20);
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("email@exemplo.com");
        if(funcionario != null) txtEmail.setText(funcionario.getEmail());
        VBox boxEmail = criarCampoInput("E-mail", txtEmail);

        TextField txtTel = new TextField();
        txtTel.setPromptText("(62) 99999-9999");
        if(funcionario != null) txtTel.setText(funcionario.getTelefone());
        VBox boxTel = criarCampoInput("Telefone", txtTel);

        HBox.setHgrow(boxEmail, Priority.ALWAYS);
        HBox.setHgrow(boxTel, Priority.ALWAYS);
        rowContato.getChildren().addAll(boxEmail, boxTel);

        // 4. Botão Salvar
        Button btnSalvar = new Button(funcionario == null ? "SALVAR CADASTRO" : "ATUALIZAR DADOS");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        String styleNormal = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        btnSalvar.setStyle(styleNormal);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleNormal));

        btnSalvar.setOnAction(e -> {
            try {
                String nome = txtNome.getText();
                String cargoStr = comboCargo.getValue();
                String email = txtEmail.getText();
                String tel = txtTel.getText();

                // Validação de tela
                if (nome.isEmpty() || cargoStr == null) {
                    mostrarAlerta(AlertType.WARNING, "Campos Vazios", "Nome e Cargo são obrigatórios!");
                    return;
                }

                Long idParaEditar = (funcionario == null) ? null : funcionario.getIdFuncionario();
                funcionarioService.salvarOuAtualizar(
                        idParaEditar,
                        nome,
                        Cargos.valueOf(cargoStr),
                        email,
                        tel
                );

                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Dados salvos com sucesso!");

                if(funcionario == null) {
                    txtNome.clear();
                    comboCargo.getSelectionModel().clearSelection();
                    txtEmail.clear();
                    txtTel.clear();
                }

            } catch (Exception ex) {
                mostrarAlerta(AlertType.ERROR, "Erro ao Salvar", ex.getMessage());
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

        try {
            List<Funcionario> funcionarios = funcionarioService.findAll();
            funcionarios.sort(Comparator.comparing(Funcionario::getNomeFuncionario));

            for (Funcionario func : funcionarios) {
                listaContainer.getChildren().add(criarCardFuncionario(func, modo));
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro de Conexão", "Falha ao carregar lista: " + e.getMessage());
        }

        scroll.setContent(listaContainer);
        contentArea.getChildren().add(scroll);
    }

    // =============================================================================================
    // COMPONENTES VISUAIS
    // =============================================================================================

    private HBox criarCardFuncionario(Funcionario func, String modo) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Ícone colorido por cargo
        Color corIcone = COR_AZUL_NOTURNO;
        if (func.getCargo() == Cargos.MEDICO) corIcone = COR_VERMELHO_RESGATE;
        else if (func.getCargo() == Cargos.CONDUTOR) corIcone = Color.web("#F59E0B");
        else if (func.getCargo() == Cargos.ENFERMEIRO) corIcone = Color.web("#10B981");

        // Circulo com a cor e letra de cada cargo
        Circle icone = new Circle(20, corIcone);
        String letraCargo = func.getCargo() != null ? func.getCargo().name().substring(0, 1) : "?";
        Label letra = new Label(letraCargo);
        letra.setTextFill(Color.WHITE);
        letra.setFont(FONTE_BOTAO2);
        StackPane iconStack = new StackPane(icone, letra);

        // --- Informações do Funcionário (Forçando visibilidade) ---
        VBox info = new VBox(2);
        info.setMinWidth(200); // Garante largura mínima para não sumir

        // Usei um metodo seguro para evitar null pointer
        String nomeStr = (func.getNomeFuncionario() != null) ? func.getNomeFuncionario() : "Nome não informado";
        Label lblNome = new Label(nomeStr);
        lblNome.setFont(FONTE_SUBTITULO);
        lblNome.setTextFill(COR_AZUL_NOTURNO);
        lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;"); // Força estilo CSS

        String cargoStr = (func.getCargo() != null) ? func.getCargo().name() : "Sem Cargo";
        Label lblCargo = new Label(cargoStr);
        lblCargo.setFont(FONTE_PEQUENA);
        lblCargo.setTextFill(COR_TEXTO_CLARO);
        lblCargo.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        info.getChildren().addAll(lblNome, lblCargo);
        HBox.setHgrow(info, Priority.ALWAYS); // Garante que o texto ocupe o espaço vazio

        // Botão Ação
        Button btnAcao = new Button(modo.equals("EDITAR") ? "EDITAR" : "EXCLUIR");
        String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
        btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        // Adicionei a ação diretamente no botão para garantir que o clique nele funcione
        btnAcao.setOnAction(e -> {
            executarAcaoCard(modo, func);
        });

        card.setOnMouseClicked(e -> {
            executarAcaoCard(modo, func);
        });

        card.getChildren().addAll(iconStack, info, btnAcao);
        return card;
    }

    // Metodo auxiliar para não repetir código de clique
    private void executarAcaoCard(String modo, Funcionario func) {
        if (modo.equals("EDITAR")) {
            mostrarFormularioCadastro(func);
        } else {
            confirmarExclusao(func);
        }
    }

    private void confirmarExclusao(Funcionario func) {
        AlertaConfirmacao alerta = new AlertaConfirmacao();

        // O metodo mostrar trava a execução até o usuário clicar
        boolean confirmou = alerta.mostrar(
                "Confirmar Remoção",
                "Tem certeza que deseja remover " + func.getNomeFuncionario() + "?"
        );

        if (confirmou) {
            try {
                funcionarioService.deleteFuncionario(func.getIdFuncionario());

                // Feedback de Sucesso
                new Alerta().mostrar("Sucesso", "Funcionário excluído.", Alerta.Tipo.SUCESSO);

                // Atualiza a lista
                mostrarListaSelecao("EXCLUIR");

            } catch (Exception e) {
                new Alerta().mostrar("Erro", "Não foi possível excluir: " + e.getMessage(), Alerta.Tipo.ERRO);
            }
        }
    }


    // --- Métodos auxiliares de layout ---

    private Button criarBotaoCrudComEmoji(String texto, String emoji, String corNormal, String corEscura) {
        Button btn = new Button();
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
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

    // Método Adapter para alertas simples
    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alerta.Tipo tipoCustom = (tipo == AlertType.ERROR) ? Alerta.Tipo.ERRO :
                (tipo == AlertType.WARNING ? Alerta.Tipo.AVISO : Alerta.Tipo.SUCESSO);
        new Alerta().mostrar(titulo, mensagem, tipoCustom);
    }
}