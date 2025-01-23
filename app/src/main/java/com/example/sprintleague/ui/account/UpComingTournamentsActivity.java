package com.example.sprintleague.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.TournamentActivity;
import com.example.sprintleague.adapters.MyTournamentsListAdapter;

import java.util.ArrayList;

public class UpComingTournamentsActivity extends AppCompatActivity {

    public static ArrayList<Tournament> upComingTournaments;

    private ImageView go_back;

    private ListView listView;

    private MyTournamentsListAdapter upComingTournamentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_coming_tournaments);


        listView = findViewById(R.id.upcoming_listview);
        go_back = findViewById(R.id.go_back);

        upComingTournamentsAdapter = new MyTournamentsListAdapter(this, upComingTournaments);

        listView.setAdapter(upComingTournamentsAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TournamentActivity.tournament = upComingTournaments.get(i);
                startActivity(new Intent(UpComingTournamentsActivity.this, TournamentActivity.class));
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}