package com.pi.grafos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    @FXML
    private Button btn;

    @FXML
    public void onClick() {
        btn.setText("You clicked!");
    }
}
