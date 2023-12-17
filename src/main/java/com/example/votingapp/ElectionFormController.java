package com.example.votingapp;

import com.example.votingapp.Model.Election;
import Util.AlertMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ElectionFormController {
    @FXML
    private Button cancelBtn;
    @FXML
    private DatePicker endInput;
    @FXML
    private TextField nameInput;
    @FXML
    private Button saveBtn;
    @FXML
    private DatePicker startInput;
    private Stage dialogStage;
    private Election election;
    @FXML
    private Label title;
    @FXML
    private Button addBtn;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void editForm(){
        nameInput.setText(election.getNom_election());
        startInput.setValue(election.getDate_debut());
        endInput.setValue(election.getDate_fin());
        saveBtn.setVisible(true);
        cancelBtn.setVisible(true);
        addBtn.setVisible(false);
        title.setText("Modifier l'élection");
    }

    public void addForm(){
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        addBtn.setVisible(true);
        title.setText("Ajouter une élection");
    }

    @FXML
    private void handleSave() throws SQLException {
        AlertMessage alertMessage = new AlertMessage();
        if(alertMessage.confirmation("Êtes-vous certain de vouloir modifier l'élection ayant l'identifiant (ID) = " + election.getId()+" ?")){
            election.setNom_election(nameInput.getText());
            election.setDate_debut(startInput.getValue());
            election.setDate_fin(endInput.getValue());
            if(!election.getNom_election().isEmpty() && election.getDate_debut() != null && election.getDate_fin() != null){
                int result = election.getDate_debut().compareTo(election.getDate_fin());
                if(result > 0){
                    alertMessage.errorMessage("date de debut d'élection est superieur à la date de fin d'élection");
                }else{
                    if(Election.updateElection(election)){
                        dialogStage.close();
                    }
                }
            }else{
                alertMessage.errorMessage("vous devez remplir tous les champs !!");
            }
        }
    }

    @FXML
    private void handleAdd() throws SQLException {
        AlertMessage alertMessage = new AlertMessage();
        election = new Election();
        election.setNom_election(nameInput.getText());
        election.setDate_debut(startInput.getValue());
        election.setDate_fin(endInput.getValue());
        if(!election.getNom_election().isEmpty() && election.getDate_debut() != null && election.getDate_fin() != null){
            int result = election.getDate_debut().compareTo(election.getDate_fin());
            if(result > 0){
                alertMessage.errorMessage("date de debut d'élection est superieur à la date de fin d'élection");
            }else{
                if(Election.addElection(election)){
                    dialogStage.close();
                }
            }
        }else{
            alertMessage.errorMessage("vous devez remplir tous les champs !!");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
