package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GaleryPhotoAdapter
        extends RecyclerView.Adapter<GaleryPhotoViewHolder>

{

    private Bundle mClub;
    private Context mContext;

    public GaleryPhotoAdapter(Bundle club, Context context) {
        mClub = club;
        mContext = context;
    }

    @Override
    public GaleryPhotoViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_galery_photo, parent, false);

        return new GaleryPhotoViewHolder(view, mContext);
    }


    @Override
    public void onBindViewHolder(GaleryPhotoViewHolder holder, int pos) {

        holder.bindCity(mClub, pos);
    }

    @Override
    public int getItemCount() {

        return Integer.parseInt(mClub.getString("club_num_photos"));
    }
}