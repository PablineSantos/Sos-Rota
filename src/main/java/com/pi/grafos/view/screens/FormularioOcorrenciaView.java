package com.pi.grafos.view.screens;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.TipoLocalizacao; // Certifique-se que o nome da classe é este mesmo (está minúsculo no seu arquivo)
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.service.grafosService;
import com.pi.grafos.service.grafosService.SugestaoAmbulancia;
import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.COR_VERMELHO_RESGATE;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FormularioOcorrenciaView {

    // Dependências (Services e Repositories)
    private final LocalizacaoRepository localizacaoRepository;
    private final AmbulanciaRepository ambulanciaRepository;
    private final grafosService grafosService;

    // Variáveis de acesso aos dados da tela
    private ComboBox<Localizacao> comboBairro; // Mudou de String para Localizacao
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboGravidade;
    private TextArea txtObservacao;
    private Label lblSlaInfo;

    // Construtor para Injeção de Dependências
    public FormularioOcorrenciaView(LocalizacaoRepository localizacaoRepository,
                                    AmbulanciaRepository ambulanciaRepository,
                                    grafosService grafosService) {
        this.localizacaoRepository = localizacaoRepository;
        this.ambulanciaRepository = ambulanciaRepository;
        this.grafosService = grafosService;
    }

    public VBox criarView() {
        // Layout Base
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // Card Branco
        VBox formCard = new VBox(25);
        formCard.setMaxWidth(850);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        // Cabeçalho
        Label lblTitulo = new Label("Nova Ocorrência");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("Preencha os dados para cálculo automático de rota e triagem.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);

        // --- CAMPOS ---

        // 1. Bairro (Carregado do Banco)
        HBox row1 = new HBox(20);
        comboBairro = new ComboBox<>();
        carregarBairrosDoBanco(); // Método auxiliar para popular a lista
        
        VBox boxBairro = criarCampoInput("Bairro", comboBairro);

        // 2. O Endereço Real (Informação para o Motorista)
        TextField txtEndereco = new TextField();
        txtEndereco.setPromptText("Ex: Rua 10, Qd 5, Lt 2 - Frente ao Mercado");
        VBox boxEndereco = criarCampoInput("Endereço / Ponto de Ref.", txtEndereco);

        // Configuração de crescimento
        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxEndereco, Priority.ALWAYS);
        boxEndereco.setMinWidth(300);

        // 3. Data (campo travado, pega a data e hora atual)
        TextField txtData = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtData.setEditable(false);
        VBox boxData = criarCampoInput("Data de Abertura", txtData);

        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxData, Priority.ALWAYS);
        row1.getChildren().addAll(boxBairro, boxEndereco, boxData);

        // 4. Tipo e Gravidade
        HBox row2 = new HBox(20);
        comboTipo = new ComboBox<>();
        comboTipo.setItems(FXCollections.observableArrayList("Acidente Trânsito", "Mal Súbito", "Trauma", "PCR", "Outros"));
        VBox boxTipo = criarCampoInput("Tipo de Ocorrência", comboTipo);

        comboGravidade = new ComboBox<>();
        comboGravidade.setItems(FXCollections.observableArrayList("ALTA", "MÉDIA", "BAIXA"));
        VBox boxGravidade = criarCampoInput("Gravidade (SLA)", comboGravidade);

        // Label de Info SLA
        lblSlaInfo = new Label("");
        lblSlaInfo.setFont(FONTE_PEQUENA);
        lblSlaInfo.setPadding(new Insets(5, 0, 0, 0));
        boxGravidade.getChildren().add(lblSlaInfo);

        // Lógica SLA
        comboGravidade.setOnAction(e -> atualizarSlaInfo());

        HBox.setHgrow(boxTipo, Priority.ALWAYS);
        HBox.setHgrow(boxGravidade, Priority.ALWAYS);
        row2.getChildren().addAll(boxTipo, boxGravidade);

        // 5. Observação
        txtObservacao = new TextArea();
        txtObservacao.setPromptText("");
        txtObservacao.setPrefHeight(100);
        txtObservacao.setWrapText(true);
        txtObservacao.setStyle("-fx-border-color: #CBD5E1; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-family: 'Poppins';");

        VBox boxObs = new VBox(8);
        Label lblObs = new Label("Observações");
        lblObs.setFont(FONTE_CORPO);
        lblObs.setTextFill(Color.web("#64748B"));
        boxObs.getChildren().addAll(lblObs, txtObservacao);

        // --- BOTÕES ---
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        boxBtn.setPadding(new Insets(20, 0, 0, 0));

        Button btnCancel = new Button("Cancelar");
        btnCancel.setFont(FONTE_BOTAO2);
        btnCancel.setStyle("-fx-background-color: white; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-cursor: hand;");
        btnCancel.setPrefHeight(50);
        btnCancel.setPrefWidth(120);

        Button btnSalvar = new Button("LOCALIZAR AMBULÂNCIA");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);

        // Estilos Botão
        String estiloNormal = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-font-size: 18px;";
        String estiloHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-font-size: 18px;";
        btnSalvar.setStyle(estiloNormal);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(estiloHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(estiloNormal));

        // --- AÇÃO DO BOTÃO (INTEGRAÇÃO COM GRAFO) ---
        btnSalvar.setOnAction(e -> {
            Stage stageAtual = (Stage) btnSalvar.getScene().getWindow();

            Localizacao bairroSelecionado = comboBairro.getValue();
            String gravidade = comboGravidade.getValue();

            if (bairroSelecionado != null && gravidade != null) {
                try {
                    System.out.println("Calculando rota para: " + bairroSelecionado.getNome());

                    // 1. Busca frota disponível no banco
                    List<Ambulancia> frotaAtiva = ambulanciaRepository.findAll(); // Pode filtrar por isAtivo aqui ou no service

                    // 2. Chama o Algoritmo de Dijkstra (Seu serviço)
                    List<SugestaoAmbulancia> sugestoes = grafosService.sugerirAmbulancias(
                        bairroSelecionado.getIdLocal(), 
                        frotaAtiva
                    );

                    // 3. Exibe o Modal passando os dados reais
                    // OBS: Você precisará atualizar o método 'exibir' do ModalSelecaoAmbulancia para aceitar essa lista!
                    new ModalSelecaoAmbulancia().exibir(stageAtual, bairroSelecionado.getNome(), gravidade); 
                    // ^^^ Para passar 'sugestoes', altere a assinatura do método no Modal.
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao calcular rotas: " + ex.getMessage());
                    alert.show();
                }

            } else {
                System.out.println("Preencha os campos!");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione o Bairro e a Gravidade.");
                alert.show();
            }
        });

        boxBtn.getChildren().addAll(btnCancel, btnSalvar);
        formCard.getChildren().addAll(lblTitulo, lblDesc, row1, row2, boxObs, boxBtn);
        root.getChildren().add(formCard);

        return root;
    }

    // --- MÉTODOS AUXILIARES ---

    private void carregarBairrosDoBanco() {
        try {
            // Busca apenas o que é BAIRRO (ignora Bases/Hospitais se necessário, ou traz tudo)
            List<Localizacao> bairros = localizacaoRepository.findByTipo(TipoLocalizacao.BAIRRO);
            
            // Se a lista vier vazia, tenta buscar tudo (caso o Seeder não tenha classificado ainda)
            if (bairros.isEmpty()) {
                bairros = localizacaoRepository.findAll();
            }

            comboBairro.setItems(FXCollections.observableArrayList(bairros));
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar bairros: " + e.getMessage());
            // Fallback visual vazio ou alerta
        }
    }

    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));

        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        if (!(input instanceof TextArea)) {
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        }

        v.getChildren().addAll(l, input);
        return v;
    }

    private void atualizarSlaInfo() {
        String val = comboGravidade.getValue();
        if (val == null) return;

        if (val.equals("ALTA")) {
            lblSlaInfo.setText("SLA: 8 min (Requer UTI)");
            lblSlaInfo.setTextFill(COR_VERMELHO_RESGATE);
        } else if (val.equals("MÉDIA")) {
            lblSlaInfo.setText("SLA: 15 min (Requer Básica)");
            lblSlaInfo.setTextFill(Color.web("#F59E0B"));
        } else {
            lblSlaInfo.setText("SLA: 30 min");
            lblSlaInfo.setTextFill(Color.web("#10B981"));
        }
    }
}