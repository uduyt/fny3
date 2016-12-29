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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import backend.Analytics;
import backend.AskPromoCode;
import backend.ClubDetail;
import backend.SetClubsFull;
import jp.wasabeef.blurry.Blurry;

public class ClubDetailFragmentGalery extends Fragment implements ClubDetail {

    private View myView;
    private ObservableScrollView mScrollView;
    private GaleryPhotoAdapter mAdapter;
    private RecyclerView recyclerView;
    Bundle Club;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_club_detail_galery, container, false);

        mScrollView = (ObservableScrollView) myView.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);

        (new SetClubsFull(this,Club, getActivity())).execute();



        return myView;
    }

    @Override
    public void LoadData(Bundle club) {
        Club=club;

        if(Integer.parseInt(Club.getString("club_num_photos"))==0){
            myView.findViewById(R.id.cv_club_detail_no_photos).setVisibility(View.VISIBLE);
        }else{
            myView.findViewById(R.id.cv_club_detail_no_photos).setVisibility(View.GONE);
        }

        mAdapter = new GaleryPhotoAdapter(Club, getActivity());

        recyclerView=(RecyclerView) myView.findViewById(R.id.rv_club_galery);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void SetPBVisibility(Boolean isVisible) {

    }
}