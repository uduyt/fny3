package backend;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.diver.diver.ClubsFragmentList;
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

public class SetClubsIntros extends AsyncTask<Object, String, String> {

    private ClubsFragmentList mFragment;
    private List<Bundle> mClubs;

    public SetClubsIntros(ClubsFragmentList fragment) {
        mFragment = fragment;
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
                    "http://diverapp.es/functions/get_clubs_intro.php?";
            builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("city", (String) params[0])
                    .appendQueryParameter("fid", "34")
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
            Snackbar.make((mFragment.getActivity()).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
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

        Toast.makeText(mFragment.getActivity(), values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {
            result = result.replace("\\u0080", "€");
            try {
                String cityName = (new JSONObject(result)).getString("city_name");
                String cityId = (new JSONObject(result)).getString("city_id");

                JSONArray clubs_json = (new JSONObject(result)).getJSONArray("data");
                JSONObject club_json;
                mClubs = new ArrayList<Bundle>();
                Bundle club;
                //success
                for (int i = 0; i < clubs_json.length(); i++) {
                    club_json = clubs_json.getJSONObject(i);
                    club = new Bundle();

                    club.putString("club_name",club_json.getString("club_name"));
                    club.putString("club_id",club_json.getString("club_id"));
                    club.putString("club_lat",String.valueOf(club_json.getDouble("club_lat")));
                    club.putString("club_long",String.valueOf(club_json.getDouble("club_long")));
                    club.putString("club_city",(club_json.getString("club_city")));

                    mClubs.add(club);
                }

                mFragment.setClubs(mClubs, cityName, cityId);

            } catch (JSONException e) {
                e.printStackTrace();
                /*LinearLayout llNoEvents = (LinearLayout) mView.findViewById(R.id.ll_no_events);
                llNoEvents.setVisibility(View.VISIBLE);*/
            }


        }
    }
}


