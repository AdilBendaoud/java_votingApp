package com.example.votingapp.Model;

import Util.DBconnection;

import java.sql.SQLException;
import java.time.LocalDate;

public class Candidate {
    private int id;
    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String genre;
    private String position;
    private String image;

    public Candidate(int id, String prenom, String nom, LocalDate date_naissance, String genre, String position, String image) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = date_naissance;
        this.genre = genre;
        this.position = position;
        this.image = image;
    }
    public Candidate(){

    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDate_naissance() {
        return dateNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosition() {
        return position;
    }

    public Candidate(int id, String prenom, String nom){
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
    }

    public static boolean addCandidateToElection(Candidate candidate, Election election) throws SQLException {
        DBconnection connection = new DBconnection();
        String query = "INSERT INTO participation(id_election, id_condidat) values (?, ?)";
        try{
            connection.openConnection();
            int result = connection.executeUpdate(query, election.getId(), candidate.getId());
            return result > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            connection.closeConnection();
        }
    }

    public static boolean deleteCandidateFromElection(Candidate candidate, Election election) throws SQLException {
        DBconnection connection = new DBconnection();
        String query = "DELETE FROM participation WHERE id_election = ? AND id_condidat = ?";
        try{
            connection.openConnection();
            int result = connection.executeUpdate(query, election.getId(), candidate.getId());
            return result > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            connection.closeConnection();
        }
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDate_naissance(LocalDate date_naissance) {
        this.dateNaissance = date_naissance;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public static boolean addCandidate(Candidate canditat) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "INSERT INTO condidats (prenom, nom, date_naissance,genre,position,image) VALUES(?, ?, ?,?,?,?)";
            int rowsAffected = DBconnection.executeUpdate(query, canditat.prenom, canditat.nom, java.sql.Date.valueOf(canditat.dateNaissance), canditat.genre, canditat.position, canditat.image);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }
    public static boolean updateCandidate(Candidate canditat) throws SQLException {
        DBconnection DBconnection = new DBconnection();
        try{
            DBconnection.openConnection();
            String query = "UPDATE condidats SET prenom = ?, nom = ?,date_naissance = ?, genre = ?,position = ?, image = ?  WHERE id = ?";
            int rowsAffected = DBconnection.executeUpdate(query, canditat.prenom, canditat.nom, java.sql.Date.valueOf(canditat.dateNaissance), canditat.genre, canditat.position, canditat.image, canditat.id);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBconnection.closeConnection();
        }
    }
    public static boolean deleteCandidate(Candidate canditat) throws SQLException{
        DBconnection connection = new DBconnection();
        try{
            connection.openConnection();
            String query = "DELETE FROM condidats WHERE id = ?";
            int rowsAffected = connection.executeUpdate(query, canditat.id);
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
        return prenom+" "+nom;
    }
}
