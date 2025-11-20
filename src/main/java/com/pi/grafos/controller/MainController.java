package com.pi.grafos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

import com.pi.grafos.service.UsuarioService;

@Component
public class MainController {

    // Dependencia de autenticação do usuário
    private final UsuarioService authService;
    
    @FXML
    private Button btn;

    // Construtor da classe MainController, já inicia com a dependencia do usuarioService
    public MainController(UsuarioService authService) {
        this.authService = authService;
    }

    @FXML
    public void onClick() {
        try {
            String a = "False";
            if(authService.autenticar("hatusgts", "123")) a = "True";
            btn.setText(a);
            System.out.println("Resultado: " + a);
        } catch (Exception e) {
            btn.setText("Error!");
            e.printStackTrace();
        }
    }
}
