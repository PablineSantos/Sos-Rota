package com.pi.grafos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import com.pi.grafos.service.UsuarioService;
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField placaAmbulancia;

    @FXML
    private TextField estatusAmbulancia;

    @FXML
    private TextField tipoAmbulancia;

    @FXML
    private TextField unidadeAmbulancia;


    public void setUsername(String username) {
        welcomeLabel.setText("Seja bem-vindo, " + username + "!");
    }

    public void cadastrarAmbulancia(){
        try {
            
            String PlacaAmbulancia = placaAmbulancia.getText();
            String EstatusAmbulancia = estatusAmbulancia.getText();
            String TipoAmbulancia = tipoAmbulancia.getText();
            String UnidadeAmbulancia = unidadeAmbulancia.getText();


            if((PlacaAmbulancia.isEmpty() || EstatusAmbulancia.isEmpty() || TipoAmbulancia.isEmpty() || UnidadeAmbulancia.isEmpty()) == true){
                System.err.println("Erro de cadastro, verifique os campos");
                return;
            } 
            


        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela do Dashboard!");
            e.printStackTrace();
        }
    }

}
