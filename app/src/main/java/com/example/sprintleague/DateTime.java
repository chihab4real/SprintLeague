package com.example.sprintleague;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTime implements Comparable<DateTime>{
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String am_pm;

    public DateTime() {
    }

    public DateTime(String year, String month, String day, String hour, String minute, String am_pm) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.am_pm = am_pm;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getAm_pm() {
        return am_pm;
    }

    public void setAm_pm(String am_pm) {
        this.am_pm = am_pm;
    }

    @Override
    public int compareTo(DateTime other) {
        long thisMillis = convertToMilliseconds();
        long otherMillis = other.convertToMilliseconds();
        return Long.compare(thisMillis, otherMillis);
    }

    // Converts the DateTime to milliseconds
    public long convertToMilliseconds() {
        // Use 24-hour format for easier conversion
        int hour24 = Integer.parseInt(this.hour);
        if (this.am_pm.equals("PM") && hour24 < 12) {
            hour24 += 12;  // Convert PM to 24-hour format
        } else if (this.am_pm.equals("AM") && hour24 == 12) {
            hour24 = 0;  // Convert 12 AM to 0 hours
        }

        String dateString = this.year + "-" + this.month + "-" + this.day + " " + hour24 + ":" + this.minute;

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



}
