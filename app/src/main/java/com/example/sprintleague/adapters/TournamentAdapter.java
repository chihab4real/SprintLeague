package com.example.sprintleague.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder> {

    private Context context;
    private ArrayList<Tournament> tournamentList;


    public TournamentAdapter(Context context, ArrayList<Tournament> tournamentList) {
        this.context = context;
        this.tournamentList = tournamentList;
    }

    @NonNull
    @Override
    public TournamentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.tournament_item, parent, false);
        return new TournamentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TournamentViewHolder holder, int position) {
        // Get the current tournament
        Tournament tournament = tournamentList.get(position);

        if(tournament.getTournamentCoverLink().isEmpty()){
            holder.coverImage.setImageResource(R.drawable.tournament_sample);
        }else{
            Picasso.get()
                    .load(tournament.getTournamentCoverLink())
                    .into(holder.coverImage);
        }

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(tournament.getOrganizerID());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


                holder.organizerText.setText(user.getFirstName()+" "+user.getLastName());
                if(user.getProfilePic().isEmpty()){
                    holder.profilePic.setImageResource(R.drawable.empty_profile_pic);

                }else{
                    Picasso.get()
                            .load(user.getProfilePic())
                            .into(holder.profilePic);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.titleText.setText(tournament.getTitle());
        holder.distanceText.setText(String.valueOf(tournament.getDistance())+" Km");
        String date_ = tournament.getDateTime().getDay()+"/"+tournament.getDateTime().getMonth()+"/"+tournament.getDateTime().getYear();
        String time_ = tournament.getDateTime().getHour()+":"+tournament.getDateTime().getMinute()+" "+tournament.getDateTime().getAm_pm();
        holder.dateTimeText.setText(date_+", "+time_);

//        profilePic.setImageResource(tournament.getProfilePicResId());



    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }




    // ViewHolder class for efficient view recycling
    public static class TournamentViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, dateTimeText, organizerText, distanceText;
        ImageView coverImage,profilePic;

        public TournamentViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.tournament_item_cover);
            titleText = itemView.findViewById(R.id.tournament_item_title);
            distanceText = itemView.findViewById(R.id.tournament_item_distance);
            dateTimeText = itemView.findViewById(R.id.tournament_item_date_time);
             profilePic = itemView.findViewById(R.id.tournament_item_profile_pic);
             organizerText = itemView.findViewById(R.id.tournament_item_organizer);
        }
    }


}
