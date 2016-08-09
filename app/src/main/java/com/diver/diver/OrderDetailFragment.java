package com.diver.diver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import backend.Analytics;
import backend.AskPromoCode;
import backend.SetEventFull;

public class OrderDetailFragment extends Fragment {
private View myView;
    private Bundle mOrder;
    private Toolbar myToolbar;
    private ImageView ivQR,  ivMap;
    private TextView tvLocalizer, tvClubName, tvDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mOrder=getArguments();

        myView = inflater.inflate(R.layout.fragment_order_detail, container, false);

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout mDrawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).GoToFragment("orders", new Bundle());
            }
        });

        //set Views and Layouts
        ivQR=(ImageView) myView.findViewById(R.id.iv_order_detail_qr);
        ivMap=(ImageView) myView.findViewById(R.id.iv_order_detail_static_map);
        tvLocalizer=(TextView) myView.findViewById(R.id.tv_order_detail_localizer);
        tvClubName=(TextView) myView.findViewById(R.id.tv_order_detail_club_name);
        tvDate=(TextView) myView.findViewById(R.id.tv_order_detail_date);
        //*set Views and Layouts

        //Load Data
        tvLocalizer.setText("Localizador: " + mOrder.getString("order_localizer"));
        tvClubName.setText(mOrder.getString("club_name"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "es"));
        Date mDate=new Date();
        try {
            mDate= dateFormat.parse(mOrder.getString("event_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "es"));
        tvDate.setText(mDateFormat.format(mDate));
        String url = "http://maps.googleapis.com/maps/api/staticmap?center="
                + mOrder.getString("club_lat") + "," + mOrder.getString("club_long") + "&markers="
                + mOrder.getString("club_lat") + "," + mOrder.getString("club_long")
                + "&zoom=16&size=1200x300&key=AIzaSyAImYMIPImjNwFDtkFSPxvFQehGnStpb-M";

        Log.v("urlmap", url);
        Picasso.with(getActivity()).load(url).into(ivMap);

        url = "https://chart.googleapis.com/chart?cht=qr&chs=545x545&chld=H|0&chl="
                + mOrder.getString("order_localizer");

        Log.v("urlqr", url);
        Picasso.with(getActivity()).load(url).into(ivQR);

        return myView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

}