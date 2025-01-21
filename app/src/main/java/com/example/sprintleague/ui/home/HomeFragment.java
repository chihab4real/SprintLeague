package com.example.sprintleague.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sprintleague.DateTime;
import com.example.sprintleague.FirstActivity;
import com.example.sprintleague.MainMenuActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.TournamentActivity;
import com.example.sprintleague.Utils;
import com.example.sprintleague.adapters.TournamentAdapter;
import com.example.sprintleague.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView recyclerview;

    private TournamentAdapter tournamentAdapter;

    private TextView totalTournaments;
    private Spinner sortingSpinner;

    private int sortByPosition = 0;

    private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerview = root.findViewById(R.id.recyclerview);
        totalTournaments = root.findViewById(R.id.home_total_tournament);
        sortingSpinner = root.findViewById(R.id.sorting_spinner);

        swipeRefreshLayout = root.findViewById(R.id.swipe_layout);



        // Set the layout manager (LinearLayoutManager for a vertical list)
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


        totalTournaments.setText(Utils.tournaments.size()+" "+getResources().getString(R.string.tournamets));

//        adapter = new TournamentAdapter(getContext(), Utils.tournaments);


//        recyclerview.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sorting,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortingSpinner.setAdapter(adapter);

        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//             Toast.makeText(getContext(),String.valueOf(i),Toast.LENGTH_SHORT).show();

                sortByPosition = i;
                sortTournaments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databaseReference = FirebaseDatabase.getInstance().getReference("Tournaments");
                ArrayList<Tournament> tournaments = new ArrayList<>();

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tournaments.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Tournament tournament = dataSnapshot.getValue(Tournament.class);
                            tournaments.add(tournament);

                            if (tournaments.size() == snapshot.getChildrenCount()) {
                                Utils.tournaments = tournaments;

                                swipeRefreshLayout.setRefreshing(false);

                                if (databaseReference != null && valueEventListener != null) {
                                    totalTournaments.setText(Utils.tournaments.size()+" "+getResources().getString(R.string.tournamets));
                                    databaseReference.removeEventListener(valueEventListener);

                                    sortTournaments();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error here if needed
                    }
                };

                databaseReference.addValueEventListener(valueEventListener);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
            );
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void sortTournaments(){

        ArrayList<Tournament> sorted = new ArrayList<>(Utils.tournaments);

        switch (sortByPosition){
            case 0:
                // DEFAULT
                break;
            case 1:
                //DISTANCE ASC
                sorted.sort(Comparator.comparing(Tournament::getDistance));
                break;
            case 2:
                //DISTANCE DESC
                sorted.sort((t1,t2) -> t2.getDistance().compareTo(t1.getDistance()));
                break;
            case 3:
                //DATE OLD 1ST
                Collections.sort(sorted);

                break;
            case 4:
                //DATE NEW 1ST
                Collections.sort(sorted);
                Collections.reverse(sorted);


                break;
        }

        tournamentAdapter = new TournamentAdapter(getContext(), sorted, tournament -> {


            TournamentActivity.tournament = tournament;
            startActivity(new Intent(getContext(),TournamentActivity.class));
        });


//        recyclerview.setNestedScrollingEnabled(false);
//        recyclerview.setItemViewCacheSize(20);
//        recyclerview.setDrawingCacheEnabled(true);
//        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        recyclerview.setAdapter(tournamentAdapter);


    }









}