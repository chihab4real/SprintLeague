package com.example.sprintleague.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.CustomeAddress;
import com.example.sprintleague.DateTime;
import com.example.sprintleague.R;
import com.example.sprintleague.Result;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.TournamentActivity;
import com.example.sprintleague.Utils;
import com.example.sprintleague.adapters.SubmitResultsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FillResultsActivity extends AppCompatActivity {

    private TextView tournamentTitle, tournamentDistance, tournamentLevel, tournamentDateTime, tournamentPlaces;
    private ImageView tournamentCover;

    private ListView listView;

    private LinearLayout clearAll, save;

    public static Tournament tournament;

    private SubmitResultsAdapter submitResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_results);

        initViews();

        submitResultsAdapter = new SubmitResultsAdapter(this, tournament.getAttendingList(),tournament.getID());

        listView.setAdapter(submitResultsAdapter);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResultsAdapter = new SubmitResultsAdapter(FillResultsActivity.this, tournament.getAttendingList(),tournament.getID());
                listView.setAdapter(submitResultsAdapter);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                makeTheTournamentResultsPosted();
                sendResultToDB();
            }
        });



    }

    private void findView(){
        tournamentTitle = findViewById(R.id.fill_results_title);
        tournamentDistance= findViewById(R.id.fill_results_title_distance);
        tournamentLevel= findViewById(R.id.fill_results_level);
        tournamentDateTime= findViewById(R.id.fill_results_date_time);
        tournamentPlaces= findViewById(R.id.fill_results_places);

        tournamentCover= findViewById(R.id.fill_results_cover);

        listView= findViewById(R.id.fill_results_listview);

        clearAll= findViewById(R.id.results_clear_all);
        save= findViewById(R.id.results_save);
    }

    private void initViews(){
        findView();

        tournamentTitle.setText(tournament.getTitle());
        tournamentDistance.setText(tournament.getDistance()+" Km");

        switch (tournament.getLevel()){
            case "1":
                tournamentLevel.setText(getString(R.string.beginner));
                tournamentLevel.setTextColor(getColor(R.color.color_1));
                break;
            case "2":
                tournamentLevel.setText(getString(R.string.intermediate));
                tournamentLevel.setTextColor(getColor(R.color.color_2));
                break;

            case "3":
                tournamentLevel.setText(getString(R.string.advanced));
                tournamentLevel.setTextColor(getColor(R.color.color_3));
                break;
        }




        String date = tournament.getDateTime().getDay() + "/" +
                tournament.getDateTime().getMonth() + "/" +
                tournament.getDateTime().getYear();
        String time = tournament.getDateTime().getHour() + ":" +
                tournament.getDateTime().getMinute() + " " +
                tournament.getDateTime().getAm_pm();


        String dateDeadline = tournament.getJoinDeadline().getDay() + "/" +
                tournament.getJoinDeadline().getMonth() + "/" +
                tournament.getJoinDeadline().getYear();
        String timeDeadline = tournament.getJoinDeadline().getHour() + ":" +
                tournament.getJoinDeadline().getMinute() + " " +
                tournament.getJoinDeadline().getAm_pm();


        if(Utils.is24HourFormat(FillResultsActivity.this)){
            time = Utils.convert12HourTo24Hour(time);

        }

        tournamentDateTime.setText(String.format("%s, %s", date, time));

        String joined = "";
        if(tournament.getAttendingList() != null && !tournament.getAttendingList().isEmpty()){
            joined = tournament.getAttendingList().size()+"";
        }else{
            joined = "0";
        }


        String max = "";
        if(tournament.getMaxParticipants() == -1){
            max = getString(R.string.unlimted_places);

        }else{
            max = tournament.getMaxParticipants()+"";
        }

        tournamentPlaces.setText(joined+"/"+max+" "+getString(R.string.joined));

        if(!tournament.getTournamentCoverLink().isEmpty()){
            Picasso.get().load(tournament.getTournamentCoverLink()).into(tournamentCover);
        }else{
            tournamentCover.setImageResource(R.drawable.tournament_sample);
        }

    }


    private void sendResultToDB() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("TournamentsResults");
        List<Result> results = submitResultsAdapter.getResults(); // Assuming this method returns a List<Result>

        for (Result result : results) {
            String tournamentID = result.getTournamentID();
            String userID = result.getUserID();

            // Save result under TournamentsResults/tournamentID/userID
            databaseRef.child(tournamentID).child(userID).setValue(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Results added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to add results", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Optionally, finish after all results are saved
        finish();
    }

    private void makeTheTournamentResultsPosted(){
        DatabaseReference resultsPostedRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournament.getID());

        resultsPostedRef.child("resultsPosted").setValue(true);


    }




}