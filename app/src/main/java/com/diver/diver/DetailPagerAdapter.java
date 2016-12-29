package com.diver.diver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DetailPagerAdapter extends FragmentStatePagerAdapter {
    private Bundle mClub;
    private ClubDetailFragment mFragment;

    public DetailPagerAdapter(FragmentManager fm, Bundle club, ClubDetailFragment fragment) {
        super(fm);
        mFragment=fragment;
        mClub=club;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                ClubDetailFragmentInfo tab1 = new ClubDetailFragmentInfo();
                tab1.Club=mClub;
                return tab1;
            case 1:
                ClubDetailFragmentGalery tab2 = new ClubDetailFragmentGalery();
                tab2.Club=mClub;
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Información";
            case 1:
                return "Galeria";
        }
        return "";
    }

    @Override
    public int getCount() {
        return 2;
    }
}