package com.pi.grafos.view.screens;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.OcorrenciaRepository;
import com.pi.grafos.service.OcorrenciaService;

import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;
import static com.pi.grafos.view.styles.AppStyles.HEX_SIDEBAR_BG; 

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

@Component
public class TelaRelatoriosView {

    private final AmbulanciaRepository ambulanciaRepo;
    private final OcorrenciaRepository ocorrenciaRepo;
    private final OcorrenciaService ocorrenciaService;

    private VBox contentArea;
    private Button btnFrota, btnOcorrencias, btnHistorico, btnTempos, btnMapa;

    public TelaRelatoriosView(AmbulanciaRepository ambulanciaRepo, 
                              OcorrenciaRepository ocorrenciaRepo,
                              OcorrenciaService ocorrenciaService) { 
        this.ambulanciaRepo = ambulanciaRepo;
        this.ocorrenciaRepo = ocorrenciaRepo;
        this.ocorrenciaService = ocorrenciaService; 
    }

    public VBox criarView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // 1. Cabeçalho
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Central de Relatórios");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Consulte estatísticas, histórico e disponibilidade da frota.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // 2. Toolbar de Navegação (Abas)
        HBox toolBar = new HBox(10);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));

        btnFrota = criarBotaoAba("Frota Disponível", "🚑");
        btnOcorrencias = criarBotaoAba("Ocorrências Abertas", "🚨");
        btnHistorico = criarBotaoAba("Histórico Geral", "📅");
        btnTempos = criarBotaoAba("Tempo de Resposta", "⏱");
        btnMapa = criarBotaoAba("Mapa de Calor", "🗺");

        // Ações dos botões
        btnFrota.setOnAction(e -> { atualizarBotoes(btnFrota); mostrarRelatorioFrota(); });
        btnOcorrencias.setOnAction(e -> { atualizarBotoes(btnOcorrencias); mostrarRelatorioOcorrencias(); });
        btnHistorico.setOnAction(e -> { atualizarBotoes(btnHistorico); mostrarHistorico(); });
        btnTempos.setOnAction(e -> { atualizarBotoes(btnTempos); mostrarEstatisticasTempo(); });
        btnMapa.setOnAction(e -> { atualizarBotoes(btnMapa); mostrarMapaCalor(); });

        toolBar.getChildren().addAll(btnFrota, btnOcorrencias, btnHistorico, btnTempos, btnMapa);

        // 3. Área de Conteúdo
        contentArea = new VBox(15);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Inicia na primeira aba
        btnFrota.fire();

        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =================================================================================
    // 1. RELATÓRIO DE FROTA
    // =================================================================================
    private void mostrarRelatorioFrota() {
        contentArea.getChildren().clear();
        
        // Filtros
        HBox filtros = new HBox(15);
        filtros.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<TipoAmbulancia> cmbTipo = new ComboBox<>(FXCollections.observableArrayList(TipoAmbulancia.values()));
        cmbTipo.setPromptText("Filtrar por Tipo");
        
        ComboBox<AmbulanciaStatus> cmbStatus = new ComboBox<>(FXCollections.observableArrayList(AmbulanciaStatus.values()));
        cmbStatus.setPromptText("Filtrar por Status");
        
        Button btnFiltrar = new Button("Aplicar Filtros");
        estilizarBotaoAcao(btnFiltrar);

        filtros.getChildren().addAll(new Label("Filtros:"), cmbTipo, cmbStatus, btnFiltrar);

        // Tabela
        TableView<Ambulancia> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ambulancia, String> colPlaca = new TableColumn<>("Placa");
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));

        TableColumn<Ambulancia, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoAmbulancia().toString()));

        TableColumn<Ambulancia, String> colBase = new TableColumn<>("Base Atual");
        colBase.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getUnidade() != null ? cell.getValue().getUnidade().getNome() : "Sem Base"
        ));

        TableColumn<Ambulancia, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatusAmbulancia().toString()));

        TableColumn<Ambulancia, String> colTurno = new TableColumn<>("Turno (Equipe)");
        colTurno.setCellValueFactory(cell -> new SimpleStringProperty(
             (cell.getValue().getEquipes() != null && !cell.getValue().getEquipes().isEmpty()) 
             ? cell.getValue().getEquipes().get(0).getTurno().toString() 
             : "Sem Equipe"
        ));

        tabela.getColumns().addAll(colPlaca, colTipo, colBase, colStatus, colTurno);

        Runnable carregarDados = () -> {
            List<Ambulancia> lista = ambulanciaRepo.findAll();
            List<Ambulancia> filtrada = lista.stream()
                .filter(a -> cmbTipo.getValue() == null || a.getTipoAmbulancia() == cmbTipo.getValue())
                .filter(a -> cmbStatus.getValue() == null || a.getStatusAmbulancia() == cmbStatus.getValue())
                .collect(Collectors.toList());
            tabela.setItems(FXCollections.observableArrayList(filtrada));
        };

        btnFiltrar.setOnAction(e -> carregarDados.run());
        carregarDados.run(); 

        contentArea.getChildren().addAll(filtros, tabela);
    }

    // =================================================================================
    // 2. RELATÓRIO DE OCORRÊNCIAS
    // =================================================================================
    private void mostrarRelatorioOcorrencias() {
        contentArea.getChildren().clear();

        HBox filtros = new HBox(15);
        filtros.setAlignment(Pos.CENTER_LEFT);

        ComboBox<OcorrenciaStatus> cmbGravidade = new ComboBox<>(FXCollections.observableArrayList(OcorrenciaStatus.values()));
        cmbGravidade.setPromptText("Gravidade");

        Button btnFiltrar = new Button("Atualizar Lista");
        estilizarBotaoAcao(btnFiltrar);
        
        filtros.getChildren().addAll(new Label("Ocorrências em Aberto:"), cmbGravidade, btnFiltrar);

        TableView<Ocorrencia> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ocorrencia, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOcorrencia"));

        TableColumn<Ocorrencia, String> colBairro = new TableColumn<>("Bairro");
        colBairro.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getLocal() != null ? cell.getValue().getLocal().getNome() : "Desconhecido"
        ));

        TableColumn<Ocorrencia, String> colTipo = new TableColumn<>("Tipo");
        // CORREÇÃO 1: Usando getNomeTipoOcorrencia() em vez de getDescricao()
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getTipoOcorrencia() != null ? cell.getValue().getTipoOcorrencia().getNomeTipoOcorrencia() : "-"
        ));

        TableColumn<Ocorrencia, String> colGravidade = new TableColumn<>("Gravidade");
        colGravidade.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGravidade().toString()));

        TableColumn<Ocorrencia, String> colStatus = new TableColumn<>("Status Atual");
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty("EM ANDAMENTO"));

        tabela.getColumns().addAll(colId, colBairro, colTipo, colGravidade, colStatus);

        Runnable carregar = () -> {
            List<Ocorrencia> lista = ocorrenciaRepo.findAll();
            List<Ocorrencia> abertas = lista.stream()
                .filter(o -> cmbGravidade.getValue() == null || o.getGravidade() == cmbGravidade.getValue())
                .collect(Collectors.toList());
            tabela.setItems(FXCollections.observableArrayList(abertas));
        };

        btnFiltrar.setOnAction(e -> carregar.run());
        carregar.run();

        contentArea.getChildren().addAll(filtros, tabela);
    }

    // =================================================================================
    // 3. HISTÓRICO GERAL
    // =================================================================================
    private void mostrarHistorico() {
        contentArea.getChildren().clear();
        
        Label lbl = new Label("Histórico Completo de Atendimentos");
        lbl.setFont(FONTE_SUBTITULO);
        
        TableView<Ocorrencia> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Ocorrencia, String> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(cell -> new SimpleStringProperty("06/12/2025 14:30")); 
        
        TableColumn<Ocorrencia, String> colGravidade = new TableColumn<>("Gravidade");
        colGravidade.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGravidade().toString()));

        TableColumn<Ocorrencia, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        tabela.getColumns().addAll(colData, colGravidade, colDesc);
        
        tabela.setItems(FXCollections.observableArrayList(ocorrenciaRepo.findAll()));
        
        contentArea.getChildren().addAll(lbl, tabela);
    }

