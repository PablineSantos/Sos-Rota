package com.pi.grafos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.pi.grafos.view.screens.TelaLogin;

import javafx.scene.Parent; // <--- ADICIONE ESSE IMPORT
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StageListener implements ApplicationListener<StageReadyEvent> {

    private final String applicationTitle;
    private final TelaLogin telaLogin;

    public StageListener(
            @Value("${spring.application.ui.title:SOS-Rota - Gestão}") String applicationTitle,
            TelaLogin telaLogin) {
        this.applicationTitle = applicationTitle;
        this.telaLogin = telaLogin;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();

        // 1. Obtém o CONTEÚDO (Layout/Parent) da tela de Login
        Parent root = telaLogin.criarConteudo(stage);

        // 2. Cria a CENA (A moldura) colocando o conteúdo dentro
        Scene scene = new Scene(root, 1000, 700); // Defina o tamanho inicial aqui

        // 3. Configura o palco (Stage)
        stage.setScene(scene);
        stage.setTitle(this.applicationTitle);
        stage.setMaximized(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.show();
    }
}