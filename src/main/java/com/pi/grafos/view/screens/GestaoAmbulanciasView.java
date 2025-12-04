package com.pi.grafos.view.screens;

import java.util.ArrayList;
import java.util.List;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.service.AmbulanciaService;
import com.pi.grafos.view.components.Alerta;
import com.pi.grafos.view.components.AlertaConfirmacao;

import static com.pi.grafos.view.styles.AppStyles.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.StringConverter;

public class GestaoAmbulanciasView {

    private final AmbulanciaService ambulanciaService;
    private final LocalizacaoRepository localizacaoRepo;

    private VBox contentArea;
    private Button btnCadastrar, btnEditar, btnExcluir;

    // Campos do Formulário (Note que removemos o comboEquipe)
    private TextField txtPlaca;
    private ComboBox<TipoAmbulancia> comboTipo;
    private ComboBox<Localizacao> comboBase;
    private ComboBox<AmbulanciaStatus> comboStatus;

    public GestaoAmbulanciasView(AmbulanciaService ambulanciaService, LocalizacaoRepository localizacaoRepo) {
        this.ambulanciaService = ambulanciaService;
        this.localizacaoRepo = localizacaoRepo;
    }

    public VBox criarView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(Region.USE_PREF_SIZE);
        Label lblTitulo = new Label("Frota de Ambulâncias");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Gerencie veículos e bases operacionais.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // Toolbar
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));
        toolBar.setMinHeight(80);
        VBox.setVgrow(toolBar, Priority.NEVER);

        btnCadastrar = criarBotaoCrudComEmoji("CADASTRAR", "🚑", "#10B981", "#059669");
        btnCadastrar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnCadastrar);
            mostrarFormulario(null);
        });

        btnEditar = criarBotaoCrudComEmoji("EDITAR", "🔧", "#F59E0B", "#D97706");
        btnEditar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnEditar);
            mostrarListaSelecao("EDITAR");
        });

        btnExcluir = criarBotaoCrudComEmoji("EXCLUIR", "🚫", "#EF4444", "#B91C1C");
        btnExcluir.setOnAction(e -> {
            atualizarSelecaoBotoes(btnExcluir);
            mostrarListaSelecao("EXCLUIR");
        });

        toolBar.getChildren().addAll(btnCadastrar, btnEditar, btnExcluir);

        contentArea = new VBox(15);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        btnCadastrar.fire();

        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =============================================================================================
    // TELA 1: FORMULÁRIO ATUALIZADO (SEM EQUIPE)
    // =============================================================================================
    private void mostrarFormulario(Ambulancia ambulancia) {
        contentArea.getChildren().clear();

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(850);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label lblAcao = new Label(ambulancia == null ? "Nova Ambulância" : "Editando: " + ambulancia.getPlaca());
        lblAcao.setFont(FONTE_SUBTITULO);
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // Grid Layout
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // 1. Placa
        txtPlaca = new TextField();
        txtPlaca.setPromptText("Ex: BRA-2E19");
        if(ambulancia != null) txtPlaca.setText(ambulancia.getPlaca());
        VBox boxPlaca = criarCampoInput("Placa do Veículo", txtPlaca);

        // 2. Tipo
        comboTipo = new ComboBox<>();
        comboTipo.setItems(FXCollections.observableArrayList(TipoAmbulancia.values()));
        comboTipo.setPromptText("Selecione...");
        comboTipo.setMaxWidth(Double.MAX_VALUE);
        if(ambulancia != null) comboTipo.setValue(ambulancia.getTipoAmbulancia());
        VBox boxTipo = criarCampoInput("Tipo de Unidade", comboTipo);

        // 3. Base
        comboBase = new ComboBox<>();
        //Puxa apenas as bases que TEM ambulancias
        comboBase.setItems(FXCollections.observableArrayList(
                localizacaoRepo.findByTipo(TipoLocalizacao.BASE_AMBULANCIA)
        ));
        comboBase.setPromptText("Selecione a base...");
        comboBase.setMaxWidth(Double.MAX_VALUE);
        comboBase.setConverter(new StringConverter<Localizacao>() {
            @Override public String toString(Localizacao l) { return l == null ? null : l.getNome(); }
            @Override public Localizacao fromString(String s) { return null; }
        });
        if(ambulancia != null) comboBase.setValue(ambulancia.getUnidade());
        VBox boxBase = criarCampoInput("Base de Lotação", comboBase);

        // Adicionando ao Grid
        grid.add(boxPlaca, 0, 0);
        grid.add(boxTipo, 1, 0);

        // Se for novo, Base ocupa a linha toda. Se for edição, divide com Status.
        if (ambulancia == null) {
            // Novo cadastro: Grid sem status (Status nasce automático pelo Service)
            grid.add(boxBase, 0, 1, 2, 1);
        } else {
            // Edição: Grid dividido
            grid.add(boxBase, 0, 1);

            // 1. Defino quais status o usuário pode escolher manualmente
            List<AmbulanciaStatus> statusPermitidos = new ArrayList<>();
            statusPermitidos.add(AmbulanciaStatus.DISPONIVEL);
            statusPermitidos.add(AmbulanciaStatus.INDISPONIVEL);
            statusPermitidos.add(AmbulanciaStatus.EM_MANUTENCAO);
            // Note que NÃO adicionei EM_ATENDIMENTO na lista!

            comboStatus = new ComboBox<>(FXCollections.observableArrayList(statusPermitidos));
            comboStatus.setValue(ambulancia.getStatusAmbulancia());
            comboStatus.setMaxWidth(Double.MAX_VALUE);

            // 2. Trava de Segurança: Se ela já estiver em atendimento, bloqueia tudo!
            if (ambulancia.getStatusAmbulancia() == AmbulanciaStatus.EM_ATENDIMENTO) {
                comboStatus.getItems().add(AmbulanciaStatus.EM_ATENDIMENTO); // Adiciona só pra mostrar
                comboStatus.setValue(AmbulanciaStatus.EM_ATENDIMENTO);
                comboStatus.setDisable(true); // Bloqueia edição

                // Dica visual extra: Explica por que está travado
                Label lblAlerta = new Label("Veículo em operação. Finalize a ocorrência para editar.");
                lblAlerta.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 10px;");
                grid.add(lblAlerta, 1, 2);
            }

            VBox boxStatus = criarCampoInput("Status Operacional", comboStatus);
            grid.add(boxStatus, 1, 1);
        }



        // Nota informativa sobre equipes
        Label lblInfo = new Label("Nota: A vinculação de equipes é feita na tela 'Gestão de Equipes'.");
        lblInfo.setFont(FONTE_PEQUENA);
        lblInfo.setTextFill(Color.web("#94A3B8"));

        Button btnSalvar = new Button("SALVAR DADOS");
        styleSalvarButton(btnSalvar);

        btnSalvar.setOnAction(e -> {
            try {
                Long id = (ambulancia == null) ? null : ambulancia.getIdAmbulancia();
                AmbulanciaStatus status = (ambulancia == null) ? null : comboStatus.getValue();

                ambulanciaService.salvarOuAtualizar(
                        id,
                        txtPlaca.getText(),
                        comboTipo.getValue(),
                        comboBase.getValue(),
                        status
                );

                new Alerta().mostrar("Sucesso", "Dados salvos com sucesso!", Alerta.Tipo.SUCESSO);

                if(ambulancia == null) {
                    txtPlaca.clear();
                    comboTipo.getSelectionModel().clearSelection();
                    comboBase.getSelectionModel().clearSelection();
                }
            } catch (Exception ex) {
                new Alerta().mostrar("Erro", ex.getMessage(), Alerta.Tipo.ERRO);
            }
        });

        formCard.getChildren().addAll(lblAcao, grid, lblInfo, btnSalvar);
        contentArea.getChildren().add(formCard);
    }

    // =============================================================================================
    // TELA 2: LISTA CORRIGIDA (Sem getEquipe)
    // =============================================================================================
    private void mostrarListaSelecao(String modo) {
        contentArea.getChildren().clear();
        Label lblInstrucao = new Label(modo.equals("EDITAR") ? "Selecione para editar:" : "Selecione para excluir:");
        lblInstrucao.setFont(FONTE_CORPO);
        lblInstrucao.setTextFill(COR_TEXTO_CLARO);
        contentArea.getChildren().add(lblInstrucao);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox lista = new VBox(10);
        lista.setPadding(new Insets(5));

        try {
            List<Ambulancia> frota = ambulanciaService.listarTodas();

            for (Ambulancia amb : frota) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(20));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

                boolean isUTI = amb.getTipoAmbulancia() == TipoAmbulancia.UTI;
                Circle icone = new Circle(25, isUTI ? COR_VERMELHO_RESGATE : COR_AZUL_NOTURNO);
                Label letra = new Label(isUTI ? "UTI" : "B");
                letra.setTextFill(Color.WHITE);
                letra.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
                StackPane iconStack = new StackPane(icone, letra);

                VBox info = new VBox(5);
                info.setMinWidth(200);

                Label lblPlaca = new Label(amb.getPlaca() + " - " + amb.getTipoAmbulancia());
                lblPlaca.setFont(FONTE_SUBTITULO);
                lblPlaca.setTextFill(COR_AZUL_NOTURNO);
                lblPlaca.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");

                String nomeBase = (amb.getUnidade() != null) ? amb.getUnidade().getNome() : "Sem Base";

                // Usamos o método novo getNomesEquipesFormatado() ou contamos a lista
                String infoEquipes;
                if (amb.getEquipes() == null || amb.getEquipes().isEmpty()) {
                    infoEquipes = "Nenhuma equipe";
                } else {
                    infoEquipes = amb.getEquipes().size() + " equipes vinculadas";
                }

                Label lblDetalhe = new Label("Base: " + nomeBase + " | " + infoEquipes);
                lblDetalhe.setFont(FONTE_PEQUENA);
                lblDetalhe.setTextFill(COR_TEXTO_CLARO);

                info.getChildren().addAll(lblPlaca, lblDetalhe);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label lblStatus = new Label(amb.getStatusAmbulancia().name());
                String corStatus = amb.getStatusAmbulancia() == AmbulanciaStatus.DISPONIVEL ? "#10B981" : "#EF4444";
                lblStatus.setStyle("-fx-text-fill: " + corStatus + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-border-color: " + corStatus + "; -fx-border-radius: 4; -fx-padding: 3 8 3 8;");

                Button btnAcao = new Button(modo.equals("EDITAR") ? "EDITAR" : "EXCLUIR");
                String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
                btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

                btnAcao.setOnAction(e -> {
                    if(modo.equals("EDITAR")) mostrarFormulario(amb);
                    else mostrarConfirmacaoExclusao(amb);
                });

                card.getChildren().addAll(iconStack, info, lblStatus, btnAcao);
                card.setOnMouseClicked(e -> btnAcao.fire());

                lista.getChildren().add(card);
            }
        } catch (Exception e) {
            new Alerta().mostrar("Erro", "Erro ao carregar lista: " + e.getMessage(), Alerta.Tipo.ERRO);
        }

        scroll.setContent(lista);
        contentArea.getChildren().add(scroll);
    }

    // =============================================================================================
    // TELA 3: CONFIRMAÇÃO
    // =============================================================================================
    private void mostrarConfirmacaoExclusao(Ambulancia amb) {
        AlertaConfirmacao alerta = new AlertaConfirmacao();
        boolean confirmou = alerta.mostrar("Confirmar Remoção",
                "Tem certeza que deseja remover o veículo " + amb.getPlaca() + "?");

        if (confirmou) {
            try {
                ambulanciaService.deletar(amb.getIdAmbulancia());
                new Alerta().mostrar("Sucesso", "Ambulância removida.", Alerta.Tipo.SUCESSO);
                mostrarListaSelecao("EXCLUIR");
            } catch (IllegalStateException ie) {
                new Alerta().mostrar("Não permitido", ie.getMessage(), Alerta.Tipo.AVISO);
            } catch (Exception e) {
                new Alerta().mostrar("Erro", e.getMessage(), Alerta.Tipo.ERRO);
            }
        }
    }

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

    private void styleSalvarButton(Button btn) {
        btn.setFont(FONTE_BOTAO2);
        btn.setPrefHeight(50);
        btn.setMaxWidth(Double.MAX_VALUE);
        String styleBase = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 16px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 16px;";
        btn.setStyle(styleBase);
        btn.setOnMouseEntered(e -> btn.setStyle(styleHover));
        btn.setOnMouseExited(e -> btn.setStyle(styleBase));
    }
}