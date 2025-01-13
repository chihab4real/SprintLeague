package com.example.sprintleague;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EnterEmailFragment();
            case 1:
                return new SecurityQuestionFragment();
            case 2:
                return new SetNewPasswordFragment();
            default:
                return new EnterEmailFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of pages
    }
}
