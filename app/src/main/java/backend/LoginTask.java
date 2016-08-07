package backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diver.diver.CityAdapter;
import com.diver.diver.MainActivity;
import com.diver.diver.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

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

public class LoginTask extends AsyncTask<Object, String, String> {

    private Context mContext;
    private JSONArray data;
    /*private ProgressBar mProgressBar;
    private TextView tvLogin;*/
    private ImageView flButton;

    public LoginTask(Context context, ProgressBar progressBar, TextView textView, ImageView imageView) {
        mContext = context;
       /* mProgressBar = progressBar;
        tvLogin = textView;*/
        flButton=imageView;
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Profile profile = Profile.getCurrentProfile();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("user_email",(String) params[1]);
        editor.apply();
        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/login13.php?";

            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("facebook_id", profile.getId())
                    .appendQueryParameter("name", profile.getName())
                    .appendQueryParameter("email", (String) params[1])
                    .appendQueryParameter("gender", (String) params[2])
                    //.appendQueryParameter("friends_data", (String) params[0])
                    .appendQueryParameter("friends_length", String.valueOf(params[3]))
                    .appendQueryParameter("version", mContext.getString(R.string.str_version_number))
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
            if (result.equals("ok")) {
                //Login...
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("city", "none");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Analytics analytics = new Analytics(mContext);
                analytics.execute("log_in_success","none");
                mContext.startActivity(intent);
                //mProgressBar.setVisibility(View.GONE);
                //tvLogin.setText("Iniciar Sesión con Facebook");
                flButton.setEnabled(true);
                //**Login...
            } else if (result.equals("update")) {
                Toast.makeText(mContext, "Debes actualizar la aplicación", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.diver.diver"));
                Analytics analytics = new Analytics(mContext);
                analytics.execute("to_update","none");
                mContext.startActivity(intent);
                //mProgressBar.setVisibility(View.GONE);
                //tvLogin.setText("Iniciar Sesión con Facebook");
                flButton.setEnabled(true);
            } else if (!result.equals("")) {
                Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                //mProgressBar.setVisibility(View.GONE);
                //tvLogin.setText("Iniciar Sesión con Facebook");
                flButton.setEnabled(true);
            }
        }else{
            Toast.makeText(mContext,"105:no hubo respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}


