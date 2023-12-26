package com.example.votingapp;

import com.example.votingapp.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UsersformController {

    @FXML
    private TextField cinInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> questionCombo;

    @FXML
    private TextField answerInput;

    @FXML
    private CheckBox adminCheckbox;

    private User user;
    private Stage dialog;

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            cinInput.setText(user.getCin());
            lastNameInput.setText(user.getLast_name());
            firstNameInput.setText(user.getFirst_name());
            emailInput.setText(user.getEmail());
            dobPicker.setValue(user.getDateOfBirth());
            adminCheckbox.setSelected(user.isAdmin());
        }
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void initialize() {
        String[] questionList = {
                "Quel est le nom de votre premier animal de compagnie ?",
                "Dans quelle ville êtes-vous né(e) ?",
                // ... (autres questions)
        };

        questionCombo.getItems().addAll(questionList);
    }

    @FXML
    private void handleSave() throws SQLException {
        if (validateUserData()) {
            updateUserData();
            if (User.updateUser(user)) {
                dialog.close();
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
        return !cinInput.getText().isEmpty() &&
                !lastNameInput.getText().isEmpty() &&
                !firstNameInput.getText().isEmpty() &&
                !emailInput.getText().isEmpty() &&
                dobPicker.getValue() != null &&
                questionCombo.getValue() != null &&
                !answerInput.getText().isEmpty();
    }

    public void updateUserData() {
        if (user == null) {
            user = new User();
        }

        user.setCin(cinInput.getText());
        user.setLast_name(lastNameInput.getText());
        user.setFirst_name(firstNameInput.getText());
        user.setEmail(emailInput.getText());
        user.setDateOfBirth(dobPicker.getValue());
        user.setAdmin(adminCheckbox.isSelected());
    }
}
