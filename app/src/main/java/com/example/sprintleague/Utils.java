package com.example.sprintleague;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Utils {

    public static ArrayList<Tournament> tournaments = new ArrayList<>();

    public static boolean is24HourFormat(Context context) {
        // Returns true if the device is set to 24-hour format, false otherwise
        return DateFormat.is24HourFormat(context);
    }

    public static String convert24HourTo12Hour(String time24) {
        try {
            // Define 24-hour and 12-hour time formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            // Parse the input time and convert it to 12-hour format
            return outputFormat.format(inputFormat.parse(time24));
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }


    public static String convert12HourTo24Hour(String time12) {
        try {
            // Define 12-hour and 24-hour time formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // Parse the input time and convert it to 24-hour format
            return outputFormat.format(inputFormat.parse(time12));
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }
}
