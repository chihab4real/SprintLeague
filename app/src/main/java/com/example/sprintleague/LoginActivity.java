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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        /*EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        validator = new StringsValidationMethods();

        emailEditText = findViewById(R.id.login_edittext_email_or_phone);
        passwordEditText = findViewById(R.id.login_edittext_password);
        login_layout = findViewById(R.id.login_click_login);

        hide_password_icon = findViewById(R.id.login_icon_hide_show_password);

        login_failed_TextView = findViewById(R.id.text_view_login_failed);
        sigup_TextView = findViewById(R.id.login_text_click_signup);

        forgot_password = findViewById(R.id.login_text_click_forgetpassword);


        accountManager = new AccountManager(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        hide_password_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordVisible){
                    hide_password_icon.setImageResource(R.drawable.ic_visible);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    hide_password_icon.setImageResource(R.drawable.ic_none_visible);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                }

                passwordEditText.setSelection(passwordEditText.length());
                isPasswordVisible = !isPasswordVisible;

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        sigup_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isInputValid()){
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    Login(email,password);

                }
            }
        });



    }


    private boolean isInputValid(){
        String email, password;

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if(validator.isEmailValid(email) && !password.isEmpty()){
            login_failed_TextView.setVisibility(View.INVISIBLE);
            return true;
        }else{

            login_failed_TextView.setVisibility(View.VISIBLE);
            return false;
        }


    }

    private void Login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            login_failed_TextView.setVisibility(View.INVISIBLE);
                            getUserFromDataBase(user.getUid());

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            login_failed_TextView.setVisibility(View.VISIBLE);
                            //
                            //updateUI(null);
                        }
                    }
                });

    }




    private void getUserFromDataBase(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);



        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

//                Toast.makeText(getApplicationContext(), user.getEmail(),Toast.LENGTH_LONG).show();




                accountManager.saveSignUpDataToSharedPref(user.getEmail(), user.getPassword(), user.getId());

                AccountManager.currentUser = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}