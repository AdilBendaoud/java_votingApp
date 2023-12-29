package com.example.votingapp;

import com.example.votingapp.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;

public class UsersformController {

    @FXML
    private TextField CINInput;

    @FXML
    private TextField nomInput;

    @FXML
    private TextField prenomInput;

    @FXML
    private TextField emailInput;

    @FXML
    private DatePicker birthInput;

    @FXML
    private CheckBox adminCheckbox;

    private User user;
    private Stage dialogStage;

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            CINInput.setText(user.getCin());
            nomInput.setText(user.getLast_name());
            prenomInput.setText(user.getFirst_name());
            emailInput.setText(user.getEmail());
            birthInput.setValue(user.getDateOfBirth());
            adminCheckbox.setSelected(user.isAdmin());
        }
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    public void setDialogStage(Stage dialogStage) {this.dialogStage=dialogStage;}

    @FXML
    private void handleSave() throws SQLException {
        if (validateUserData()) {
            updateUserData();
            if (User.updateUser(user)) {
                dialogStage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez remplir tous les champs !");
            alert.showAndWait();
        }
    }

    public boolean validateUserData() {
        // Implement your validation logic here
        return !CINInput.getText().isEmpty() &&
                !nomInput.getText().isEmpty() &&
                !prenomInput.getText().isEmpty() &&
                !emailInput.getText().isEmpty() &&
                birthInput.getValue() != null;
    }

    public void updateUserData() {
        if (user == null) {
            user = new User();
        }
        user.setCin(CINInput.getText());
        user.setLast_name(nomInput.getText());
        user.setFirst_name(prenomInput.getText());
        user.setEmail(emailInput.getText());
        user.setDateOfBirth(birthInput.getValue());
        user.setAdmin(adminCheckbox.isSelected());
    }
}