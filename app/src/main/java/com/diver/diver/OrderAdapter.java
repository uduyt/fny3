package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter
        extends RecyclerView.Adapter<OrderViewHolder>

{

    private Context mContext;
    private List<Bundle> mOrders;

    public OrderAdapter(List<Bundle> orders, Context context) {

        mOrders=orders;
        mContext = context;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_data, parent, false);

        return new OrderViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int pos) {

        holder.bindData(mOrders.get(pos),pos);
    }


    @Override
    public int getItemCount() {

        return mOrders.size();
    }
}