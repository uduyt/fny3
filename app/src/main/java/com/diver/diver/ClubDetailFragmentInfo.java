package com.diver.diver;

import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import backend.Analytics;
import backend.AskPromoCode;
import backend.ClubDetail;
import backend.SetClubsFull;
import backend.SetClubsIntros;
import jp.wasabeef.blurry.Blurry;

public class ClubDetailFragmentInfo extends Fragment implements ClubDetail {

    private View myView;
    private ObservableScrollView mScrollView;
    private ProgressBar progressBar;
    private View cover;
    ExpandableTextView expTv1;
    Bundle Club;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_club_detail_info, container, false);

        mScrollView = (ObservableScrollView) myView.findViewById(R.id.scrollView);
        progressBar= (ProgressBar) myView.findViewById(R.id.pb_club_detail);
        cover=myView.findViewById(R.id.v_club_detail_cover);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);

        // sample code snippet to set the text content on the ExpandableTextView
        expTv1 = (ExpandableTextView) myView.findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);

        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display


        expTv1.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if(isExpanded)
                    (myView.findViewById(R.id.v_gradient)).setVisibility(View.GONE);
                else
                    (myView.findViewById(R.id.v_gradient)).setVisibility(View.VISIBLE);
            }
        });


        (new SetClubsFull(this,Club, getActivity())).execute();
        SetPBVisibility(true);

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void SetPBVisibility(Boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
            cover.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            cover.setVisibility(View.GONE);
        }
    }

    @Override
    public void LoadData(Bundle club) {
        Club=club;
        expTv1.setText(Club.getString("club_info"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_map_direction)).setText(Club.getString("club_address"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_time)).setText(Club.getString("club_horario"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_ropero)).setText(Club.getString("club_ropero"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_price)).setText(Club.getString("club_precio"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_price_drink)).setText(Club.getString("club_precio_copa"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_price_vip)).setText(Club.getString("club_precio_botella"));
        ((TextView)myView.findViewById(R.id.tv_club_detail_seal)).setText(Club.getString("club_seal"));

        String url = "http://maps.googleapis.com/maps/api/staticmap?center="
                + Club.getString("club_lat") + "," + Club.getString("club_long") + "&markers=" + Club.getString("club_lat")
                + "," + Club.getString("club_long")+ "&zoom=16&size=1200x300&key=AIzaSyAImYMIPImjNwFDtkFSPxvFQehGnStpb-M";

        Log.v("urlmap", url);

        ImageView ivMap=(ImageView) myView.findViewById(R.id.iv_club_map);
        Picasso.with(getActivity()).load(url).into(ivMap);

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Club.getString("club_lat") + "," + Club.getString("club_long") + "(" + Club.getString("club_name") + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }


}