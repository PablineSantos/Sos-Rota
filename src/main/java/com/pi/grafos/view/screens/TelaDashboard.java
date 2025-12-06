package com.pi.grafos.view.screens;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.service.AmbulanciaService;
import com.pi.grafos.service.FuncionarioService;
import com.pi.grafos.service.grafosService; // <--- MUDANÇA 1: Import do serviço de grafos
import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_BRANCO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_CINZA_FUNDO;
import static com.pi.grafos.view.styles.AppStyles.HEX_SIDEBAR_BG;
import static com.pi.grafos.view.styles.AppStyles.HEX_SIDEBAR_HOVER;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

@Component
public class TelaDashboard {
    
    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private AmbulanciaService ambulanciaService;

    @Autowired
    private AmbulanciaRepository ambulanciaRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private grafosService grafosService; // <--- MUDANÇA 2: Injetando o serviço de Grafos


    // --- CONFIGURAÇÕES VISUAIS ---
    private static final double LARGURA_SIDEBAR = 240;
    private static final double LARGURA_RESUMO = 320;

    // --- ESTADO DA TELA (Variáveis globais da classe) ---
    private HBox rootLayout;      // O layout principal que segura tudo
    private Region centerMap;     // O mapa original
    private List<Button> botoesMenu = new ArrayList<>(); // Lista para controlar qual botão está ativo

    private final ObjectProvider<GestaoEquipesView> gestaoEquipesProvider;

    public TelaDashboard(ObjectProvider<GestaoEquipesView> gestaoEquipesProvider) {
        this.gestaoEquipesProvider = gestaoEquipesProvider;
    }


    public Parent criarConteudo(Stage stage) {

        // =============================================================================================
        // 1. COLUNA ESQUERDA: MENU LATERAL
        // =============================================================================================
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(LARGURA_SIDEBAR);
        sidebar.setMinWidth(LARGURA_SIDEBAR);
        sidebar.setStyle("-fx-background-color: " + HEX_SIDEBAR_BG + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // --- LOGO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo2.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { System.err.println("Erro logo dashboard"); }

        // --- TÍTULO DO PAINEL ---
        Label lblTituloPainel = new Label("PAINEL");
        lblTituloPainel.setFont(FONTE_TITULO);
        lblTituloPainel.setTextFill(COR_TEXTO_BRANCO);

        // --- BOTÕES DE NAVEGAÇÃO ---
        Button btnDashboard = criarBotaoMenu("Dashboard", "🏠");
        // Ação: Voltar para o Mapa e pintar de vermelho
        btnDashboard.setOnAction(e -> {
            atualizarEstiloBotao(btnDashboard);
            setConteudoCentral(centerMap);
        });

        Button btnNovaOcorrencia = criarBotaoMenu("Nova Ocorrência", "➕");
        // Ação: Mostrar formulário de cadastro
        btnNovaOcorrencia.setOnAction(e -> {
            atualizarEstiloBotao(btnNovaOcorrencia);
            
            // MUDANÇA 3: Passando as dependências necessárias para o construtor
            setConteudoCentral(new FormularioOcorrenciaView(
                localizacaoRepository, 
                ambulanciaRepository, 
                grafosService
            ).criarView());
        });

        Button btnFrota = criarBotaoMenu("Ambulâncias", "🚑");
        btnFrota.setOnAction(e -> {
            atualizarEstiloBotao(btnFrota);
            // Injeta os repositórios reais do Spring na View
            setConteudoCentral(new GestaoAmbulanciasView(ambulanciaService, localizacaoRepository).criarView());
        });

        Button btnEquipe = criarBotaoMenu("Equipe", "👨‍⚕️");

        btnEquipe.setOnAction(e -> {
            atualizarEstiloBotao(btnEquipe);
            GestaoEquipesView view = gestaoEquipesProvider.getObject();
            setConteudoCentral(view.criarView());
        });

        Button btnColaborador = criarBotaoMenu("Colaboradores", "⚕");
        btnColaborador.setOnAction(e -> {
            atualizarEstiloBotao(btnColaborador);
            setConteudoCentral(new GestaoFuncionariosView(funcionarioService).criarView());
        });

        Button btnRelatorio = criarBotaoMenu("Relatórios", "");
        btnRelatorio.setOnAction(e -> {
            atualizarEstiloBotao(btnRelatorio);
            setConteudoCentral(criarPlaceholderFormulario("Relatório"));
        });



        // Espacador
        Region spacerMenu = new Region();
        VBox.setVgrow(spacerMenu, Priority.ALWAYS);

        Button btnSair = criarBotaoMenu("Sair do Sistema", "🚪");
        btnSair.setOnAction(e -> {
            System.out.println("Saindo...");
            stage.close();
        });

        sidebar.getChildren().addAll(logoView, lblTituloPainel, btnDashboard, btnNovaOcorrencia, btnFrota, btnEquipe, btnColaborador, btnRelatorio, spacerMenu, btnSair);


        // =============================================================================================
        // 2. COLUNA CENTRAL: MAPA DA CIDADE
        // =============================================================================================
        centerMap = new Region();

        try {
            Image imgMap = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, true); // COVER
            BackgroundImage bgImage = new BackgroundImage(
                    imgMap,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );
            centerMap.setBackground(new Background(bgImage));
        } catch (Exception e) {
            centerMap.setStyle("-fx-background-color: #CBD5E1;");
        }

        // =============================================================================================
        // 3. COLUNA DIREITA: RESUMO
        // =============================================================================================
        VBox rightPanel = new VBox(25);
        rightPanel.setPadding(new Insets(30));
        rightPanel.setPrefWidth(LARGURA_RESUMO);
        rightPanel.setMinWidth(LARGURA_RESUMO);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, -5, 0);");

