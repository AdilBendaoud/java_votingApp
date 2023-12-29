package com.example.votingapp.Model;

import Util.DBconnection;

import java.sql.SQLException;

public class Vote {
    private String cin_user;
    private int id_election;
    private int id_candidate;

    public Vote(String cin_user, int id_election, int id_candidate){
        this.cin_user = cin_user;
        this.id_election = id_election;
        this.id_candidate = id_candidate;
    }

    public String getCin_user() {
        return cin_user;
    }
    public void setCin_user(String cin_user) {
        this.cin_user = cin_user;
    }
    public int getId_election() {
        return id_election;
    }
    public void setId_election(int id_election) {
        this.id_election = id_election;
    }

    public boolean newVote() throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "INSERT INTO votes (cin_user, id_election, id_candidate) VALUES (?, ?, ?)";
            int rowsAffected = DBconnection.executeUpdate(query, cin_user, id_election, id_candidate);
            return rowsAffected > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBconnection.closeConnection();
        }
    }

}
