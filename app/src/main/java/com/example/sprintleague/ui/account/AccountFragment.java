package com.example.sprintleague.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.CreateTournamentActivity;
import com.example.sprintleague.LoginActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.SignupActivity;
import com.example.sprintleague.User;
import com.example.sprintleague.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private TextView userFirstName, userLastName, userRank;
    private RelativeLayout logout_clik;
    private LinearLayout user_logged_in_layout, user_not_logged_in_layout;

    private LinearLayout editMyAccount, upcomingTour,archiveTour, createTour, myTour;


    private RelativeLayout login_click, signup_clik;

    private ImageView profile_pic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user_logged_in_layout = root.findViewById(R.id.account_layout_logged_in);
        user_not_logged_in_layout = root.findViewById(R.id.account_layout_not_logged_in);

        userFirstName = root.findViewById(R.id.account_user_first_name);
        userLastName = root.findViewById(R.id.account_user_last_name);

        logout_clik = root.findViewById(R.id.logout_click);


        login_click = root.findViewById(R.id.account_click_login);
        signup_clik = root.findViewById(R.id.account_click_signup);

        editMyAccount = root.findViewById(R.id.account_editaccount_layout);
        upcomingTour = root.findViewById(R.id.account_upcomin_tour_layout);
        archiveTour = root.findViewById(R.id.account_archive_tour_layout);
        createTour = root.findViewById(R.id.account_create_tour_layout);
        myTour = root.findViewById(R.id.account_my_tour_layout);

        userRank = root.findViewById(R.id.account_user_rank);

        profile_pic = root.findViewById(R.id.profile_pic);

        if(AccountManager.currentUser != null){
            user_logged_in_layout.setVisibility(View.VISIBLE);
            logout_clik.setVisibility(View.VISIBLE);
            user_not_logged_in_layout.setVisibility(View.GONE);

            userFirstName.setText(AccountManager.currentUser.getFirstName());
            userLastName.setText(AccountManager.currentUser.getLastName());

            userRank.setText(getResources().getString(R.string.rank)+": "+String.valueOf(AccountManager.currentUser.getRanking()));

            if(!AccountManager.currentUser.getProfilePic().isEmpty()){
                Picasso.get()
                        .load(AccountManager.currentUser.getProfilePic())
                        .into(profile_pic);
            }else{
                profile_pic.setImageResource(R.drawable.empty_profile_pic);
            }
        }else{

            user_logged_in_layout.setVisibility(View.GONE);
            logout_clik.setVisibility(View.GONE);
            user_not_logged_in_layout.setVisibility(View.VISIBLE);
        }


        logout_clik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });


        login_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        signup_clik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SignupActivity.class));
            }
        });

        editMyAccount.setOnClickListener(view -> {
            startActivity(new Intent(root.getContext(), EditMyAccountActivity.class));
        });

        upcomingTour.setOnClickListener(view -> {
            Toast.makeText(getContext(),R.string.upcoming_tournaments,Toast.LENGTH_SHORT).show();
        });


        archiveTour.setOnClickListener(view -> {
            Toast.makeText(getContext(),R.string.archive_tournaments,Toast.LENGTH_SHORT).show();
        });


        createTour.setOnClickListener(view -> {
//            Toast.makeText(getContext(),R.string.add_tournament,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), CreateTournamentActivity.class));
        });


        myTour.setOnClickListener(view -> {
            Toast.makeText(getContext(),R.string.my_tournament,Toast.LENGTH_SHORT).show();
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void Logout(){

        AccountManager accountManager = new AccountManager(getContext());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        accountManager.clearAll();  // Clear saved user details from SharedPreferences
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
        AccountManager.currentUser = null;

        user_logged_in_layout.setVisibility(View.GONE);
        logout_clik.setVisibility(View.GONE);
        user_not_logged_in_layout.setVisibility(View.VISIBLE);


    }


    @Override
    public void onStart() {
        super.onStart();

        if(AccountManager.currentUser != null){
            user_logged_in_layout.setVisibility(View.VISIBLE);
            logout_clik.setVisibility(View.VISIBLE);
            user_not_logged_in_layout.setVisibility(View.GONE);

            userFirstName.setText(AccountManager.currentUser.getFirstName());
            userLastName.setText(AccountManager.currentUser.getLastName());


            if(!AccountManager.currentUser.getProfilePic().isEmpty()){
                Picasso.get()
                        .load(AccountManager.currentUser.getProfilePic())
                        .into(profile_pic);
            }else{
                profile_pic.setImageResource(R.drawable.empty_profile_pic);
            }
        }else{
            user_logged_in_layout.setVisibility(View.GONE);
            logout_clik.setVisibility(View.GONE);
            user_not_logged_in_layout.setVisibility(View.VISIBLE);
        }

    }
}