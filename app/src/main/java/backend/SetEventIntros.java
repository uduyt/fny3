package backend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diver.diver.CityAdapter;
import com.diver.diver.Event;
import com.diver.diver.EventLab;
import com.diver.diver.R;
import com.facebook.Profile;

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

public class SetEventIntros extends AsyncTask<Object, String, String> {

    private Context mContext;
    private ProgressBar mProgressBar;
    private Event.EventAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View mView;
    private TextView tvCity;

    public SetEventIntros(Context context, RecyclerView recyclerView, Event.EventAdapter adapter, ProgressBar progressBar, View myView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mProgressBar = progressBar;
        mAdapter = adapter;
        mView = myView;
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        try {
            final String REQUEST_BASE_URL;
            Uri builtUri;

            REQUEST_BASE_URL =
                    "http://diverapp.es/functions/get_event_intro.php?";
            builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("city", (String) params[0])
                    .appendQueryParameter("fid", Profile.getCurrentProfile().getId())
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
            Snackbar.make(((Activity) mContext).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
            //publishProgress("error in connection");
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
        tvCity = (TextView) mView.findViewById(R.id.tv_toolbar_title);
        mProgressBar.setVisibility(View.GONE);

        if (result != null) {
            result = result.replace("\\u0080", "€");
            try {
                String cityName = (new JSONObject(result)).getString("city_name");
                String cityId = (new JSONObject(result)).getString("city_id");
                tvCity.setText(cityName);

                JSONArray events_json = (new JSONObject(result)).getJSONArray("data");
                JSONObject event_json;
                List<Event> Events = new ArrayList<Event>();
                Event event;
                //success
                for (int i = 0; i < events_json.length(); i++) {
                    event_json = events_json.getJSONObject(i);
                    event = new Event(mContext);
                    event.setEventID(Integer.valueOf(event_json.getString("event_id")));
                    event.setName(event_json.getString("event_name"));
                    event.setPrice(event_json.getString("price"));
                    event.setDate(Date.valueOf(event_json.getString("datetime_start")));
                    event.setClubName(event_json.getString("club_name"));
                    event.setLat(Float.valueOf(event_json.getString("lat")));
                    event.setLong(Float.valueOf(event_json.getString("long")));
                    event.setCityId(cityId);

                    Events.add(event);
                }
                EventLab.setEvents(Events);

                mAdapter = new Event.EventAdapter(Events, mContext);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                LinearLayout llNoEvents = (LinearLayout) mView.findViewById(R.id.ll_no_events);
                llNoEvents.setVisibility(View.VISIBLE);
            }


        }
    }
}


