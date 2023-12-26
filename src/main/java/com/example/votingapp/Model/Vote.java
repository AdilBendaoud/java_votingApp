package com.example.votingapp.Model;

import Util.DBconnection;

import java.sql.SQLException;

public class Vote {
    private String cin_user;
    private String id_election;

    public Vote(String cin_user, String id_election){
        this.cin_user = cin_user;
        this.id_election = id_election;
    }

    public String getCin_user() {
        return cin_user;
    }
    public void setCin_user(String cin_user) {
        this.cin_user = cin_user;
    }
    public String getId_election() {
        return id_election;
    }
    public void setId_election(String id_election) {
        this.id_election = id_election;
    }

    public boolean newVote(String cin_user, String id_election) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "INSERT INTO votes (cin_user, id_election) VALUES (?, ?)";
            int rowsAffected = DBconnection.executeUpdate(query, cin_user, id_election);
            return rowsAffected > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBconnection.closeConnection();
        }
    }

}
