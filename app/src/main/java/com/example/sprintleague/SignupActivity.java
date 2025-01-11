package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {


    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText;
    private RelativeLayout click_signup;
    private TextView login_text_view, valid_name, valid_email, valid_password, valid_password_req;
    private ImageView hide_password_icon;
    private boolean isPasswordVisible = false;

    private StringsValidationMethods validator;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
/*        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        validator = new StringsValidationMethods();

        firstNameEditText = findViewById(R.id.signup_edittext_firstname);
        lastNameEditText = findViewById(R.id.signup_edittext_lastname);
        emailEditText = findViewById(R.id.signup_edittext_email_or_phone);
        passwordEditText = findViewById(R.id.signup_edittext_password);


        click_signup = findViewById(R.id.signup_click_signup);
        login_text_view = findViewById(R.id.signup_text_click_login);
        valid_name = findViewById(R.id.text_view_enter_valid_name);
        valid_email = findViewById(R.id.text_view_enter_valid_login);
        valid_password = findViewById(R.id.text_view_enter_valid_password);
        valid_password_req = findViewById(R.id.text_view_enter_password_req);


        hide_password_icon = findViewById(R.id.signup_icon_hide_show_password);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        click_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isInputsValid()){

                    mAuth = FirebaseAuth.getInstance();
                    emailSignUp(firstNameEditText.getText().toString(),lastNameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString());
                }

            }
        });

        login_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

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


    }

    private boolean isInputsValid(){

        String firstName, lastName, email, password;

        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        /*if(firstName.isEmpty()){

            valid_name.setVisibility(View.VISIBLE);
            return false;
        }else{
            valid_name.setVisibility(View.GONE);
        }

        if(lastName.isEmpty()){
            valid_name.setVisibility(View.VISIBLE);
            return false;
        }else{
            valid_name.setVisibility(View.GONE);
        }



        if(email.isEmpty()){

            valid_email.setVisibility(View.VISIBLE);
            return false;
        }else{
            valid_email.setVisibility(View.GONE);
        }

        if(password.isEmpty()){

            valid_password.setVisibility(View.VISIBLE);
            return false;
        }else{
            valid_password.setVisibility(View.GONE);
        }*/

        if(validator.isNameValid(firstName)){

            valid_name.setVisibility(View.GONE);

            if(validator.isNameValid(lastName)){

                valid_name.setVisibility(View.GONE);

                if(validator.isEmailValid(email)){

                    valid_email.setVisibility(View.GONE);
                    if(validator.isPasswordValid(password)){

                        valid_password.setVisibility(View.GONE);
                        valid_password_req.setVisibility(View.GONE);
                        return true;
                    }else{

                        valid_password.setVisibility(View.VISIBLE);
                        valid_password_req.setVisibility(View.VISIBLE);
                        return false;
                    }
                }else{

                    valid_email.setVisibility(View.VISIBLE);
                    return false;
                }
            }else{
                valid_name.setVisibility(View.VISIBLE);
                return false;
            }
        }else{

            valid_name.setVisibility(View.VISIBLE);
            return false;
        }


    }

    private void emailSignUp(String firstName,String lastName,String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Create user to database
                            createUserDataBase(user.getUid(), firstName, lastName, email, password);
                            Toast.makeText(getApplicationContext(),"signed up", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            Toast.makeText(getApplicationContext(),"not signed up", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void createUserDataBase(String id, String firstName, String lastName,String email, String password){
        User user = new User(id,email, firstName, lastName, password, "", false);


        AccountManager accountManager = new AccountManager(SignupActivity.this);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("users").child(id).setValue(user);

        AccountManager.currentUser = user;

        accountManager.saveSignUpDataToSharedPref(email, password, id);
    }
}