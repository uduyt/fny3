package com.diver.diver;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import backend.Analytics;

public class DataViewHolder extends RecyclerView.ViewHolder {
    private TextView tvPerson;
    private EditText etName, etSecondName, etDNI, etDoB;
    private View mItemView;
    private Context mContext;
    private java.text.SimpleDateFormat dateFormatter;
    private DatePickerDialog mPickerDialog;
    private TextInputLayout tilDoB;
    private Bundle mEntry;

    public DataViewHolder(View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
        etName = (EditText) itemView.findViewById(R.id.et_data_name);
        etSecondName = (EditText) itemView.findViewById(R.id.et_data_name);
        etDNI = (EditText) itemView.findViewById(R.id.et_data_dni);
        tvPerson= (TextView) itemView.findViewById(R.id.tv_data_person);
        tilDoB= (TextInputLayout) itemView.findViewById(R.id.til_data_dob) ;
        etDoB = (EditText) itemView.findViewById(R.id.et_data_dob);
        etDoB.setInputType(InputType.TYPE_NULL);
    }

    public void bindData(final int pos, final Bundle entry) {

        mEntry=entry;
        tvPerson.setText("Persona " + String.valueOf(pos+1));

        dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("es", "es"));

        if(!entry.getString("ask_name").equals("1")){
            etName.setVisibility(View.GONE);
            etSecondName.setVisibility(View.GONE);
        }
        if(!entry.getString("ask_dni").equals("1")){
            etDNI.setVisibility(View.GONE);
        }
        if(!entry.getString("ask_dob").equals("1")){
            etDoB.setVisibility(View.GONE);
        }else{
            etDoB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        Calendar newCalendar = Calendar.getInstance();
                        mPickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                etDoB.setText(dateFormatter.format(newDate.getTime()));
                            }

                        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        mPickerDialog.show();
                    }
                }
            });
        }
    }

    public int isError(){
        if(mEntry.getString("ask_name").equals("1")){
            if( etName.getText().toString().equals("") | etSecondName.getText().toString().equals(""))
                return 1;
        }
        if(mEntry.getString("ask_dni").equals("1")){
            if(etDNI.getText().toString().equals(""))
                return 1;
        }
        if(mEntry.getString("ask_dob").equals("1")){
            if(etDoB.getText().toString().equals(""))
                return 1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date strDate = sdf.parse(etDoB.getText().toString());

                Calendar cal = Calendar.getInstance();
                cal.setTime(strDate);
                cal.add(Calendar.YEAR, Integer.valueOf(mEntry.getString("max_years")));

                Calendar c = Calendar.getInstance();

                Date today = c.getTime();

                if(cal.getTime().getTime()>Calendar.getInstance().getTime().getTime()) return 2;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public JSONObject getData(){
        JSONObject jsonResult= new JSONObject();

        try {
            jsonResult.put("name",etName.getText().toString());
            jsonResult.put("second_name",etSecondName.getText().toString());
            jsonResult.put("dni",etDNI.getText().toString());
            jsonResult.put("dob",etDoB.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }
}