// =================================================================================
    // 4. TEMPO MÉDIO DE RESPOSTA (AGORA REAL!)
    // =================================================================================
    private void mostrarEstatisticasTempo() {
        contentArea.getChildren().clear();
        
        Label lbl = new Label("Indicadores de Desempenho (KPIs)");
        lbl.setFont(FONTE_SUBTITULO);

        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        
        // --- CÁLCULO REAL ---
        String mediaGeral = ocorrenciaService.calcularTempoMedioGeral();
        String mediaAlta  = ocorrenciaService.calcularTempoMedioPorGravidade(OcorrenciaStatus.ALTA);
        String mediaBaixa = ocorrenciaService.calcularTempoMedioPorGravidade(OcorrenciaStatus.BAIXA);
        
        // Passando os valores reais para os cards
        cards.getChildren().add(criarCardKPI("Tempo Médio (Geral)", mediaGeral, Color.web("#3B82F6")));
        cards.getChildren().add(criarCardKPI("Tempo (Alta Gravidade)", mediaAlta, Color.web("#EF4444"))); 
        cards.getChildren().add(criarCardKPI("Tempo (Baixa Gravidade)", mediaBaixa, Color.web("#10B981"))); 
        
        Label lblNota = new Label("Nota: Cálculo baseado na diferença entre 'Hora Chamado' e 'Hora Chegada'.");
        lblNota.setFont(FONTE_CORPO);
        lblNota.setTextFill(COR_TEXTO_CLARO);

        contentArea.getChildren().addAll(lbl, cards, lblNota);
    }

    private VBox criarCardKPI(String titulo, String valor, Color corDestaque) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B;");
        
        Label lblVal = new Label(valor);
        lblVal.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        lblVal.setTextFill(corDestaque);
        
        card.getChildren().addAll(lblTit, lblVal);
        return card;
    }

    // =================================================================================
    // 5. MAPA DE CALOR
    // =================================================================================
    private void mostrarMapaCalor() {
        contentArea.getChildren().clear();
        
        Label lbl = new Label("Volume de Chamadas por Bairro");
        lbl.setFont(FONTE_SUBTITULO);

        List<Ocorrencia> todas = ocorrenciaRepo.findAll();
        Map<String, Long> contagemPorBairro = todas.stream()
            .filter(o -> o.getLocal() != null)
            .collect(Collectors.groupingBy(o -> o.getLocal().getNome(), Collectors.counting()));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Bairro");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Qtd. Chamadas");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ocorrências");

        contagemPorBairro.forEach((bairro, qtd) -> {
            series.getData().add(new XYChart.Data<>(bairro, qtd));
        });

        barChart.getData().add(series);
        barChart.setMinHeight(400);

        contentArea.getChildren().addAll(lbl, barChart);
    }

    // =================================================================================
    // UTILITÁRIOS UI
    // =================================================================================
    
    private Button criarBotaoAba(String texto, String emoji) {
        Button btn = new Button();
        
        Text txtEmoji = new Text(emoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.WHITE);
        
        Text txtLabel = new Text("  " + texto);
        txtLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        txtLabel.setFill(Color.WHITE);
        
        HBox content = new HBox(txtEmoji, txtLabel);
        content.setAlignment(Pos.CENTER);
        btn.setGraphic(content);
        
        // CORREÇÃO 2: Substituindo COR_AZUL_NOTURNO (Objeto Color) por String Hexadecimal
        // Usamos HEX_SIDEBAR_BG que é "#1E293B" (definido em AppStyles)
        btn.setUserData(new String[]{"#94A3B8", HEX_SIDEBAR_BG}); 
        
        btn.setStyle("-fx-background-color: #94A3B8; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 20 8 20;");
        
        return btn;
    }

    private void atualizarBotoes(Button btnAtivo) {
        HBox toolbar = (HBox) btnAtivo.getParent();
        for (javafx.scene.Node node : toolbar.getChildren()) {
            if (node instanceof Button) {
                Button b = (Button) node;
                String[] cores = (String[]) b.getUserData();
                if (b == btnAtivo) {
                    b.setStyle("-fx-background-color: " + cores[1] + "; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                } else {
                    b.setStyle("-fx-background-color: " + cores[0] + "; -fx-background-radius: 20;");
                }
            }
        }
    }
    
    private void estilizarBotaoAcao(Button btn) {
        btn.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
    }
}