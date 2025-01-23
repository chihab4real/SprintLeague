package com.example.sprintleague.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.sprintleague.R;
import com.example.sprintleague.Tournament;
import com.example.sprintleague.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyTournamentsListAdapter extends BaseAdapter {

    private Context context;
    private List<Tournament> tournaments;

    public MyTournamentsListAdapter(Context context, List<Tournament> tournaments) {
        this.context = context;
        this.tournaments = tournaments;
    }

    @Override
    public int getCount() {
        return tournaments.size();
    }

    @Override
    public Object getItem(int i) {
        return tournaments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.my_tournaments_item, viewGroup, false);

            holder = new ViewHolder();

            holder.coverImage = view.findViewById(R.id.tournament_item_cover);
            holder.title = view.findViewById(R.id.tournament_item_title);
            holder.distance = view.findViewById(R.id.tournament_item_distance);
            holder.dateTime = view.findViewById(R.id.tournament_item_date_time);
            holder.status = view.findViewById(R.id.status);


            view.setTag(holder);


        }else{
            holder = (ViewHolder) view.getTag();
        }



        Tournament item = tournaments.get(i);


        if(!item.getTournamentCoverLink().isEmpty()){
            Picasso.get().load(item.getTournamentCoverLink()).into(holder.coverImage);
        }else{
            holder.coverImage.setImageResource(R.drawable.tournament_sample);
        }

        holder.title.setText(item.getTitle());
        holder.distance.setText(String.valueOf(item.getDistance())+" Km");

        String date_ = item.getDateTime().getDay()+"/"+item.getDateTime().getMonth()+"/"+item.getDateTime().getYear();

        String time_ = item.getDateTime().getHour()+":"+item.getDateTime().getMinute()+" "+item.getDateTime().getAm_pm();

        if(Utils.is24HourFormat(context)){


            time_ = Utils.convert12HourTo24Hour(time_);
        }

        holder.dateTime.setText(date_+", "+time_);

        if(item.isResultsPosted()){
            holder.status.setText("Results are Ready");
        }else{

            if(!Utils.isDateBiggerThanToday(item.getDateTime())){
                holder.status.setText("Waiting for organizer to provide results");
            }else{
                holder.status.setText("Still Open");
            }

        }

        return view;
    }


    private static class ViewHolder{
        CardView cardView;
        ImageView coverImage;
        TextView title;
        TextView distance;
        TextView dateTime;
        TextView status;
    }
}
