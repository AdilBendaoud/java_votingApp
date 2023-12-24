package com.example.votingapp;

import com.example.votingapp.Model.Candidate;
import Util.AlertMessage;
import Util.DBconnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CandidatController {

    @FXML
    private TableView<Candidate> candidatTable;

    @FXML
    private TableColumn<Candidate, Void> candidat_col_actions;

    @FXML
    private TableColumn<Candidate, Date> candidat_col_date_naiss;

    @FXML
    private TableColumn<Candidate, String> candidat_col_genrer;

    @FXML
    private TableColumn<Candidate, Integer> candidat_col_id;

    @FXML
    private TableColumn<Candidate, String> candidat_col_nom;

    @FXML
    private TableColumn<Candidate, String> candidat_col_photo;

    @FXML
    private TableColumn<Candidate, String> candidat_col_pos;

    @FXML
    private TableColumn<Candidate, String> candidat_col_prenom;

    @FXML
    private TextField searchInput;

    @FXML
    void addCandidat() {
        showDialog(null, (Stage) candidatTable.getScene().getWindow(), false);
    }

    @FXML
    void handleSearch() {
        try {
            candidatTableDisplay(searchInput.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Candidate> getCandidatesData(String word) throws SQLException {
        DBconnection connection = new DBconnection();
        ObservableList<Candidate> listData = FXCollections.observableArrayList();
        String query = "";
        if (!word.isEmpty()) {
            query = "SELECT * FROM condidats WHERE nom LIKE '%" + word + "%' ORDER BY id";
        } else {
            query = "SELECT * FROM condidats ORDER BY id";
        }
        connection.openConnection();
        try {
            ResultSet result = connection.executeQuery(query);
            Candidate candidate;
            while (result.next()) {
                candidate = new Candidate(result.getInt("id"),
                        result.getString("prenom"),
                        result.getString("nom"),
                        result.getDate("date_naissance").toLocalDate(),
                        result.getString("genre"),
                        result.getString("position"),
                        result.getString("image"));
                listData.add(candidate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return listData;
    }

    public void candidatTableDisplay(String word) throws SQLException {
        ObservableList<Candidate> candidates = getCandidatesData(word);
        candidat_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        candidat_col_nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        candidat_col_prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        candidat_col_pos.setCellValueFactory(new PropertyValueFactory<>("position"));
        candidat_col_photo.setCellValueFactory(new PropertyValueFactory<>("image"));
        candidat_col_genrer.setCellValueFactory(new PropertyValueFactory<>("genre"));
        candidat_col_date_naiss.setCellValueFactory(new PropertyValueFactory<>("date_naissance"));

        candidat_col_actions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editer");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(editButton, deleteButton);

            {
                buttons.setSpacing(10);
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().addAll("action-button", "edit-button");
                deleteButton.getStyleClass().addAll("action-button", "delete-button");

                editButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    showDialog(candidate, (Stage) candidatTable.getScene().getWindow(), true);
                });

                deleteButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    AlertMessage alert = new AlertMessage();
                    if (alert.confirmation("Êtes-vous certain de vouloir supprimer le candidat ayant l'identifiant (ID) = " + candidate.getId() + " ?")) {
                        try {
                            if (Candidate.deleteCandidate(candidate)) {
                                System.out.println("Delete: candidate id = " + candidate.getId());
                                candidatTableDisplay("");
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

        candidatTable.setItems(candidates);
    }

    private void showDialog(Candidate candidate, Stage primaryStage, boolean edit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Candidat-Form.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            CandidatFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            if (edit) {
                controller.setCandidate(candidate);
                controller.editForm();
            } else {
                controller.addForm();
            }
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (!dialogStage.isShowing()) {
                candidatTableDisplay("");
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() throws SQLException {
        candidatTableDisplay("");
    }
}
