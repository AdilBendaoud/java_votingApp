package com.example.votingapp;

import Util.AlertMessage;
import Util.DBconnection;
import com.example.votingapp.Model.Election;
import com.example.votingapp.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class ElectionManagementController {
    private User user;

    @FXML
    private TableView<Election> electionTable;

    @FXML
    private TableColumn<Election, Date> election_col_date_debut;

    @FXML
    private TableColumn<Election, Void> election_col_actions;

    @FXML
    private TableColumn<Election, Date> election_col_date_fin;

    @FXML
    private TableColumn<Election, String> election_col_gagnant;

    @FXML
    private Button electionAddButton;

    @FXML
    private TableColumn<Election, Integer> election_col_id;

    @FXML
    private TableColumn<Election, String> election_col_nom;

    @FXML
    private TextField searchInput;

    @FXML
    private Label username_label;

    public ElectionManagementController() {
    }
    public void setUser(User user){this.user = user;}
    public User getUser(){return user;}
    public void initData(String username) {username_label.setText(username);}

    public ObservableList<Election> getElectionsData(String word) throws SQLException {
        DBconnection connection = new DBconnection();
        ObservableList<Election> listData = FXCollections.observableArrayList();
        String query = "";
        if(!word.isEmpty()){
            query = "SELECT * FROM elections WHERE nom_election LIKE '%"+word+"%' ORDER BY id";
        }else{
            query = "SELECT * FROM elections ORDER BY id";
        }
        connection.openConnection();
        try {
            ResultSet result = connection.executeQuery(query);
            Election election;
            while (result.next()) {
                election = new Election(result.getInt("id"),
                        result.getString("nom_election"), result.getDate("date_debut").toLocalDate(),
                        result.getDate("date_fin").toLocalDate(),result.getString("gagnant"));
                listData.add(election);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
        return listData;
    }

    public void electionTableDisplay(String word) throws SQLException {
        ObservableList<Election> elections = getElectionsData(word);
        election_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        election_col_nom.setCellValueFactory(new PropertyValueFactory<>("nom_election"));
        election_col_date_debut.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        election_col_date_fin.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        election_col_gagnant.setCellValueFactory(new PropertyValueFactory<>("gagnant"));
        election_col_actions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editer");
            private final Button deleteButton = new Button("Suppr");
            private final Button participationButton = new Button("Candidats");
            private final HBox buttons = new HBox(editButton, deleteButton, participationButton);
            {
                buttons.setSpacing(10);
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().addAll("action-button", "edit-button");
                deleteButton.getStyleClass().addAll("action-button", "delete-button");
                participationButton.getStyleClass().add("action-button");

                participationButton.setOnAction(event -> {
                    Election election = getTableView().getItems().get(getIndex());
                    showCandidates(election, (Stage) electionTable.getScene().getWindow());
                });

                editButton.setOnAction(event -> {
                    Election election = getTableView().getItems().get(getIndex());
                    showDialog(election,(Stage) electionTable.getScene().getWindow(), true);
                });

                deleteButton.setOnAction(event -> {
                    Election election = getTableView().getItems().get(getIndex());
                    AlertMessage alert = new AlertMessage();
                    if(alert.confirmation("Êtes-vous certain de vouloir supprimer l'élection ayant l'identifiant (ID) = " + election.getId()+" ?")){
                        try {
                            if(Election.deleteElection(election)){
                                System.out.println("Delete: election id = " + election.getId());
                                electionTableDisplay("");
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
        electionTable.setItems(elections);
    }

    public void handleSearch() throws SQLException {
        electionTableDisplay(searchInput.getText());
    }

    public void goToCandidate() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Canditat-management.fxml"));
        Parent root = loader.load();
        CandidatController candidatController = loader.getController();
        candidatController.initData(user.getFirst_name() + " " + user.getLast_name());
        candidatController.setUser(user);
        Stage stage = getStage(root);
        stage.setTitle("Page des Candidats");
        stage.show();
    }

    public void goToUser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Gestion-Users.fxml"));
        Parent root = loader.load();
        UsersManagementController mainController = loader.getController();
        mainController.initData(user.getFirst_name() + " " + user.getLast_name());
        mainController.setUser(user);
        Stage stage = getStage(root);
        stage.setTitle("Page des Utilisateurs");
        stage.show();
    }

    public void goToResults() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resultat-view.fxml"));
        Parent root = loader.load();
        ResultatController candidatController = loader.getController();
        candidatController.initData(user.getFirst_name() + " " + user.getLast_name());
        candidatController.setUser(user);
        Stage stage = getStage(root);
        stage.setTitle("Page des Résultats");
        stage.show();
    }

    public void goToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth-view.fxml"));
        Parent root = loader.load();
        Stage stage = getStage(root);
        stage.setTitle("Login");
        stage.show();
    }

    private Stage getStage(Parent root) {
        Stage stage = (Stage) electionAddButton.getScene().getWindow();
        stage.centerOnScreen();
        stage.setScene(new Scene(root));
        return stage;
    }


    public void addElection(){
        showDialog(null, (Stage) electionTable.getScene().getWindow(), false);
    }

    private void showDialog(Election election, Stage primaryStage, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("election-form.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            ElectionFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            if(edit){
                controller.setElection(election);
                controller.editForm();
            }else{
                controller.addForm();
            }
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if(!dialogStage.isShowing()){
                electionTableDisplay("");
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCandidates(Election election, Stage primaryStage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("participation-view.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            ParticipationController controller = loader.getController();
            controller.setElection(election);
            controller.setDialogStage(dialogStage);
            controller.refresh();

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        }catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() throws SQLException {
        electionTableDisplay("");
    }
}
