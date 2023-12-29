package com.example.votingapp;

import Util.DBconnection;
import com.example.votingapp.Model.Candidate;
import com.example.votingapp.Model.CandidateWrapper;
import com.example.votingapp.Model.Election;
import com.example.votingapp.Model.User;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ResultatController implements Initializable {
    @FXML
    private ImageView image;

    @FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label labelWiner,statusLabel;

    @FXML
    private ComboBox<Election> comboBox;

    @FXML
    private Label username_label;

    @FXML
    private Button rapportBtnPdf;

    private ArrayList<CandidateWrapper> electionResults;

    private User user;

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

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

    public void initComboBox() throws SQLException {
        ObservableList<Election> elections = getElections();
        comboBox.getItems().addAll(elections);
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

    public void handleComboBoxChanged() throws SQLException {
        Election selectedElection = comboBox.getValue();
        if(selectedElection != null && LocalDate.now().isAfter(selectedElection.getDate_fin())){
            electionResults = new ArrayList<>();
            getElectionResults(selectedElection.getId());

            ObservableList<XYChart.Series<String, Integer>> barChartData = getElectionResultsBarChart();
            barChart.setData(barChartData);
            barChart.setTitle("resultats d'election");

            ObservableList<PieChart.Data> pieChartData = getElectionResultsPieChart();
            pieChart.setData(pieChartData);
            pieChartData.forEach(data ->
                    data.nameProperty().bind(
                            Bindings.concat(data.getName()," ", Math.round(data.pieValueProperty().getValue()/calculateTotalVotes(pieChartData)* 100),"%")
                    )
            );
            image.setVisible(false);
            CandidateWrapper winner = getWinner();
            if(winner.getCandidate().getImage() != null){
                image.setImage( new Image(winner.getCandidate().getImage()));
                image.setVisible(true);
            }
            labelWiner.setText("gagnant c'est : "+ winner.getCandidate().getPrenom()+" "+getWinner().getCandidate().getNom());

            statusLabel.setVisible(false);
            barChart.setVisible(true);
            pieChart.setVisible(true);
            labelWiner.setVisible(true);
        }else{
            statusLabel.setText("The election is ongoing.");
            statusLabel.setVisible(true);
            barChart.setVisible(false);
            pieChart.setVisible(false);
            image.setVisible(false);
            labelWiner.setVisible(false);
        }
    }

    private int calculateTotalVotes(ObservableList<PieChart.Data> pieChartData) {
        int totalVotes = 0;
        for (PieChart.Data data : pieChartData) {
            totalVotes += (int) data.getPieValue();
        }
        return totalVotes;
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

    private ObservableList<XYChart.Series<String, Integer>> getElectionResultsBarChart(){
        ObservableList<XYChart.Series<String, Integer>> seriesList = FXCollections.observableArrayList();
        for(CandidateWrapper c : electionResults){
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            String candidateName = c.getCandidate().getPrenom()+" "+c.getCandidate().getNom();
            series.setName(candidateName);
            series.getData().add(new XYChart.Data<>(candidateName, c.getVotes()));
            seriesList.add(series);
        }
        return seriesList;
    }

    private ObservableList<PieChart.Data> getElectionResultsPieChart(){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for(CandidateWrapper c : electionResults){
                String candidateName = c.getCandidate().getPrenom()+" "+c.getCandidate().getNom();
                PieChart.Data data = new PieChart.Data(candidateName, c.getVotes());
                pieChartData.add(data);
            }
        return pieChartData;
    }

    public void goToElection() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("election-management-view.fxml"));
        Parent root = loader.load();
        ElectionManagementController mainController = loader.getController();
        mainController.initData(user.getFirst_name() + " " + user.getLast_name());
        mainController.setUser(user);
        Stage stage = getStage(root);
        stage.show();
    }
    public void goToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth-view.fxml"));
        Parent root = loader.load();
        Stage stage = getStage(root);
        stage.show();
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

    public void goToUser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Gestion-Users.fxml"));
        Parent root = loader.load();
        UsersManagementController mainController = loader.getController();
        mainController.initData(user.getFirst_name() + " " + user.getLast_name());
        mainController.setUser(user);
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
        stage.setTitle("Page des Résultats");
        return stage;
    }
    public  void handlePdfGenerateBtn(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            File file = fileChooser.showSaveDialog(rapportBtnPdf.getScene().getWindow());

            if(file != null){
                try (PdfWriter writer = new PdfWriter(file);
                     PdfDocument pdf = new PdfDocument(writer);
                     Document document = new Document(pdf)) {
                    document.add(new Paragraph("Election Results:"));
                    for(CandidateWrapper c : electionResults){
                        document.add(new Paragraph(c.getCandidate().getPrenom() + " "+c.getCandidate().getNom() + " : " + c.getVotes()+ " votes"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try {
            initComboBox();
            electionResults = new ArrayList<>();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initData(String s) {username_label.setText(s);}
}
