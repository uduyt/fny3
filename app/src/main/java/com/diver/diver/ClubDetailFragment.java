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
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.github.florent37.materialviewpager.header.MaterialViewPagerImageHeader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import backend.Analytics;
import backend.AskPromoCode;
import backend.SetEventFull;
import jp.wasabeef.blurry.Blurry;

public class ClubDetailFragment extends Fragment {

    private View myView;

    private Toolbar myToolbar;

    Bundle Club;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_club_detail, container, false);


        if (savedInstanceState != null) {
            // Restore last state for checked position.
            Club = savedInstanceState;
        }

        DrawerLayout mDrawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //TabLayout mTabLayout = (TabLayout) myView.findViewById(R.id.tl_club_detail);
        final MaterialViewPager mViewPager = (MaterialViewPager) myView.findViewById(R.id.vp_club_detail);

        Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            ((MainActivity)getActivity()).setSupportActionBar(toolbar);

            ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("city",Club.getString("club_city"));
                    ((MainActivity) getActivity()).GoToFragment("clubs", bundle);
                }
            });

            toolbar.setTitle("");
        }

        /*
        mTabLayout.addTab(mTabLayout.newTab().setText("Información"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Galeria"));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);*/

        ViewPager viewPager = mViewPager.getViewPager();

        DetailPagerAdapter mAdapter = new DetailPagerAdapter
                (getActivity().getSupportFragmentManager(), Club, this);

        viewPager.setAdapter(mAdapter);

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final String url="http://www.diverapp.es/clubs/images/club_detail_header" + Club.getString("club_id") + ".jpg?q=" + Math.random();
        final String url2="http://www.diverapp.es/clubs/images/club_detail_logo" + Club.getString("club_id") + ".png?q=" + Math.random();

        mViewPager.setImageUrl(url, 2);

        Log.d("image_tag",url);

        Picasso.with(getActivity()).load(url)
                .into((ImageView)myView.findViewById(R.id.iv_club_detail_header));

        Picasso.with(getActivity()).load(url2)
                .into((ImageView)myView.findViewById(R.id.logo_white));

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        HeaderDesign headerDesign=HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                url);

                        return headerDesign;
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                               url);
                    default:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                               url);
                }

                //execute others actions if needed (ex : modify your header logo)

            }
        });

        return myView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState=Club;

        super.onSaveInstanceState(outState);
    }
}