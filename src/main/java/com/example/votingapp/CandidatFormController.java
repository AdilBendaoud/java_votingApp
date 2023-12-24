package com.example.votingapp;

import com.example.votingapp.Model.Candidate;
import Util.AlertMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;

public class CandidatFormController {

    @FXML
    private TextField nom_Cand;

    @FXML
    private TextField Prénom_Cand;

    @FXML
    private TextField Pos_Cand;

    @FXML
    private DatePicker Datenaiss_Cand;

    @FXML
    private RadioButton F_Cand;

    @FXML
    private RadioButton H_Cand;

    @FXML
    private ImageView Image_Cand;

    @FXML
    private Button importBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label title;

    private Stage dialogStage;
    private Candidate candidate=new Candidate();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void editForm() {
        nom_Cand.setText(candidate.getNom());
        Prénom_Cand.setText(candidate.getPrenom());
        Pos_Cand.setText(candidate.getPosition());
        Datenaiss_Cand.setValue(candidate.getDate_naissance());
        if ("H".equals(candidate.getGenre())) {
            H_Cand.setSelected(true);
        } else {
            F_Cand.setSelected(true);
        }
        Image_Cand.setImage(new Image(String.valueOf(candidate.getImage())));
        saveBtn.setVisible(true);
        cancelBtn.setVisible(true);
        importBtn.setVisible(true);
        addBtn.setVisible(false);
        title.setText("Modifier le candidat");
    }

    public void addForm() {
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        importBtn.setVisible(true);
        title.setText("Ajouter un candidat");
    }

    @FXML
    private void handleSave() throws SQLException {
        AlertMessage alertMessage = new AlertMessage();
        if (alertMessage.confirmation("Êtes-vous certain de vouloir modifier le candidat ayant l'identifiant (ID) = " + candidate.getId() + " ?")) {
            updateCandidateData();
            if (validateCandidateData()) {
                if (Candidate.updateCandidate(candidate)) {
                    dialogStage.close();
                }
            } else {
                alertMessage.errorMessage("Veuillez remplir tous les champs !");
            }
        }
    }

    @FXML
    private void handleAdd() throws SQLException {
        AlertMessage alertMessage = new AlertMessage();
        updateCandidateData();
        if (validateCandidateData()) {
            if (Candidate.addCandidate(candidate)) {
                dialogStage.close();
            }
        } else {
            alertMessage.errorMessage("Veuillez remplir tous les champs !");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(dialogStage);
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                Image_Cand.setImage(image);

                // Mettez à jour l'URL de l'image dans l'objet Candidate
                candidate.setImage(selectedFile.toURI().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCandidateData() {
        candidate.setNom(nom_Cand.getText());
        candidate.setPrenom(Prénom_Cand.getText());
        candidate.setPosition(Pos_Cand.getText());
        candidate.setDate_naissance(Datenaiss_Cand.getValue());
        candidate.setGenre(H_Cand.isSelected() ? "M" : "F");
        candidate.setImage(Image_Cand.getImage().getUrl());

    }

    private boolean validateCandidateData() {
        return !nom_Cand.getText().isEmpty() &&
                !Prénom_Cand.getText().isEmpty() &&
                !Pos_Cand.getText().isEmpty() &&
                Datenaiss_Cand.getValue() != null &&
                (H_Cand.isSelected() || F_Cand.isSelected());
    }
}
