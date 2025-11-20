package com.pi.grafos.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Component
public class StageListener implements ApplicationListener<StageReadyEvent> {

    private final String applicationTitle;
    private final TelaLogin telaLogin;

    // Construtor com Injeção de Dependência automática do Spring
    public StageListener(
            @Value("${spring.application.ui.title:SOS-Rota - Gestão}") String applicationTitle,
            TelaLogin telaLogin) { // O Spring traz a classe pra cá
        this.applicationTitle = applicationTitle;
        this.telaLogin = telaLogin;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();

        Scene scene = telaLogin.criarCena(stage);

        stage.setScene(scene);
        stage.setTitle(this.applicationTitle);

        // --- SOLUÇÃO 1: INICIAR MAXIMIZADO ---
        stage.setMaximized(true); // Isso faz abrir ocupando a tela toda (com a barra do Windows)
        // Se quiser tela cheia "de jogo" (sem o X de fechar), use: stage.setFullScreen(true);

        // Define um tamanho mínimo para não quebrar o layout se o usuário tentar diminuir muito
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.show();
    }
}