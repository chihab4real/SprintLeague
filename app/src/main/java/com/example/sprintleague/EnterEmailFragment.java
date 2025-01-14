package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterEmailFragment extends Fragment {

    private PageNavigationCallback callback;

    private StringsValidationMethods validator;

    private EditText emailEditTetx;
    private TextView valid_email, exist_email;
    private RelativeLayout send_email;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PageNavigationCallback) {
            callback = (PageNavigationCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PageNavigationCallback");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.enter_email_fragment, container, false);
        validator = new StringsValidationMethods();

        emailEditTetx = view.findViewById(R.id.forgot_pass_edittext_email);

        valid_email = view.findViewById(R.id.forgot_pass_enter_valid_login);
        exist_email = view.findViewById(R.id.forgot_pass_no_email);

        send_email = view.findViewById(R.id.forgot_pass_click_send);


        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    getUserFromDataBase(emailEditTetx.getText().toString());
                }

            }
        });


        return view;
    }

    private boolean isInputValid(){
        String email = emailEditTetx.getText().toString();

        if(validator.isEmailValid(email)){

            valid_email.setVisibility(View.GONE);
            return true;
        }else{


            valid_email.setVisibility(View.VISIBLE);
            return false;
        }
    }







    private void getUserFromDataBase(String email) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userExists = false;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);

                    if (user != null && email.equals(user.getEmail())) {
                        userExists = true;
                        exist_email.setVisibility(View.INVISIBLE);
                        // User exists, you can break or take further action here
                        ForgotPasswordActivity.user = user;
                        callback.onValidationSuccess();
                        break;
                    }
                }

                if (!userExists) {
                    exist_email.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
