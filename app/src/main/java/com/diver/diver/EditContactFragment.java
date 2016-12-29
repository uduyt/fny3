package com.diver.diver;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.List;

public class EditContactFragment extends Fragment {
    private View myView;
    private Toolbar myToolbar;
    private ImageView ivQR, ivMap;
    private TextView tvLocalizer, tvClubName, tvDate;
    private Event mEvent;
    private Bundle mBundle;
    private DataAdapter mAdapter;
    private int mCheckedOption;

    static EditContactFragment GetInstance(Bundle bundle, Event event) {

        EditContactFragment fragment = new EditContactFragment();
        fragment.setmBundle(bundle);
        fragment.setmEvent(event);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_edit_contact, container, false);

        String hint;
        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mBundle.getString("contact_method").equals("phone")) {
            myToolbar.setTitle("Introduce tu número de móvil");
            hint = "número de móvil";
        } else {
            myToolbar.setTitle("Introduce tu correo electrónico");
            hint = "correo electrónico";
        }

        myToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        DrawerLayout mDrawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("event_id", String.valueOf(mEvent.getEventID()));
                ((MainActivity) getActivity()).ToContact(mBundle, mEvent);
            }
        });

        ((TextInputLayout) myView.findViewById(R.id.til_order)).setHint(hint);
        final EditText etContactData = (EditText) myView.findViewById(R.id.et_order_contact);
        myView.findViewById(R.id.fab_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etContactData.getText().toString().length() < 1) {
                    Snackbar.make(getActivity().findViewById(R.id.container), "Por favor, introduce los datos", Snackbar.LENGTH_LONG).show();
                }else{
                    mBundle.putString("contact_data",etContactData.getText().toString());
                    ((MainActivity) getActivity()).ShowSuccessDialog(mBundle, mEvent);
                }

            }
        });

        return myView;
    }

    public Event getmEvent() {
        return mEvent;
    }

    public void setmEvent(Event mEvent) {
        this.mEvent = mEvent;
    }

    public Bundle getmBundle() {
        return mBundle;
    }

    public void setmBundle(Bundle mBundle) {
        this.mBundle = mBundle;
    }


}