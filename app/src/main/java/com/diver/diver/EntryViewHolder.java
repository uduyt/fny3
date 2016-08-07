package com.diver.diver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import backend.Analytics;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName, tvDescription;
    private LinearLayout llContainer;
    private View mItemView;
    private RadioButton rbEntry;
    private Bundle mEntry;
    private Context mContext;
    private EntryAdapter mAdapter;

    public EntryViewHolder(View itemView, Context context, EntryAdapter adapter) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
        tvName = (TextView) itemView.findViewById(R.id.tv_entry_name);
        tvDescription = (TextView) itemView.findViewById(R.id.tv_entry_description);
        rbEntry= (RadioButton) itemView.findViewById(R.id.rb_entry);
        llContainer = (LinearLayout) itemView.findViewById(R.id.ll_entry_container);
        mAdapter=adapter;

        rbEntry.setChecked(false);
        mAdapter.addRadioButton(rbEntry);
    }

    public void bindEntry(final Bundle entry) {
        mEntry=entry;
        tvName.setText(entry.getString("name"));
        tvDescription.setText(entry.getString("description"));
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.checkRadioButton(entry.getInt("id",0));
            }
        };
        llContainer.setOnClickListener(click);
        rbEntry.setOnClickListener(click);
    }
}