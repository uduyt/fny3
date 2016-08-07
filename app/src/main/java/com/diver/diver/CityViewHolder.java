package com.diver.diver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import backend.Analytics;

public class CityViewHolder extends RecyclerView.ViewHolder {
    private String Name;
    private TextView tvName;
    private LinearLayout llItemCity;
    private View mItemView;
    private Context mContext;

    public CityViewHolder(View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
        tvName = (TextView) itemView.findViewById(R.id.tv_city_name);
        llItemCity = (LinearLayout) itemView.findViewById(R.id.ll_item_city);
    }

    public void bindCity(final Bundle city) {
        Name = city.getString("name");
        tvName.setText(city.getString("name"));
        llItemCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics analytics = new Analytics(mContext);
                analytics.execute("press_city_selected","text",city.getString("name"));
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("city", city.getString("id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }
}