package com.example.sprintleague.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintleague.R;
import com.example.sprintleague.Sponsor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TournamentSponsorAdapter extends RecyclerView.Adapter<TournamentSponsorAdapter.TournamentSposnorViewHolder> {

    private List<Sponsor> sponsors;
    private Context context;


    public TournamentSponsorAdapter(Context context, List<Sponsor> sponsors){
        this.context = context;
        this.sponsors = sponsors;

    }

    @NonNull
    @Override
    public TournamentSponsorAdapter.TournamentSposnorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tournament_sponsor, parent, false);
        return new TournamentSponsorAdapter.TournamentSposnorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentSponsorAdapter.TournamentSposnorViewHolder holder, int position) {
        Sponsor sponsor = sponsors.get(position);
        holder.sponsorName.setText(sponsor.getSponsorName());
        Picasso.get().load(sponsor.getSponsorImgLink()).into(holder.sponsorImage);
//        holder.sponsorImage.setImageURI(sponsor.getSponsorImgUri());


    }

    @Override
    public int getItemCount() {
        return sponsors.size();
    }

    static class TournamentSposnorViewHolder extends RecyclerView.ViewHolder {
        ImageView sponsorImage;
        TextView sponsorName;


        public TournamentSposnorViewHolder(@NonNull View itemView) {
            super(itemView);
            sponsorImage = itemView.findViewById(R.id.sponsor_image);
            sponsorName = itemView.findViewById(R.id.sponsor_name);

        }
    }

}

