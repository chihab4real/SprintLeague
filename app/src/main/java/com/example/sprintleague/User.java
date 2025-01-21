package com.example.sprintleague;

import java.util.ArrayList;

public class User {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String profilePic;
    private boolean isActive;

    private String securityQuestion;
    private String securityQuestionResponse;

    private ArrayList<String> attendingTournaments;



    private double ranking;



    public User() {
    }

    public User(String id, String email, String firstName, String lastName, String password, String profilePic, boolean isActive, String securityQuestion, String securityQuestionResponse,double ranking, ArrayList<String> attendingTournaments) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.profilePic = profilePic;
        this.isActive = isActive;
        this.securityQuestion = securityQuestion;
        this.securityQuestionResponse = securityQuestionResponse;

        this.ranking = ranking;

        this.attendingTournaments = attendingTournaments;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityQuestionResponse() {
        return securityQuestionResponse;
    }

    public void setSecurityQuestionResponse(String securityQuestionResponse) {
        this.securityQuestionResponse = securityQuestionResponse;
    }

    public double getRanking() {
        return ranking;
    }

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }

    public ArrayList<String> getAttendingTournaments() {
        return attendingTournaments;
    }

    public void setAttendingTournaments(ArrayList<String> attendingTournaments) {
        this.attendingTournaments = attendingTournaments;
    }
}
