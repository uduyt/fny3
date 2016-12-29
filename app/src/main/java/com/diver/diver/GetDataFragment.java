package com.diver.diver;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetDataFragment extends Fragment {
    private View myView;
    private Toolbar myToolbar;
    private ImageView ivQR, ivMap;
    private TextView tvLocalizer, tvClubName, tvDate;
    private Event mEvent;
    private Bundle mBundle;
    private DataAdapter mAdapter;

    static GetDataFragment GetInstance(Bundle bundle, Event event) {

        GetDataFragment fragment = new GetDataFragment();
        fragment.setmBundle(bundle);
        fragment.setmEvent(event);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_get_data, container, false);

        myToolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        myToolbar.setTitle("Rellena tus datos");
        myToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        //Load Data
        RecyclerView rvData = (RecyclerView) myView.findViewById(R.id.rv_fragment_data);
        mAdapter = new DataAdapter(mBundle, getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvData.setLayoutManager(mLayoutManager);
        rvData.setItemAnimator(new DefaultItemAnimator());
        rvData.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        View fabRight = myView.findViewById(R.id.fab_right);
        final TextView tvError = (TextView) myView.findViewById(R.id.tv_fragment_data_error);

        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (mAdapter.isError() == 2) {
                    tvError.setText("Una de las personas no tiene "
                            + mBundle.getString("max_years") + " años, no nos hacemos responsables de que no le " +
                            "dejen entrar en el local si no cumple la edad");

                    Snackbar.make(getActivity().findViewById(R.id.container), "Una de las personas no tiene "
                            + mBundle.getString("max_years") + " años, no nos hacemos responsables de que no le " +
                            "dejen entrar en el local si no cumple la edad", Snackbar.LENGTH_LONG).show();
                }
                else if (mAdapter.isError() == 1) {
                    tvError.setText("Por favor, rellene todos los campos");

                    Snackbar.make(getActivity().findViewById(R.id.container),"Por favor, rellene todos los campos", Snackbar.LENGTH_LONG).show();
                }else {
                    mBundle.putString("data", mAdapter.getData());
                    if (mBundle.getString("order_type").contains("pay")) {

                        ((MainActivity)getActivity()).ToPayment(mBundle,mEvent);

                    } else if (mBundle.getString("order_type").contains("contact")) {
                        ((MainActivity)getActivity()).ToContact(mBundle,mEvent);
                    } else {
                        ((MainActivity)getActivity()).ShowSuccessDialog(mBundle,mEvent);
                    }
                }
            }
        });
        //TODO GoToNext

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