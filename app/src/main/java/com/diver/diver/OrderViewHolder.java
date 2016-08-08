package com.diver.diver;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;
    private View mItemView;
    private Bundle mOrder;
    private TextView tvOrderName, tvClubName, tvState, tvPeoplePrice, tvDate;
    private LinearLayout llOrder;

    public OrderViewHolder(View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;

        llOrder= (LinearLayout) mItemView.findViewById(R.id.ll_order);
        tvOrderName= (TextView) mItemView.findViewById(R.id.tv_order_name);
        tvClubName= (TextView) mItemView.findViewById(R.id.tv_order_club_name);
        tvState= (TextView) mItemView.findViewById(R.id.tv_order_state);
        tvPeoplePrice= (TextView) mItemView.findViewById(R.id.tv_order_people_price);
        tvDate= (TextView) mItemView.findViewById(R.id.tv_order_date);

    }

    public void bindData(Bundle order, int pos) {
        mOrder=order;

        tvOrderName.setText(mOrder.getString("event_name"));
        tvClubName.setText(mOrder.getString("club_name"));
        tvState.setText(mOrder.getString("order_state"));
        tvPeoplePrice.setText("x" + mOrder.getString("order_people") + " (" +
                Integer.valueOf(mOrder.getString("order_price"))*Integer.valueOf(mOrder.getString("order_people")) + "€)");

        SimpleDateFormat dateFormat = new SimpleDateFormat("d   EEE", new Locale("es", "es"));
        String date=(dateFormat.format(java.sql.Date.valueOf(mOrder.getString("order_date")))).toUpperCase();
        tvDate.setText(date.substring(0,date.length()-1));
    }
}