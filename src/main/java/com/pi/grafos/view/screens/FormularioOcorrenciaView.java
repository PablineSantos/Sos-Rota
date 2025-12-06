package com.pi.grafos.view.screens;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Localizacao;
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

    private final LocalizacaoRepository localizacaoRepository;
    private final AmbulanciaRepository ambulanciaRepository;
    private final grafosService grafosService;

    private ComboBox<Localizacao> comboBairro;
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboGravidade;
    private TextArea txtObservacao;
    private Label lblSlaInfo;

    public FormularioOcorrenciaView(LocalizacaoRepository localizacaoRepository,
                                    AmbulanciaRepository ambulanciaRepository,
                                    grafosService grafosService) {
        this.localizacaoRepository = localizacaoRepository;
        this.ambulanciaRepository = ambulanciaRepository;
        this.grafosService = grafosService;
    }

    public VBox criarView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        VBox formCard = new VBox(25);
        formCard.setMaxWidth(850);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label lblTitulo = new Label("Nova Ocorrência");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("Preencha os dados para cálculo automático de rota e triagem.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);

        // --- CAMPOS ---
        HBox row1 = new HBox(20);
        comboBairro = new ComboBox<>();
        carregarBairrosDoBanco();
        VBox boxBairro = criarCampoInput("Local da Ocorrência", comboBairro);

        TextField txtEndereco = new TextField();
        txtEndereco.setPromptText("Ex: Rua 10, Qd 5, Lt 2 - Frente ao Mercado");
        VBox boxEndereco = criarCampoInput("Endereço / Ponto de Ref.", txtEndereco);

        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxEndereco, Priority.ALWAYS);
        
        TextField txtData = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtData.setEditable(false);
        VBox boxData = criarCampoInput("Data de Abertura", txtData);

        row1.getChildren().addAll(boxBairro, boxEndereco, boxData);

        HBox row2 = new HBox(20);
        comboTipo = new ComboBox<>();
        comboTipo.setItems(FXCollections.observableArrayList("Acidente Trânsito", "Mal Súbito", "Trauma", "PCR", "Outros"));
        VBox boxTipo = criarCampoInput("Tipo de Ocorrência", comboTipo);

        comboGravidade = new ComboBox<>();
        comboGravidade.setItems(FXCollections.observableArrayList("ALTA", "MÉDIA", "BAIXA"));
        VBox boxGravidade = criarCampoInput("Gravidade (SLA)", comboGravidade);

        lblSlaInfo = new Label("");
        lblSlaInfo.setFont(FONTE_PEQUENA);
        boxGravidade.getChildren().add(lblSlaInfo);
        comboGravidade.setOnAction(e -> atualizarSlaInfo());

        HBox.setHgrow(boxTipo, Priority.ALWAYS);
        HBox.setHgrow(boxGravidade, Priority.ALWAYS);
        row2.getChildren().addAll(boxTipo, boxGravidade);

        txtObservacao = new TextArea();
        txtObservacao.setPrefHeight(100);
        txtObservacao.setWrapText(true);
        txtObservacao.setStyle("-fx-border-color: #CBD5E1; -fx-background-radius: 5; -fx-border-radius: 5;");
        VBox boxObs = new VBox(8);
        Label lblObs = new Label("Observações");
        lblObs.setFont(FONTE_CORPO);
        boxObs.getChildren().addAll(lblObs, txtObservacao);

        // --- BOTÕES ---
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancel = new Button("Cancelar");
        btnCancel.setStyle("-fx-background-color: white; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        btnCancel.setPrefHeight(50);

        Button btnSalvar = new Button("LOCALIZAR AMBULÂNCIA");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");

        // --- AÇÃO PRINCIPAL ---
        btnSalvar.setOnAction(e -> {
            Stage stageAtual = (Stage) btnSalvar.getScene().getWindow();
            Localizacao bairroSelecionado = comboBairro.getValue();
            String gravidade = comboGravidade.getValue();

            if (bairroSelecionado != null && gravidade != null) {
                try {
                    // 1. Busca frota disponível (Ativa e Disponível)
                    List<Ambulancia> frotaDisponivel = ambulanciaRepository.findAll().stream()
                        .filter(a -> Boolean.TRUE.equals(a.getIsAtivo()))
                        .filter(a -> a.getStatusAmbulancia() == com.pi.grafos.model.enums.AmbulanciaStatus.DISPONIVEL)
                        .toList();

                    if (frotaDisponivel.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhuma ambulância DISPONÍVEL encontrada.\nCadastre uma equipe na ambulância para ativá-la.");
                        alert.show();
                        return;
                    }

                    // 2. Calcula Rota (O LOG MOSTROU QUE ISSO FUNCIONA!)
                    List<SugestaoAmbulancia> sugestoes = grafosService.sugerirAmbulancias(
                        bairroSelecionado.getIdLocal(), 
                        frotaDisponivel
                    );

                    if (sugestoes.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nenhuma rota encontrada para o local selecionado.");
                        alert.show();
                    } else {
                        // 3. PASSA A LISTA PARA O MODAL (ESSA É A CORREÇÃO PRINCIPAL)
                        new ModalSelecaoAmbulancia().exibir(stageAtual, bairroSelecionado.getNome(), gravidade, sugestoes);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage());
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione o Local e a Gravidade.");
                alert.show();
            }
        });

        boxBtn.getChildren().addAll(btnCancel, btnSalvar);
        formCard.getChildren().addAll(lblTitulo, lblDesc, row1, row2, boxObs, boxBtn);
        root.getChildren().add(formCard);

        return root;
    }

    private void carregarBairrosDoBanco() {
        try {
            // Traz TUDO (Bairros + Bases) para você conseguir testar qualquer rota
            List<Localizacao> todos = localizacaoRepository.findAll();
            // Ordena alfabeticamente
            todos.sort((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()));
            comboBairro.setItems(FXCollections.observableArrayList(todos));
        } catch (Exception e) {
            e.printStackTrace();
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
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        }
        v.getChildren().addAll(l, input);
        return v;
    }

    private void atualizarSlaInfo() {
        String val = comboGravidade.getValue();
        if (val != null) {
            if (val.equals("ALTA")) {
                lblSlaInfo.setText("SLA: 8 min (Requer UTI)");
                lblSlaInfo.setTextFill(COR_VERMELHO_RESGATE);
            } else if (val.equals("MÉDIA")) {
                lblSlaInfo.setText("SLA: 15 min");
                lblSlaInfo.setTextFill(Color.web("#F59E0B"));
            } else {
                lblSlaInfo.setText("SLA: 30 min");
                lblSlaInfo.setTextFill(Color.web("#10B981"));
            }
        }
    }
}