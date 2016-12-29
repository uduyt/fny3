package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by carlo on 20/11/2016.
 */
public class ClubAdapter
        extends RecyclerView.Adapter<ClubViewHolder> {

    private List<Bundle> mClubs;
    private Context mContext;

    public ClubAdapter(List<Bundle> clubs, Context context) {

        mClubs = clubs;
        mContext = context;
    }

    @Override
    public ClubViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club, parent, false);

        return new ClubViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(ClubViewHolder holder, int pos) {

        Bundle club = mClubs.get(pos);
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