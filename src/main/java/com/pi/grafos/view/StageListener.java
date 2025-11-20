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
    private final Resource fxml;
    private final ApplicationContext applicationContext;

    // Define o titulo e o caminho xml
    public StageListener(
            @Value("${spring.application.ui.title:Grafos App}") String applicationTitle,
            @Value("classpath:/fxml/main.fxml") Resource fxml, // Caminho para o fxml, no caso em src/main/resources/fxml
            ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.fxml = fxml;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            var url = fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            
            // Permite que o spring adicione dependencias aos controllers
            fxmlLoader.setControllerFactory(applicationContext::getBean); 
            
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML", e);
        }
    }
}