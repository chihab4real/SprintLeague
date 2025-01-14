package com.example.sprintleague;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountManager {

    public static User currentUser = null ;
    private static final String PREF_NAME = "MyPrefs";
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private Context context;



    public AccountManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void saveString(String key, String value){
        editor.putString(key,value);
        editor.apply();
    }

    public String loadString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void clearAll() {
        editor.clear();
        editor.apply();
    }

    public void saveSignUpDataToSharedPref(String email, String password, String uid){
        editor.putString("userEmail", email);
        editor.putString("userPassword", password);
        editor.putString("userUid", uid);
        editor.apply();
    }






}
