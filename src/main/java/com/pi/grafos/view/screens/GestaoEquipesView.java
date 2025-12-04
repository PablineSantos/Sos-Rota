package com.pi.grafos.view.screens;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Equipe;
import com.pi.grafos.model.Funcionario;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.service.AmbulanciaService;
import com.pi.grafos.service.EquipeService;
import com.pi.grafos.view.components.Alerta;
import com.pi.grafos.view.components.AlertaConfirmacao;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.StringConverter;
import javafx.event.Event;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
@Scope("prototype")
public class GestaoEquipesView {

    private final EquipeService equipeService;
    private final AmbulanciaService ambulanciaService;

    private VBox contentArea;
    private Button btnCadastrar, btnEditar, btnExcluir;

    // Componentes Globais
    private VBox containerMembros;
    private TextField txtNomeEquipe;
    private ComboBox<String> comboTurno;
    private ComboBox<Ambulancia> comboAmbulancia;

    private List<ComboBox<Funcionario>> combosMembrosDinamicos = new ArrayList<>();
    private List<Funcionario> cacheFuncionariosDisponiveis = new ArrayList<>();

    public GestaoEquipesView(EquipeService equipeService, AmbulanciaService ambulanciaService) {
        this.equipeService = equipeService;
        this.ambulanciaService = ambulanciaService;
    }

