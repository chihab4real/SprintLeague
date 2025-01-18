package com.example.sprintleague;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Tournament {

    private String ID;
    private String title;
    private String organizerID;
    private Float distance;
    private DateTime dateTime;
    private int maxParticipants;

    private CustomeAddress address;

    private ArrayList<Sponsor> sponsors;

    private String tournamentCoverLink;


    public Tournament() {
    }

    public Tournament(String id, String title, String organizerID, Float distance, DateTime dateTime, int maxParticipants, CustomeAddress address, ArrayList<Sponsor> sponsors, String coverlink) {
        this.ID = id;
        this.title = title;
        this.organizerID = organizerID;
        this.distance = distance;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.address = address;
        this.sponsors = sponsors;
        this.tournamentCoverLink = coverlink;
    }

    public Tournament(String ID, String title, String organizerID, Float distance, DateTime dateTime, int maxParticipants, CustomeAddress address) {
        this.ID = ID;
        this.title = title;
        this.organizerID = organizerID;
        this.distance = distance;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.address = address;
    }

    public Tournament(String ID, String title, String organizerID, Float distance, DateTime dateTime, int maxParticipants, CustomeAddress address, ArrayList<Sponsor> sponsors) {
        this.ID = ID;
        this.title = title;
        this.organizerID = organizerID;
        this.distance = distance;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.address = address;
        this.sponsors = sponsors;
    }

    public Tournament(String ID, String title, String organizerID, Float distance, DateTime dateTime, int maxParticipants, CustomeAddress address, String tournamentCoverLink) {
        this.ID = ID;
        this.title = title;
        this.organizerID = organizerID;
        this.distance = distance;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.address = address;
        this.tournamentCoverLink = tournamentCoverLink;
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


}
