package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CityAdapter
        extends RecyclerView.Adapter<CityViewHolder>

{

    private List<Bundle> mCities;
    private Context mContext;

    public CityAdapter(List<Bundle> cities, Context context) {

        mCities = cities;
        mContext = context;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new CityViewHolder(view, mContext);
    }

    public void setNames(List<Bundle> cities) {
        mCities = cities;
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int pos) {

        holder.bindCity(mCities.get(pos));
    }

    @Override
    public int getItemCount() {
        if (mCities == null) {
            return 0;
        }
        return mCities.size();
    }
}