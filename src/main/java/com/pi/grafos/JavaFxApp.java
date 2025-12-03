package com.pi.grafos;

import com.pi.grafos.view.screens.TelaLogin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class JavaFxApp extends Application {

    // NOME DA VARIÁVEL DECLARADA AQUI
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        // Começa o Spring, usando a classe GrafosSpringApp como fonte
        context = new SpringApplicationBuilder(GrafosSpringApp.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {
        // CORREÇÃO: Usamos 'context' e não 'applicationContext'
        TelaLogin telaLogin = context.getBean(TelaLogin.class);

        Parent rootLogin = telaLogin.criarConteudo(stage);

        Scene scene = new Scene(rootLogin, 1000, 700);

        stage.setTitle("SOS Rota - Dispatch System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}