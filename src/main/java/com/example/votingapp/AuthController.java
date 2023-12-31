package com.example.votingapp;

import com.example.votingapp.Model.User;
import Util.AlertMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class AuthController implements Initializable {
    @FXML
    private AnchorPane changePass_form;

    @FXML
    private AnchorPane forgot_form;

    @FXML
    private AnchorPane login_form;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Button login_btn;

    @FXML
    private TextField login_cin;

    @FXML
    private Button login_newAccount;

    @FXML
    private PasswordField login_password;

    @FXML
    private Hyperlink login_resetPassword;

    @FXML
    private CheckBox login_showPassword;

    @FXML
    private TextField resetpass_answer;

    @FXML
    private Button resetpass_btn;

    @FXML
    private TextField resetpass_cin;

    @FXML
    private PasswordField resetpass_confirmPassword;

    @FXML
    private Button resetpass_continue;
    @FXML
    private TextField login_passwordShowen;
    @FXML
    private Button resetpass_login;

    @FXML
    private PasswordField resetpass_newPassword;

    @FXML
    private ComboBox<String> resetpass_question;

    @FXML
    private TextField signup_CIN;

    @FXML
    private DatePicker signup_DOB;

    @FXML
    private TextField signup_answer;

    @FXML
    private Button signup_btn;

    @FXML
    private PasswordField signup_confirmPassword;

    @FXML
    private TextField signup_email;

    @FXML
    private TextField signup_firstName;

    @FXML
    private TextField signup_lastName;

    @FXML
    private Button signup_login;

    @FXML
    private PasswordField signup_password;

    @FXML
    private ComboBox<String> signup_question;

    AlertMessage alertMessage;
    User user = null;
   @FXML
    protected void login() throws SQLException {
        alertMessage = new AlertMessage();
        if(login_cin.getText().isEmpty() || login_password.getText().isEmpty()){
            alertMessage.errorMessage("vous devez remplir tous les champs !!");
        } else {
            try {
                user = User.getUserByCode(login_cin.getText());
                if (user != null && login_password.getText().equals(user.getPassword())) {
                    openMainScene();
                } else {
                    alertMessage.errorMessage("CIN ou Mot de Passe Incorrect");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void register() throws SQLException {
        User user = null;
        alertMessage = new AlertMessage();

        // Vérification que tous les champs sont remplis
        if (signup_firstName.getText().isEmpty() || signup_lastName.getText().isEmpty() ||
                signup_answer.getText().isEmpty() || signup_question.getSelectionModel().getSelectedItem() == null ||
                signup_email.getText().isEmpty() || signup_CIN.getText().isEmpty() ||
                signup_password.getText().isEmpty() || signup_confirmPassword.getText().isEmpty() ||
                signup_DOB.getValue() == null) {
            alertMessage.errorMessage("Vous devez remplir tous les champs !");
            return;
        }

        // Vérification de la longueur du CIN
        if (signup_CIN.getText().length() < 5) {
            alertMessage.errorMessage("Le CIN doit contenir au moins 5 caractères");
            return;
        }
        // Vérification du format du CIN (deux lettres majuscules au début)
        if (!signup_CIN.getText().matches("^[A-Z]{2}\\d+")) {
            alertMessage.errorMessage("Format Incorect");
            return;
        }
        // Vérification de l'unicité du CIN
        try {
            user = User.getUserByCode(signup_CIN.getText());
            if (user != null) {
                alertMessage.errorMessage("Le CIN existe déjà !");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Vérification du format de l'email
        String emailPattern = "[a-zA-Z0-9._%+-]+@gmail\\.com";
        if (!signup_email.getText().matches(emailPattern)) {
            alertMessage.errorMessage("Format d'email invalide. Veuillez utiliser une adresse Gmail.");
            return;
        }

        // Vérification de l'unicité de l'email
        try {
            user = User.getUserByEmail(signup_email.getText());
            if (user != null) {
                alertMessage.errorMessage("Cette adresse Gmail existe déjà !");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Vérification du mot de passe
        String password = signup_password.getText();
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[!@#$%^&*()-+=].*")) {
            alertMessage.errorMessage("Le mot de passe doit contenir au moins 8 caractères, une majuscule et un caractère spécial.");
            return;
        }

        // Vérification de la confirmation du mot de passe
        if (!password.equals(signup_confirmPassword.getText())) {
            alertMessage.errorMessage("Les mots de passe ne sont pas identiques.");
            return;
        }

        // Ajoutez d'autres vérifications si nécessaire

        // Création d'un nouvel utilisateur si toutes les vérifications passent
        LocalDate selectedDate = signup_DOB.getValue();
        boolean userCreated = User.newUser(signup_CIN.getText(), signup_lastName.getText(), signup_firstName.getText(),
                signup_email.getText(), password, selectedDate,
                signup_question.getSelectionModel().getSelectedItem(), signup_answer.getText(), false);

        if (userCreated) {
            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Votre compte a été créé avec succès!");
            successAlert.showAndWait();

            // Effacer les champs et revenir à l'écran de connexion
            registerClearFields();
            signup_form.setVisible(false);
            login_form.setVisible(true);
        }
    }


    public void showPassword() {
        if (login_showPassword.isSelected()) {
            login_passwordShowen.setText(login_password.getText());
            login_passwordShowen.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_password.setText(login_passwordShowen.getText());
            login_passwordShowen.setVisible(false);
            login_password.setVisible(true);
        }

    }
    private User tmpUser;
    public void forgotPassword() throws SQLException {
       alertMessage = new AlertMessage();
       User user;
        if (resetpass_cin.getText().isEmpty()
                || resetpass_question.getSelectionModel().getSelectedItem() == null
                || resetpass_answer.getText().isEmpty()) {
            alertMessage.errorMessage("vous devez remplir tous les champs !!");
        }else {
            try{
                user = User.getUserByCode(resetpass_cin.getText());
                if(user == null){
                    alertMessage.errorMessage("CIN introuvable");
                }else {
                    if(user.getQuestion().equals((String) resetpass_question.getSelectionModel().getSelectedItem()) &&
                    user.getReponse().equals(resetpass_answer.getText())){
                        tmpUser = user;
                        resetpass_cin.setText("");
                        resetpass_question.getSelectionModel().clearSelection();
                        resetpass_answer.setText("");
                        forgot_form.setVisible(false);
                        changePass_form.setVisible(true);
                    }else{
                        alertMessage.errorMessage("les donnees sont invalides !!");
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void changePassword() throws SQLException {
        alertMessage = new AlertMessage();

        // Vérification des champs vides
        if (resetpass_newPassword.getText().isEmpty() || resetpass_confirmPassword.getText().isEmpty()) {
            alertMessage.errorMessage("Vous devez remplir tous les champs !!");
        } else if (!Objects.equals(resetpass_confirmPassword.getText(), resetpass_newPassword.getText())) {
            alertMessage.errorMessage("Les mots de passe saisis sont différents !!");
        } else {
            // Validation du nouveau mot de passe
            String newPassword = resetpass_newPassword.getText();
            if (!validatePassword(newPassword)) {
                // Si la validation échoue, afficher un message d'erreur
                alertMessage.errorMessage("Le mot de passe doit contenir au moins 8 caractères, une majuscule et un caractère spécial.");
            } else {
                try {
                    // Mise à jour du mot de passe dans la base de données
                    if (tmpUser.updatePassword(newPassword)) {
                        tmpUser = null;
                        changePass_form.setVisible(false);
                        login_form.setVisible(true);
                    } else {
                        alertMessage.errorMessage("Erreur dans la base de données");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Fonction de validation du mot de passe
    private boolean validatePassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[!@#$%^&*()-+=].*");
    }


    public void registerClearFields(){
       signup_CIN.setText("");
       signup_firstName.setText("");
       signup_lastName.setText("");
       signup_email.setText("");
       signup_password.setText("");
       signup_answer.setText("");
       signup_confirmPassword.setText("");
       signup_question.getSelectionModel().clearSelection();
       signup_DOB.setValue(null);
    }

    private final String[] questionList = { "Quel est le nom de votre premier animal de compagnie ?",
            "Dans quelle ville êtes-vous né(e) ?",
            "Quelle est votre couleur préférée ?",
            "Quel est le nom de votre école primaire ?",
            "Quel est votre plat préféré ?",
            "Quel est le prénom de votre grand-père maternel ?",
            "Quelle est la marque de votre première voiture ?",
            "Quel est le nom de votre meilleur(e) ami(e) d'enfance ?",
            "Quel est le prénom de votre professeur préféré à l'école ?"};

    public void questions() {
        List<String> listQ = new ArrayList<>(Arrays.asList(questionList));
        ObservableList<String> listData = FXCollections.observableArrayList(listQ);
        signup_question.setItems(listData);
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == signup_login || event.getSource() == resetpass_login) {
            signup_form.setVisible(false);
            login_form.setVisible(true);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_newAccount) { // THE LOGIN FORM WILL BE VISIBLE
            signup_form.setVisible(true);
            login_form.setVisible(false);
            forgot_form.setVisible(false);
            changePass_form.setVisible(false);
        } else if (event.getSource() == login_resetPassword) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changePass_form.setVisible(false);
            // TO SHOW THE DATA TO OUR COMBOBOX
            forgotListQuestion();
        }
    }

    private void forgotListQuestion() {
        List<String> listQ = new ArrayList<>(Arrays.asList(questionList));
        ObservableList<String> listData = FXCollections.observableArrayList(listQ);
        resetpass_question .setItems(listData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        questions();
    }


    private void openMainScene() {
        try {
            if(user.isAdmin()){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("election-management-view.fxml"));
                Parent root = loader.load();
                ElectionManagementController mainController = loader.getController();
                mainController.initData(user.getFirst_name() + " " + user.getLast_name());
                mainController.setUser(user);
                Stage stage = getStage(root);
                stage.show();
            }else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("User-Vote.fxml"));
                Parent root = loader.load();
                VoteContoller voteContoller = loader.getController();
                voteContoller.setUser(user);
                Stage stage = getStage(root);
                stage.show();            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage getStage(Parent root) {
        Stage stage = (Stage) login_btn.getScene().getWindow();
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getVisualBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) / 2.0;
        double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) / 2.0;

        stage.setX(centerX-400);
        stage.setY(centerY+20);
        stage.setScene(new Scene(root));
        stage.setTitle("Page des elections");
        return stage;
    }
}