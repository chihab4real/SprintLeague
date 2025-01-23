package com.example.sprintleague.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.adapters.MyTournamentsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnGoingTournamentsFragment extends Fragment {


    private ListView ongoingListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.on_going_tournament_fragment, container, false);

        ongoingListView = view.findViewById(R.id.ongoing_list);




        MyTournamentsListAdapter adapter = new MyTournamentsListAdapter(requireContext(), TournamentsArchiveActivity.onGoing);
        ongoingListView.setAdapter(adapter);

        return view;
    }
}
