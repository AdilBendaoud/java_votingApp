package com.example.votingapp;

import com.example.votingapp.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.Date;

public class UsersManagementController {

    @FXML
    private TableView<User> UserTable;

    @FXML
    private TableColumn<User, Date> User_col_Datebirth;

    @FXML
    private TableColumn<User,Void> User_col_actions;

    @FXML
    private TableColumn<User, String> User_col_cin;

    @FXML
    private TableColumn<User, String> User_col_email;

    @FXML
    private TableColumn<User, String> User_col_nom;

    @FXML
    private TableColumn<User, String> User_col_prenom;

    @FXML
    private TextField searchInput;

    @FXML
    private Label username_label;

    @FXML
    void handleSearch(KeyEvent event) {

    }



}

