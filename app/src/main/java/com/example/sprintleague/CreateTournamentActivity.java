package com.example.sprintleague;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.Geocoder;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintleague.adapters.SponsorAdapter;
import com.example.sprintleague.adapters.TournamentSponsorAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateTournamentActivity extends AppCompatActivity {


    private EditText titleEditText, organizerEditText, distanceEditText, dateEditText, timeEditText, max_participantsEditText, cityEditText, postalCodeEditText, streetEditText, countryEditText;

    private LinearLayout addSponsor, createTournament, clearAll;
    private RecyclerView sponsorRecyclerView;
    private ImageView tournamentCover;
    private boolean coverLoaded = false;
    private TextView removeTournamentCover;

    private TextView valid_title, valid_distance, valid_date, valid_time, hint_max_participants, valid_address, valid_max_part;
    private TextView open_map;
    private StringsValidationMethods validator;
    private Calendar currentCalendar, selectedCalendar;

    private static final int REQUEST_CODE_SELECT_LOCATION = 1;

    private static final int MAP_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 123;

    private List<Sponsor> sponsors = new ArrayList<>();
    private SponsorAdapter sponsorAdapter;
    private static final int SPONSOR_IMAGE_REQUEST_CODE = 124;
    private Uri tempSponsorImageUri;

    private AlertDialog sponsorDialog, waiting_dialog;

    private FirebaseStorage storage;

    private ImageView go_back;

    private boolean isEditMode;

    private TextView activity_title, create_save_text;


    public static Tournament tournamentToEdit;


    private TextView valid_date_deadline, valid_time_deadline, levelText, valid_level;

    private EditText dateDeadlineEditText, timeDeadlineEditText;

    private int levelSelected;

    private Spinner levelSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);
        validator = new StringsValidationMethods();


        isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        initViews();



        organizerEditText.setText(AccountManager.currentUser.getFirstName()+" "+ AccountManager.currentUser.getLastName());

        max_participantsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hint_max_participants.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(max_participantsEditText.getText().toString().isEmpty()){
                    hint_max_participants.setVisibility(View.VISIBLE);
                }else{
                    hint_max_participants.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        currentCalendar = Calendar.getInstance();
        selectedCalendar = Calendar.getInstance();


        dateEditText.setInputType(InputType.TYPE_NULL);
        timeEditText.setInputType(InputType.TYPE_NULL);

        dateDeadlineEditText.setInputType(InputType.TYPE_NULL);
        timeDeadlineEditText.setInputType(InputType.TYPE_NULL);



        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                openDatePicker("normal");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker("normal");
            }
        });

        dateDeadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker("deadline");
            }
        });

        timeDeadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker("deadline");
            }
        });


        open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateTournamentActivity.this, MapActivity.class);
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleEditText.setText("");

                distanceEditText.setText("");

                dateEditText.setText("");
                timeEditText.setText("");

                dateDeadlineEditText.setText("");
                timeDeadlineEditText.setText("");

                max_participantsEditText.setText("");

                streetEditText.setText("");
                cityEditText.setText("");
                postalCodeEditText.setText("");
                countryEditText.setText("");

                tournamentCover.setImageResource(R.drawable.ic_add_outline);
                coverLoaded = false;
                removeTournamentCover.setVisibility(View.GONE);

                sponsors.clear();

                sponsorAdapter.notifyDataSetChanged();

            }
        });

        createTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){



                    if(isEditMode){

                        if(isInputValidEdit()){
                            Toast.makeText(getApplicationContext(), "All good - EDIT", Toast.LENGTH_SHORT).show();

//                            sendTournamentCoverToStorage(tournamentToEdit.getID());


                            saveTournamentData(tournamentToEdit.getID(), tournamentToEdit.getTournamentCoverLink(), tournamentToEdit.getSponsors());
                        }




                    }else{
                        Toast.makeText(getApplicationContext(), "All good", Toast.LENGTH_SHORT).show();
                        String id = AccountManager.generateAlphaNumericId(16);
                        sendTournamentCoverToStorage(id);
                    }

                }
            }
        });

        tournamentCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isEditMode){
                    openGallery();
                }else{
                    Toast.makeText(getApplicationContext(), "You cant Change the cover", Toast.LENGTH_SHORT).show();
                }

            }
        });

        removeTournamentCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tournamentCover.setImageResource(R.drawable.ic_add_outline);
                coverLoaded = false;
                removeTournamentCover.setVisibility(View.GONE);
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isEditMode){
                    DatabaseReference lockRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournamentToEdit.getID());

                    lockRef.child("lockedForEdit").setValue(false);
                }

                finish();
            }
        });



        if(isEditMode){
            sponsorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            TournamentSponsorAdapter tournamentSponsorAdapter;
            tournamentSponsorAdapter = new TournamentSponsorAdapter(getApplicationContext(), sponsors);

            sponsorRecyclerView.setAdapter(tournamentSponsorAdapter);
        }else{
            // In onCreate, replace your existing RecyclerView initialization with:
            sponsorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            sponsorAdapter = new SponsorAdapter(this, sponsors, position -> {
                // Let the adapter handle the removal
                sponsorAdapter.removeSponsor(position);
            });
            sponsorRecyclerView.setAdapter(sponsorAdapter);
        }


