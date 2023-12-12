package com.example.votingapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ElectionManagementController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        // You can perform initialization tasks here
        // For example, set the welcome label text
        welcomeLabel.setText("Welcome to the Main Scene!");
    }

}
