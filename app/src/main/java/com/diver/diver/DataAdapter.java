package com.diver.diver;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter
        extends RecyclerView.Adapter<DataViewHolder>

{

    private Bundle mEntry;
    private Context mContext;
    private List<DataViewHolder> data;

    public DataAdapter(Bundle entry, Context context) {

        mEntry=entry;
        mContext = context;
        data=  new ArrayList<>();
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog_data, parent, false);

        DataViewHolder vh=new DataViewHolder(view, mContext);
        data.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int pos) {

        holder.bindData(pos,mEntry);
    }

    public Integer isError(){
        int result=0;
        for(DataViewHolder dvh: data){
            if(dvh.isError()==2)return 2;
            if(dvh.isError()==1)result=1;
        }
       return result;
    }

    public String getData(){
        JSONArray jsonResult= new JSONArray();

        for(int i=0;i<data.size();i++){
            try {
                jsonResult.put(i,data.get(i).getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonResult.toString();
    }
    @Override
    public int getItemCount() {
        if (mEntry.getString("people") == null) {
            return 0;
        }
        int people= Integer.valueOf((mEntry.getString("people")));
        return people;
    }
}