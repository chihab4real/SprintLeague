package com.example.sprintleague.ui.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.CustomeAddress;
import com.example.sprintleague.DateTime;
import com.example.sprintleague.ForgotPasswordActivity;
import com.example.sprintleague.R;
import com.example.sprintleague.StringsValidationMethods;
import com.example.sprintleague.Tournament;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class EditMyAccountActivity extends AppCompatActivity {


    private EditText emailEditText, fullNameEditText, oldPasswordEditText, newPasswordEditText;
    private RelativeLayout editProfile, save;

    private DatabaseReference mDatabase;


    private TextView removePic, enter_valid_password,enter_valid_password_req, forgotPassword;

    private ImageView go_back, profile_pic;

    private static final int GALLERY_REQUEST_CODE = 123;


    private boolean userHasProfilePic = false;
    private boolean picLoaded = false;
    private boolean picChanged = false;

    private FirebaseStorage storage;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private AlertDialog sponsorDialog, waiting_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_account);
        /*EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        initViews();

        removePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picLoaded = false;

                if(userHasProfilePic){
                    picChanged = true;
                }
                profile_pic.setImageResource(R.drawable.empty_profile_pic);
                removePic.setVisibility(View.GONE);


            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(EditMyAccountActivity.this, ForgotPasswordActivity.class));
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                checkPassword();

            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void initViews(){

        emailEditText = findViewById(R.id.editprofile_edittext_email_or_phone);
        fullNameEditText = findViewById(R.id.editprofile_edittext_fullname);
        oldPasswordEditText = findViewById(R.id.editprofile_edittext_old_password);
        newPasswordEditText = findViewById(R.id.editprofile_edittext_new_password);


        editProfile = findViewById(R.id.editaccount_edit_pic);
        save = findViewById(R.id.editprofile_click_save);


        go_back = findViewById(R.id.go_back);

        profile_pic = findViewById(R.id.editaccount_profile_pic);



        removePic = findViewById(R.id.editaccount_remove_image);

        if(!AccountManager.currentUser.getProfilePic().isEmpty()){
            Picasso.get()
                    .load(AccountManager.currentUser.getProfilePic())
                    .into(profile_pic);

            userHasProfilePic = true;
            picLoaded = true;

            removePic.setVisibility(View.VISIBLE);

        }else{
            userHasProfilePic = false;
            picLoaded = false;

            removePic.setVisibility(View.GONE);

        }


        enter_valid_password = findViewById(R.id.text_view_enter_valid_password);
        enter_valid_password_req = findViewById(R.id.text_view_enter_password_req);

        forgotPassword = findViewById(R.id.editaccount_text_click_forgetpassword);

        emailEditText.setEnabled(false);
        fullNameEditText.setEnabled(false);



        emailEditText.setText(AccountManager.currentUser.getEmail());
        fullNameEditText.setText(AccountManager.currentUser.getFirstName()+" "+AccountManager.currentUser.getLastName());




    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Show only images
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the image URI from the intent
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Set the selected image to the ImageView
                profile_pic.setImageURI(selectedImageUri);

                removePic.setVisibility(View.VISIBLE);
                picLoaded = true;


            } else {

                picLoaded = false;
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }






    }

    private void sendUserPicToDB(String userID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_uploading_tournament, null);
        builder.setView(dialogView);

        waiting_dialog = builder.create();

        waiting_dialog.setCancelable(false);
        waiting_dialog.setCanceledOnTouchOutside(false);

        waiting_dialog.show();

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        if(userHasProfilePic){
            if(!picLoaded){
                // remove it
                DeletePic("Users/"+userID+"/profilepic/profile.jpg");
            }else{
                if(picChanged){
                    //Delete old And Upload new
                    DeletePic("Users/"+userID+"/profilepic/profile.jpg");
                    UploadPic("Users/"+userID+"/profilepic/profile.jpg");
                }
            }


        }else{

            if(picLoaded){
                // put the new one
                UploadPic("Users/"+userID+"/profilepic/profile.jpg");
            }
        }









    }


    private void DeletePic(String folder){
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


        StorageReference desertRef = storageRef.child(folder);


        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                updateUserDB("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    private void UploadPic(String folder){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_uploading_tournament, null);
        builder.setView(dialogView);


        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();



            StorageReference tournamentRef = storageRef.child(folder);

            profile_pic.setDrawingCacheEnabled(true);
            profile_pic.buildDrawingCache();

            Bitmap bitmap = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = tournamentRef.putBytes(data);

            uploadTask.addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Error uploading Cover", Toast.LENGTH_SHORT).show()
            ).addOnSuccessListener(taskSnapshot -> {

                tournamentRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String picUrl = uri.toString();
                    updateUserDB(picUrl);
                    Toast.makeText(getApplicationContext(), "Cover uploaded successfully!", Toast.LENGTH_SHORT).show();



                }).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to get cover URL", Toast.LENGTH_SHORT).show()
                );
            });

    }

    private void updateUserDB(String newValue){

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(AccountManager.currentUser.getId()).child("profilePic").setValue(newValue)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                      AccountManager.currentUser.setProfilePic(newValue);

                        waiting_dialog.dismiss();



                    } else {
                    }
                });
    }


    private void checkPassword(){
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        StringsValidationMethods validator = new StringsValidationMethods();


        if(oldPassword.isEmpty() && newPassword.isEmpty()){
            enter_valid_password.setVisibility(View.GONE);
            enter_valid_password_req.setVisibility(View.GONE);

            sendUserPicToDB(AccountManager.currentUser.getId());
        }else{
            if(!oldPassword.isEmpty() && !newPassword.isEmpty()){

                if(oldPassword.equals(AccountManager.currentUser.getPassword())){
                    if(!newPassword.equals(oldPassword)){
                        if(validator.isPasswordValid(newPassword)){
                            // CHANGE IT
                            enter_valid_password.setVisibility(View.GONE);
                            enter_valid_password_req.setVisibility(View.GONE);

                            changePassword(AccountManager.currentUser.getEmail(), AccountManager.currentUser.getPassword(), newPassword);


                        }else{

                            // NEW PASSWORD DOES NOT MATCHRQIREMENTS
                            enter_valid_password.setVisibility(View.VISIBLE);
                            enter_valid_password_req.setVisibility(View.VISIBLE);

                        }
                    }else{
                        // OLD PASSWORD SHOULD NOT BE LIKE THE NEW ONE
                        enter_valid_password.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"Current Password and New Password should be different", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // OLD PASSWORD NOT CORRECT
                    enter_valid_password.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Current Password not Correct", Toast.LENGTH_SHORT).show();
                }

            }else{

                // FIELD CANT BE EMPTY
                enter_valid_password.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Fields cant be Empty", Toast.LENGTH_SHORT).show();
            }


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
                            changePasswordinDB(AccountManager.currentUser.getId(), newpassword);
                        } else {
                            Log.e("AuthError", "Failed to get current user after login.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthError", "Login failed: " + e.getMessage());
                    //Toast.makeText(getContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void changePasswordinDB(String id, String newpassword){
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(id).child("password").setValue(newpassword);


        currentUser.updatePassword(newpassword);

        mAuth.signOut();

        currentUser = null;

        if(AccountManager.currentUser != null){
            AccountManager.currentUser.setPassword(newpassword);
            AccountManager accountManager = new AccountManager(getApplicationContext());

            accountManager.saveString("userPassword", newpassword);
        }

        Toast.makeText(getApplicationContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();

        sendUserPicToDB(AccountManager.currentUser.getId());






    }



}