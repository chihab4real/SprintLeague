package com.example.sprintleague.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprintleague.FirstActivity;
import com.example.sprintleague.LadderActivity;
import com.example.sprintleague.MainMenuActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.Result;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.Utils;
import com.example.sprintleague.adapters.LadderAdapter;
import com.example.sprintleague.adapters.MyTournamentsListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class FinshedWithResultsTournamentsFragment extends Fragment {

    private ListView finishedListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finished_with_results_fragment, container, false);

        finishedListView = view.findViewById(R.id.finished_list);


        MyTournamentsListAdapter adapter = new MyTournamentsListAdapter(requireContext(), TournamentsArchiveActivity.finishedWithResults);
        finishedListView.setAdapter(adapter);


        finishedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                retrieveResultsFromDB(TournamentsArchiveActivity.finishedWithResults.get(i));

            }
        });

        return view;
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

                        startActivity(new Intent(getContext(), LadderActivity.class));
//                        Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
                    }


                }

                // Do something with the results, e.g., display them in a RecyclerView

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve results", Toast.LENGTH_SHORT).show();
            }
        });
    }




}

