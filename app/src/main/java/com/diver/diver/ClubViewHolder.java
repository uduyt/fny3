package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by carlo on 20/11/2016.
 */
public class ClubViewHolder extends RecyclerView.ViewHolder {
    private TextView tvClubName;
    private ImageView ivClub;
    private View mItemView;
    private Context mContext;
    private CardView cvClub;
    private Bundle mClub;

    public ClubViewHolder(View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
        tvClubName = (TextView) mItemView.findViewById(R.id.tv_club_name);
        ivClub=(ImageView) mItemView.findViewById(R.id.iv_club);
        cvClub=(CardView) mItemView.findViewById(R.id.cv_club);
    }

    public void bindClub(final Bundle club) {
        mClub = club;
        tvClubName.setText(club.getString("club_name"));
        Picasso.with(mContext).load("http://diverapp.es/clubs/images/club_intro_image" + mClub.getString("club_id") + ".jpg" ).into(ivClub);

        cvClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)mContext).GoToClubDetail(club);
            }
        });

    }
}
