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

public class SponsorAdapter extends RecyclerView.Adapter<SponsorAdapter.SponsorViewHolder> {
    private List<Sponsor> sponsors;
    private Context context;
    private OnSponsorDeleteListener deleteListener;

    public interface OnSponsorDeleteListener {
        void onDelete(int position);
    }

    public SponsorAdapter(Context context, List<Sponsor> sponsors, OnSponsorDeleteListener listener) {
        this.context = context;
        this.sponsors = sponsors;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public SponsorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sponsor, parent, false);
        return new SponsorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SponsorViewHolder holder, int position) {
        Sponsor sponsor = sponsors.get(position);
        holder.sponsorName.setText(sponsor.getSponsorName());
        if(sponsor.getSponsorImgLink() != null && !sponsor.getSponsorImgLink().isEmpty()){
            Picasso.get().load(sponsor.getSponsorImgLink()).into(holder.sponsorImage);
        }else{
            holder.sponsorImage.setImageURI(sponsor.getSponsorImgUri());
        }


        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sponsors.size();
    }

    static class SponsorViewHolder extends RecyclerView.ViewHolder {
        ImageView sponsorImage;
        TextView sponsorName;
        ImageView deleteButton;

        public SponsorViewHolder(@NonNull View itemView) {
            super(itemView);
            sponsorImage = itemView.findViewById(R.id.sponsor_image);
            sponsorName = itemView.findViewById(R.id.sponsor_name);
            deleteButton = itemView.findViewById(R.id.sponsor_delete);
        }
    }


    public void removeSponsor(int position) {
        if (position >= 0 && position < sponsors.size()) {
            sponsors.remove(position);
            notifyDataSetChanged();  // Use notifyDataSetChanged instead of notifyItemRemoved for safety
        }
    }
}