package com.example.sprintleague.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprintleague.LadderActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.Result;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.TournamentActivity;
import com.example.sprintleague.adapters.MyTournamentsListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyTournamentsListActivity extends AppCompatActivity {

    private ListView listView;
    private MyTournamentsListAdapter myTournamentsListAdapter;
    public static ArrayList<Tournament> myTournamets;

    private ImageView go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tournaments_list);

        listView = findViewById(R.id.mytournaments_listview);
        go_back = findViewById(R.id.go_back);

        myTournamentsListAdapter = new MyTournamentsListAdapter(this, myTournamets);

        listView.setAdapter(myTournamentsListAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), myTournamets.get(i).getTitle(),Toast.LENGTH_SHORT).show();

                Tournament x = myTournamets.get(i);

                if(x.isResultsPosted()){
                    retrieveResultsFromDB(x);
                }else{
                    TournamentActivity.tournament = x;
                    startActivity(new Intent(MyTournamentsListActivity.this, TournamentActivity.class));
                }
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void retrieveResultsFromDB(Tournament tournament) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("TournamentsResults").child(tournament.getID());

        // Access results under TournamentsResults/tournamentID
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Result> resultsList = new ArrayList<>();

                // Iterate through all users' results
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Result result = userSnapshot.getValue(Result.class);
                    if (result != null) {



                        resultsList.add(result);
                    }

                    if(resultsList.size() == dataSnapshot.getChildrenCount()){
                        LadderActivity.results = resultsList;
                        LadderActivity.tournament = tournament;

                        startActivity(new Intent(MyTournamentsListActivity.this, LadderActivity.class));
//                        Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
                    }


                }

                // Do something with the results, e.g., display them in a RecyclerView

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyTournamentsListActivity.this, "Failed to retrieve results", Toast.LENGTH_SHORT).show();
            }
        });
    }
}