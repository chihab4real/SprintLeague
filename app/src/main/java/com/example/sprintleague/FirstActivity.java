package com.example.sprintleague;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity {

    private AccountManager accountManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        /*EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        accountManager = new AccountManager(FirstActivity.this);

        mAuth = FirebaseAuth.getInstance();

        String email = accountManager.loadString("userEmail", "-1");
        String password = accountManager.loadString("userPassword", "-1");
        String id = accountManager.loadString("userUid", "-1");



        getAllTournaments();

        if (isUserDataSaved(email, password, id)) {


            autoLoginUser(email, password, id);
        }else{
           // startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
        }
    }


    private boolean isUserDataSaved(String email, String password, String id) {


        return !email.equals("-1") && !password.equals("-1") && !id.equals("-1");
    }

    private void autoLoginUser(String email, String password, String id) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user.isEmailVerified()){
                                getUserFromDataBase(user.getUid());

                                //startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
                                //finish();
                            }else{
                                //startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
                                //finish();
                            }

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
                            //finish();
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




                AccountManager.currentUser = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }




    private void getAllTournaments(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Tournaments");
        ArrayList<Tournament> tournaments = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournaments.clear();

                if(snapshot.getChildrenCount() == 0){
                    startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
                    finish();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournaments.add(tournament);

                    if (tournaments.size() == snapshot.getChildrenCount()) {
                        Utils.tournaments = tournaments;

                        startActivity(new Intent(FirstActivity.this, MainMenuActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here if needed

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener when the activity is destroyed
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

}