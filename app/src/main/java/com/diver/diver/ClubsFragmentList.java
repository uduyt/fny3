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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import backend.Analytics;
import backend.City;
import backend.GetCity;
import backend.SetClubsIntros;
import backend.SetEventIntros;

public class ClubsFragmentList extends Fragment implements City {
    private static Toolbar myToolbar;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LocationManager mLocationManager;
    private View myView;
    private Bundle extras;
    private ClubAdapter mAdapter;
    private List<Bundle> mClubs;
    private String mCityId;
    private static final int REQUEST_COARSE_LOCATION = 0;
    ClubsFragment mParentFragment;

    public ClubsFragmentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_clubs_list, container, false);

        mainActivity = (MainActivity) getActivity();

        recyclerView = (RecyclerView) myView.findViewById(R.id.rv_clubs);
        progressBar = (ProgressBar) myView.findViewById(R.id.pb_clubs);

        extras = getArguments();

        if (!extras.getString("city", "none").equals("none"))
            SetClubsIntros(extras.getString("city", "1"));
        else {
            (new GetCity(this)).execute();
        }


        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!extras.getString("city", "none").equals("none"))
            SetClubsIntros(extras.getString("city", "1"));
        else {
            (new GetCity(this)).execute();
        }
    }


    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                } else {
                    // no granted
                }
                return;
            }

        }
    }

    @Override
    public void setCity(String city) {
        SetClubsIntros(city);

    }

    public void SetClubsIntros(String city) {
        (new SetClubsIntros(this)).execute(city);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setClubs(List<Bundle> clubs, String cityName, String cityId) {
        recyclerView = (RecyclerView) myView.findViewById(R.id.rv_clubs);
        progressBar = (ProgressBar) myView.findViewById(R.id.pb_clubs);

        progressBar.setVisibility(View.GONE);

        mParentFragment.setToolbarTitle(cityName);
        mCityId=cityId;
        mClubs = clubs;

        mAdapter = new ClubAdapter(mClubs, getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


}
