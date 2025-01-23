package com.example.sprintleague.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sprintleague.ui.account.FinshedWithResultsTournamentsFragment;
import com.example.sprintleague.ui.account.OnGoingTournamentsFragment;

import java.util.List;

public class FinishedPagesAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public FinishedPagesAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        // Initialize fragments for the two pages
        fragments = List.of(
                new OnGoingTournamentsFragment(),
                new FinshedWithResultsTournamentsFragment()
        );
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
