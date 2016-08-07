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

import backend.Analytics;
import backend.GetCity;
import backend.SetEventIntros;

public class HomeFragment extends Fragment {
    private static android.support.v7.widget.Toolbar myToolbar;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LocationManager mLocationManager;
    private View myView;
    private Bundle extras;
    private Event.EventAdapter adapter;
    private static final int REQUEST_COARSE_LOCATION = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_home, container, false);

        mainActivity = (MainActivity) getActivity();

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

        recyclerView = (RecyclerView) myView.findViewById(R.id.rv_events);
        progressBar = (ProgressBar) myView.findViewById(R.id.pb_events);
        adapter = new Event.EventAdapter(new ArrayList<Event>(), getActivity());
        extras = getArguments();

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        loadPermissions("android.permission.ACCESS_COARSE_LOCATION", REQUEST_COARSE_LOCATION);

        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);

        String provider = mLocationManager.getBestProvider(criteria, false);
        try {
            Location location = null;
            if (provider != null) {
                location = mLocationManager.getLastKnownLocation(provider);
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            if (location != null) {
                editor.putFloat("lat", Float.valueOf(String.valueOf(location.getLatitude())));
                editor.putFloat("long", Float.valueOf(String.valueOf(location.getLongitude())));
                editor.commit();

                JSONObject jsonLocation= new JSONObject();

                try {
                    jsonLocation.put("lat",String.valueOf(location.getLatitude()));
                    jsonLocation.put("long",String.valueOf(location.getLongitude()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                (new Analytics(getActivity())).execute("location","text",jsonLocation.toString());
            }

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000,
                    1000, new LocationListener() {
                        @Override
                        public void onLocationChanged(final Location location) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            if (location != null) {
                                editor.putFloat("lat", Float.valueOf(String.valueOf(location.getLatitude())));
                                editor.putFloat("long", Float.valueOf(String.valueOf(location.getLongitude())));
                                editor.apply();
                            }
                        }
//hi git
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (!extras.getString("city", "none").equals("none"))
            SetEventIntros(extras.getString("city", "1"));
        else {
            (new GetCity(this)).execute();
        }

        return myView;
    }

    public void SetEventIntros(String city) {
        SetEventIntros setEvents = new SetEventIntros(getActivity(), recyclerView, adapter, progressBar, myView);
        setEvents.execute(city);
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
}
