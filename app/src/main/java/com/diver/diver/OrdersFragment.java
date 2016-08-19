package com.diver.diver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

import backend.SetOrders;

public class OrdersFragment extends Fragment {
    private static Toolbar myToolbar;
    private MainActivity mainActivity;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;
    private List<Bundle> mOrders;

    private View myView;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_orders, container, false);

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

        mProgressBar = (ProgressBar) myView.findViewById(R.id.pb_orders);
        mRecyclerView = (RecyclerView) myView.findViewById(R.id.rv_orders);

        myView.findViewById(R.id.bt_no_orders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.GoToFragment("home",new Bundle());
            }
        });

        (new SetOrders(getActivity(),this)).execute();
        return myView;
    }

    public void LoadData(List<Bundle> orders) {
        mOrders = orders;

        mProgressBar.setVisibility(View.GONE);

        mAdapter = new OrderAdapter(mOrders,getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if(mOrders.size()==0)
            (myView.findViewById(R.id.ll_no_orders)).setVisibility(View.VISIBLE);
        else
            (myView.findViewById(R.id.ll_no_orders)).setVisibility(View.GONE);
    }

}
