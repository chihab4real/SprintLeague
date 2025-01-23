package com.example.sprintleague.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.Utils;
import com.example.sprintleague.adapters.SearchAdapter;
import com.example.sprintleague.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private EditText searchEdit;
    private ListView listView;
    private SearchAdapter searchAdapter;
    private ArrayList<Tournament> searchArrayList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI elements
        searchEdit = root.findViewById(R.id.search_edit);
        listView = root.findViewById(R.id.search_listview);

        // Initialize SearchAdapter and set it to ListView
        searchAdapter = new SearchAdapter(requireContext(), searchArrayList);
        listView.setAdapter(searchAdapter);

        // Set TextWatcher for search input
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Optional: Handle ListView item click events
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tournament selectedTournament = searchArrayList.get(position);
                // Handle item click (e.g., open tournament details)
                openTournamentDetails(selectedTournament);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void doSearch(String input) {
        searchArrayList.clear(); // Clear the existing search results
        if (!input.isEmpty()) {
            for (Tournament t : Utils.tournaments) {
                if (!t.isResultsPosted() && Utils.isDateBiggerThanToday(t.getDateTime())) {
                    if (t.getTitle().toLowerCase().contains(input.toLowerCase()) ||
                            String.valueOf(t.getDistance()).contains(input)) {
                        searchArrayList.add(t);
                    }
                }
            }
        }
        // Notify adapter of data change
        searchAdapter.notifyDataSetChanged();
    }

    private void openTournamentDetails(Tournament tournament) {
        // Navigate to a details screen or perform desired action
        // Example:
        // Intent intent = new Intent(requireContext(), TournamentDetailsActivity.class);
        // intent.putExtra("tournament", tournament);
        // startActivity(intent);
    }
}
