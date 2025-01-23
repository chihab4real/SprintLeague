package com.example.sprintleague.ui.account;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.adapters.FinishedPagesAdapter;

import java.util.ArrayList;

public class TournamentsArchiveActivity extends AppCompatActivity {

    private LinearLayout inProgressTab, resultsTab;
    private TextView inProgressLine, resultsLine;
    private ViewPager2 viewPager;

    public static ArrayList<Tournament> onGoing, finishedWithResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournaments_archive);

        // Initialize views
        findViews();


        // Set up ViewPager2 adapter
        FinishedPagesAdapter adapter = new FinishedPagesAdapter(this);
        viewPager.setAdapter(adapter);

        // Tab click listeners
        inProgressTab.setOnClickListener(v -> selectTab(0));
        resultsTab.setOnClickListener(v -> selectTab(1));

        // ViewPager2 page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectTab(position);
            }
        });

        // Initialize with the first tab selected
        selectTab(0);

        // Back button listener
        findViewById(R.id.go_back).setOnClickListener(v -> onBackPressed());
    }

    private void findViews(){
        inProgressTab = findViewById(R.id.inprogress_layout_click);
        resultsTab = findViewById(R.id.results_layout_click);
        inProgressLine = findViewById(R.id.inprogress_layout_line);
        resultsLine = findViewById(R.id.resultslayout_line);
        viewPager = findViewById(R.id.viewPager);
    }

    private void selectTab(int position) {
        // Update tab colors
        if (position == 0) {
            inProgressLine.setBackgroundColor(getColor(R.color.solmon)); // Highlight "In Progress"
            resultsLine.setBackgroundColor(Color.TRANSPARENT);          // Remove highlight from "Results"
        } else {
            inProgressLine.setBackgroundColor(Color.TRANSPARENT);       // Remove highlight from "In Progress"
            resultsLine.setBackgroundColor(getColor(R.color.solmon));  // Highlight "Results"
        }

        // Set the ViewPager2 current item
        if (viewPager.getCurrentItem() != position) {
            viewPager.setCurrentItem(position);
        }
    }
}