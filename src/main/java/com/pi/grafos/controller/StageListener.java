package com.pi.grafos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.pi.grafos.view.screens.TelaLogin;

import javafx.scene.Scene;
import javafx.stage.Stage;

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

        // Seleciono a tela que quero chamar inicialmente
        Scene scene = telaLogin.criarCena(stage);
        // Scene scene = new TelaDashboard().criarCena(stage);

        stage.setScene(scene);
        stage.setTitle(this.applicationTitle);

        stage.setMaximized(true); // Faz abrir ocupando a tela toda COM a barra do Windows
        //stage.setFullScreen(true); // Se quiser tela cheia (F11, sem o X de fechar)

        // Define um tamanho mínimo para não quebrar o layout se o usuário tentar diminuir muito
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.show();
    }
}