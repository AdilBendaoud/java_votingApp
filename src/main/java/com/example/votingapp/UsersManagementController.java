package com.example.votingapp;

import com.example.votingapp.Model.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UsersManagementController {
    private User user;
    @FXML
    private TableView<User> UserTable;

    @FXML
    private TableColumn<User, Void> User_col_actions;

    @FXML
    private TableColumn<User, String> User_col_cin;

    @FXML
    private TableColumn<User, String> User_col_nom;
    @FXML
    private Label username_label;
    @FXML
    private TableColumn<User, String> User_col_prenom;

    @FXML
    private TableColumn<User, String> User_col_email;

    @FXML
    private TableColumn<User, Boolean> User_col_Role;

    @FXML
    private TableColumn<User, LocalDate> User_col_Datebirth;

    @FXML
    private TextField searchInput;

    @FXML
    private Label usernameLabel;

    @FXML
    void handleSearch(KeyEvent event) {
        try {
            userTableDisplay(searchInput.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setUser(User user){this.user = user;}
    @FXML
    private void initialize() throws SQLException {
        userTableDisplay("");
    }
    public void initData(String username) {username_label.setText(username);}
    public void userTableDisplay(String word) throws SQLException {
        ObservableList<User> users = User.getAllUsers();
        User_col_cin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        User_col_nom.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        User_col_prenom.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        User_col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        // User_col_Role.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        User_col_Datebirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        User_col_actions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editer");
            private final Button deleteButton = new Button("Suppr");
            private final HBox buttons = new HBox(editButton, deleteButton);
            {
                buttons.setSpacing(10);
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().addAll("action-button", "edit-button");
                deleteButton.getStyleClass().addAll("action-button", "delete-button");

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDialog(user, UserTable.getScene().getWindow());
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Supprimer l'utilisateur");
                    alert.setContentText("Êtes-vous certain de vouloir supprimer l'utilisateur avec CIN = " + user.getCin() + " ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            if (User.deleteUser(user.getCin())) {
                                System.out.println("Delete: user CIN = " + user.getCin());
                                userTableDisplay("");
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
        UserTable.setItems(users);
    }

    public void goToCandidate() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Canditat-management.fxml"));
        Parent root = loader.load();
        CandidatController candidatController = loader.getController();
        candidatController.initData(user.getFirst_name() + " " + user.getLast_name());
        candidatController.setUser(user);
        Stage stage = getStage(root);
        stage.show();
    }
    public void goToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth-view.fxml"));
        Parent root = loader.load();
        Stage stage = getStage(root);
        stage.show();
    }
    public void goToResults () throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resultat-view.fxml"));
        Parent root = loader.load();
        ResultatController candidatController = loader.getController();
        candidatController.initData(user.getFirst_name() + " " + user.getLast_name());
        candidatController.setUser(user);
        Stage stage = getStage(root);
        stage.show();
    }

    public void goToElection () throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("election-management-view.fxml"));
        Parent root = loader.load();
        ElectionManagementController candidatController = loader.getController();
        candidatController.initData(user.getFirst_name() + " " + user.getLast_name());
        candidatController.setUser(user);
        Stage stage = getStage(root);
        stage.show();
    }

    private Stage getStage(Parent root) {
        Stage stage = (Stage) username_label.getScene().getWindow();
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getVisualBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) / 2.0;
        double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) / 2.0;

        stage.setX(centerX-400);
        stage.setY(centerY+20);
        stage.setScene(new Scene(root));
        stage.setTitle("Page des Utilisateurs");
        return stage;
    }

    private void showUserDialog(User user, Window primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Users-form.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            UsersformController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUser(user);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            if (!dialogStage.isShowing()) {
                userTableDisplay("");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
