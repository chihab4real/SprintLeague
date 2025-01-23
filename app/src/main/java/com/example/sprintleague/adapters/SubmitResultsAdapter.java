package com.example.sprintleague.adapters;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.sprintleague.R;
import com.example.sprintleague.Result;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.User;
import com.example.sprintleague.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Locale;

public class SubmitResultsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> usersIDS;
    private ValueEventListener valueEventListener;

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    private ArrayList<Result> results;

    public SubmitResultsAdapter(Context context, ArrayList<String> usersIDS, String tID){
        this.context = context;

        if(usersIDS != null && !usersIDS.isEmpty()){
            this.usersIDS = usersIDS;
        }else{
            this.usersIDS = new ArrayList<>();
        }


        this.results = new ArrayList<>();

        if(usersIDS != null && !usersIDS.isEmpty()){
            for(String s: usersIDS){
                results.add(new Result(tID,s,"NA"));
            }
        }

    }
    @Override
    public int getCount() {

        return usersIDS.size();
    }

    @Override
    public Object getItem(int i) {
        return usersIDS.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SubmitResultsAdapter.ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_result_item, viewGroup, false);

            holder = new SubmitResultsAdapter.ViewHolder();

            holder.timing = view.findViewById(R.id.result_user_timing);
            holder.userProfile = view.findViewById(R.id.result_user_profile);
            holder.userFullName = view.findViewById(R.id.result_user_fullname);

            holder.save = view.findViewById(R.id.result_save);
            holder.edit = view.findViewById(R.id.result_edit);
            holder.hint = view.findViewById(R.id.result_hint);

            holder.timing.setInputType(InputType.TYPE_NULL);

            view.setTag(holder);


        }else{
            holder = (SubmitResultsAdapter.ViewHolder) view.getTag();
        }

        String item = usersIDS.get(i);


        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(item);


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);


                if(user != null){
                    holder.userFullName.setText(user.getFirstName()+" "+user.getLastName());
                    if(user.getProfilePic().isEmpty()){
                        holder.userProfile.setImageResource(R.drawable.empty_profile_pic);

                    }else{
                        Picasso.get()
                                .load(user.getProfilePic())
                                .into(holder.userProfile);
                    }

                    if (mDatabase != null && valueEventListener != null) {

                        mDatabase.removeEventListener(valueEventListener);
                    }



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDatabase.addValueEventListener(valueEventListener);

        holder.timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a custom dialog for setting hours, minutes, and seconds
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.dialog_time_picker, null);
                builder.setView(customView);

                // Find the NumberPickers in the custom layout
                NumberPicker hourPicker = customView.findViewById(R.id.hourPicker);
                NumberPicker minutePicker = customView.findViewById(R.id.minutePicker);
                NumberPicker secondPicker = customView.findViewById(R.id.secondPicker);

                // Set the range for each picker
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(23);

                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(59);

                secondPicker.setMinValue(0);
                secondPicker.setMaxValue(59);

                // Initialize default values
                hourPicker.setValue(0);
                minutePicker.setValue(0);
                secondPicker.setValue(0);

                // Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected values
                        int selectedHour = hourPicker.getValue();
                        int selectedMinute = minutePicker.getValue();
                        int selectedSecond = secondPicker.getValue();

                        // Format the selected time as HH:MM:SS
                        String duration = String.format(Locale.getDefault(), "%02d:%02d:%02d", selectedHour, selectedMinute, selectedSecond);
                        holder.timing.setText(duration);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                // Show the dialog
                builder.create().show();
            }
        });


        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                results.get(i).setTiming(holder.timing.getText().toString());

                holder.hint.setText(context.getString(R.string.saved));
                holder.hint.setTextColor(context.getColor(R.color.color_1));
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.timing.setText("");
                results.get(i).setTiming("NA");
                holder.hint.setText(context.getString(R.string.hint_result));
                holder.hint.setTextColor(context.getColor(R.color.gray_white));
            }
        });

        return view;
    }


    private static class ViewHolder{

        ImageView userProfile;
        TextView userFullName, hint;
        EditText timing;

        LinearLayout edit, save;

    }


}
