package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView hide_password_icon;
    private boolean isPasswordVisible = false;
    private RelativeLayout login_layout;
    private TextView sigup_TextView, login_failed_TextView, forgot_password;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private AccountManager accountManager;
    private StringsValidationMethods validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        validator = new StringsValidationMethods();
        accountManager = new AccountManager(this);

        emailEditText = findViewById(R.id.login_edittext_email_or_phone);
        passwordEditText = findViewById(R.id.login_edittext_password);
        login_layout = findViewById(R.id.login_click_login);
        hide_password_icon = findViewById(R.id.login_icon_hide_show_password);
        login_failed_TextView = findViewById(R.id.text_view_login_failed);
        sigup_TextView = findViewById(R.id.login_text_click_signup);
        forgot_password = findViewById(R.id.login_text_click_forgetpassword);

        accountManager = new AccountManager(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

        hide_password_icon.setOnClickListener(view -> {
            if (isPasswordVisible) {
                hide_password_icon.setImageResource(R.drawable.ic_visible);
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                hide_password_icon.setImageResource(R.drawable.ic_none_visible);
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.length());
            isPasswordVisible = !isPasswordVisible;
        });

        forgot_password.setOnClickListener(view -> {
          startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        sigup_TextView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        login_layout.setOnClickListener(view -> {
            if (isInputValid()) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Login(email, password);
            }
        });
    }

    private boolean isInputValid() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (validator.isEmailValid(email) && !password.isEmpty()) {
            login_failed_TextView.setVisibility(View.INVISIBLE);
            return true;
        } else {
            login_failed_TextView.setText(R.string.login_failed);
            login_failed_TextView.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private void Login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        if (user.isEmailVerified()) {
                            // Proceed with login
                            login_failed_TextView.setVisibility(View.INVISIBLE);
//                            Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_LONG).show();
                            updateUserStatusToActive(user.getUid(),email,password);


                        } else {
                            login_failed_TextView.setText(R.string.verify_account);
                            login_failed_TextView.setVisibility(View.VISIBLE);
                            mAuth.signOut();  // Log out the user if email is not verified
                        }
                    } else {
                        login_failed_TextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void updateUserStatusToActive(String userId,String email,String password) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Update the 'isActive' field to 'true' after email verification
        mDatabase.child("users").child(userId).child("active").setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        accountManager.saveSignUpDataToSharedPref(email,password,userId);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

//                        Toast.makeText(LoginActivity.this, "Account activated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(LoginActivity.this, "Failed to activate account. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
