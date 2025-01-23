package com.example.sprintleague;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintleague.R;

import com.example.sprintleague.Result;
import com.example.sprintleague.adapters.LadderAdapter;
import com.example.sprintleague.ui.account.TournamentsArchiveActivity;


import java.util.ArrayList;
import java.util.List;

public class LadderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLadder;
    private LadderAdapter ladderAdapter;

    private ProgressBar progressBar;
    private TextView progress_bar_text, avg_speed,avg_duration;


    public static ArrayList<Result> results;
    public static Tournament tournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder);

        // Initialize RecyclerView
        recyclerViewLadder = findViewById(R.id.recyclerViewLadder);
        recyclerViewLadder.setLayoutManager(new LinearLayoutManager(this));

        // Create and populate list of user ranks

        // Add more users as needed

        // Set the adapter
        ladderAdapter = new LadderAdapter(results);
        recyclerViewLadder.setAdapter(ladderAdapter);

        progressBar = findViewById(R.id.progress1);
        progress_bar_text = findViewById(R.id.progress1_text);

        avg_duration = findViewById(R.id.avg_duration);
        avg_speed = findViewById(R.id.avg_speed);

        progressBar.setProgress((int)getPercentage());
        progress_bar_text.setText((int)getPercentage()+"%");


        int averageSeconds = calculateAverageTime(results);


        String averageTime = convertSecondsToTime(averageSeconds);

        avg_duration.setText(averageTime);

        // Example: Calculate speed (assuming distance is known, e.g., 50 kilometers)
        double distance = tournament.getDistance(); // Example distance in kilometers
        int totalSeconds = 0;
        for (Result r : results) {
            totalSeconds += convertTimeToSeconds(r.getTiming());
        }

        double speed = calculateSpeed(averageTime, distance);
      avg_speed.setText(String.format("%.2f", speed)+ " Km/h");




    }

    private float getPercentage(){

        return (float) results.size()/(float) tournament.getAttendingList().size()*100;
    }



    private  int calculateAverageTime(ArrayList<Result> results) {
        int totalSeconds = 0;

        for (Result r : results) {
            totalSeconds += convertTimeToSeconds(r.getTiming());
        }

        return totalSeconds / results.size();
    }

    private  int convertTimeToSeconds(String timeEntry) {
        String[] timeParts = timeEntry.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        return (hours * 3600) + (minutes * 60) + seconds;
    }

    private   String convertSecondsToTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    public static double calculateSpeed(String timeString, double distanceInKm) {
        // Split the time string into hours, minutes, and seconds
        String[] timeParts = timeString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        // Convert the time to total hours
        double totalHours = hours + (minutes / 60.0) + (seconds / 3600.0);

        // Calculate the speed in km/h
        return distanceInKm / totalHours;
    }


}
