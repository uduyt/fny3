package com.diver.diver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private Bundle extras;
    private ClubsFragment mFragment;

    public PagerAdapter(FragmentManager fm, String city, ClubsFragment fragment) {
        super(fm);
        extras=new Bundle();
        extras.putString("city",city);
        mFragment=fragment;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                ClubsFragmentList tab1 = new ClubsFragmentList();
                tab1.mParentFragment=mFragment;
                tab1.setArguments(extras);
                return tab1;
            case 1:
                ClubsFragmentMap tab2 = new ClubsFragmentMap();
                tab2.mParentFragment=mFragment;
                tab2.setArguments(extras);
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}