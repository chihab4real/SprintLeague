package com.example.sprintleague.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.User;
import com.example.sprintleague.Utils;
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
    private OnItemClickListener listener;
    private ValueEventListener valueEventListener;


    public TournamentAdapter(Context context, ArrayList<Tournament> tournamentList,  OnItemClickListener listener) {
        this.context = context;
        this.tournamentList = new ArrayList<>();
        for(Tournament t: tournamentList){
            if(Utils.isDateBiggerThanToday(t.getDateTime())){
                this.tournamentList.add(t);
            }
        }
        this.listener = listener;
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


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);


                if(user != null){
                    holder.organizerText.setText(user.getFirstName()+" "+user.getLastName());
                    if(user.getProfilePic().isEmpty()){
                        holder.profilePic.setImageResource(R.drawable.empty_profile_pic);

                    }else{
                        Picasso.get()
                                .load(user.getProfilePic())
                                .into(holder.profilePic);
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

        if(AccountManager.currentUser!= null &&tournament.getID().equals(AccountManager.currentUser.getId())){


                holder.organizerText.setText(AccountManager.currentUser.getFirstName()+" "+AccountManager.currentUser.getLastName());
                if(AccountManager.currentUser.getProfilePic().isEmpty()){
                    holder.profilePic.setImageResource(R.drawable.empty_profile_pic);

                }else{
                    Picasso.get()
                            .load(AccountManager.currentUser.getProfilePic())
                            .into(holder.profilePic);
                }




        }else{
            mDatabase.addValueEventListener(valueEventListener);
        }





        holder.titleText.setText(tournament.getTitle());
        holder.distanceText.setText(String.valueOf(tournament.getDistance())+" Km");

        String date_ = tournament.getDateTime().getDay()+"/"+tournament.getDateTime().getMonth()+"/"+tournament.getDateTime().getYear();
        String date_deadline = tournament.getJoinDeadline().getDay()+"/"+tournament.getJoinDeadline().getMonth()+"/"+tournament.getJoinDeadline().getYear();

        String time_ = tournament.getDateTime().getHour()+":"+tournament.getDateTime().getMinute()+" "+tournament.getDateTime().getAm_pm();
        String time_deadline = tournament.getJoinDeadline().getHour()+":"+tournament.getJoinDeadline().getMinute()+" "+tournament.getJoinDeadline().getAm_pm();

        if(Utils.is24HourFormat(context)){


            time_ = Utils.convert12HourTo24Hour(time_);
            time_deadline = Utils.convert12HourTo24Hour(time_deadline);
        }

        holder.dateTimeText.setText(date_+", "+time_);
        holder.dateTimeDeadlineText.setText(date_deadline+", "+time_deadline);

//        profilePic.setImageResource(tournament.getProfilePicResId());


        holder.itemView.setOnClickListener(v -> listener.onItemClick(tournament));


    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }



    public interface OnItemClickListener {
        void onItemClick(Tournament tournament);
    }

    // ViewHolder class for efficient view recycling
    public static class TournamentViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, dateTimeText, organizerText, distanceText,dateTimeDeadlineText;
        ImageView coverImage,profilePic;

        public TournamentViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.tournament_item_cover);
            titleText = itemView.findViewById(R.id.tournament_item_title);
            distanceText = itemView.findViewById(R.id.tournament_item_distance);
            dateTimeText = itemView.findViewById(R.id.tournament_item_date_time);
            dateTimeDeadlineText = itemView.findViewById(R.id.tournament_item_date_time_deadline);
             profilePic = itemView.findViewById(R.id.tournament_item_profile_pic);
             organizerText = itemView.findViewById(R.id.tournament_item_organizer);
        }
    }


}
