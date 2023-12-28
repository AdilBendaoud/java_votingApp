package com.example.votingapp.Model;

import Util.DBconnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class User {
    private String cin;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private boolean isAdmin;
    private String question;
    private String reponse;

    public User(String cin, String first_name, String last_name, String email, String password, LocalDate dateOfBirth, boolean isAdmin, String question, String reponse) {
        this.cin = cin;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.isAdmin = isAdmin;
        this.question = question;
        this.reponse = reponse;
    }

    public User() {}

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public static boolean newUser(String cin, String last_name, String first_name, String email, String password,
                                  LocalDate dateOfBirth, String question, String answer, boolean isAdmin ) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        LocalDate today = LocalDate.now();
        try{
            DBconnection.openConnection();
            String query = "INSERT INTO Users (CIN, last_name, first_name, email, password, date_of_birth, question, answer, isAdmin)\n" +
                    "VALUES (?,?, ?, ?, ?, ?, ?, ?, ?);";
            int rowsAffected = DBconnection.executeUpdate(query, cin, last_name, first_name, email, password,
                    java.sql.Date.valueOf(dateOfBirth), question, answer, isAdmin);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }

    public boolean updatePassword(String newPassword) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "UPDATE users SET password = ? WHERE CIN = ?";
            int rowsAffected = DBconnection.executeUpdate(query, newPassword, getCin());
            return rowsAffected > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }

    public static User getUserByCode(String code) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "SELECT * FROM users WHERE CIN = ?";
            ResultSet resultSet = DBconnection.executeQuery(query, code);
            if (resultSet.next()) {
                String cin = resultSet.getString("CIN");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                boolean isAdmin = resultSet.getBoolean("isAdmin");
                String quest = resultSet.getString("question");
                String reponse = resultSet.getString("answer");
                return new User(cin, firstName, lastName, email, password, dateOfBirth, isAdmin, quest, reponse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBconnection.closeConnection();
        }
        return null;
    }
    public static boolean deleteUser(String cin) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try {
            DBconnection.openConnection();
            String query = "DELETE FROM users WHERE CIN = ?";
            int rowsAffected = DBconnection.executeUpdate(query, cin);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBconnection.closeConnection();
        }
    }

    public static boolean updateUser(User user) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try {
            DBconnection.openConnection();
            String query = "UPDATE users SET last_name = ?, first_name = ?, email = ?, " +
                    "date_of_birth = ?, isAdmin = ? WHERE CIN = ?";
            int rowsAffected = DBconnection.executeUpdate(query,
                    user.getLast_name(),
                    user.getFirst_name(),
                    user.getEmail(),
                    java.sql.Date.valueOf(user.getDateOfBirth()),
                    user.isAdmin(),
                    user.getCin());
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBconnection.closeConnection();
        }
    }


    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        DBconnection DBconnection = new DBconnection();
        try {
            DBconnection.openConnection();
            String query = "SELECT * FROM users";
            ResultSet resultSet = DBconnection.executeQuery(query);

            while (resultSet.next()) {
                User user = new User();
                user.setCin(resultSet.getString("CIN"));
                user.setPassword(resultSet.getString("password"));
                user.setFirst_name(resultSet.getString("first_name"));
                user.setLast_name(resultSet.getString("last_name"));
                user.setEmail(resultSet.getString("email"));
                user.setDateOfBirth(resultSet.getDate("date_of_birth").toLocalDate());
                user.setAdmin(resultSet.getBoolean("isAdmin"));
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBconnection.closeConnection();
        }
        return usersList;
    }
}

