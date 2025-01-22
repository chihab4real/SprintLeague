package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity  {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, secuirtyAnswer;
    private RelativeLayout click_signup;
    private TextView login_text_view, valid_name, valid_email, valid_password, valid_password_req, valid_answer;
    private ImageView hide_password_icon;
    private boolean isPasswordVisible = false;

    private Spinner secuirtyQuestions;

    private StringsValidationMethods validator;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private String currentQuestion;
    private int currentQuestionID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        valid_answer = findViewById(R.id.text_view_enter_valid_answer);


        hide_password_icon = findViewById(R.id.signup_icon_hide_show_password);

        secuirtyAnswer = findViewById(R.id.secuirty_answer);
        secuirtyQuestions = findViewById(R.id.secuirty_questions);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        click_signup.setOnClickListener(view -> {
            if (isInputsValid()) {
                mAuth = FirebaseAuth.getInstance();
                emailSignUp(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(), currentQuestion,secuirtyAnswer.getText().toString());
            }
        });

        login_text_view.setOnClickListener(view -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

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



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_question,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        secuirtyQuestions.setAdapter(adapter);

        secuirtyQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentQuestionID = i;
                currentQuestion = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentQuestionID = 0;
                currentQuestion = adapterView.getItemAtPosition(0).toString();
            }
        });
    }

    private boolean isInputsValid() {

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String answer = secuirtyAnswer.getText().toString();




        if (validator.isNameValid(firstName) && validator.isNameValid(lastName)){
            valid_name.setVisibility(View.GONE);


                if(validator.isEmailValid(email)){
                    valid_email.setVisibility(View.GONE);

                    if(validator.isPasswordValid(password)){
                        valid_password.setVisibility(View.GONE);
//                        valid_password_req.setVisibility(View.GONE);

                        if(currentQuestionID !=0 && validator.isNameValid(answer)){
                            valid_answer.setVisibility(View.GONE);

                            return true;
                        }else{

                            valid_answer.setVisibility(View.VISIBLE);
                        }

                    }else{
                        valid_password.setVisibility(View.VISIBLE);
//                        valid_password_req.setVisibility(View.VISIBLE);

                    }
                }else{
                    valid_email.setVisibility(View.VISIBLE);
                }





        }else{
            valid_name.setVisibility(View.VISIBLE);
        }


        return false;


    }

    private void emailSignUp(String firstName, String lastName, String email, String password, String question, String answer) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Send verification email
                        sendEmailVerification(user);

                        // Create user data in the database
                        createUserDataBase(user.getUid(), firstName, lastName, email, password, question, answer);

                        Toast.makeText(getApplicationContext(), "Signed up. Please verify your email and login again", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Send verification email
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignupActivity", "Verification email sent to " + user.getEmail());
                    } else {
                        Log.e("SignupActivity", "Failed to send verification email.", task.getException());
                    }
                });
    }

    private void createUserDataBase(String id, String firstName, String lastName, String email, String password, String question, String answer) {
        User user = new User(id, email, firstName, lastName, password, "", false, question,answer,0, new ArrayList<>());


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(id).setValue(user);

    }
}