        // --- Título Resumo ---
        Label lblResumo = new Label("Resumo Operacional");
        lblResumo.setFont(FONTE_SUBTITULO);
        lblResumo.setTextFill(COR_AZUL_NOTURNO);

        // --- SEÇÃO 1: LISTA DE OCORRÊNCIAS ---
        Label lblPendentes = new Label("Ocorrências Pendentes");
        lblPendentes.setFont(FONTE_CORPO);
        lblPendentes.setTextFill(COR_TEXTO_CLARO);

        VBox listaContainer = new VBox(10);
        listaContainer.getChildren().add(criarCardOcorrencia("Acidente Centro", "Alta Prioridade - Requer UTI", HEX_VERMELHO, "Centro", "ALTA"));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito", "Média Prioridade - Jd. América", "#F59E0B", "Jardim América", "MÉDIA"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade - Vila Nova", "#10B981", "Vila Nova", "BAIXA"));

        ScrollPane scrollPane = new ScrollPane(listaContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // --- SEÇÃO 2: CONTADOR DE FROTA ---
        VBox painelFrota = new VBox(5);
        painelFrota.setAlignment(Pos.CENTER);
        painelFrota.setPadding(new Insets(20));
        painelFrota.setStyle("-fx-background-color: " + HEX_CINZA_FUNDO + "; -fx-background-radius: 8;");

        Label lblFrotaTitulo = new Label("Ambulâncias Disponíveis");
        lblFrotaTitulo.setFont(FONTE_CORPO);
        lblFrotaTitulo.setTextFill(COR_TEXTO_BRANCO);

        Label lblFrotaNumero = new Label("5");
        lblFrotaNumero.setFont(FONTE_TITULO);
        lblFrotaNumero.setTextFill(COR_TEXTO_BRANCO);

        Label lblFrotaTotal = new Label("de 12 viaturas totais");
        lblFrotaTotal.setFont(FONTE_PEQUENA);
        lblFrotaTotal.setTextFill(COR_TEXTO_BRANCO);

        painelFrota.getChildren().addAll(lblFrotaTitulo, lblFrotaNumero, lblFrotaTotal);

        rightPanel.getChildren().addAll(lblResumo, lblPendentes, scrollPane, painelFrota);


        // =============================================================================================
        // MONTAGEM FINAL
        // =============================================================================================
        rootLayout = new HBox();
        rootLayout.getChildren().addAll(sidebar, centerMap, rightPanel);

        HBox.setHgrow(sidebar, Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.NEVER);
        HBox.setHgrow(centerMap, Priority.ALWAYS);

        // Marca o botão Dashboard como ativo inicialmente
        atualizarEstiloBotao(btnDashboard);

        return rootLayout;
    }

