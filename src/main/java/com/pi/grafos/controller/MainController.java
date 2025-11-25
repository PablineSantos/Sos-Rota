package com.pi.grafos.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import com.pi.grafos.service.UsuarioService;

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
    public void cadastrarUsuario() {
        try {
            String NomeUsuario = nomeUsuario.getText();
            String SenhaUsuario = senhaUsuario.getText();

            if((NomeUsuario.isEmpty() || SenhaUsuario.isEmpty()) == true){
                System.err.println("Erro de cadastro, verifique os campos");
                return;
            } 
            
            if(authService.autenticar(NomeUsuario, SenhaUsuario)){
                System.err.println("Erro de cadastro, usuário já existente");
                return;
            }

            authService.cadastrarUsuario(NomeUsuario, SenhaUsuario);
            System.err.println("Usuário Cadastrado, redirecionando!");
            System.out.println("Usuário cadastrado: " + NomeUsuario);
            
            FXMLLoader dashboard = new FXMLLoader(getClass().getResource(("/view/dashboard.fxml")));
            Parent root = dashboard.load();

            Stage stage = (Stage) cadastrarButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

            //Método de passar o nome para frente
            DashboardController dashboardController = dashboard.getController();
            dashboardController.setUsername(NomeUsuario);


        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela do Dashboard!");
            e.printStackTrace();
        }
    }

    public void logar(){
        try {
            String NomeUsuario = nomeUsuario.getText();
            String SenhaUsuario = senhaUsuario.getText();

            if((NomeUsuario.isEmpty() || SenhaUsuario.isEmpty()) == true){
                System.err.println("Erro de cadastro, verifique os campos");
                return;
            } 
            
            if(authService.autenticar(NomeUsuario, SenhaUsuario)){
                FXMLLoader dashboard = new FXMLLoader(getClass().getResource(("/view/dashboard.fxml")));
                Parent root = dashboard.load();

                Stage stage = (Stage) cadastrarButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.show();

                //Método de passar o nome para frente
                DashboardController dashboardController = dashboard.getController();
                dashboardController.setUsername(NomeUsuario);

                System.err.println("Usuário Logado, Redirecionando!");
            } else{
                System.err.println("Usuário inexistente, tente fazer um cadastro!");
            }


        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela do Dashboard!");
            e.printStackTrace();
        }
    }
}
