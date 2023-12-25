package com.example.votingapp.Model;

import Util.DBconnection;

import java.sql.SQLException;
import java.time.LocalDate;

public class Election {
    private int id;
    private String nom_election;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private String gagnant;
    public Election(int id, String nom_election, LocalDate date_debut, LocalDate date_fin, String gagnant) {
        this.id = id;
        this.nom_election = nom_election;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.gagnant = gagnant;
    }

    public Election(String nom_election){
        this.nom_election = nom_election;
    }

    public Election(){}

    public int getId() {
        return id;
    }

    public String getNom_election() {
        return nom_election;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public String getGagnant() {
        return gagnant;
    }

    public void setNom_election(String nom_election) {
        this.nom_election = nom_election;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    public void setGagnant(String gagnant) {
        this.gagnant = gagnant;
    }

    public static boolean addElection(Election election) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "INSERT INTO elections(nom_election, date_debut, date_fin) VALUES(?, ?, ?)";
            int rowsAffected = DBconnection.executeUpdate(query, election.nom_election, java.sql.Date.valueOf(election.date_debut), java.sql.Date.valueOf(election.date_fin));
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }

    public static boolean updateElection(Election election) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "UPDATE elections SET nom_election = ?, date_debut = ?, date_fin = ?  WHERE id = ?";
            int rowsAffected = DBconnection.executeUpdate(query, election.nom_election, java.sql.Date.valueOf(election.date_debut), java.sql.Date.valueOf(election.date_fin), election.id);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }

    public static boolean deleteElection(Election election) throws SQLException{
        DBconnection connection = new DBconnection();
        try{
            connection.openConnection();
            String query = "DELETE FROM elections WHERE id = ?";
            int rowsAffected = connection.executeUpdate(query, election.id);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            connection.closeConnection();
        }
    }

    @Override
    public String toString() {
        return nom_election ;
    }
}
