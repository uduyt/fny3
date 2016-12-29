package backend;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.diver.diver.ClubDetailFragmentInfo;
import com.diver.diver.ClubsFragmentList;
import com.diver.diver.R;

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
import java.util.List;

public class SetClubsFull extends AsyncTask<Object, String, String> {

    private ClubDetail mFragment;
    private Activity mContext;
    private Bundle mClub;

    public SetClubsFull(ClubDetail fragment, Bundle club, Activity context) {
        mContext=context;
        mFragment = fragment;
        mClub=club;
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
                    "http://diverapp.es/functions/get_clubs_full.php?";
            builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("club_id", mClub.getString("club_id"))
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
            Snackbar.make(mContext.findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
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

        mFragment.SetPBVisibility(false);
        if (result != null) {
            result = result.replace("\\u0080", "€");
            try {
                JSONObject club_json = new JSONArray(result).getJSONObject(0);

                mClub.putString("club_info",club_json.getString("club_info"));
                mClub.putString("club_address",club_json.getString("club_address"));
                mClub.putString("club_horario",club_json.getString("club_horario"));
                mClub.putString("club_ropero",club_json.getString("club_ropero"));
                mClub.putString("club_precio",club_json.getString("club_precio"));
                mClub.putString("club_precio_copa",club_json.getString("club_precio_copa"));
                mClub.putString("club_precio_botella",club_json.getString("club_precio_botella"));
                mClub.putString("club_seal",club_json.getString("club_seal"));
                mClub.putString("club_num_photos",club_json.getString("club_num_photos"));

                mFragment.LoadData(mClub);

            } catch (JSONException e) {
                e.printStackTrace();
                /*LinearLayout llNoEvents = (LinearLayout) mView.findViewById(R.id.ll_no_events);
                llNoEvents.setVisibility(View.VISIBLE);*/
            }


        }
    }
}


