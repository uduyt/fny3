package com.diver.diver;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    private View myView;
    private Toolbar myToolbar;
    private ImageView ivQR, ivMap;
    private TextView tvLocalizer, tvClubName, tvDate;
    private Event mEvent;
    private Bundle mBundle;
    private DataAdapter mAdapter;
    private int mCheckedOption;
    private RadioButton rbTelefono, rbCorreo, rbFacebook;

    static ContactFragment GetInstance(Bundle bundle, Event event) {

        ContactFragment fragment = new ContactFragment();
        fragment.setmBundle(bundle);
        fragment.setmEvent(event);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_contact, container, false);

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        myToolbar.setTitle("Contacto");
        myToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

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

        TextView tvOrder = (TextView) myView.findViewById(R.id.tv_order);
        tvOrder.setText("Muchas gracias por reservar con Diver, " + Profile.getCurrentProfile().getFirstName() + ".\n\n" +
                "En breves momentos le contactará uno de nuestros relaciones públicas, \n\n" +
                "¿qué medio de contacto prefiere?");

        rbTelefono = (RadioButton) myView.findViewById(R.id.rb_telefono);
        rbFacebook = (RadioButton) myView.findViewById(R.id.rb_facebook);
        rbCorreo = (RadioButton) myView.findViewById(R.id.rb_correo);

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

        myView.findViewById(R.id.ll_telefono).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbTelefono.performClick();
            }
        });
        myView.findViewById(R.id.ll_correo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbCorreo.performClick();
            }
        });
        myView.findViewById(R.id.ll_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbFacebook.performClick();
            }
        });

        myView.findViewById(R.id.fab_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckedOption == rbFacebook.getId())
                    mBundle.putString("contact_method", "facebook");
                else if (mCheckedOption == rbCorreo.getId())
                    mBundle.putString("contact_method", "email");
                else
                    mBundle.putString("contact_method", "phone");

                if (mCheckedOption == rbFacebook.getId())
                    ((MainActivity) getActivity()).ShowSuccessDialog(mBundle, mEvent);
                else
                    ((MainActivity) getActivity()).ToEditContact(mBundle, mEvent);
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