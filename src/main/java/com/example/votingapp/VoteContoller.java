package com.example.votingapp;

import Util.AlertMessage;
import Util.DBconnection;
import com.example.votingapp.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VoteContoller implements Initializable {
    @FXML
    private TableView<Candidate> candidatTable;

    @FXML
    private TableColumn<Candidate, Void> candidat_col_actions;

    @FXML
    private TableColumn<Candidate, LocalDate> candidat_col_date_naiss;

    @FXML
    private TableColumn<Candidate, String> candidat_col_nom;

    @FXML
    private TableColumn<Candidate, String> candidat_col_photo;

    @FXML
    private TableColumn<Candidate, String> candidat_col_pos;

    @FXML
    private TableColumn<Candidate, String> candidat_col_prenom;

    @FXML
    private AnchorPane erorPage;

    @FXML
    private ComboBox<Election> comboBox;
    @FXML
    private Label dateTermine;
    @FXML
    private AnchorPane gagnantPage;

    @FXML
    private ImageView image;

    @FXML
    private Label labelWiner;

    @FXML
    private AnchorPane votePage;

    private User user;
    private ArrayList<CandidateWrapper> electionResults;
    @FXML
    private void handleComboBoxChange() throws SQLException {
        Election selectedElection = comboBox.getValue();
        if(selectedElection != null && LocalDate.now().isAfter(selectedElection.getDate_fin())){
            electionResults = new ArrayList<>();
            getElectionResults(selectedElection.getId());
            image.setVisible(false);
            CandidateWrapper winner = getWinner();
            if(winner.getCandidate().getImage() != null){
                image.setImage( new Image(winner.getCandidate().getImage()));
                image.setVisible(true);
            }
            labelWiner.setText("Gagnant c'est : "+ winner.getCandidate().getPrenom()+" "+getWinner().getCandidate().getNom());
            labelWiner.setVisible(true);
            gagnantPage.setVisible(true);
            votePage.setVisible(false);
            erorPage.setVisible(false);
        }else{
            gagnantPage.setVisible(false);
            DBconnection dBconnection = new DBconnection();
            try {
                dBconnection.openConnection();
                String sql = "select * from votes where cin_user = ? and id_election = ?";
                assert selectedElection != null;
                ResultSet resultSet = dBconnection.executeQuery(sql, user.getCin(), selectedElection.getId());
                if(resultSet!=null  && resultSet.next()){
                    votePage.setVisible(false);
                    erorPage.setVisible(true);
                    dateTermine.setText("l'election va terminer le "+selectedElection.getDate_fin());
                }else{
                    candidatTableDisplay(selectedElection.getId());
                    votePage.setVisible(true);
                    erorPage.setVisible(false);
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }finally {
                dBconnection.closeConnection();
            }
        }
    }
    private void getElectionResults(int electionId) throws SQLException {
        DBconnection connection = new DBconnection();
        String sql = """
                select prenom, nom,image , count(*) as vote_count
                from votes join condidats on condidats.id = votes.id_candidate
                where id_election = ?
                group by prenom,nom,image;""";
        try {
            connection.openConnection();
            ResultSet resultSet = connection.executeQuery(sql, electionId);
            while (resultSet.next()) {
                Candidate cn = new Candidate();
                cn.setPrenom(resultSet.getString("prenom") );
                cn.setNom(resultSet.getString("nom"));
                cn.setImage(resultSet.getString("image"));
                CandidateWrapper cnw = new CandidateWrapper(cn,resultSet.getInt("vote_count"));
                electionResults.add(cnw);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.closeConnection();
        }
    }
    public void setUser(User user) {this.user = user;}

    public void initComboBox() throws SQLException {
        ObservableList<Election> elections = getElections();
        comboBox.getItems().addAll(elections);
    }
    public ObservableList<Election> getElections() throws SQLException {
        DBconnection connection = new DBconnection();
        ObservableList<Election> listData = FXCollections.observableArrayList();
        String query = "SELECT * FROM elections ORDER BY id";
        try {
            connection.openConnection();
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
        } finally {
            connection.closeConnection();
        }
        return listData;
    }
    public void goToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth-view.fxml"));
        Parent root = loader.load();
        Stage stage = getStage(root);
        stage.setTitle("Login");
        stage.show();
    }

    private Stage getStage(Parent root) {
        Stage stage = (Stage) labelWiner.getScene().getWindow();
        stage.centerOnScreen();
        stage.setScene(new Scene(root));
        return stage;
    }
    private CandidateWrapper getWinner() {
        CandidateWrapper tmp = new CandidateWrapper();
        for(CandidateWrapper cw : electionResults){
            if(cw.getVotes() > tmp.getVotes()){
                tmp = cw;
            }
        }
        return tmp;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gagnantPage.setVisible(false);
        votePage.setVisible(false);
        erorPage.setVisible(false);
        electionResults = new ArrayList<>();
        try {
            initComboBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<Candidate> getCandidatesData(int electionId) throws SQLException {
        DBconnection connection = new DBconnection();
        ObservableList<Candidate> listData = FXCollections.observableArrayList();
        String query = "select * from participation join condidats on participation.id_condidat = condidats.id where participation.id_election = ?;";
        try {
            connection.openConnection();
            ResultSet result = connection.executeQuery(query,electionId);
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

    public void candidatTableDisplay(int electionId) throws SQLException {
        ObservableList<Candidate> candidates = getCandidatesData(electionId);
        candidat_col_nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        candidat_col_prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        candidat_col_pos.setCellValueFactory(new PropertyValueFactory<>("position"));
        candidat_col_photo.setCellValueFactory(new PropertyValueFactory<>("image"));
        candidat_col_date_naiss.setCellValueFactory(new PropertyValueFactory<>("date_naissance"));

        candidat_col_photo.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    HBox hbxImg = new HBox();
                    hbxImg.setAlignment(Pos.CENTER);
                    Image image = new Image(imagePath);
                    imageView.setImage(image);
                    hbxImg.getChildren().add(imageView);
                    setGraphic(hbxImg);
                }
            }
        });
        candidat_col_actions.setCellFactory(param -> new TableCell<>() {
            private final Button voteButton = new Button("Voter");
            private final HBox buttons = new HBox(voteButton);
            {
                buttons.setAlignment(Pos.CENTER);
                voteButton.getStyleClass().addAll("action-button", "edit-button");
                voteButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    Vote vote = new Vote(user.getCin(), electionId, candidate.getId());
                    try {
                        AlertMessage alert = new AlertMessage();
                        if(alert.confirmation("Êtes-vous certain de vouloir voter sur le candidat "+candidate.getPrenom()+" "+candidate.getNom() +" ?")){
                            if(vote.newVote()){
                                alert.successMessage("votre vote a ete enregistrer");
                                votePage.setVisible(false);
                                erorPage.setVisible(true);
                            }else{
                                alert.errorMessage("erreur base de donnees !");
                            }
                        }
                    }catch (SQLException e) {
                        throw new RuntimeException(e);
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
}
