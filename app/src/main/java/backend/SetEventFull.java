package backend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.diver.diver.CityAdapter;
import com.diver.diver.EntryAdapter;
import com.diver.diver.Event;
import com.diver.diver.EventDetailFragment;
import com.diver.diver.EventLab;
import com.diver.diver.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SetEventFull extends AsyncTask<Object, String, String> {

    private Context mContext;
    private EntryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EventDetailFragment mFragment;
    private Event mEvent;

    public SetEventFull(Context context, Event event, EventDetailFragment fragment, RecyclerView recyclerView, EntryAdapter adapter) {
        mContext = context;
        mRecyclerView = recyclerView;
        mAdapter = adapter;
        mFragment = fragment;
        mEvent=event;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        float Lat, Long;
        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/get_event_full.php?";

            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("event_id", String.valueOf(params[0]))
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v("mytag", "Built URI " + builtUri.toString());
            // Open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                publishProgress("no input stream");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            responseString = reader.readLine();

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            Snackbar.make(((Activity)mContext).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    publishProgress("try to close reader");
                    return null;
                }
            }

        }
        return responseString;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            result = result.replace("\\u0080", "€");

            List<Bundle> entries = new ArrayList<>();
            try {
                JSONObject jsonResult= new JSONObject(result);

                JSONObject event_json= (jsonResult.getJSONArray("event")).getJSONObject(0);

                    mEvent.setDescription(event_json.getString("event_description"));
                    mEvent.setMusic(event_json.getString("music"));
                    mEvent.setDJ(event_json.getString("dj"));
                    mEvent.setDateText(event_json.getString("time_text"));
                    mEvent.setDate(Date.valueOf(event_json.getString("datetime_start")));
                    mEvent.setMaxPeople(Integer.valueOf(event_json.getString("max_people")));
                    mEvent.setCachimba(event_json.getString("cachimba"));
                    mEvent.setReservado(event_json.getString("reservado"));
                    mEvent.setSmokeArea(Integer.valueOf(event_json.getString("smoke_area")));
                    mEvent.setTerrace(Integer.valueOf(event_json.getString("terrace")));
                    mEvent.setMaxYears(event_json.getString("max_age"));
                    mEvent.setPromo(Integer.valueOf(event_json.getString("promo_enabled")));


                JSONArray entries_json = jsonResult.getJSONArray("entries");
                JSONObject entry_json;
                Bundle entry;
                //success
                for (int i = 0; i < entries_json.length(); i++) {
                    entry_json = entries_json.getJSONObject(i);
                    entry = new Bundle();
                    entry.putString("name", entry_json.getString("name"));
                    entry.putString("description", entry_json.getString("description"));
                    entry.putString("entry_id", entry_json.getString("entry_id"));
                    entry.putString("price", entry_json.getString("price"));
                    entry.putString("order_type", entry_json.getString("order_type"));
                    entry.putString("ask_name", entry_json.getString("ask_name"));
                    entry.putString("ask_dni", entry_json.getString("ask_dni"));
                    entry.putString("ask_dob", entry_json.getString("ask_dob"));
                    entry.putString("success_text", entry_json.getString("success_text"));
                    entry.putString("success_text_payment", entry_json.getString("success_text_payment"));
                    entry.putString("buy_button_text", entry_json.getString("buy_button_text"));
                    entry.putInt("id", i);
                    entries.add(entry);
                }

                mAdapter = new EntryAdapter(entries, mContext, mFragment);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


                mFragment.LoadData(mEvent);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}


