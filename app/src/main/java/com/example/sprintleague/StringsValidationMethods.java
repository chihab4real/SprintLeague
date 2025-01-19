package com.example.sprintleague;

import java.util.regex.Pattern;

public class StringsValidationMethods {

    public StringsValidationMethods() {
    }

    public boolean isNameValid(String name){

        return Pattern.matches("^[A-Za-z]+( [A-Za-z]+)*$", name);


    }

    public boolean isEmailValid(String email){


        return Pattern.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)", email);
    }


    public boolean isPasswordValid(String password){

        //return Pattern.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", password);
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,30}$", password);
    }

    public boolean isValidTournamentTitle(String title){
        return Pattern.matches("^[a-zA-Z0-9 ]+$", title);
    }

    public boolean isValidStreetAddress(String address){
        return Pattern.matches("^[\\p{L}\\p{N} ,.\\-]+$", address);
    }

    public boolean isValidPostalCode(String postalcode){
        return Pattern.matches("^\\d{2}-\\d{3}$", postalcode);
    }

    public boolean isValidCityName(String cityName){
        return Pattern.matches("^[\\p{L} ,.\\'-]+$", cityName);
    }
}