// Modify your existing addSponsor click listener
        addSponsor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isEditMode){
                    showAddSponsorDialog();
                }

            }
        });



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tournament_level,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        levelSpinner.setAdapter(adapter);

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//             Toast.makeText(getContext(),String.valueOf(i),Toast.LENGTH_SHORT).show();

                levelSelected = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        // Show a toast message

        DatabaseReference lockRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournamentToEdit.getID());

        lockRef.child("lockedForEdit").setValue(false);


        // Call the super method if you want the default back button behavior
        super.onBackPressed();
    }
    private void findViews(){
        titleEditText = findViewById(R.id.add_tournament_edittext_title);
        organizerEditText = findViewById(R.id.add_tournament_edittext_organizer);
        distanceEditText = findViewById(R.id.add_tournament_edittext_distance);

        dateEditText = findViewById(R.id.add_tournament_edittext_date);
        timeEditText = findViewById(R.id.add_tournament_edittext_time);


        max_participantsEditText = findViewById(R.id.add_tournament_edittext_max_participants);

        cityEditText = findViewById(R.id.add_tournament_edittext_city);
        postalCodeEditText = findViewById(R.id.add_tournament_edittext_postal_code);
        streetEditText = findViewById(R.id.add_tournament_edittext_street);
        countryEditText = findViewById(R.id.add_tournament_edittext_country);



        addSponsor = findViewById(R.id.add_tournament_sponsor_add);
        createTournament = findViewById(R.id.add_tournament_create);
        clearAll = findViewById(R.id.add_tournament_clear_all);

        sponsorRecyclerView = findViewById(R.id.add_tournament_sponsorsrecyclerview);

        valid_title = findViewById(R.id.text_view_enter_valid_title);
        valid_distance = findViewById(R.id.text_view_enter_valid_distance);
        valid_date = findViewById(R.id.text_view_enter_valid_date);
        valid_time = findViewById(R.id.text_view_enter_valid_time);
        hint_max_participants = findViewById(R.id.hint_max_participants);
        valid_address = findViewById(R.id.text_view_enter_valid_addresse);
        valid_max_part = findViewById(R.id.text_view_enter_valid_max_part);
        open_map = findViewById(R.id.add_tournament_open_map);

        tournamentCover = findViewById(R.id.tournament_cover);
        removeTournamentCover = findViewById(R.id.remove_tournament_cover);
        go_back = findViewById(R.id.go_back);

        activity_title = findViewById(R.id.activity_title);
        create_save_text = findViewById(R.id.tournament_create_save_text);

        valid_date_deadline = findViewById(R.id.text_view_enter_valid_date_deadline);
        valid_time_deadline= findViewById(R.id.text_view_enter_valid_time_deadline);

        dateDeadlineEditText = findViewById(R.id.add_tournament_edittext_date_deadline);
        timeDeadlineEditText = findViewById(R.id.add_tournament_edittext_time_deadline);

        levelSpinner = findViewById(R.id.level_spinner);
        levelText = findViewById(R.id.level_text);
        valid_level = findViewById(R.id.valid_level);


    }


    private void initViews(){

        findViews();

        if(isEditMode){

            addSponsor.setVisibility(View.GONE);
            activity_title.setText(getString(R.string.edit_tournament));
            create_save_text.setText(getString(R.string.save));


            titleEditText.setText(tournamentToEdit.getTitle());
            distanceEditText.setText(tournamentToEdit.getDistance()+"");

            dateEditText.setText(tournamentToEdit.getDateTime().getYear()+"-"+tournamentToEdit.getDateTime().getMonth()+"-"+tournamentToEdit.getDateTime().getDay());
            dateDeadlineEditText.setText(tournamentToEdit.getJoinDeadline().getYear()+"-"+tournamentToEdit.getJoinDeadline().getMonth()+"-"+tournamentToEdit.getJoinDeadline().getDay());

            String time = tournamentToEdit.getDateTime().getHour()+":"+tournamentToEdit.getDateTime().getMinute() +" "+tournamentToEdit.getDateTime().getAm_pm();
            String deadLinetime = tournamentToEdit.getJoinDeadline().getHour()+":"+tournamentToEdit.getJoinDeadline().getMinute() +" "+tournamentToEdit.getJoinDeadline().getAm_pm();

            if(Utils.is24HourFormat(this)){
                time = Utils.convert12HourTo24Hour(time);
                deadLinetime = Utils.convert12HourTo24Hour(deadLinetime);

            }

            timeEditText.setText(time);
            timeDeadlineEditText.setText(deadLinetime);

            if(tournamentToEdit.getMaxParticipants() != -1){
                max_participantsEditText.setText(tournamentToEdit.getMaxParticipants()+"");
            }


            cityEditText.setText(tournamentToEdit.getAddress().getCity());
            postalCodeEditText.setText(tournamentToEdit.getAddress().getPostalCode());
            streetEditText.setText(tournamentToEdit.getAddress().getStreet());
            countryEditText.setText(tournamentToEdit.getAddress().getCountry());


            if(!tournamentToEdit.getTournamentCoverLink().isEmpty()){
                Picasso.get().load(tournamentToEdit.getTournamentCoverLink()).into(tournamentCover);
                removeTournamentCover.setVisibility(View.GONE);
                coverLoaded = true;

            }else{
                removeTournamentCover.setVisibility(View.GONE);
                tournamentCover.setImageResource(R.drawable.ic_add_outline);
                coverLoaded = false;
            }

            if(tournamentToEdit.getSponsors() != null && !tournamentToEdit.getSponsors().isEmpty()){
                sponsors = tournamentToEdit.getSponsors();
            }


            levelSpinner.setVisibility(View.GONE);
            levelText.setVisibility(View.VISIBLE);

            levelSelected = Integer.parseInt(tournamentToEdit.getLevel());

            switch (tournamentToEdit.getLevel()){
                case "1":
                    levelText.setText(getString(R.string.beginner));
                    break;
                case "2":
                    levelText.setText(getString(R.string.intermediate));
                    break;
                case "3":
                    levelText.setText(getString(R.string.advanced));
                    break;
            }




        }else{
            activity_title.setText(getString(R.string.add_tournament));
            create_save_text.setText(getString(R.string.create));
        }



    }

    private boolean isInputValid(){
        String title = titleEditText.getText().toString();
        String distance = distanceEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String maxP = max_participantsEditText.getText().toString();


        String city = cityEditText.getText().toString();
        String postal_code = postalCodeEditText.getText().toString();
        String street = streetEditText.getText().toString();
        String country = countryEditText.getText().toString();

        String dateDeadline = dateEditText.getText().toString();
        String timeDeadline = timeEditText.getText().toString();

        if(validator.isValidTournamentTitle(title)){
            valid_title.setVisibility(View.GONE);

            if(!distance.isEmpty() && !distance.equals("0")){
                valid_distance.setVisibility(View.GONE);



                if(!date.isEmpty()){
                    valid_date.setVisibility(View.GONE);

                    if(!time.isEmpty()){
                        valid_time.setVisibility(View.GONE);


                        if(!dateDeadline.isEmpty()){
                            valid_date_deadline.setVisibility(View.GONE);

                            if(!timeDeadline.isEmpty()){
                                valid_time_deadline.setVisibility(View.GONE);


                                if(levelSelected != 0){
                                    valid_level.setVisibility(View.GONE);

                                    if(!maxP.equals("0")){
                                        valid_max_part.setVisibility(View.GONE);

                                        if(validator.isValidCityName(city) && validator.isValidPostalCode(postal_code) && validator.isValidStreetAddress(street) && validator.isValidCityName(country)){
                                            valid_address.setVisibility(View.GONE);

                                            return true;
                                        }else{
                                            valid_address.setVisibility(View.VISIBLE);
                                        }

                                    }else{
                                        valid_max_part.setVisibility(View.VISIBLE);
                                    }

                                }else{
                                    valid_level.setVisibility(View.VISIBLE);
                                }



                            }else{
                                valid_time_deadline.setVisibility(View.VISIBLE);
                            }
                        }else{
                            valid_date_deadline.setVisibility(View.VISIBLE);
                        }




                    }else{
                        valid_time.setVisibility(View.VISIBLE);
                    }
                }else{
                    valid_date.setVisibility(View.VISIBLE);
                }





            }else{
                valid_distance.setVisibility(View.VISIBLE);
            }

        }else{
            valid_title.setVisibility(View.VISIBLE);
        }

        return false;

    }

    private void openDatePicker(String forWhich) {
        // Get the current date
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTournamentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // Update the selectedCalendar with the selected date
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay);



                // Format the selected date and set it in the EditText
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                if ("deadline".equals(forWhich)) {
                    dateDeadlineEditText.setText(dateFormat.format(selectedCalendar.getTime()));
                } else {
                    dateEditText.setText(dateFormat.format(selectedCalendar.getTime()));
                }

                // Clear the time field if the date changes
                timeEditText.setText("");
            }
        }, year, month, day);

        // Restrict past dates for "normal" and "deadline"
        if ("deadline".equals(forWhich)) {
            // Optionally set additional constraints for deadlines
            // For example, you can restrict deadlines to be before the event date
            if (!dateEditText.getText().toString().isEmpty()) {
                try {
                    Date eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateEditText.getText().toString());
                    if (eventDate != null) {
                        datePickerDialog.getDatePicker().setMaxDate(eventDate.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

        datePickerDialog.show();
    }

    private void openTimePicker(String forWhich) {
        // Get the current time
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        // Get the selected date
        boolean isCurrentDay = isSameDay(currentCalendar, selectedCalendar);

        // Check if the device uses 24-hour format
        boolean is24HourFormat = Utils.is24HourFormat(this);

        // Create TimePickerDialog based on the format
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTournamentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                // Update the selectedCalendar with the selected time
                selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedCalendar.set(Calendar.MINUTE, selectedMinute);

                // Format the selected time and set it in the appropriate EditText
                SimpleDateFormat timeFormat = new SimpleDateFormat(is24HourFormat ? "HH:mm" : "hh:mm a", Locale.getDefault());
                if ("deadline".equals(forWhich)) {
                    timeDeadlineEditText.setText(timeFormat.format(selectedCalendar.getTime()));
                } else {
                    timeEditText.setText(timeFormat.format(selectedCalendar.getTime()));
                }
            }
        }, isCurrentDay ? Math.max(currentHour, 0) : 0, isCurrentDay ? currentMinute : 0, is24HourFormat);

        // Limit past times if the selected date is today
        if (isCurrentDay) {
            timePickerDialog.updateTime(Math.max(currentHour, 0), Math.max(currentMinute, 0));
        }

        timePickerDialog.show();
    }



    private boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve the extras
            String address = data.getStringExtra("address");
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String googleMapsUrl = data.getStringExtra("google_maps_url");


            String [] parts = address.split(", ");

            streetEditText.setText(parts[0]);
            postalCodeEditText.setText(parts[1].split(" ")[0]);
            cityEditText.setText(parts[1].split(" ")[1]);
            countryEditText.setText(parts[2]);


        }


        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the image URI from the intent
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Set the selected image to the ImageView
                tournamentCover.setImageURI(selectedImageUri);
                coverLoaded = true;
                removeTournamentCover.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }




        if (requestCode == SPONSOR_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            tempSponsorImageUri = data.getData();
            if (tempSponsorImageUri != null && sponsorDialog != null && sponsorDialog.isShowing()) {
                ImageView imagePreview = sponsorDialog.findViewById(R.id.sponsor_image_preview);
                if (imagePreview != null) {
                    imagePreview.setImageURI(tempSponsorImageUri);
                }
            }
        }

    }



    // Method to open the gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Show only images
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    private void showAddSponsorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_sponsor, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.sponsor_name_input);
        ImageView imagePreview = dialogView.findViewById(R.id.sponsor_image_preview);
        LinearLayout selectImageButton = dialogView.findViewById(R.id.select_image_button);

        TextView add = dialogView.findViewById(R.id.sponsor_dialog_add);
        TextView cancel = dialogView.findViewById(R.id.sponsor_dialog_cancel);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SPONSOR_IMAGE_REQUEST_CODE);
        });

