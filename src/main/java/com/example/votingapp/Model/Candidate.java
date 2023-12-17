package com.example.votingapp.Model;

import Util.DBconnection;

import java.sql.SQLException;
import java.time.LocalDate;

public class Candidate {
    private int id;
    private String prenom;
    private String nom;
    private LocalDate date_naissance;
    private char genre;
    private String position;
    private String image;

    public Candidate(int id, String prenom, String nom, LocalDate date_naissance, char genre, String position, String image) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.date_naissance = date_naissance;
        this.genre = genre;
        this.position = position;
        this.image = image;
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
        return date_naissance;
    }

    public char getGenre() {
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

    @Override
    public String toString() {
        return prenom+" "+nom;
    }
}
