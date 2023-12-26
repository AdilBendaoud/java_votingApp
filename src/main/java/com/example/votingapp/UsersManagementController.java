package com.example.votingapp;

import com.example.votingapp.Model.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UsersManagementController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Void> userColActions;

    @FXML
    private TableColumn<User, String> userColCin;

    @FXML
    private TableColumn<User, String> userColLastName;

    @FXML
    private TableColumn<User, String> userColFirstName;

    @FXML
    private TableColumn<User, String> userColEmail;

    @FXML
    private TableColumn<User, LocalDate> userColDateOfBirth;

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

    @FXML
    private void initialize() throws SQLException {
        userTableDisplay("");
    }

    public void userTableDisplay(String word) throws SQLException {
        ObservableList<User> users = User.getAllUsers();
        userColCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        userColLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        userColFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        userColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        userColDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        userColActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editer");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(editButton, deleteButton);

            {
                buttons.setSpacing(10);
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().addAll("action-button", "edit-button");
                deleteButton.getStyleClass().addAll("action-button", "delete-button");
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDialog(user, userTable.getScene().getWindow(), true);
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

        userTable.setItems(users);
    }

    private void showUserDialog(User user, Window primaryStage, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserForm.fxml"));
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(primaryStage);
            dialog.setTitle("Editer l'utilisateur");
            dialog.setHeaderText("Editer l'utilisateur");

            loader.setControllerFactory(c -> {
                UsersformController controller = new UsersformController();
                controller.setUser(user);
                controller.setDialog(dialog);
                return controller;
            });

            dialog.getDialogPane().setContent(loader.load());
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                UsersformController controller = loader.getController();
                if (controller.validateUserData()) {
                    controller.updateUserData();
                    if (User.updateUser(user)) {
                        userTableDisplay("");
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de validation");
                    alert.setHeaderText("Veuillez remplir tous les champs !");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
