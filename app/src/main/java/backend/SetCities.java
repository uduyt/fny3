package backend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.diver.diver.CityAdapter;
import com.diver.diver.R;
import com.diver.diver.SelectCityActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SetCities extends AsyncTask<Object, String, String> {

    private Context mContext;
    private ProgressBar mProgressBar;
    private CityAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public SetCities(Context context, RecyclerView recyclerView,CityAdapter adapter, ProgressBar progressBar) {
        mContext = context;
        mRecyclerView = recyclerView;
        mProgressBar = progressBar;
        mAdapter=adapter;
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        float Lat, Long;
        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/get_cities.php?";

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            Lat = sharedPref.getFloat("lat", 36.6015f);
            Long = sharedPref.getFloat("long", -6.23664f);


            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("lat", String.valueOf(Lat))
                    .appendQueryParameter("long", String.valueOf(Long))
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
            Snackbar.make(((SelectCityActivity)mContext).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
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
        if (result != null) {
            mProgressBar.setVisibility(View.GONE);
            List<Bundle> cities = new ArrayList<>();
            try {
                JSONArray cities_json = new JSONArray(result);
                JSONObject city_json;
                Bundle city;
                //success
                for (int i = 0; i < cities_json.length(); i++) {
                    city_json = cities_json.getJSONObject(i);
                    city=new Bundle();
                    city.putString("name",city_json.getString("name"));
                    city.putString("id",city_json.getString("id"));
                    cities.add(city);
                }

                mAdapter = new CityAdapter(cities, mContext);

                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}