    // =============================================================================================
    // MÉTODOS AUXILIARES (Mantidos inalterados)
    // =============================================================================================

    private void setConteudoCentral(Node novoConteudo) {
        // O índice 1 é sempre o centro (0=Esquerda, 1=Centro, 2=Direita)
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(1, novoConteudo);

        HBox.setHgrow(novoConteudo, Priority.ALWAYS);

        if (novoConteudo instanceof Region) {
            ((Region) novoConteudo).setMaxWidth(Double.MAX_VALUE);
            ((Region) novoConteudo).setMaxHeight(Double.MAX_VALUE);
        }
    }

    private void atualizarEstiloBotao(Button btnAtivo) {
        String estiloNormal = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloAtivo  = "-fx-background-color: " + HEX_SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloHover  = "-fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";

        for (Button b : botoesMenu) {
            b.setStyle(estiloNormal);
            alterarCorTextoBotao(b, Color.web("#E2E8F0"));

            b.setOnMouseEntered(e -> {
                if (b != btnAtivo) {
                    b.setStyle(estiloHover);
                    alterarCorTextoBotao(b, Color.WHITE);
                }
            });
            b.setOnMouseExited(e -> {
                if (b != btnAtivo) {
                    b.setStyle(estiloNormal);
                    alterarCorTextoBotao(b, Color.web("#E2E8F0"));
                }
            });
        }

        btnAtivo.setStyle(estiloAtivo);
        alterarCorTextoBotao(btnAtivo, Color.WHITE);
        btnAtivo.setOnMouseEntered(null);
        btnAtivo.setOnMouseExited(null);
    }

    private void alterarCorTextoBotao(Button btn, Color cor) {
        if (btn.getGraphic() instanceof TextFlow) {
            TextFlow flow = (TextFlow) btn.getGraphic();
            for (Node n : flow.getChildren()) {
                if (n instanceof Text) {
                    ((Text) n).setFill(cor);
                }
            }
        }
    }

    private VBox criarPlaceholderFormulario(String titulo) {
        VBox form = new VBox(20);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color: #F1F5F9;");

        Label lbl = new Label(titulo);
        lbl.setFont(FONTE_TITULO);
        lbl.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("O formulário de " + titulo + " será implementado aqui.");
        lblDesc.setFont(FONTE_CORPO);

        form.getChildren().addAll(lbl, lblDesc);
        return form;
    }

    private Button criarBotaoMenu(String texto, String iconeEmoji) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setMinHeight(45);
        btn.setMaxHeight(45);

        javafx.scene.text.Text txtEmoji = new javafx.scene.text.Text(iconeEmoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.web("#E2E8F0"));

        javafx.scene.text.Text txtLabel = new javafx.scene.text.Text("  " + texto);
        txtLabel.setFont(FONTE_BOTAO2);
        txtLabel.setFill(Color.web("#E2E8F0"));

        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(txtEmoji, txtLabel);
        flow.setTextAlignment(TextAlignment.LEFT);

        btn.setGraphic(flow);
        botoesMenu.add(btn);

        return btn;
    }

    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus, String bairro, String gravidade) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        String estiloNormal = "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #F1F5F9; -fx-border-color: " + corStatus + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";

        card.setStyle(estiloNormal);

        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e -> card.setStyle(estiloNormal));

        card.setOnMouseClicked(e -> {
            System.out.println("Abrindo despacho rápido para: " + titulo);
            Stage stageAtual = (Stage) card.getScene().getWindow();
            // Abre o Modal com dados fictícios para teste rápido
            new ModalSelecaoAmbulancia().exibir(stageAtual, bairro, gravidade);
        });

        Circle statusDot = new Circle(5, Color.web(corStatus));

        VBox textos = new VBox(4);
        Label lblTit = new Label(titulo);
        lblTit.setFont(FONTE_CORPO);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblSub = new Label(subtitulo);
        lblSub.setFont(FONTE_PEQUENA);
        lblSub.setTextFill(Color.web(corStatus));

        textos.getChildren().addAll(lblTit, lblSub);
        card.getChildren().addAll(statusDot, textos);
        HBox.setHgrow(textos, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }
}