package backend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.diver.diver.R;
import com.facebook.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Analytics extends AsyncTask<Object, String, String> {

    private Context mContext;
    private String param;
    private String facebookId;

    public Analytics(Context context) {
        mContext = context;
        if(Profile.getCurrentProfile()!=null) facebookId=Profile.getCurrentProfile().getId();
        else facebookId="0";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private final String LOG_TAG = Analytics.class.getSimpleName();

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/analytics.php?";
            if(!params[1].equals("none")){
                param=(String) params[2];
            }else{
                param="null";
            }
            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("type", String.valueOf(params[0]))
                    .appendQueryParameter("facebook_id", facebookId)
                    .appendQueryParameter("value", param)
                    .appendQueryParameter("value_type", (String)params[1])
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
            Snackbar.make(((Activity)mContext).findViewById(R.id.container), "Por favor, revise su conexión a internet", Snackbar.LENGTH_LONG).show();
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

    }
}


