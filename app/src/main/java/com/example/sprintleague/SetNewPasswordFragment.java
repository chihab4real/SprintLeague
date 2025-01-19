package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;


public class SetNewPasswordFragment extends Fragment{


    private EditText new_password_EditText;
    private RelativeLayout change;
    private ImageView hide_password_icon;
    private TextView valid_password, valid_password_req;
    private boolean isPasswordVisible = false;

    private StringsValidationMethods validator;

    private PageNavigationCallback callback;

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

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

        View view = inflater.inflate(R.layout.set_new_password_fragment, container, false);


        validator = new StringsValidationMethods();



        new_password_EditText = view.findViewById(R.id.enter_new_pass_edittext_password);

        change = view.findViewById(R.id.enter_new_pass_click_change);

        hide_password_icon = view.findViewById(R.id.enter_new_pass_icon_hide_show_password);

        valid_password = view.findViewById(R.id.text_view_enter_valid_password);

        valid_password_req = view.findViewById(R.id.text_view_enter_valid_password_req);


        hide_password_icon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                hide_password_icon.setImageResource(R.drawable.ic_visible);
                new_password_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                hide_password_icon.setImageResource(R.drawable.ic_none_visible);
                new_password_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            new_password_EditText.setSelection(new_password_EditText.length());
            isPasswordVisible = !isPasswordVisible;
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInput()){


                    String oldPasword = ForgotPasswordActivity.user.getPassword();
                    changePassword(ForgotPasswordActivity.user.getEmail(), oldPasword, new_password_EditText.getText().toString());
                }
            }
        });




        return view;
    }


    private boolean isValidInput(){
        String password = new_password_EditText.getText().toString();

        if(validator.isPasswordValid(password)){

            valid_password.setVisibility(View.GONE);
            valid_password_req.setVisibility(View.GONE);
            return true;
        }else{
            valid_password.setVisibility(View.VISIBLE);
            valid_password_req.setVisibility(View.VISIBLE);
            return false;
        }
    }


//    private void changePassword(String userId, String newPassword) {
//        // Step 1: Update password in Firebase Authentication
//        updateAuthPassword(newPassword, task -> {
//            if (task.isSuccessful()) {
//                Log.d("AuthUpdate", "Password updated in Firebase Auth.");
//
//                // Step 2: Update password in Realtime Database
//                updateDatabasePassword(userId, newPassword);
//
//                Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_LONG).show();
//
//                if (callback != null) {
//                    ((ForgotPasswordActivity) getActivity()).finish(); // Finish the activity
//                }
//
//
//            } else {
//                Log.e("AuthError", "Failed to update password in Firebase Auth: " + task.getException().getMessage());
//            }
//        });
//    }
//
//
//    private void updateAuthPassword(String newPassword, OnCompleteListener<Void> onCompleteListener) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//
//        if (user != null) {
//            user.updatePassword(newPassword)
//                    .addOnCompleteListener(onCompleteListener);
//        } else {
//            Log.e("AuthError", "No authenticated user found.");
//        }
//    }
//
//    private void updateDatabasePassword(String userId, String newPassword) {
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
//
//        mDatabase.child("password").setValue(newPassword)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("DatabaseUpdate", "Password updated successfully in database.");
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("DatabaseError", "Error updating password: " + e.getMessage());
//                });
//    }


    private void changePasswordinDB(String id, String newpassword){
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(id).child("password").setValue(newpassword);


        currentUser.updatePassword(newpassword);

        mAuth.signOut();

        currentUser = null;

        if(AccountManager.currentUser != null){
            AccountManager.currentUser.setPassword(newpassword);
            AccountManager accountManager = new AccountManager(getContext());

            accountManager.saveString("userPassword", newpassword);
        }

        Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();

        if (callback != null) {
            ((ForgotPasswordActivity) getActivity()).finish(); // Finish the activity
        }




    }


    private void changePassword(String email, String password, String newpassword) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        currentUser = mAuth.getCurrentUser();

                        if (currentUser != null) {
                            // Now proceed with password change
                            changePasswordinDB(ForgotPasswordActivity.user.getId(), newpassword);
                        } else {
                            Log.e("AuthError", "Failed to get current user after login.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthError", "Login failed: " + e.getMessage());
                    Toast.makeText(getContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }







}