    public VBox criarView() {
        // Layout base
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(Region.USE_PREF_SIZE);
        Label lblTitulo = new Label("Gestão de Equipes");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Monte as tripulações e gerencie escalas.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // Toolbar
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));
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

        contentArea = new VBox(15);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        btnCadastrar.fire();

        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =============================================================================================
    // FORMULÁRIO OTIMIZADO
    // =============================================================================================

    private void mostrarFormulario(Equipe equipe) {
        contentArea.getChildren().clear();
        combosMembrosDinamicos.clear();
        cacheFuncionariosDisponiveis.clear();

        VBox formCard = new VBox(15); // Reduzi o espaçamento vertical geral
        formCard.setMaxWidth(950); // Aumentei a largura para caber tudo na linha
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        // Título Menor
        Label lblAcao = new Label(equipe == null ? "Nova Equipe" : "Editando: " + equipe.getNomeEquipe());
        lblAcao.setFont(Font.font("Poppins", FontWeight.BOLD, 18)); // Reduzi de SUBTITULO para 18px
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // --- LINHA ÚNICA: NOME | TURNO | AMBULÂNCIA ---
        HBox rowMain = new HBox(15);

        // 1. Nome
        txtNomeEquipe = new TextField();
        txtNomeEquipe.setPromptText("Ex: Equipe Alpha");
        if(equipe != null) txtNomeEquipe.setText(equipe.getNomeEquipe());
        VBox boxNome = criarCampoInput("Nome da Equipe", txtNomeEquipe);
        HBox.setHgrow(boxNome, Priority.ALWAYS); // Nome cresce mais

        // 2. Turno
        comboTurno = new ComboBox<>();
        comboTurno.getItems().addAll("MANHÃ", "TARDE", "NOITE");
        comboTurno.setPrefWidth(150);
        if(equipe != null) {
            comboTurno.setValue(equipe.getTurno());
            cacheFuncionariosDisponiveis = equipeService.buscarDisponiveisParaTurno(equipe.getTurno(), equipe);
        }

        // Listener Turno
        comboTurno.setOnAction(e -> {
            String turnoSelecionado = comboTurno.getValue();
            if (turnoSelecionado != null) {
                cacheFuncionariosDisponiveis = equipeService.buscarDisponiveisParaTurno(turnoSelecionado, equipe);
                containerMembros.getChildren().clear();
                combosMembrosDinamicos.clear();
                adicionarLinhaMembro(Cargos.CONDUTOR, null, false);
                adicionarLinhaMembro(Cargos.ENFERMEIRO, null, false);
                adicionarLinhaMembro(Cargos.MEDICO, null, true);
            }
        });
        VBox boxTurno = criarCampoInput("Turno", comboTurno);
        boxTurno.setMinWidth(150);

        // 3. Ambulância (Com opção Nenhuma)
        comboAmbulancia = new ComboBox<>();

        // Carrega lista e adiciona opção NULL no topo
        List<Ambulancia> listaAmb = new ArrayList<>();
        listaAmb.add(null); // Opção "Nenhuma"
        listaAmb.addAll(ambulanciaService.listarTodas());

        comboAmbulancia.setItems(FXCollections.observableArrayList(listaAmb));
        comboAmbulancia.setPrefWidth(200);
        comboAmbulancia.setConverter(new StringConverter<Ambulancia>() {
            @Override public String toString(Ambulancia a) { return a == null ? "--- Nenhuma ---" : a.getPlaca() + " (" + a.getTipoAmbulancia() + ")"; }
            @Override public Ambulancia fromString(String s) { return null; }
        });

        // Seleciona atual ou null
        comboAmbulancia.setValue(equipe != null ? equipe.getAmbulancia() : null);
        VBox boxAmb = criarCampoInput("Vincular Veículo", comboAmbulancia);
        boxAmb.setMinWidth(200);

        rowMain.getChildren().addAll(boxNome, boxTurno, boxAmb);

        // --- MEMBROS ---
        Label lblMembros = new Label("Membros da Equipe");
        lblMembros.setFont(Font.font("Poppins", 16));
        lblMembros.setTextFill(COR_AZUL_NOTURNO);

        Label lblObs = new Label("(Mínimo: 1 Condutor + 1 Enfermeiro)");
        lblObs.setFont(FONTE_PEQUENA);
        lblObs.setTextFill(Color.web("#94A3B8"));
        HBox headerMembros = new HBox(10, lblMembros, lblObs);
        headerMembros.setAlignment(Pos.CENTER_LEFT);

        containerMembros = new VBox(8); // Espaçamento menor entre linhas
        ScrollPane scrollMembros = new ScrollPane(containerMembros);
        scrollMembros.setFitToWidth(true);
        // AQUI ESTÁ O GANHO DE ESPAÇO: ScrollPane flexível que cresce
        scrollMembros.setPrefHeight(300);
        VBox.setVgrow(scrollMembros, Priority.ALWAYS);
        scrollMembros.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        // Preenchimento
        if (equipe != null) {
            for (Funcionario f : equipe.getMembros()) {
                adicionarLinhaMembro(f.getCargo(), f, true);
            }
        } else {
            adicionarLinhaMembro(Cargos.CONDUTOR, null, false);
            adicionarLinhaMembro(Cargos.ENFERMEIRO, null, false);
            adicionarLinhaMembro(Cargos.MEDICO, null, true);
        }

        Button btnAddMembro = new Button("+ Adicionar Outro Profissional");
        btnAddMembro.setFont(FONTE_PEQUENA);
        btnAddMembro.setMaxWidth(Double.MAX_VALUE);
        btnAddMembro.setStyle("-fx-background-color: white; -fx-text-fill: " + HEX_VERMELHO + "; -fx-border-color: " + HEX_VERMELHO + "; -fx-border-radius: 5; -fx-cursor: hand; -fx-border-style: dashed;");
        btnAddMembro.setOnAction(e -> adicionarLinhaMembro(null, null, true));

        // Salvar
        Button btnSalvar = new Button("SALVAR EQUIPE");
        styleSalvarButton(btnSalvar);
        btnSalvar.setOnAction(e -> salvarEquipe(equipe));

        formCard.getChildren().addAll(lblAcao, rowMain, headerMembros, scrollMembros, btnAddMembro, btnSalvar);

        // O formulário agora ocupa todo o espaço vertical disponível
        VBox.setVgrow(formCard, Priority.ALWAYS);
        contentArea.getChildren().add(formCard);
    }

    private void salvarEquipe(Equipe equipeExistente) {
        try {
            Equipe equipe = (equipeExistente == null) ? new Equipe() : equipeExistente;
            equipe.setNomeEquipe(txtNomeEquipe.getText());

            List<Funcionario> membrosSelecionados = new ArrayList<>();
            for (ComboBox<Funcionario> cb : combosMembrosDinamicos) {
                if (cb.getValue() != null) membrosSelecionados.add(cb.getValue());
            }

            equipeService.salvarEquipe(
                    equipe,
                    membrosSelecionados,
                    comboAmbulancia.getValue(), // Pode ser null agora (correto)
                    comboTurno.getValue()
            );

            new Alerta().mostrar("Sucesso", "Equipe salva com sucesso!", Alerta.Tipo.SUCESSO);

            if(equipeExistente == null) {
                txtNomeEquipe.clear();
                comboTurno.setValue(null);
                comboAmbulancia.setValue(null);
                mostrarFormulario(null);
            }

        } catch (IllegalArgumentException ex) {
            new Alerta().mostrar("Atenção", ex.getMessage(), Alerta.Tipo.AVISO);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alerta().mostrar("Erro", ex.getMessage(), Alerta.Tipo.ERRO);
        }
    }

    // =============================================================================================
    // LISTA (CARD VISUAL RICO)
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
            List<Equipe> equipes = equipeService.listarTodas();
            for (Equipe eq : equipes) {
                // CARD VISUAL MELHORADO
                HBox card = new HBox(15);
                card.setPadding(new Insets(15));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

                // Ícone
                Circle icone = new Circle(25, COR_AZUL_NOTURNO);
                Label letra = new Label(eq.getNomeEquipe().substring(0, 1).toUpperCase());
                letra.setTextFill(Color.WHITE);
                letra.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
                StackPane iconStack = new StackPane(icone, letra);

                // Detalhes
                VBox info = new VBox(4);
                Label lblNome = new Label(eq.getNomeEquipe());
                lblNome.setStyle("-fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1E293B;");

                String turno = eq.getTurno() != null ? eq.getTurno() : "N/A";
                String placa = (eq.getAmbulancia() != null) ? eq.getAmbulancia().getPlaca() : "Sem Veículo";
                String qtd = eq.getMembros().size() + " profissionais";

                Label lblDetalhe = new Label(turno + " • " + placa + " • " + qtd);
                lblDetalhe.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-text-fill: #64748B;");

                info.getChildren().addAll(lblNome, lblDetalhe);
                HBox.setHgrow(info, Priority.ALWAYS);

                // Botão Lateral
                Button btnAcao = new Button(modo.equals("EDITAR") ? "EDITAR" : "EXCLUIR");
                String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
                btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

                btnAcao.setOnAction(e -> {
                    if(modo.equals("EDITAR")) mostrarFormulario(eq);
                    else mostrarConfirmacaoExclusao(eq);
                });

                card.setOnMouseClicked(e -> btnAcao.fire());
                card.getChildren().addAll(iconStack, info, btnAcao);
                lista.getChildren().add(card);
            }
        } catch (Exception e) {
            new Alerta().mostrar("Erro", "Erro ao listar: " + e.getMessage(), Alerta.Tipo.ERRO);
        }
        scroll.setContent(lista);
        contentArea.getChildren().add(scroll);
    }

    private void mostrarConfirmacaoExclusao(Equipe equipe) {
        AlertaConfirmacao alerta = new AlertaConfirmacao();
        boolean confirmou = alerta.mostrar("Confirmar Remoção", "Excluir a equipe " + equipe.getNomeEquipe() + "?");
        if (confirmou) {
            try {
                equipeService.excluirEquipe(equipe.getIdEquipe());
                new Alerta().mostrar("Sucesso", "Equipe excluída.", Alerta.Tipo.SUCESSO);
                mostrarListaSelecao("EXCLUIR");
            } catch (Exception e) {
                new Alerta().mostrar("Erro", e.getMessage(), Alerta.Tipo.ERRO);
            }
        }
    }

    // --- AUXILIARES ---

    private void adicionarLinhaMembro(Cargos cargoFixo, Funcionario preSelecionado, boolean podeExcluir) {
        HBox linha = new HBox(10);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 5; -fx-background-radius: 5; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        ComboBox<Cargos> comboFuncao = new ComboBox<>();
        comboFuncao.setItems(FXCollections.observableArrayList(Cargos.values()));
        comboFuncao.setPrefWidth(130);
        comboFuncao.setPrefHeight(35); // Mais compacto
        comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 12px;");

        ComboBox<Funcionario> comboNome = new ComboBox<>();
        comboNome.setPromptText("Selecione...");
        comboNome.setMaxWidth(Double.MAX_VALUE);
        comboNome.setPrefHeight(35); // Mais compacto
        comboNome.setConverter(new StringConverter<Funcionario>() {
            @Override public String toString(Funcionario f) { return f == null ? null : f.getNomeFuncionario(); }
            @Override public Funcionario fromString(String s) { return null; }
        });
        HBox.setHgrow(comboNome, Priority.ALWAYS);
        combosMembrosDinamicos.add(comboNome);

        Runnable carregarNomes = () -> {
            Cargos cargo = comboFuncao.getValue();
            if (cargo != null) {
                List<Funcionario> filtrados = cacheFuncionariosDisponiveis.stream()
                        .filter(f -> f.getCargo() == cargo)
                        .collect(Collectors.toList());
                // Se tiver pre-selecionado, garante que ele esteja na lista (mesmo se ja tiver equipe, pois é ele mesmo)
                if (preSelecionado != null && preSelecionado.getCargo() == cargo && !filtrados.contains(preSelecionado)) {
                    filtrados.add(0, preSelecionado);
                }
                comboNome.setItems(FXCollections.observableArrayList(filtrados));
            }
        };

        if (cargoFixo != null) {
            comboFuncao.setValue(cargoFixo);
            comboFuncao.setDisable(true);
            comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 12px; -fx-opacity: 1; -fx-background-color: #E2E8F0; -fx-text-fill: black;");
            carregarNomes.run();
        } else {
            comboFuncao.setPromptText("Função...");
            comboFuncao.setOnAction(e -> carregarNomes.run());
        }

        if (preSelecionado != null) comboNome.setValue(preSelecionado);

        linha.getChildren().addAll(comboFuncao, comboNome);

        if (podeExcluir) {
            Button btnRemover = new Button("✕");
            btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12px;");
            btnRemover.setOnAction(e -> {
                containerMembros.getChildren().remove(linha);
                combosMembrosDinamicos.remove(comboNome);
            });
            linha.getChildren().add(btnRemover);
        }
        containerMembros.getChildren().add(linha);
    }

    private Button criarBotaoCrudComEmoji(String texto, String emoji, String corNormal, String corEscura) {
        Button btn = new Button();
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        btn.setUserData(new String[]{corNormal, corEscura});
        Text txtEmoji = new Text(emoji); txtEmoji.setFont(Font.font("Segoe UI Emoji", 20)); txtEmoji.setFill(Color.WHITE); txtEmoji.setBoundsType(TextBoundsType.VISUAL);
        Label lblTexto = new Label(texto); lblTexto.setFont(Font.font("Poppins", FontWeight.BOLD, 18)); lblTexto.setTextFill(Color.WHITE); lblTexto.setPadding(Insets.EMPTY);
        HBox container = new HBox(8); container.setAlignment(Pos.CENTER); container.getChildren().addAll(txtEmoji, lblTexto);
        btn.setGraphic(container);
        btn.setStyle("-fx-background-color: " + corNormal + "; -fx-background-radius: 8; -fx-cursor: hand;");
        return btn;
    }
    private void atualizarSelecaoBotoes(Button btnAtivo) {
        Button[] todos = {btnCadastrar, btnEditar, btnExcluir};
        for (Button b : todos) {
            String[] cores = (String[]) b.getUserData();
            if (b == btnAtivo) b.setStyle("-fx-background-color: " + cores[1] + "; -fx-background-radius: 8; -fx-effect: innerShadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
            else b.setStyle("-fx-background-color: " + cores[0] + "; -fx-background-radius: 8;");
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
        String styleBase = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        btn.setStyle(styleBase);
        btn.setOnMouseEntered(e -> btn.setStyle(styleHover));
        btn.setOnMouseExited(e -> btn.setStyle(styleBase));
    }
}