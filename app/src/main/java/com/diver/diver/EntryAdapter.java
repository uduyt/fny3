package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

public class EntryAdapter
        extends RecyclerView.Adapter<EntryViewHolder>

{

    private List<Bundle> mEntries;
    private Context mContext;
    private List<RadioButton> mRadioButtons;
    private Bundle mEntry;
    private EventDetailFragment mFragment;

    public EntryAdapter(List<Bundle> entries, Context context, EventDetailFragment fragment) {

        mEntries = entries;
        mContext = context;
        mRadioButtons= new ArrayList<>();
        mEntry= new Bundle();
        mFragment=fragment;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);

        return new EntryViewHolder(view, mContext, this);
    }

    public void setEntries(List<Bundle> entries) {
        mEntries = entries;
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int pos) {

        holder.bindEntry(mEntries.get(pos));
        if(pos+1==mEntries.size() & mEntry.getString("entry_id")!=null){
            mFragment.setEntry(mEntry);
            mFragment.UpdatePriceBoard();
        }


    }

    @Override
    public int getItemCount() {
        if (mEntries == null) {
            return 0;
        }
        return mEntries.size();
    }

    public void addRadioButton(RadioButton radioButton){
        mRadioButtons.add(radioButton);

        mRadioButtons.get(0).setChecked(true);
        mEntry=mEntries.get(0);
    }


    public void checkRadioButton(int i){
        for(RadioButton radioButton:mRadioButtons){
            radioButton.setChecked(false);
        }
        mRadioButtons.get(i).setChecked(true);
        mEntry=mEntries.get(i);

        if(mEntry.getString("entry_id")!=null){
            mFragment.setEntry(mEntry);
            mFragment.UpdatePriceBoard();
        }

    }

}