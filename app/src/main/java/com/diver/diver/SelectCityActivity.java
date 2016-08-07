package com.diver.diver;

import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import backend.SetCities;

public class SelectCityActivity extends AppCompatActivity {
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        RecyclerView rvCities = (RecyclerView) findViewById(R.id.rv_cities);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);

        String provider = mLocationManager.getBestProvider(criteria, false);


        try {
            Location location=null;
            if(provider!=null){
                location= mLocationManager.getLastKnownLocation(provider);
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            if (location != null) {
                editor.putFloat("lat", Float.valueOf(String.valueOf(location.getLatitude())));
                editor.putFloat("long", Float.valueOf(String.valueOf(location.getLongitude())));
                editor.commit();
            }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,
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

        ProgressBar pbSelectCity = (ProgressBar) findViewById(R.id.pb_select_cities);
        CityAdapter mAdapter= new CityAdapter(new ArrayList<Bundle>(),this);
        SetCities setCities = new SetCities(this, rvCities, mAdapter, pbSelectCity);
        setCities.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
