package com.diver.diver;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Club {
    private int ClubID;
    private Drawable Icon;
    private String Name;
    private String Description;

    public int getClubID() {
        return ClubID;
    }

    public Club(Context context) {
        ClubID = 0;
        Name = "Discoteca de rueba";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        private com.diver.diver.Club mClub;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvPrice;
        private View mItemView;

        public ClubViewHolder(View itemView, Context context) {
            super(itemView);
            mItemView = itemView;
        }

        public void bindClub(com.diver.diver.Club club) {
            mClub = club;

        }
    }


    public static class ClubAdapter
            extends RecyclerView.Adapter<ClubViewHolder>

    {

        private List<com.diver.diver.Club> mClubs;
        private Context mContext;

        public ClubAdapter(List<com.diver.diver.Club> clubs, Context context) {

            mClubs = clubs;
            mContext = context;
        }

        @Override
        public ClubViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);

            return new ClubViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(com.diver.diver.Club.ClubViewHolder holder, int pos) {

            com.diver.diver.Club club = mClubs.get(pos);
            holder.bindClub(club);
        }

        @Override
        public int getItemCount() {
            if (mClubs == null) {
                return 0;
            }
            return mClubs.size();
        }
    }
}
