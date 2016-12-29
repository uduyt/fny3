package backend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diver.diver.Event;
import com.diver.diver.EventLab;
import com.diver.diver.HomeFragment;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GetCity extends AsyncTask<Object, String, String> {

    private Context mContext;
    private Event.EventAdapter mAdapter;
    private TextView tvCity;
    private Fragment mFragment;
    private City mCity;

    public GetCity(City city) {
        mFragment=(Fragment) city;
        mCity=city;
        mContext=mFragment.getActivity();
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);


        try {
            final String REQUEST_BASE_URL;
            Uri builtUri;

                Float Lat, Long;
                Lat = sharedPref.getFloat("lat", 36.6015f);
                Long = sharedPref.getFloat("long", -6.23664f);

                REQUEST_BASE_URL =
                        "http://diverapp.es/functions/get_city.php?";
                builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
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
            Snackbar.make(((Activity)mContext).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
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
            mCity.setCity(result);
        }else{
            Snackbar.make(((Activity)mContext).findViewById(R.id.container), "No hay buena conexión a internet, inténtelo más tarde", Snackbar.LENGTH_LONG).show();
        }
    }
}


