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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    private TextView tvOrderName, tvClubName, tvState, tvPeoplePrice, tvOrderDate;
    private LinearLayout llOrder;
    private ImageView ivOrder;

    public OrderViewHolder(View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;

        llOrder= (LinearLayout) mItemView.findViewById(R.id.ll_order);
        tvOrderName= (TextView) mItemView.findViewById(R.id.tv_order_name);
        tvClubName= (TextView) mItemView.findViewById(R.id.tv_order_club_name);
        tvState= (TextView) mItemView.findViewById(R.id.tv_order_state);
        tvOrderDate= (TextView) mItemView.findViewById(R.id.tv_order_date);
        tvPeoplePrice= (TextView) mItemView.findViewById(R.id.tv_order_people_price);
        ivOrder= (ImageView) mItemView.findViewById(R.id.iv_order_pic);
    }

    public void bindData(Bundle order, int pos, OrderAdapter adapter) {
        mOrder=order;
        Picasso.with(mContext).load("http://diverapp.es/clubs/images/" + mOrder.getString("event_id") + "event_image.jpg").into(ivOrder);
        tvOrderName.setText(mOrder.getString("event_name"));
        tvClubName.setText(mOrder.getString("club_name"));
        tvState.setText(mOrder.getString("order_state"));
        tvPeoplePrice.setText("x" + mOrder.getString("order_people") + " (" +
                Integer.valueOf(mOrder.getString("order_price"))*Integer.valueOf(mOrder.getString("order_people")) + "€)");
        llOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).GoToFragment("order_detail",mOrder);
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "es"));
        Date mDate=new Date();
        try {
             mDate= dateFormat.parse(mOrder.getString("event_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(adapter.isInDates(mDate))tvOrderDate.setVisibility(View.GONE);
        else{
            Calendar mDateCalendar=Calendar.getInstance();
            Calendar mDateCalendarTomorrow=Calendar.getInstance();
            mDateCalendar.setTime(mDate);
            mDateCalendarTomorrow=mDateCalendar;
            int day=mDateCalendar.get(Calendar.DAY_OF_YEAR);
            int today=Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if(day==today){
                //date is today
                tvOrderDate.setText("Hoy");
            }else if(day==today+1){
                //date is tomorrow
                tvOrderDate.setText("Mañana");
            }else{
                SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "es"));
                tvOrderDate.setText(mDateFormat.format(mDate));
            }
        }
    }
}