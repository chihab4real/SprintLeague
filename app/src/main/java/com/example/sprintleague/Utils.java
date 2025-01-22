package com.example.sprintleague;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    // Converts the DateTime to milliseconds
    public static long convertToMilliseconds(DateTime dateTime) {
        // Use 24-hour format for easier conversion
        int hour24 = Integer.parseInt(dateTime.getHour());
        if (dateTime.getAm_pm().equals("PM") && hour24 < 12) {
            hour24 += 12;  // Convert PM to 24-hour format
        } else if (dateTime.getAm_pm().equals("AM") && hour24 == 12) {
            hour24 = 0;  // Convert 12 AM to 0 hours
        }

        String dateString = dateTime.getYear() + "-" + dateTime.getMonth() + "-" + dateTime.getDay() + " " + hour24 + ":" + dateTime.getMinute();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  // Ensure we handle time zones correctly

        try {
            Date date = sdf.parse(dateString);
            return date.getTime();  // Returns time in milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isDateBiggerThanToday(DateTime dateTime1){
       return convertToMilliseconds(dateTime1) > System.currentTimeMillis();
    }
}
