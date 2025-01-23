package com.example.sprintleague;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Tournament  implements Comparable<Tournament>{

    private String ID;
    private String title;
    private String organizerID;
    private Float distance;
    private DateTime dateTime;
    private int maxParticipants;

    private CustomeAddress address;

    private ArrayList<Sponsor> sponsors;

    private String tournamentCoverLink;

    private ArrayList<String> attendingList;

    private boolean lockedForEdit;

    private DateTime joinDeadline;

    private String level;

    private boolean resultsPosted;


    public Tournament() {
    }

    public Tournament(String id, String title, String organizerID, Float distance, DateTime dateTime, int maxParticipants, CustomeAddress address, ArrayList<Sponsor> sponsors, String coverlink, ArrayList<String> attendingList, boolean lockedForEdit, DateTime joinDeadline, String level, boolean resultsPosted) {
        this.ID = id;
        this.title = title;
        this.organizerID = organizerID;
        this.distance = distance;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.address = address;

        this.attendingList = attendingList;

        if(sponsors != null){
            this.sponsors = sponsors;
        }else{
            this.sponsors = new ArrayList<>();
        }

        this.tournamentCoverLink = coverlink;

        this.lockedForEdit = lockedForEdit;

        this.joinDeadline = joinDeadline;
        this.level = level;
        this.resultsPosted = resultsPosted;


    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public CustomeAddress getAddress() {
        return address;
    }

    public void setAddress(CustomeAddress address) {
        this.address = address;
    }

    public ArrayList<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(ArrayList<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public String getTournamentCoverLink() {
        return tournamentCoverLink;
    }

    public void setTournamentCoverLink(String tournamentCoverLocation) {
        this.tournamentCoverLink = tournamentCoverLocation;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public int compareTo(Tournament other) {
        // Compare based on the DateTime field
        return this.dateTime.compareTo(other.dateTime);
    }


    public ArrayList<String> getAttendingList() {
        return attendingList;
    }

    public void setAttendingList(ArrayList<String> attendingList) {
        this.attendingList = attendingList;
    }

    public boolean isLockedForEdit() {
        return lockedForEdit;
    }

    public void setLockedForEdit(boolean lockedForEdit) {
        this.lockedForEdit = lockedForEdit;
    }

    public DateTime getJoinDeadline() {
        return joinDeadline;
    }

    public void setJoinDeadline(DateTime joinDeadline) {
        this.joinDeadline = joinDeadline;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isResultsPosted() {
        return resultsPosted;
    }

    public void setResultsPosted(boolean resultsPosted) {
        this.resultsPosted = resultsPosted;
    }
}
