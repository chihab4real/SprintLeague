package com.example.sprintleague;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sprintleague.adapters.TournamentSponsorAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TournamentActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Tournament tournament;

    private TextView tournamentTitle, tournamentDistance, tournamentDateTime, tournamentPlaces, tournamentAddress;
    private ImageView tournamentCover;
    private RecyclerView tournamentSponsors;
    private CustomMapView tournamentMapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private TournamentSponsorAdapter tournamentSponsorAdapter;

    private LatLng tournamentLocation;

    private RelativeLayout map_current, map_direction;
    private LinearLayout joinLayout, cantJoinLayout, editLayout, loginFirstLayout, alreadJoinedLayout;

    private LinearLayout joinClick, editClick;

    private SwipeRefreshLayout swipe_layout;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        initViews();




        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        tournamentMapView.onCreate(mapViewBundle);
        tournamentMapView.getMapAsync(this); // Ensure the map is initialized

        // Button to re-center map to the tournament location

        map_current.setOnClickListener(v -> {
            if (tournamentLocation != null) {
                tournamentMapView.getMapAsync(googleMap ->
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tournamentLocation, 15f))
                );
            } else {
                Toast.makeText(this, "Location not available yet", Toast.LENGTH_SHORT).show();
            }
        });

        map_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMapsWithDirections();
            }
        });

        joinClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinTournament();
            }
        });

        editClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference lockRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournament.getID());

                lockRef.child("lockedForEdit").setValue(true);

                Intent intent = new Intent(TournamentActivity.this, CreateTournamentActivity.class);
                intent.putExtra("isEditMode", true);
                CreateTournamentActivity.tournamentToEdit = tournament;
                startActivity(intent);
            }
        });

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databaseReference = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournament.getID());

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tournament newTournament = snapshot.getValue(Tournament.class);

                        if(newTournament != null){

                            swipe_layout.setRefreshing(false);


                            tournament = newTournament;

                            initViews();

                            if (databaseReference != null && valueEventListener != null) {

                                databaseReference.removeEventListener(valueEventListener);
                            }

                        }else{
                            Toast.makeText(getApplicationContext(), "Error refreshing", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                databaseReference.addValueEventListener(valueEventListener);


            }
        });

    }

    private void initViews(){

        findViewsIds();
        if (tournament == null) {
            Toast.makeText(this, "Tournament data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        tournamentTitle.setText(tournament.getTitle());
        tournamentDistance.setText(tournament.getDistance() + " Km");
//        tournamentPlaces.setText(String.valueOf(tournament.getMaxParticipants()));

        if (tournament.getTournamentCoverLink().isEmpty()) {
            tournamentCover.setImageResource(R.drawable.tournament_sample);
        } else {
            Picasso.get()
                    .load(tournament.getTournamentCoverLink())
                    .into(tournamentCover);
        }

        String date = tournament.getDateTime().getDay() + "/" +
                tournament.getDateTime().getMonth() + "/" +
                tournament.getDateTime().getYear();
        String time = tournament.getDateTime().getHour() + ":" +
                tournament.getDateTime().getMinute() + " " +
                tournament.getDateTime().getAm_pm();
        tournamentDateTime.setText(String.format("%s, %s", date, time));

        tournamentAddress.setText(String.format("%s\n%s, %s", tournament.getAddress().getStreet(), tournament.getAddress().getPostalCode(), tournament.getAddress().getCity()));

        tournamentMapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tournamentMapView.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                tournamentMapView.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });


        if (tournament.getSponsors()!= null && !tournament.getSponsors().isEmpty()) {


            tournamentSponsors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            tournamentSponsorAdapter = new TournamentSponsorAdapter(getApplicationContext(), tournament.getSponsors());

            tournamentSponsors.setAdapter(tournamentSponsorAdapter);
        }



        checkAvailableSpots();
        whihcLayoutToShow();


    }

    private void findViewsIds() {
        tournamentTitle = findViewById(R.id.tournament_title);
        tournamentDistance = findViewById(R.id.tournament_distance);
        tournamentDateTime = findViewById(R.id.tournament_date_time);
        tournamentPlaces = findViewById(R.id.tournament_places);
        tournamentAddress = findViewById(R.id.tournament_address);
        tournamentCover = findViewById(R.id.tournament_cover);
        tournamentSponsors = findViewById(R.id.tournament_sponsorsrecyclerview);
        tournamentMapView = findViewById(R.id.tournament_mapview);
        map_current = findViewById(R.id.map_current);
        map_direction = findViewById(R.id.map_direction);
        
        joinLayout = findViewById(R.id.join_layout);
        cantJoinLayout = findViewById(R.id.cantjoin_layout);
        editLayout = findViewById(R.id.edit_layout);
        loginFirstLayout = findViewById(R.id.loginfirst_layout);
        alreadJoinedLayout = findViewById(R.id.already_joined_layout);
        swipe_layout = findViewById(R.id.swipe_layout_tournament);

        joinClick = findViewById(R.id.join_joinbutton);
        editClick = findViewById(R.id.edit_editbutton);



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (tournament == null) {
            Toast.makeText(this, "Tournament data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = tournament.getAddress().getStreet() + ", " +
                tournament.getAddress().getPostalCode() + ", " +
                tournament.getAddress().getCity() + ", " +
                tournament.getAddress().getCountry();

        tournamentLocation = getLatLngFromAddress(this, fullAddress);

        if (tournamentLocation == null) {
            Toast.makeText(this, "Geocoding failed, using default location", Toast.LENGTH_SHORT).show();
            tournamentLocation = new LatLng(40.7128, -74.0060); // Default to NYC
        }

        googleMap.addMarker(new MarkerOptions().position(tournamentLocation).title("Tournament Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tournamentLocation, 15f));

        applyMapStyle(googleMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        tournamentMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tournamentMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tournamentMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        tournamentMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        tournamentMapView.onLowMemory();
    }

    public LatLng getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            Toast.makeText(context, "Geocoder service unavailable", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    private void applyMapStyle(GoogleMap googleMap) {
        try {
            // Choose which mode to load, based on user preference or system theme
            String mapStyleJson;
            if (isDarkMode()) {
                mapStyleJson = loadJSONFromRaw(R.raw.map_style_dark);
            } else {
                mapStyleJson = loadJSONFromRaw(R.raw.map_style_light);
            }

            if (mapStyleJson != null) {
                googleMap.setMapStyle(new MapStyleOptions(mapStyleJson));
            } else {
                Toast.makeText(this, "Map style loading failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error applying map style", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDarkMode() {
        // This is just an example. You can check the system-wide theme or user preference.
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    private String loadJSONFromRaw(int rawResourceId) {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(rawResourceId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void openGoogleMapsWithDirections() {
        // Get the tournament location
        if (tournament == null || tournament.getAddress() == null) {
            Toast.makeText(this, "Tournament location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = tournament.getAddress().getStreet() + ", " +
                tournament.getAddress().getPostalCode() + ", " +
                tournament.getAddress().getCity() + ", " +
                tournament.getAddress().getCountry();

        // Convert address to LatLng
        LatLng destinationLatLng = getLatLngFromAddress(this, fullAddress);
        if (destinationLatLng == null) {
            Toast.makeText(this, "Geocoding failed, cannot get directions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the URI for Google Maps directions
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destinationLatLng.latitude + "," + destinationLatLng.longitude);

        // Create an Intent to open Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if the Google Maps app is installed
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // If the app is not installed, open Google Maps in a web browser
            Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }

    }

    private void checkAvailableSpots() {
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournament.getID());


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> attendingList = new ArrayList<>();

                // Check if the "attendingList" node exists and has children
                if (snapshot.exists() && snapshot.hasChild("attendingList")) {

                    DataSnapshot attendingSnapshot = snapshot.child("attendingList");



                    // Check if there are any participants in the attendingList
                    if (attendingSnapshot.exists() && attendingSnapshot.hasChildren()) {
                        for (DataSnapshot participantSnapshot : attendingSnapshot.getChildren()) {
                            String participantID = participantSnapshot.getValue(String.class);

                            if (participantID != null) {

                                attendingList.add(participantID);
                            }
                        }
                    }
                }

                // Process the data (e.g., update UI based on list size)

                tournament.setAttendingList(attendingList);

                // Remove the listener after data is fetched
                tournamentRef.child("attendingList").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        };

        // Attach the listener to the database reference
        tournamentRef.child("attendingList").addListenerForSingleValueEvent(valueEventListener);
    }



    private void joinTournament() {
        // Get a reference to the tournament in the Firebase database
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("Tournaments").child(tournament.getID());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(AccountManager.currentUser.getId());

        // Create a listener to check the lockedForEdit status and fetch data
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the tournament is locked for editing
                Boolean lockedForEdit = snapshot.child("lockedForEdit").getValue(Boolean.class);

                if (lockedForEdit != null && lockedForEdit) {
                    // Tournament is locked, user cannot join
                    Toast.makeText(getApplicationContext(), "This tournament is currently locked and cannot be joined.", Toast.LENGTH_SHORT).show();

                    whihcLayoutToShow(); // Update UI appropriately
                    return;
                }

                // Proceed to fetch and process attendingList
                ArrayList<String> attendingList = new ArrayList<>();

                if (snapshot.exists() && snapshot.hasChild("attendingList")) {
                    DataSnapshot attendingSnapshot = snapshot.child("attendingList");

                    if (attendingSnapshot.exists() && attendingSnapshot.hasChildren()) {
                        for (DataSnapshot participantSnapshot : attendingSnapshot.getChildren()) {
                            String participantID = participantSnapshot.getValue(String.class);
                            if (participantID != null) {
                                attendingList.add(participantID);
                            }
                        }
                    }
                }

                // Check if there is space in the tournament
                int currentParticipants = attendingList.size();
                if (tournament.getMaxParticipants() == -1 || currentParticipants < tournament.getMaxParticipants()) {
                    // Add the user to the attending list
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming Firebase Auth
                    attendingList.add(userID);

                    tournamentRef.child("attendingList").setValue(attendingList).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully added user to the tournament
                            Toast.makeText(getApplicationContext(), "You have successfully joined the tournament!", Toast.LENGTH_SHORT).show();

                            if (AccountManager.currentUser.getAttendingTournaments() != null) {
                                ArrayList<String> temp = AccountManager.currentUser.getAttendingTournaments();
                                temp.add(tournament.getID());
                                AccountManager.currentUser.setAttendingTournaments(temp);
                            } else {
                                ArrayList<String> temp = new ArrayList<>();
                                temp.add(tournament.getID());
                                AccountManager.currentUser.setAttendingTournaments(temp);
                            }

                            userRef.child("attendingTournaments").setValue(AccountManager.currentUser.getAttendingTournaments());
                            tournament.setAttendingList(attendingList);

                            whihcLayoutToShow();
                        } else {
                            // Handle failure
                            Toast.makeText(getApplicationContext(), "Failed to join the tournament. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // No space available
                    Toast.makeText(getApplicationContext(), "Sorry, the tournament is full.", Toast.LENGTH_SHORT).show();

                    whihcLayoutToShow();
                }

                // Remove the listener after processing
                tournamentRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
                Toast.makeText(getApplicationContext(), "Error checking availability. Please try again.", Toast.LENGTH_SHORT).show();

                // Remove the listener in case of cancellation
                tournamentRef.removeEventListener(this);
            }
        };

        // Attach the listener to the database reference
        tournamentRef.addListenerForSingleValueEvent(valueEventListener);
    }


    private void whihcLayoutToShow(){

        //check if user is logged in (if not show login panel)
        if(AccountManager.currentUser != null){

            // check if user is not the owner of this tournament (if not show edit panel)
            if(!AccountManager.currentUser.getId().equals(tournament.getOrganizerID())){

                //check if attendigList is not null or empty (if not show join panel)
                if(tournament.getAttendingList() != null && !tournament.getAttendingList().isEmpty()){

                    //check if attending list does not contain the current user (if not show join panel)
                    if(!tournament.getAttendingList().contains(AccountManager.currentUser.getId())){

                        //check if there empty spots (if not show cant join panel)
                        if(tournament.getMaxParticipants()!=-1 && tournament.getMaxParticipants() > tournament.getAttendingList().size()){

                            changeLayout("join");
                        }else{
                            changeLayout("cantjoin");
                        }
                    }else{
                        changeLayout("already_joined");
                    }
                }else{


                    if(tournament.getMaxParticipants() == 0){
                        changeLayout("cantjoin");
                    }else{

                        changeLayout("join");
                    }


                }

            }else{
                changeLayout("edit");
            }
        }else{
            changeLayout("login");

        }

        changeAvailableSpotsEditText();
    }

    private void changeLayout(String layoutName){


        if(layoutName.equals("join")){
            joinLayout.setVisibility(View.VISIBLE);
            cantJoinLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.GONE);
            loginFirstLayout.setVisibility(View.GONE);
            alreadJoinedLayout.setVisibility(View.GONE);

        }

        if(layoutName.equals("cantjoin")){
            joinLayout.setVisibility(View.GONE);
            cantJoinLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            loginFirstLayout.setVisibility(View.GONE);
            alreadJoinedLayout.setVisibility(View.GONE);
        }

        if(layoutName.equals("edit")){
            joinLayout.setVisibility(View.GONE);
            cantJoinLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
            loginFirstLayout.setVisibility(View.GONE);
            alreadJoinedLayout.setVisibility(View.GONE);
        }

        if(layoutName.equals("login")){
            joinLayout.setVisibility(View.GONE);
            cantJoinLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.GONE);
            loginFirstLayout.setVisibility(View.VISIBLE);
            alreadJoinedLayout.setVisibility(View.GONE);
        }

        if(layoutName.equals("already_joined")){
            joinLayout.setVisibility(View.GONE);
            cantJoinLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.GONE);
            loginFirstLayout.setVisibility(View.GONE);
            alreadJoinedLayout.setVisibility(View.VISIBLE);
        }
    }


    private void changeAvailableSpotsEditText(){
        if(tournament.getMaxParticipants() == -1){
            tournamentPlaces.setText(getString(R.string.unlimted_places));
        }else{

            if(tournament.getAttendingList() != null && !tournament.getAttendingList().isEmpty()){
                tournamentPlaces.setText(tournament.getMaxParticipants() - tournament.getAttendingList().size()+" "+getString(R.string.spots_left));
            }else{
                tournamentPlaces.setText(tournament.getMaxParticipants()+" "+getString(R.string.spots_left));
            }



        }
    }






}
