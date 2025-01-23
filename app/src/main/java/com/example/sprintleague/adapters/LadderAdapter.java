package com.example.sprintleague.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintleague.AccountManager;
import com.example.sprintleague.R;
import com.example.sprintleague.Result;
import com.example.sprintleague.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> {

    private List<Result> userRankList;

    private ValueEventListener valueEventListener;

    public LadderAdapter(List<Result> userRankList) {
        this.userRankList = userRankList;

        Collections.sort(this.userRankList, new Comparator<Result>() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            @Override
            public int compare(Result r1, Result r2) {
                try {
                    long time1 = sdf.parse(r1.getTiming()).getTime();
                    long time2 = sdf.parse(r2.getTiming()).getTime();
                    return Long.compare(time1, time2);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid timing format", e);
                }
            }
        });
    }

    @NonNull
    @Override
    public LadderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ladder_item, parent, false);
        return new LadderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LadderViewHolder holder, int position) {
        Result result = userRankList.get(position);

        // Bind the user rank data to the view
        holder.rankTextView.setText(String.valueOf(position+1));
        holder.usernameTextView.setText(result.getUserID());
        holder.scoreTextView.setText(result.getTiming());

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(result.getUserID());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);


                if(user != null){
                    holder.usernameTextView.setText(user.getFirstName()+" "+user.getLastName());
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

        if(AccountManager.currentUser!= null &&result.getUserID().equals(AccountManager.currentUser.getId())){


            holder.usernameTextView.setText(AccountManager.currentUser.getFirstName()+" "+AccountManager.currentUser.getLastName());
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


    }

    @Override
    public int getItemCount() {
        return userRankList.size();
    }

    public static class LadderViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, usernameTextView, scoreTextView;
        ImageView profilePic;

        public LadderViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            profilePic = itemView.findViewById(R.id.tournament_item_profile_pic);

        }
    }
}
