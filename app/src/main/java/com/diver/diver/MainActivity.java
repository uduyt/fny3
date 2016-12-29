package com.diver.diver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.internal.LoginAuthorizationType;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import backend.Analytics;
import backend.SendLists;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private static android.support.v7.widget.Toolbar myToolbar;
    private Context mContext = this;
    private ImageView ivHeader;
    private MaterialDialog dialog, editDialog;
    private int mCheckedOption;
    private SharedPreferences sharedPref;
    private RadioButton rbTelefono, rbFacebook, rbCorreo;
    private MaterialDialog.Builder dialogBuilder;
    private EditText etContact, etCardNumber, etCardDateMonth, etCardDateYear, etCardCVC;
    private TextView tvCardNumberError, tvCardRestError;
    private CheckBox cbCardSave;
    private TextInputLayout tilOrder;
    private Bundle mEntry;
    private Event mEvent;
    private MaterialDialog.SingleButtonCallback cancelCallback;
    private MaterialDialog progressDialog;
    private DataAdapter mAdapter;
    private int z = 0;
    private Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_COARSE_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());

        GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                try {

                    loadPermissions("android.permission.ACCESS_COARSE_LOCATION", REQUEST_COARSE_LOCATION);

                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putFloat("lat", Float.valueOf(String.valueOf(currentLocation.getLatitude())));
                            editor.putFloat("long", Float.valueOf(String.valueOf(currentLocation.getLongitude())));
                            editor.apply();
                        }
                    };

                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setFastestInterval(100)
                            .setInterval(200)
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                if (currentLocation != null) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putFloat("lat", Float.valueOf(String.valueOf(currentLocation.getLatitude())));
                    editor.putFloat("long", Float.valueOf(String.valueOf(currentLocation.getLongitude())));
                    editor.apply();

                    JSONObject jsonLocation = new JSONObject();

                    try {
                        jsonLocation.put("lat", String.valueOf(currentLocation.getLatitude()));
                        jsonLocation.put("long", String.valueOf(currentLocation.getLongitude()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    (new Analytics(mContext)).execute("location", "text", jsonLocation.toString());

                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };

        GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                SharedPreferences.Editor editor = sharedPref.edit();
                Log.i("connection_fail", connectionResult.toString());
            }
        };

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        if (navigationView.getMenu().findItem(R.id.nav_home) != null)
            navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ivHeader = (ImageView) header.findViewById(R.id.iv_header);

        Uri uri = Profile.getCurrentProfile().getProfilePictureUri(1000, 600);
        Picasso.with(this)
                .load(uri)
                .into(ivHeader);
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics analytics = new Analytics(mContext);
                analytics.execute("press_header_menu", "none");
            }
        });
        if (getIntent().getExtras() != null) {
            String fragment = getIntent().getExtras().getString("fragment", "clubs");
            Bundle extras = new Bundle();
            if (fragment.equals("home") | fragment.equals("clubs")) {
                extras.putString("city", getIntent().getExtras().getString("city", "none"));
            }
            GoToFragment(fragment, extras);
        } else {
            Bundle extras = new Bundle();
            extras.putString("city", "none");
            GoToFragment("clubs", extras);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof HomeFragment) {
                moveTaskToBack(true);
            } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof EventDetailFragment) {
                Bundle bundle = new Bundle();
                bundle.putString("city", ((EventDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getCityId());
                GoToFragment("home", bundle);
            } else if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof ClubDetailFragment) {
                Bundle bundle = new Bundle();
                bundle.putString("city", ((ClubDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container)).Club.getString("club_city"));
                GoToFragment("clubs", bundle);
            }
        }

    }

    public void GoToFragment(String fragment, Bundle extras) {


        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        switch (fragment) {
            case "home":
                HomeFragment mFragment = new HomeFragment();
                mFragment.setArguments(extras);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.replace(R.id.container, mFragment);
                fragmentTransaction.commit();
                break;
            case "clubs":
                ClubsFragment mClubsFragment = new ClubsFragment();
                mClubsFragment.setArguments(extras);
                FragmentTransaction clubsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                clubsFragmentTransaction.addToBackStack("home");
                clubsFragmentTransaction.replace(R.id.container, mClubsFragment);
                clubsFragmentTransaction.commit();
                break;

            case "event_detail":
                EventDetailFragment mDetailFragment = new EventDetailFragment();
                mDetailFragment.setArguments(extras);
                FragmentTransaction eventfragmentTransaction = getSupportFragmentManager().beginTransaction();
                eventfragmentTransaction.addToBackStack("event_detail");
                eventfragmentTransaction.replace(R.id.container, mDetailFragment);
                eventfragmentTransaction.commit();
                break;

            case "discotecas":

                break;
            case "orders":
                OrdersFragment mOrdersFragment = new OrdersFragment();
                mOrdersFragment.setArguments(extras);
                FragmentTransaction ordersFragmentTransaction = getSupportFragmentManager().beginTransaction();
                ordersFragmentTransaction.addToBackStack("orders");
                ordersFragmentTransaction.replace(R.id.container, mOrdersFragment);
                ordersFragmentTransaction.commit();
                break;
            case "order_detail":
                OrderDetailFragment mOrderDetailFragment = new OrderDetailFragment();
                mOrderDetailFragment.setArguments(extras);
                FragmentTransaction orderDetailFragmentTransaction = getSupportFragmentManager().beginTransaction();
                orderDetailFragmentTransaction.addToBackStack("order_detail");
                orderDetailFragmentTransaction.replace(R.id.container, mOrderDetailFragment);
                orderDetailFragmentTransaction.commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void SharedElementTransition(View view, String transitionName, int eventId) {
        EventDetailFragment mFragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putInt("event_id", eventId);
        mFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("event_detail");
        fragmentTransaction.addSharedElement(view, transitionName);
        fragmentTransaction.replace(R.id.container, mFragment);
        fragmentTransaction.commit();
    }

    public void GoToClubDetail(Bundle club){
        ClubDetailFragment mClubsFragment = new ClubDetailFragment();
        mClubsFragment.Club=club;
        FragmentTransaction clubsFragmentTransaction = getSupportFragmentManager().beginTransaction();
        clubsFragmentTransaction.addToBackStack("home");
        clubsFragmentTransaction.replace(R.id.container, mClubsFragment);
        clubsFragmentTransaction.commit();
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    List<Fragment> fragments= manager.getFragments();
                    if(fragments.size()==1) backStackEntryCount=0;
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount);
                    fragment.onResume();
                }
            }
        };
        return result;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Analytics analytics = new Analytics(mContext);
            analytics.execute("press_home_menu", "none");
            GoToFragment("home", new Bundle());
        } else if (id == R.id.nav_clubs) {
            Analytics analytics = new Analytics(mContext);
            analytics.execute("press_clubs_menu", "none");
            GoToFragment("clubs", new Bundle());
        }
        /*if (id == R.id.nav_my_orders) {
            Analytics analytics = new Analytics(mContext);
            analytics.execu
            te("press_my_entries", "none");
            GoToFragment("orders", new Bundle());

        }*/ else if (id == R.id.nav_select_city) {
            Analytics analytics = new Analytics(mContext);
            analytics.execute("press_select_city_menu", "none");
            Intent intent = new Intent(this, SelectCityActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            LogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void LogOut() {
        Analytics analytics = new Analytics(mContext);
        analytics.execute("log_out", "none");
        LoginManager.getInstance().logOut();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_email", "nulo");
        editor.apply();


        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public DrawerLayout getDrawerLayout() {
        return drawer;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }



    public void ShowSuccessDialog(final Bundle entry, Event event) {
        GoToFragment("home",new Bundle());
        mEntry=entry;
        mEvent=event;
        SendLists sendLists = new SendLists(this, mEntry);
        sendLists.execute();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void GetData(Bundle bundle, Event event){
        GetDataFragment mFragment= GetDataFragment.GetInstance(bundle,event);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("get_data");
        fragmentTransaction.replace(R.id.container, mFragment);
        fragmentTransaction.commit();
    }

    public void ToPayment(Bundle bundle, Event event){
        PaymentFragment mFragment= PaymentFragment.GetInstance(bundle,event);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("payment");
        fragmentTransaction.replace(R.id.container, mFragment);
        fragmentTransaction.commit();
    }

    public void ToContact(Bundle bundle, Event event){
        ContactFragment mFragment= ContactFragment.GetInstance(bundle,event);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("contact");
        fragmentTransaction.replace(R.id.container, mFragment);
        fragmentTransaction.commit();
    }

    public void ToEditContact(Bundle bundle, Event event){
        EditContactFragment mFragment= EditContactFragment.GetInstance(bundle,event);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("edit_contact");
        fragmentTransaction.replace(R.id.container, mFragment);
        fragmentTransaction.commit();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
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
/*
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }*/
}
