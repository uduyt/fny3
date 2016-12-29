package com.diver.diver;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import backend.Analytics;

public class PaymentFragment extends Fragment {
    private View myView;
    private Toolbar myToolbar;
    private ImageView ivQR, ivMap;
    private TextView tvLocalizer, tvClubName, tvDate;
    private Event mEvent;
    private Bundle mBundle;
    private DataAdapter mAdapter;
    private EditText etContact, etCardNumber, etCardDateMonth, etCardDateYear, etCardCVC;
    private TextView tvCardNumberError, tvCardRestError;
    private CheckBox cbCardSave;
    private SharedPreferences sharedPref;
    private int z;
    private MaterialDialog progressDialog;

    static PaymentFragment GetInstance(Bundle bundle, Event event) {

        PaymentFragment fragment = new PaymentFragment();
        fragment.setmBundle(bundle);
        fragment.setmEvent(event);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_payment, container, false);

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        myToolbar.setTitle("Realiza el pago");
        myToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("city", mEvent.getCityId());
                ((MainActivity) getActivity()).GoToFragment("home", bundle);
            }
        });

        DrawerLayout mDrawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("event_id", String.valueOf(mEvent.getEventID()));
                ((MainActivity) getActivity()).GoToFragment("home", bundle);
            }
        });

        if (mBundle.getString("order_type").contains("paydoor")) {
            myView.findViewById(R.id.ll_door_pay).setVisibility(View.VISIBLE);
        } else {
            myView.findViewById(R.id.ll_door_pay).setVisibility(View.GONE);
        }

        if (mBundle.getString("order_type").contains("payrrpp")) {
            myView.findViewById(R.id.ll_rrpp_pay).setVisibility(View.VISIBLE);
        } else {
            myView.findViewById(R.id.ll_rrpp_pay).setVisibility(View.GONE);
        }

        myView.findViewById(R.id.bt_door_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBundle.putString("payment_method", "door");
                ((MainActivity) getActivity()).ShowSuccessDialog(mBundle, mEvent);
            }
        });

        myView.findViewById(R.id.bt_rrpp_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBundle.putString("payment_method", "rrpp");
                ((MainActivity) getActivity()).ToContact(mBundle, mEvent);
            }
        });

        //Payment
        final Card card = new Card("", 0, 0, "000");

        etCardNumber = (EditText) myView.findViewById(R.id.et_card_number);
        etCardDateMonth = (EditText) myView.findViewById(R.id.et_card_date_month);
        etCardDateYear = (EditText) myView.findViewById(R.id.et_card_date_year);
        etCardCVC = (EditText) myView.findViewById(R.id.et_card_cvc);
        cbCardSave = (CheckBox) myView.findViewById(R.id.cb_card_save);
        tvCardNumberError = (TextView) myView.findViewById(R.id.tv_card_number_error);
        tvCardRestError = (TextView) myView.findViewById(R.id.tv_card_rest_error);


        etCardNumber.setText(sharedPref.getString("credit_card_number", ""));
        etCardDateMonth.setText(sharedPref.getString("credit_card_date_month", ""));
        etCardDateYear.setText(sharedPref.getString("credit_card_date_year", ""));

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

        myView.findViewById(R.id.bt_stripe_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new MaterialDialog.Builder(getActivity())
                        .title("Espera porfavor")
                        .content("Procesando el pago...")
                        .progress(true, 0)
                        .progressIndeterminateStyle(false)
                        .build();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (etCardNumber.getText().toString().equals("") & etCardDateMonth.getText().toString().equals("")
                        & etCardDateYear.getText().toString().equals("") & etCardCVC.getText().toString().equals("")) {
                    tvCardRestError.setText("Por favor, rellena todos los campos");
                } else {

                    mBundle.putString("payment_method", "stripe_try");

                    card.setNumber(etCardNumber.getText().toString());
                    card.setCVC(etCardCVC.getText().toString());
                    card.setExpMonth(Integer.valueOf(etCardDateMonth.getText().toString()));
                    card.setExpYear(Integer.valueOf(etCardDateYear.getText().toString()));
                    if (!card.validateCard()) {
                        tvCardRestError.setText("La tarjeta no es válida, por favor revisa todos los campos");
                    }

                    try {
                        Stripe stripe = new Stripe("pk_live_RjlyNa6HeWEP3aS4uN7Z9VAH ");
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {

                                        if (cbCardSave.isChecked()) {
                                            SharedPreferences.Editor editor = sharedPref.edit();

                                            editor.putString("credit_card_number", etCardNumber.getText().toString());
                                            editor.putString("credit_card_date_month", etCardDateMonth.getText().toString());
                                            editor.putString("credit_card_date_year", etCardDateYear.getText().toString());

                                            editor.apply();
                                        }


                                        // Send token to your server
                                        progressDialog.dismiss();
                                        mBundle.putString("payment_token", token.getId());
                                        mBundle.putString("payment_last_4", token.getCard().getLast4());
                                        //ShowSuccessDialog();
                                        Log.i("payment", token.toString());
                                        (new Analytics(getActivity())).execute("stripe_success", "text", token.getId());
                                        mBundle.putString("payment_method", "stripe");
                                        ((MainActivity) getActivity()).ShowSuccessDialog(mBundle, mEvent);
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        progressDialog.dismiss();
                                        Snackbar.make(getActivity().findViewById(R.id.container), "No se ha podido procesar el pago ya que la tarjeta introducida no es válida, por favor, revisa los datos", Snackbar.LENGTH_LONG).show();
                                        (new Analytics(getActivity())).execute("stripe_error", "text", error.toString());
                                    }
                                }
                        );
                    } catch (AuthenticationException e) {
                        progressDialog.dismiss();
                        Snackbar.make(getActivity().findViewById(R.id.container), "Ha ocurrido un error, por favor inténtelo de nuevo o contacte con alguien de nuestro equipo", Snackbar.LENGTH_LONG).show();
                        (new Analytics(getActivity())).execute("stripe_error", "text", "authentication exception: " + e.toString());
                        e.printStackTrace();
                    }
                }

                }

            }

            );
            return myView;
        }

        public Event getmEvent () {
            return mEvent;
        }

        public void setmEvent (Event mEvent){
            this.mEvent = mEvent;
        }

        public Bundle getmBundle () {
            return mBundle;
        }

        public void setmBundle (Bundle mBundle){
            this.mBundle = mBundle;
        }

    }