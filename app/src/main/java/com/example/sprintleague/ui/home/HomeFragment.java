package com.example.sprintleague.ui.home;

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

import com.example.sprintleague.DateTime;
import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.Utils;
import com.example.sprintleague.adapters.TournamentAdapter;
import com.example.sprintleague.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private RecyclerView recyclerview;

    private TournamentAdapter adapter;

    private TextView totalTournaments;
    private Spinner sortingSpinner;

    private int sortByPosition = 0;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerview = root.findViewById(R.id.recyclerview);
        totalTournaments = root.findViewById(R.id.home_total_tournament);
        sortingSpinner = root.findViewById(R.id.sorting_spinner);

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

        adapter = new TournamentAdapter(getContext(), sorted);


        recyclerview.setAdapter(adapter);


    }




}