package com.diver.diver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import backend.Analytics;
import backend.City;
import backend.GetCity;
import backend.SetCities;
import backend.SetEventIntros;

public class ClubsFragment extends Fragment{
    private static Toolbar myToolbar;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LocationManager mLocationManager;
    private View myView;
    private Bundle extras;
    private Event.EventAdapter adapter;
    private static final int REQUEST_COARSE_LOCATION = 0;
    private String mCity;

    public ClubsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_clubs, container, false);

        mainActivity = (MainActivity) getActivity();

        extras=getArguments();

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        DrawerLayout mDrawerLayout = mainActivity.getDrawerLayout();

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, myToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            public void onDrawerOpened(View drawerView) {

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        LinearLayout llToolbarCity = (LinearLayout) myToolbar.findViewById(R.id.ll_toolbar_city);
        llToolbarCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics analytics = new Analytics(getActivity());
                analytics.execute("press_select_city_toolbar", "none");
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                getActivity().startActivity(intent);
            }
        });

        if (!extras.getString("city", "none").equals("none"))
            mCity = extras.getString("city", "none");
        else {
            mCity="none";
        }

        TabLayout mTabLayout = (TabLayout) myView.findViewById(R.id.tl_clubs);
        final ViewPager mViewPager = (ViewPager) myView.findViewById(R.id.vp_clubs);

        mTabLayout.addTab(mTabLayout.newTab().setText("Clubs"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Mapa"));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        PagerAdapter mAdapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), mCity, this);

        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return myView;
    }

    public void setToolbarTitle(String title){
        ((TextView) myView.findViewById(R.id.tv_city_title)).setText(title);
    }

}
