package com.example.sprintleague;

public class CustomeAddress {

    private String city;
    private String postalCode;
    private String street;
    private String country;



    public CustomeAddress() {
    }

    public CustomeAddress(String city, String postalCode, String street, String country) {
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.country = country;

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
