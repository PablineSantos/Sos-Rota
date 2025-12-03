 package com.pi.grafos.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.List;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.Cargos;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.COR_VERMELHO_RESGATE;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

    public class GestaoAmbulanciasView {
        private final AmbulanciaRepository ambulanciaRepository;
        private final LocalizacaoRepository localizacaoRepository;

        // --- COMPONENTES DE UI ---
        private VBox contentArea;
        private Button btnCadastrar, btnEditar, btnExcluir;

        // Campos do Formulário
        private TextField txtPlaca;
        private ComboBox<String> comboTipo;
        private ComboBox<String> comboBase;
        private ComboBox<EquipeMock> comboEquipe;
        private Label lblStatusBadge;

        // --- MOCK DE DADOS ---
        public static class EquipeMock {
            String nome;
            boolean temMedico;
            public EquipeMock(String nome, boolean temMedico) { this.nome = nome; this.temMedico = temMedico; }
            @Override public String toString() { return nome + (temMedico ? " (UTI Capable)" : ""); }
        }

        public static class AmbulanciaMock {
            String placa, tipo, base, status;
            EquipeMock equipe;
            public AmbulanciaMock(String placa, String tipo, String base, String status, EquipeMock equipe) {
                this.placa = placa; this.tipo = tipo; this.base = base; this.status = status; this.equipe = equipe;
            }
        }

        private List<AmbulanciaMock> listaAmbulancias = new ArrayList<>();
        private List<EquipeMock> listaEquipesDisponiveis = new ArrayList<>();

        public GestaoAmbulanciasView(AmbulanciaRepository ambulanciaRepository, LocalizacaoRepository localizacaoRepository) {
            this.ambulanciaRepository = ambulanciaRepository;
            this.localizacaoRepository = localizacaoRepository;

            carregarMocks();
        }

        private void carregarMocks() {
            EquipeMock eq1 = new EquipeMock("Equipe Alpha", true);
            EquipeMock eq2 = new EquipeMock("Equipe Bravo", false);
            listaEquipesDisponiveis.addAll(List.of(eq1, eq2));

            listaAmbulancias.add(new AmbulanciaMock("ABC-1234", "USA (UTI)", "Base Central", "DISPONÍVEL", eq1));
            listaAmbulancias.add(new AmbulanciaMock("XYZ-9876", "USB (Básica)", "Posto Norte", "INDISPONÍVEL", null));
        }

        // =============================================================================================
        // ESTRUTURA PRINCIPAL
        // =============================================================================================
        public VBox criarView() {
            VBox root = new VBox(20);
            root.setPadding(new Insets(40));
            root.setAlignment(Pos.TOP_CENTER);
            root.setStyle("-fx-background-color: #F1F5F9;");

            // Cabeçalho
            VBox header = new VBox(5);
            header.setAlignment(Pos.CENTER_LEFT);

            Label lblTitulo = new Label("Frota de Ambulâncias");
            lblTitulo.setFont(FONTE_TITULO);
            lblTitulo.setTextFill(COR_AZUL_NOTURNO);

            Label lblDesc = new Label("Gerencie veículos, aloque equipes e defina bases operacionais.");
            lblDesc.setFont(FONTE_CORPO);
            lblDesc.setTextFill(COR_TEXTO_CLARO);

            header.getChildren().addAll(lblTitulo, lblDesc);

            // Toolbar
            HBox toolBar = criarToolbar();

            // Conteúdo
            contentArea = new VBox(15);
            contentArea.setAlignment(Pos.TOP_CENTER);
            VBox.setVgrow(contentArea, Priority.ALWAYS);

            // Inicia no cadastro
            btnCadastrar.fire();

            root.getChildren().addAll(header, toolBar, contentArea);
            return root;
        }

        private HBox criarToolbar() {
            HBox toolBar = new HBox(15);
            toolBar.setAlignment(Pos.CENTER_LEFT);
            toolBar.setPadding(new Insets(10, 0, 20, 0));
            toolBar.setMinHeight(80);

            btnCadastrar = criarBotaoCrud("CADASTRAR", "🚑", "#10B981", "#059669");
            btnCadastrar.setOnAction(e -> {
                atualizarEstiloBotoes(btnCadastrar);
                mostrarFormulario(null);
            });

            btnEditar = criarBotaoCrud("EDITAR", "🔧", "#F59E0B", "#D97706");
            btnEditar.setOnAction(e -> {
                atualizarEstiloBotoes(btnEditar);
                mostrarListaSelecao("EDITAR");
            });

            btnExcluir = criarBotaoCrud("EXCLUIR", "🚫", "#EF4444", "#B91C1C");
            btnExcluir.setOnAction(e -> {
                atualizarEstiloBotoes(btnExcluir);
                mostrarListaSelecao("EXCLUIR");
            });

            toolBar.getChildren().addAll(btnCadastrar, btnEditar, btnExcluir);
            return toolBar;
        }

        // =============================================================================================
        // VISUALIZAÇÃO 1: FORMULÁRIO (CORRIGIDO)
        // =============================================================================================
        private void mostrarFormulario(AmbulanciaMock ambulancia) {
            contentArea.getChildren().clear();

            VBox formCard = new VBox(20);
            formCard.setMaxWidth(850);
            formCard.setPadding(new Insets(30));
            formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

            Label lblAcao = new Label(ambulancia == null ? "Nova Ambulância" : "Editando: " + ambulancia.placa);
            lblAcao.setFont(FONTE_SUBTITULO);
            lblAcao.setTextFill(COR_AZUL_NOTURNO);

            // --- GRID FIX ---
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(15);

            // AQUI ESTÁ A MÁGICA: ColumnConstraints forçam 50% de largura para cada coluna
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(50);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(50);
            grid.getColumnConstraints().addAll(col1, col2);

            // 1. Placa
            txtPlaca = new TextField();
            txtPlaca.setPromptText("Ex: BRA-2E19");
            if(ambulancia != null) txtPlaca.setText(ambulancia.placa);
            VBox boxPlaca = criarCampoInput("Placa do Veículo", txtPlaca);

            // 2. Tipo
            comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("UTI Móvel", "Suporte Básico");
            comboTipo.setPromptText("Selecione o tipo...");
            comboTipo.setMaxWidth(Double.MAX_VALUE); // Garante que preencha a VBox
            if(ambulancia != null) comboTipo.setValue(ambulancia.tipo);
            VBox boxTipo = criarCampoInput("Tipo de Unidade", comboTipo);

            // 3. Base
            comboBase = new ComboBox<>();
            comboBase.getItems().addAll("Base Central (HMB)", "Posto Avançado Norte", "Hospital Santa Vida");
            comboBase.setPromptText("Selecione a base...");
            comboBase.setMaxWidth(Double.MAX_VALUE);
            if(ambulancia != null) comboBase.setValue(ambulancia.base);
            VBox boxBase = criarCampoInput("Base de Lotação", comboBase);

            // 4. Equipe
            comboEquipe = new ComboBox<>();
            comboEquipe.getItems().addAll(listaEquipesDisponiveis);
            comboEquipe.setPromptText("Vincular equipe...");
            comboEquipe.setMaxWidth(Double.MAX_VALUE);
            if(ambulancia != null) comboEquipe.setValue(ambulancia.equipe);
            VBox boxEquipe = criarCampoInput("Equipe Responsável", comboEquipe);

            // Adicionando ao Grid
            grid.add(boxPlaca, 0, 0);
            grid.add(boxTipo, 1, 0);
            grid.add(boxBase, 0, 1);
            grid.add(boxEquipe, 1, 1);

            // --- STATUS MONITOR ---
            HBox statusBox = new HBox(15);
            statusBox.setAlignment(Pos.CENTER_LEFT);
            statusBox.setPadding(new Insets(15));
            statusBox.setStyle("-fx-background-color: #F8FAFC; -fx-border-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

            Label lblStatusTitulo = new Label("Status Atual:");
            lblStatusTitulo.setFont(FONTE_CORPO);

            lblStatusBadge = new Label(ambulancia != null ? ambulancia.status : "AGUARDANDO DADOS");
            lblStatusBadge.setPadding(new Insets(5, 15, 5, 15));
            lblStatusBadge.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #64748B; -fx-background-radius: 20; -fx-font-weight: bold;");

            statusBox.getChildren().addAll(lblStatusTitulo, lblStatusBadge);

            // Botão Salvar
            Button btnSalvar = new Button("SALVAR DADOS");
            styleSalvarButton(btnSalvar);

            formCard.getChildren().addAll(lblAcao, grid, statusBox, btnSalvar);
            contentArea.getChildren().add(formCard);

            btnSalvar.setOnAction(e -> {
                try {
                    String placaAmbulancia = txtPlaca.getText();
                    String tipoAmbulancia = comboTipo.getValue();
                    String baseAmbulancia = comboBase.getValue();
                    //String equipeAmbulancia = comboEquipe.getValue();

                    if(placaAmbulancia.isEmpty() || tipoAmbulancia == null || baseAmbulancia == null){

                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Campos vazios");
                        alert.setHeaderText(null);
                        alert.setContentText("Preencha todos os campos antes de salvar!");
                        alert.showAndWait();
                        return;

                    } else {
                        Ambulancia novaAmbulancia = new Ambulancia();
                        novaAmbulancia.setPlaca(placaAmbulancia);
                        novaAmbulancia.setTipoAmbulancia(TipoAmbulancia.fromDescricao(tipoAmbulancia));
                        novaAmbulancia.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
                        novaAmbulancia.setIsAtivo(true);

                        ambulanciaRepository.save(novaAmbulancia);

                        new Alert(Alert.AlertType.INFORMATION, "Ambulância cadastrada com sucesso!").showAndWait();

                        /*
                        List<Localizacao> unidades = localizacaoRepository.findByNome(baseAmbulancia);

                        if (unidades.isEmpty()) {
                            throw new RuntimeException("Base não encontrada!");
                        }

                        Localizacao unidade = unidades.get(0);
                        novaAmbulancia.setUnidade(unidade);
                        */
                    }


                } catch (Exception error) {
                    error.printStackTrace();
                }
            });
        }

        // =============================================================================================
        // VISUALIZAÇÃO 2: LISTA (CORRIGIDO TIPO COR)
        // =============================================================================================
        private void mostrarListaSelecao(String modo) {
            contentArea.getChildren().clear();

            Label lblInstrucao = new Label(modo.equals("EDITAR") ?
                    "Selecione o veículo para manutenção:" : "Selecione o veículo para baixa:");
            lblInstrucao.setFont(FONTE_CORPO);
            lblInstrucao.setTextFill(COR_TEXTO_CLARO);
            contentArea.getChildren().add(lblInstrucao);

            ScrollPane scroll = new ScrollPane();
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

            VBox lista = new VBox(10);
            lista.setPadding(new Insets(5));

            for (AmbulanciaMock amb : listaAmbulancias) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(20));
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

                // CORREÇÃO APLICADA: Usando COR_VERMELHO_RESGATE (Color) em vez de String
                Circle icone = new Circle(25, amb.tipo.contains("UTI") ? COR_VERMELHO_RESGATE : COR_AZUL_NOTURNO);

                Label letra = new Label(amb.tipo.contains("UTI") ? "UTI" : "B");
                letra.setTextFill(Color.WHITE);
                letra.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
                StackPane iconStack = new StackPane(icone, letra);

                VBox info = new VBox(5);
                Label lblPlaca = new Label(amb.placa + " - " + amb.tipo);
                lblPlaca.setFont(FONTE_SUBTITULO);
                lblPlaca.setTextFill(COR_AZUL_NOTURNO);

                Label lblDetalhe = new Label("Base: " + amb.base + " | Equipe: " + (amb.equipe != null ? amb.equipe.nome : "Nenhuma"));
                lblDetalhe.setFont(FONTE_PEQUENA);
                lblDetalhe.setTextFill(COR_TEXTO_CLARO);

                info.getChildren().addAll(lblPlaca, lblDetalhe);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label lblStatusCard = new Label(amb.status);
                String corStatus = amb.status.equals("DISPONÍVEL") ? "#10B981" : "#EF4444";
                lblStatusCard.setStyle("-fx-text-fill: " + corStatus + "; -fx-font-weight: bold; -fx-font-size: 11px; " +
                        "-fx-border-color: " + corStatus + "; -fx-border-radius: 4; -fx-padding: 3 8 3 8;");

                card.getChildren().addAll(iconStack, info, lblStatusCard);

                card.setOnMouseClicked(e -> {
                    if(modo.equals("EDITAR")) mostrarFormulario(amb);
                    else mostrarConfirmacaoExclusao(amb);
                });

                lista.getChildren().add(card);
            }

            scroll.setContent(lista);
            contentArea.getChildren().add(scroll);
        }

        // =============================================================================================
        // VISUALIZAÇÃO 3: CONFIRMAÇÃO
        // =============================================================================================
        private void mostrarConfirmacaoExclusao(AmbulanciaMock amb) {
            contentArea.getChildren().clear();

            VBox alertCard = new VBox(20);
            alertCard.setMaxWidth(500);
            alertCard.setAlignment(Pos.CENTER);
            alertCard.setPadding(new Insets(30));
            alertCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #EF4444; -fx-border-width: 2; -fx-border-radius: 12;");

            Label lblTitulo = new Label("Confirmar Remoção");
            lblTitulo.setFont(FONTE_TITULO);
            lblTitulo.setTextFill(Color.web("#EF4444"));

            Label lblMsg = new Label("Tem certeza que deseja remover o veículo " + amb.placa + "?");
            lblMsg.setFont(FONTE_CORPO);

            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER);

            Button btnCancel = new Button("Cancelar");
            btnCancel.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-text-fill: #64748B; -fx-background-radius: 5; -fx-cursor: hand; -fx-pref-height: 40;");
            btnCancel.setOnAction(e -> mostrarListaSelecao("EXCLUIR"));

            Button btnConfirm = new Button("CONFIRMAR");
            btnConfirm.setFont(FONTE_BOTAO2);
            btnConfirm.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-pref-height: 40;");
            btnConfirm.setOnAction(e -> {
                listaAmbulancias.remove(amb);
                mostrarListaSelecao("EXCLUIR");
            });

            actions.getChildren().addAll(btnCancel, btnConfirm);
            alertCard.getChildren().addAll(lblTitulo, lblMsg, actions);

            contentArea.getChildren().add(alertCard);
        }

        // =============================================================================================
        // UTILITÁRIOS
        // =============================================================================================

        private Button criarBotaoCrud(String texto, String emoji, String corNormal, String corEscura) {
            Button btn = new Button();
            btn.setPrefWidth(180);
            btn.setPrefHeight(50);
            btn.setUserData(new String[]{corNormal, corEscura});

            Text txtEmoji = new Text(emoji);
            txtEmoji.setFont(Font.font("Segoe UI Emoji", 20));
            txtEmoji.setFill(Color.WHITE);

            Label lblTexto = new Label(texto);
            lblTexto.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
            lblTexto.setTextFill(Color.WHITE);

            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER);
            container.getChildren().addAll(txtEmoji, lblTexto);

            btn.setGraphic(container);
            btn.setStyle("-fx-background-color: " + corNormal + "; -fx-background-radius: 8; -fx-cursor: hand;");
            return btn;
        }

        private void atualizarEstiloBotoes(Button btnAtivo) {
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