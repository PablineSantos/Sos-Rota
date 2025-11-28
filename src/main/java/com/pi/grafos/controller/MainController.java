package com.pi.grafos.controller;

import org.springframework.stereotype.Component;

import com.pi.grafos.service.UsuarioService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


@Component
public class MainController {

    // Dependencia de autenticação do usuário
    private final UsuarioService authService;

    @FXML
    private Button cadastrarButton;

    @FXML
    private TextField nomeUsuario;

    @FXML
    private TextField senhaUsuario;

    // Construtor da classe MainController, já inicia com a dependencia do usuarioService
    public MainController(UsuarioService authService) {
        this.authService = authService;
    }

    @FXML
    public void cadastrarUsuario(String NomeUsuario, String SenhaUsuario) {
        try {
            if(authService.autenticar(NomeUsuario, SenhaUsuario)){
                return;
            }

            authService.cadastrarUsuario(NomeUsuario, SenhaUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean logar(String NomeUsuario, String SenhaUsuario){
        try {         
            if(authService.autenticar(NomeUsuario, SenhaUsuario) == true){
                return true;
            } else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
