package com.diver.diver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import backend.Analytics;

public class GaleryPhotoViewHolder extends RecyclerView.ViewHolder {

    private View mItemView;
    private ImageView ivPhoto;
    private Context mContext;

    public GaleryPhotoViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
        ivPhoto = (ImageView) itemView.findViewById(R.id.iv_club_detail_photo_item);
    }

    public void bindCity(final Bundle club, int pos) {

        String url="http://www.diverapp.es/clubs/images/club_image_" + club.getString("club_id") + "_"
                + String.valueOf(pos+1) + ".jpg?q=" + String.valueOf(Math.random());

        Picasso.with(mContext).load(url).into(ivPhoto);


        Log.d("galery_tag",url);

    }
}