package com.example.sprintleague;

public class Result {

    private String tournamentID;
    private String userID;
    private String timing;

    Result(){

    }
    public Result(String tournamentID, String userID, String timing) {
        this.tournamentID = tournamentID;
        this.userID = userID;
        this.timing = timing;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}
