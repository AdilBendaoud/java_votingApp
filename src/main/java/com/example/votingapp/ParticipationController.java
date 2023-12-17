package com.example.votingapp;

import Util.AlertMessage;
import Util.DBconnection;
import com.example.votingapp.Model.Candidate;
import com.example.votingapp.Model.Election;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipationController {

    @FXML
    private Button addBtn;

    @FXML
    private ListView<Candidate> candidateListe;

    @FXML
    private ComboBox<Candidate> candidatesComboBox;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label title;

    private Stage dialogStage;

    private Election election;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setElection(Election election){this.election = election;}

    private ObservableList<Candidate> getCandidatesCombobox() throws SQLException {
        ObservableList<Candidate> candidatesList = FXCollections.observableArrayList();
        DBconnection connection = new DBconnection();
        String query = """
                SELECT c.id, c.prenom, c.nom
                FROM condidats c
                LEFT JOIN participation p ON c.id = p.id_condidat
                WHERE p.id_condidat IS NULL OR p.id_election != ?""";
        try{
            connection.openConnection();
            ResultSet resultSet = connection.executeQuery(query, election.getId());
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String prenom = resultSet.getString("prenom");
                String nom = resultSet.getString("nom");
                Candidate candidate = new Candidate(id, prenom, nom);
                candidatesList.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
        return candidatesList;
    }

    private ObservableList<Candidate> candidatesParticipatingInElection;
    private void refreshCandidatesListView() throws SQLException {
        candidatesParticipatingInElection = FXCollections.observableArrayList();
        DBconnection connection = new DBconnection();
        String query = """
                SELECT c.id, c.prenom, c.nom
                FROM condidats c
                LEFT JOIN participation p ON c.id = p.id_condidat
                WHERE p.id_election = ?""";
        try{
            connection.openConnection();
            ResultSet resultSet = connection.executeQuery(query, election.getId());
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String prenom = resultSet.getString("prenom");
                String nom = resultSet.getString("nom");
                Candidate candidate = new Candidate(id, prenom, nom);
                candidatesParticipatingInElection.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
    }

    public void handleAddBtn() throws SQLException {
        Candidate tmp = candidatesComboBox.getValue();
        AlertMessage alertMessage = new AlertMessage();
        if(Candidate.addCandidateToElection(tmp, election)){
            candidateListe.getItems().add(tmp);
            candidatesComboBox.getItems().remove(tmp);
            alertMessage.successMessage("Condidat : "+tmp+" a bien ete ajouter a l'élection : "+election.getNom_election());
        }else{
            alertMessage.errorMessage("erreur dans la base de donnees");
        }
    }

    public void handleDeleteBtn() throws SQLException {
        Candidate selectedCandidate = candidateListe.getSelectionModel().getSelectedItem();
        AlertMessage alertMessage = new AlertMessage();
        if(alertMessage.confirmation("Êtes-vous certain de vouloir supprimer "+selectedCandidate+" de l'élection ??")){
            if(Candidate.deleteCandidateFromElection(selectedCandidate, election)){
                candidatesParticipatingInElection.remove(selectedCandidate);
                candidatesComboBox.getItems().add(selectedCandidate);
                alertMessage.successMessage("Condidat : "+selectedCandidate+" a bien ete ajouter à l'élection : "+election.getNom_election());
            }else{
                alertMessage.errorMessage("erreur dans la base de donnees");
            }
        }
    }

    public void refresh() throws SQLException {
        refreshCandidatesListView();
        candidatesComboBox.setItems(getCandidatesCombobox());
        candidateListe.setItems(candidatesParticipatingInElection);
        title.setText(election.getNom_election());
    }
}
