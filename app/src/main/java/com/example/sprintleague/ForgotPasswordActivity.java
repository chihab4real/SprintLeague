package com.example.sprintleague;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class ForgotPasswordActivity extends AppCompatActivity implements PageNavigationCallback {


    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    public static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        /*EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        viewPager2 = findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(this);

        viewPager2.setAdapter(adapter);

        user = new User();

        viewPager2.setUserInputEnabled(false);
    }


    public void goToNextPage() {
        int currentPage = viewPager2.getCurrentItem();
        if (currentPage < adapter.getItemCount() - 1) {
            viewPager2.setCurrentItem(currentPage + 1);
        }
    }


    public void goToPreviousPage() {
        int currentPage = viewPager2.getCurrentItem();
        if (currentPage > 0) {
            viewPager2.setCurrentItem(currentPage - 1);
        }
    }

    @Override
    public void onValidationSuccess() {
        goToNextPage();
    }



}