package com.example.sprintleague.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.LoginActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.SignupActivity;
import com.example.sprintleague.User;
import com.example.sprintleague.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.A;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private TextView userFirstName;
    private RelativeLayout logout_clik;
    private LinearLayout user_logged_in_layout, user_not_logged_in_layout;


    private RelativeLayout login_click, signup_clik;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user_logged_in_layout = root.findViewById(R.id.account_layout_logged_in);
        user_not_logged_in_layout = root.findViewById(R.id.account_layout_not_logged_in);

        userFirstName = root.findViewById(R.id.account_user_first_name);
        logout_clik = root.findViewById(R.id.logout_click);


        login_click = root.findViewById(R.id.account_click_login);
        signup_clik = root.findViewById(R.id.account_click_signup);

        if(AccountManager.currentUser != null){
            user_logged_in_layout.setVisibility(View.VISIBLE);
            user_not_logged_in_layout.setVisibility(View.GONE);

            userFirstName.setText(AccountManager.currentUser.getFirstName());
        }else{
            user_logged_in_layout.setVisibility(View.GONE);
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
        user_not_logged_in_layout.setVisibility(View.VISIBLE);


    }


    @Override
    public void onStart() {
        super.onStart();

        if(AccountManager.currentUser != null){
            user_logged_in_layout.setVisibility(View.VISIBLE);
            user_not_logged_in_layout.setVisibility(View.GONE);

            userFirstName.setText(AccountManager.currentUser.getFirstName());
        }else{
            user_logged_in_layout.setVisibility(View.GONE);
            user_not_logged_in_layout.setVisibility(View.VISIBLE);
        }

    }
}