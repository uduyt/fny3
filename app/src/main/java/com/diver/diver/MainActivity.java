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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;
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
    private static final int REQUEST_COARSE_LOCATION=0;

    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)

            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION) //live
            .clientId("AShABEQ_UsfwJFzS_6ob5FPCJjenqCSFy5N4DmOkTKu_V0hWDgMPBjUH3UHJRB40mcua0cJ_jNVCLkzp"); //live

            /*.environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //sandbox
            .clientId("Ab4BTGKn8LXaRDm9qGfV68OwL4WZfCgyU4p6RjmijN0lIr5aFTBW3tzElTI-VHMXbuVLvRla4HcrU--j"); //sandbox*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //paypal
        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
        //*paypal

        GoogleApiClient.ConnectionCallbacks connectionCallbacks= new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                try {

                    loadPermissions("android.permission.ACCESS_COARSE_LOCATION",REQUEST_COARSE_LOCATION);

                    LocationListener locationListener= new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            SharedPreferences.Editor editor= sharedPref.edit();
                            editor.putFloat("lat", Float.valueOf(String.valueOf(currentLocation.getLatitude())));
                            editor.putFloat("long", Float.valueOf(String.valueOf(currentLocation.getLongitude())));
                            editor.apply();
                        }
                    };

                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setFastestInterval(100)
                                    .setInterval(200)
                                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest, locationListener);
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                if (currentLocation != null) {
                    SharedPreferences.Editor editor= sharedPref.edit();
                    editor.putFloat("lat", Float.valueOf(String.valueOf(currentLocation.getLatitude())));
                    editor.putFloat("long", Float.valueOf(String.valueOf(currentLocation.getLongitude())));
                    editor.apply();

                    JSONObject jsonLocation= new JSONObject();

                    try {
                        jsonLocation.put("lat",String.valueOf(currentLocation.getLatitude()));
                        jsonLocation.put("long",String.valueOf(currentLocation.getLongitude()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    (new Analytics(mContext)).execute("location","text",jsonLocation.toString());

                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };

        GoogleApiClient.OnConnectionFailedListener connectionFailedListener= new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                SharedPreferences.Editor editor= sharedPref.edit();
                Log.i("connection_fail",connectionResult.toString());
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
            String fragment = getIntent().getExtras().getString("fragment", "home");
            Bundle extras = new Bundle();
            if (fragment.equals("home")) {
                extras.putString("city", getIntent().getExtras().getString("city", "none"));
            }
            GoToFragment(fragment, extras);
        } else {
            Bundle extras = new Bundle();
            extras.putString("city", "none");
            GoToFragment("home", extras);
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
            case "event_detail":
                EventDetailFragment mEventFragment = new EventDetailFragment();
                mEventFragment.setArguments(extras);
                FragmentTransaction eventFragmentTransaction = getSupportFragmentManager().beginTransaction();
                eventFragmentTransaction.addToBackStack("event_detail");
                eventFragmentTransaction.replace(R.id.container, mEventFragment);
                eventFragmentTransaction.commit();
                break;

            case "discotecas":

                break;
            case "mis_entradas":

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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Analytics analytics = new Analytics(mContext);
            analytics.execute("press_home_menu", "none");
            GoToFragment("home", new Bundle());
        } else if (id == R.id.nav_select_city) {
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

    public void ShowOrderDialog(final Bundle entry, Event event) {

        mEntry = entry;
        mEvent = event;

        cancelCallback = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Analytics analytics = new Analytics(mContext);
                analytics.execute("cancel_order", "int", String.valueOf(mEvent.getEventID()));
                Snackbar.make(findViewById(R.id.container), "Se ha cancelado la reserva", Snackbar.LENGTH_LONG).show();
            }
        };

        if (mEntry.getString("order_type").contains("data")) {
            if (mEntry.getString("order_type").contains("contact"))
                ShowDataDialog("cancel", "contact", "");
            else
                ShowDataDialog("cancel", "success", "");
        } else if (mEntry.getString("order_type").contains("contact")) {
            ShowContactDialog("cancel", "success");
        } else if (mEntry.getString("order_type").contains("pay")) {
            Payment();
        } else {
            ShowSuccessDialog();
        }

    }

    public void ShowDataDialog(final String negative, final String positive, String error) {

        dialogBuilder = new MaterialDialog.Builder(mContext);
        dialogBuilder
                .customView(R.layout.dialog_data, false)
                .title("Introduce tus datos")
                .titleColorRes(R.color.White)
                .positiveText("confirmar")
                .backgroundColor(getResources().getColor(R.color.colorPrimary));

        if (negative.equals("cancel")) {
            dialogBuilder.negativeText("cancelar");
            dialogBuilder.onNegative(cancelCallback);
        } else if (negative.equals("contact")) {
            dialogBuilder.negativeText("atrás");
            ShowContactDialog("cancel", "data");
        }

        MaterialDialog.SingleButtonCallback onPositive = new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                if (mAdapter.isError() == 2)
                    ShowDataDialog(negative, positive, "Una de las personas no tiene "
                            + mEntry.getString("max_years") + " años, no nos hacemos responsables de que no le " +
                            "dejen entrar en el local si no cumple la edad");
                else if (mAdapter.isError() == 1)
                    ShowDataDialog(negative, positive, "Por favor, rellene todos los campos");
                else {
                    mEntry.putString("data", mAdapter.getData());
                    if (positive.equals("success"))
                        Payment();
                    else if (positive.equals("contact"))
                        ShowContactDialog("data", "success");
                }
            }
        };
        dialogBuilder.onPositive(onPositive);

        editDialog = dialogBuilder.build();
        editDialog.setCanceledOnTouchOutside(false);
        etContact = (EditText) editDialog.findViewById(R.id.et_order_contact);
        ((TextView) editDialog.findViewById(R.id.tv_data_error)).setText(error);

        RecyclerView rvData = (RecyclerView) editDialog.findViewById(R.id.rv_dialog_data);
        mAdapter = new DataAdapter(mEntry, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvData.setLayoutManager(mLayoutManager);
        rvData.setItemAnimator(new DefaultItemAnimator());
        rvData.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        editDialog.setCanceledOnTouchOutside(false);
        editDialog.show();

    }

    public void ShowContactDialog(final String negative, final String positive) {

        MaterialDialog dialog;

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .customView(R.layout.dialog_order, false)
                .positiveText("confirmar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        switch (mCheckedOption) {
                            case R.id.rb_telefono:
                                mEntry.putString("contact_method", "telefono");
                                break;
                            case R.id.rb_correo:
                                mEntry.putString("contact_method", "correo");
                                break;
                            case R.id.rb_facebook:
                                mEntry.putString("contact_method", "facebook");
                                break;
                        }
                        ShowEditDialog(negative, positive, "");
                    }
                })
                .backgroundColor(getResources().getColor(R.color.colorPrimary));

        if (negative.equals("cancel")) {
            builder.negativeText("cancelar");
        } else {
            builder.negativeText("atrás");
        }

        MaterialDialog.SingleButtonCallback onNegative = new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (negative.equals("cancel")) {
                    builder.onNegative(cancelCallback);
                } else if (negative.equals("data")) {
                    ShowDataDialog("cancel", "contact", "");
                } else if (negative.equals("pay")) {
                    Payment();
                }
            }
        };
        builder.onNegative(onNegative);
        dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);

        TextView tvOrder = (TextView) dialog.findViewById(R.id.tv_order);
        tvOrder.setText("Muchas gracias por reservar con Diver, " + Profile.getCurrentProfile().getFirstName() + ".\n\n" +
                "En breves momentos le contactará uno de nuestros relaciones públicas, \n\n" +
                "¿qué medio de contacto prefiere?");

        rbTelefono = (RadioButton) dialog.findViewById(R.id.rb_telefono);
        rbFacebook = (RadioButton) dialog.findViewById(R.id.rb_facebook);
        rbCorreo = (RadioButton) dialog.findViewById(R.id.rb_correo);

        mCheckedOption = rbTelefono.getId();

        final List<RadioButton> mRadioButtons = new ArrayList<>();
        mRadioButtons.add(rbTelefono);
        mRadioButtons.add(rbFacebook);
        mRadioButtons.add(rbCorreo);

        View.OnClickListener rbListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RadioButton rb : mRadioButtons) {
                    rb.setChecked(false);
                }
                ((RadioButton) view).setChecked(true);
                mCheckedOption = view.getId();
            }
        };
        for (RadioButton rb : mRadioButtons) {
            rb.setOnClickListener(rbListener);
        }

        dialog.findViewById(R.id.ll_telefono).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbTelefono.performClick();
            }
        });
        dialog.findViewById(R.id.ll_correo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbCorreo.performClick();
            }
        });
        dialog.findViewById(R.id.ll_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbFacebook.performClick();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void ShowEditDialog(final String negative, final String positive, String error) {
        if (mCheckedOption == rbCorreo.getId() | mCheckedOption == rbTelefono.getId()) {
            dialogBuilder = new MaterialDialog.Builder(mContext);
            dialogBuilder
                    .customView(R.layout.dialog_edit_contact, false)
                    .negativeText("atrás")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ShowContactDialog(negative, positive);
                        }
                    })
                    .positiveText("confirmar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mEntry.putString("contact_data", etContact.getText().toString());
                            if (mCheckedOption == rbCorreo.getId()) {
                                if (etContact.getText().toString().contains("@")) {
                                    onEditPositive(negative, positive);
                                } else {
                                    ShowEditDialog(negative, positive, "Correo electrónico inválido");
                                }

                            } else {
                                if (etContact.getText().toString().length() == 9) {
                                    onEditPositive(negative, positive);
                                } else {
                                    ShowEditDialog(negative, positive, "Número de teléfono inválido");
                                }
                            }
                        }
                    })
                    .titleColorRes(R.color.White)
                    .backgroundColor(getResources().getColor(R.color.colorPrimary));

            if (mCheckedOption == rbCorreo.getId()) {
                dialogBuilder.title("Introduce tu correo electrónico");
            } else {
                dialogBuilder.title("Introduce tu número de teléfono");
            }
            editDialog = dialogBuilder.build();
            editDialog.setCanceledOnTouchOutside(false);
            etContact = (EditText) editDialog.findViewById(R.id.et_order_contact);
            tilOrder = (TextInputLayout) editDialog.findViewById(R.id.til_order);
            if (!error.equals("")) {
                tilOrder.setErrorEnabled(true);
                tilOrder.setError(error);
            }
            if (mCheckedOption == rbCorreo.getId()) {
                String email = sharedPref.getString("user_email", "nulo");
                email = email.equals("nulo") ? "" : email;
                if (error.equals("")) etContact.setText(email);
                else etContact.setText("");

            } else {
                //TODO try get phone number
                etContact.setText("");
                etContact.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            editDialog.setCanceledOnTouchOutside(false);
            editDialog.show();

        } else {

            //TODO fill mEntry
            if (positive.equals("success")) {
                Payment();
            } else if (positive.equals("data")) {
                ShowDataDialog("contact", "success", "");
            } else if (positive.equals("success_no_pay")) {
                ShowSuccessDialog();
            }
        }
    }

    private void onEditPositive(String negative, String positive) {

        //TODO fill mEntry
        if (positive.equals("success")) {

            Payment();
        } else if (positive.equals("data")) {

            ShowDataDialog("contact", "success", "");
        } else if (positive.equals("success_no_pay")) {
            ShowSuccessDialog();
        }
    }

    private void Payment() {

        if (mEntry.getString("order_type").contains("pay")) {
            ShowPayOptionDialog();
        } else {
            ShowSuccessDialog();
        }
    }

    public void ShowSuccessDialog() {
        SendLists sendLists = new SendLists(this, mEntry);
        sendLists.execute();
    }

    public void ShowPayOptionDialog() {
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);

        final MaterialDialog dialog = dialogBuilder
                .customView(R.layout.dialog_payment_method, false)
                .negativeText("cancelar")
                .onNegative(cancelCallback)
                .title("Pago")
                .titleColorRes(R.color.White)
                .backgroundColorRes(R.color.colorPrimary)
                .build();

        if (!mEntry.getString("order_type").contains("optionalpay")) {
            dialog.findViewById(R.id.ll_choose_puerta).setVisibility(View.GONE);
        }
        if (sharedPref.getString("credit_card_number", null) == null) {
            dialog.findViewById(R.id.ll_credit_card).setVisibility(View.GONE);
        } else {
            ((TextView) dialog.findViewById(R.id.tv_card_number)).setText(sharedPref.getString("credit_card_number", "**** **** **** ****"));
            ((TextView) dialog.findViewById(R.id.tv_card_date)).setText(sharedPref.getString("credit_card_date_month", "**") +
                    "/" + sharedPref.getString("credit_card_date_year", "**"));
        }
        dialog.findViewById(R.id.ll_credit_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mEntry.putString("payment_method", "credit_card");
                PayCreditCard("old");
            }
        });
        dialog.findViewById(R.id.ll_add_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mEntry.putString("payment_method", "credit_card");
                PayCreditCard("new");
            }
        });
        dialog.findViewById(R.id.ll_choose_paypal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mEntry.putString("payment_method", "paypal");
                PayPalPay();
            }
        });
        dialog.findViewById(R.id.ll_choose_puerta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mEntry.putString("payment_method", "puerta");
                if (mEntry.getString("order_type").contains("optionalpaycont")) {
                    ShowContactDialog("pay", "success_no_pay");
                } else {
                    ShowSuccessDialog();
                }
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void PayCreditCard(final String type) {
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);
        final Card card = new Card("", 0, 0, "000");

        final MaterialDialog dialog = dialogBuilder
                .customView(R.layout.dialog_credit_card_pay, false)
                .negativeText("atrás")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Payment();
                    }
                })
                .positiveText("PAGAR")
                .title("Introduzca los datos de la tarjeta de crédito")
                .titleColorRes(R.color.White)
                .backgroundColorRes(R.color.colorPrimary)
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCardNumber.getText().toString().equals("") & etCardDateMonth.getText().toString().equals("")
                        & etCardDateYear.getText().toString().equals("") & etCardCVC.getText().toString().equals("")) {
                    tvCardRestError.setText("La tarjeta no es válida, por favor revisa todos los campos");
                } else {


                    card.setNumber(etCardNumber.getText().toString());
                    card.setCVC(etCardCVC.getText().toString());
                    card.setExpMonth(Integer.valueOf(etCardDateMonth.getText().toString()));
                    card.setExpYear(Integer.valueOf(etCardDateYear.getText().toString()));
                    if (!card.validateCard()) {
                        tvCardRestError.setText("La tarjeta no es válida, por favor revisa todos los campos");
                    } else {
                        dialog.dismiss();

                        progressDialog = new MaterialDialog.Builder(mContext)
                                .title("Espera porfavor")
                                .content("Procesando el pago...")
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .build();
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        if (cbCardSave.isChecked()) {
                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putString("credit_card_number", etCardNumber.getText().toString());
                            editor.putString("credit_card_date_month", etCardDateMonth.getText().toString());
                            editor.putString("credit_card_date_year", etCardDateYear.getText().toString());

                            editor.apply();
                        }

                        try {
                            Stripe stripe = new Stripe("pk_test_AQdkzKwecFMC8X7ax6XhXVNJ");
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Send token to your server
                                            progressDialog.dismiss();
                                            mEntry.putString("payment_token",token.getId());
                                            mEntry.putString("payment_last_4",token.getCard().getLast4());
                                            ShowSuccessDialog();
                                            Log.i("payment",token.toString());
                                            (new Analytics(mContext)).execute("stripe_success","text",token.getId());
                                        }
                                        public void onError(Exception error) {
                                            // Show localized error message
                                            Snackbar.make(findViewById(R.id.container), "Ha ocurrido un error, por favor inténtelo de nuevo o contacte con alguien de nuestro equipo", Snackbar.LENGTH_LONG).show();
                                            (new Analytics(mContext)).execute("stripe_error","text",error.toString());
                                        }
                                    }
                            );
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        etCardNumber = (EditText) dialog.findViewById(R.id.et_card_number);
        etCardDateMonth = (EditText) dialog.findViewById(R.id.et_card_date_month);
        etCardDateYear = (EditText) dialog.findViewById(R.id.et_card_date_year);
        etCardCVC = (EditText) dialog.findViewById(R.id.et_card_cvc);
        cbCardSave = (CheckBox) dialog.findViewById(R.id.cb_card_save);
        tvCardNumberError = (TextView) dialog.findViewById(R.id.tv_card_number_error);
        tvCardRestError = (TextView) dialog.findViewById(R.id.tv_card_rest_error);

        if (type.equals("old")) {
            etCardNumber.setText(sharedPref.getString("credit_card_number", ""));
            etCardDateMonth.setText(sharedPref.getString("credit_card_date_month", ""));
            etCardDateYear.setText(sharedPref.getString("credit_card_date_year", ""));
        }

        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 < i1) z = 1;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (z == 0) {
                    if (editable.toString().length() == 4) {
                        etCardNumber.setText(editable.toString() + "-");
                        etCardNumber.setSelection(etCardNumber.length());
                    }
                    if (editable.toString().length() == 9) {
                        etCardNumber.setText(editable.toString() + "-");
                        etCardNumber.setSelection(etCardNumber.length());
                    }
                    if (editable.toString().length() == 14) {
                        etCardNumber.setText(editable.toString() + "-");
                        etCardNumber.setSelection(etCardNumber.length());
                    }
                    if (editable.toString().length() == 19) {
                        etCardDateMonth.requestFocus();
                    }
                } else {
                    z = 0;
                }

            }
        });

        etCardDateMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 2) etCardDateYear.requestFocus();
            }
        });

        etCardDateYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 2) etCardCVC.requestFocus();
            }
        });

        etCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    card.setNumber(etCardNumber.getText().toString());
                    if (!card.validateNumber()) {
                        tvCardNumberError.setText("El número introducido no es válido");
                    } else {
                        tvCardNumberError.setText("");
                    }
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void PayPalPay() {
        onBuyPressed(String.valueOf(Integer.valueOf(mEntry.getString("price")) * Integer.valueOf(mEntry.getString("people"))));
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onBuyPressed(String amount) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "EUR", "Reserva de " + Profile.getCurrentProfile().getName() +
                " en " + mEvent.getName(),
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    mEntry.putString("payment_token",confirm.getProofOfPayment().getPaymentId());
                    if(confirm.getProofOfPayment().getState().equals("approved")){
                        ShowSuccessDialog();
                    }else{
                        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);

                        MaterialDialog dialog = dialogBuilder
                                .positiveText("ok")
                                .title("Error al pagar")
                                .titleColorRes(R.color.White)
                                .content("Lo sentimos, parece que ha habido un error en el pago. \n\nNo le deberíamos haber cobrado, si este fuera" +
                                        " el caso, póngase en contacto con nuestro equipo en cualquier correo de diver como pagos@diverapp.es")
                                .contentColorRes(R.color.White)
                                .backgroundColorRes(R.color.colorPrimary)
                                .build();

                        dialog.show();
                    }
                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
            Snackbar.make(findViewById(R.id.container), "Se ha cancelado la reserva", Snackbar.LENGTH_LONG).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            Snackbar.make(findViewById(R.id.container), "La cuenta de PayPal no es válida.\nSe ha cancelado la reserva.", Snackbar.LENGTH_LONG).show();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
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
                }
                else{
                    // no granted
                }
                return;
            }

        }

    }
}