//        builder.setPositiveButton("Add", null); // We'll override this below
//        builder.setNegativeButton("Cancel", null);

        sponsorDialog = builder.create();

        // Override the positive button to prevent dialog from closing if validation fails
        sponsorDialog.setOnShowListener(dialogInterface -> {

            add.setOnClickListener(view -> {
                String sponsorName = nameInput.getText().toString();
                if (!sponsorName.isEmpty() && tempSponsorImageUri != null) {
                    Sponsor s = new Sponsor(sponsorName, "");
                    s.setSponsorImgUri(tempSponsorImageUri);
                    sponsors.add(s);
                    sponsorAdapter.notifyDataSetChanged();
                    tempSponsorImageUri = null;
                    sponsorDialog.dismiss();
                } else {
                    Toast.makeText(CreateTournamentActivity.this,
                            "Please enter sponsor name and select an image",
                            Toast.LENGTH_SHORT).show();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sponsorDialog.dismiss();
                }
            });
        });

        sponsorDialog.show();
    }




    private void sendTournamentCoverToStorage(String tournamentID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_uploading_tournament, null);
        builder.setView(dialogView);

        waiting_dialog = builder.create();
        waiting_dialog.setCancelable(false);
        waiting_dialog.setCanceledOnTouchOutside(false);

        waiting_dialog.show();

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        if(coverLoaded){
            String folder = "Tournaments/" + tournamentID + "/Cover/";
            StorageReference tournamentRef = storageRef.child(folder + "cover.jpg");

            tournamentCover.setDrawingCacheEnabled(true);
            tournamentCover.buildDrawingCache();

            Bitmap bitmap = ((BitmapDrawable) tournamentCover.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = tournamentRef.putBytes(data);

            uploadTask.addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Error uploading Cover", Toast.LENGTH_SHORT).show()
            ).addOnSuccessListener(taskSnapshot -> {

                tournamentRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String coverUrl = uri.toString();
                    Toast.makeText(getApplicationContext(), "Cover uploaded successfully!", Toast.LENGTH_SHORT).show();


                    sendTournamentSponsorsToStorage(tournamentID, coverUrl);


                }).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to get cover URL", Toast.LENGTH_SHORT).show()
                );
            });
        }else{

            sendTournamentSponsorsToStorage(tournamentID, "");


        }

    }


    private void sendTournamentSponsorsToStorage(String tournamentID, String coverUrl) {
        if (sponsors.isEmpty()) {
            // No sponsors to upload, proceed with saving tournament data
            saveTournamentData(tournamentID, coverUrl, new ArrayList<>());
            return;
        }

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ArrayList<Sponsor> finalSponsors = new ArrayList<>();

        List<String> sponsorImageUrls = new ArrayList<>();

        for (Sponsor sponsor : sponsors) {
            Uri sponsorImageUri = sponsor.getSponsorImgUri();
            String sponsorName = sponsor.getSponsorName();
            String sponsorImagePath = "Tournaments/" + tournamentID + "/Sponsors/" + sponsorName + ".jpg";

            StorageReference sponsorRef = storageRef.child(sponsorImagePath);

            sponsorRef.putFile(sponsorImageUri)
                    .addOnSuccessListener(taskSnapshot -> sponsorRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        sponsorImageUrls.add(uri.toString());
                        finalSponsors.add(new Sponsor(sponsorName,uri.toString()));

                        // Check if all sponsor images are uploaded
                        if (sponsorImageUrls.size() == sponsors.size()) {
                            saveTournamentData(tournamentID, coverUrl, finalSponsors);
                        }
                    }))
                    .addOnFailureListener(e ->
                            Toast.makeText(getApplicationContext(), "Error uploading sponsor: " + sponsorName, Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void saveTournamentData(String tournamentID, String coverUrl, ArrayList<Sponsor> sponsors) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournamentID);

        String textFromTimeEditText = timeEditText.getText().toString();
        String textFromTimeDeadlingEditText = timeDeadlineEditText.getText().toString();

        if(Utils.is24HourFormat(this)){
            textFromTimeEditText = Utils.convert24HourTo12Hour(textFromTimeEditText);
            textFromTimeDeadlingEditText = Utils.convert24HourTo12Hour(textFromTimeDeadlingEditText);
        }

        String[] time = textFromTimeEditText.split(" ");
        String[] date = dateEditText.getText().toString().split("-");

        String[] timeDeadline = textFromTimeDeadlingEditText.split(" ");
        String[] dateDeadline = dateDeadlineEditText.getText().toString().split("-");

        int max;
        if(max_participantsEditText.getText().toString().isEmpty()){
            max=-1;
        }else{
            max = Integer.parseInt(max_participantsEditText.getText().toString());
        }

        String am_pm = time[1];
        String[] hh_mm = time[0].split(":");

        String am_pm_deadline = timeDeadline[1];
        String[] hh_mm_deadline = timeDeadline[0].split(":");





        Tournament tournament = new Tournament(
                tournamentID,
                titleEditText.getText().toString(),
                AccountManager.currentUser.getId(),
                Float.parseFloat(distanceEditText.getText().toString()),
                new DateTime(date[0], date[1], date[2], hh_mm[0], hh_mm[1], am_pm),
                max,
                new CustomeAddress(
                        cityEditText.getText().toString(),
                        postalCodeEditText.getText().toString(),
                        streetEditText.getText().toString(),
                        countryEditText.getText().toString()),
                sponsors,
                coverUrl, new ArrayList<>(), false,
                new DateTime(dateDeadline[0], dateDeadline[1], dateDeadline[2], hh_mm_deadline[0], hh_mm_deadline[1], am_pm_deadline),

                String.valueOf(levelSelected), false);








        databaseRef.setValue(tournament).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(isEditMode){
                    Toast.makeText(getApplicationContext(), "Tournament updated successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Tournament created successfully!", Toast.LENGTH_SHORT).show();
                    waiting_dialog.dismiss();
                }

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to save tournament data", Toast.LENGTH_SHORT).show();
                waiting_dialog.dismiss();
            }
        });




    }


    private boolean isInputValidEdit(){

        String maxEdited = max_participantsEditText.getText().toString();

        if(maxEdited.isEmpty()){
            return true;
        }else{

            int max = Integer.parseInt(maxEdited);

            if(tournamentToEdit.getAttendingList() != null && !tournamentToEdit.getAttendingList().isEmpty()){

                if(max >= tournamentToEdit.getAttendingList().size()){
                    return true;
                }else{
                    return false;
                }

            }else{
                return  true;
            }

        }


    }






}