package com.example.votingapp.Model;

public class CandidateWrapper {
    private Candidate candidate;
    private int votes;

    public CandidateWrapper(Candidate candidate, int votes) {
        this.candidate = candidate;
        this.votes = votes;
    }

    public CandidateWrapper(){
        candidate = new Candidate();
        votes = 0;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
