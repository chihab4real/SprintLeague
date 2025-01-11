package com.example.sprintleague;

import java.util.regex.Pattern;

public class StringsValidationMethods {

    public StringsValidationMethods() {
    }

    protected boolean isNameValid(String name){

        return Pattern.matches("^[A-Za-z]+( [A-Za-z]+)*$", name);


    }

    protected boolean isEmailValid(String email){


        return Pattern.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)", email);
    }


    protected boolean isPasswordValid(String password){

        //return Pattern.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", password);
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{6,30}$", password);
    }
}
