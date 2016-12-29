package backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diver.diver.MainActivity;
import com.diver.diver.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask13 extends AsyncTask<Object, String, String> {

    private Context mContext;
    private JSONArray data;
    /*private ProgressBar mProgressBar;
    private TextView tvLogin;*/
    private ImageView flButton;
    private InputStream is;

    public LoginTask13(Context context, ProgressBar progressBar, TextView textView, ImageView imageView) {
        mContext = context;
       /* mProgressBar = progressBar;
        tvLogin = textView;*/
        flButton = imageView;
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection con = null;
        BufferedReader reader = null;
        Profile profile = Profile.getCurrentProfile();

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_email", (String) params[1]);
        editor.apply();


        try {

            con = (HttpURLConnection) (new URL("http://diverapp.es/functions/login13.php")).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            String paramss = Uri.parse("").buildUpon()
                    .appendQueryParameter("nothing", "nothing")
                    .appendQueryParameter("name", profile.getName())
                    .appendQueryParameter("fid", profile.getId())
                    .appendQueryParameter("email", (String) params[1])
                    .appendQueryParameter("gender", (String) params[2])
                    .appendQueryParameter("friends_data", (String) params[0])
                    .appendQueryParameter("friends_length", String.valueOf(params[3]))
                    .appendQueryParameter("birthday", String.valueOf(params[4]))
                    .appendQueryParameter("total_friends", String.valueOf(params[5]))
                    .appendQueryParameter("version", mContext.getString(R.string.str_version_number))
                    .build().toString();

            con.getOutputStream().write(paramss.getBytes());

            is = con.getInputStream();

            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = convertStreamToString(is);
        return result;
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
                analytics.execute("log_in_success", "none");
                mContext.startActivity(intent);
                //mProgressBar.setVisibility(View.GONE);
                //tvLogin.setText("Iniciar Sesión con Facebook");
                flButton.setEnabled(true);
                //**Login...
            } else if (result.equals("update")) {
                Toast.makeText(mContext, "Debes actualizar la aplicación", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.diverapp.diver"));
                Analytics analytics = new Analytics(mContext);
                analytics.execute("to_update", "none");
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
        } else {
            Toast.makeText(mContext, "105:no hubo respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}


