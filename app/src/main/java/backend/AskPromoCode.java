package backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diver.diver.CityAdapter;
import com.diver.diver.EventDetailFragment;

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

public class AskPromoCode extends AsyncTask<Object, String, String> {

    private Context mContext;
    private ProgressBar mProgressBar;
    private CityAdapter mAdapter;
    private TextView tvPromoDescription, tvTotalPrice, tvPromoUse;
    public String type;
    private EventDetailFragment mFragment;
    private TextInputLayout tilPromo;

    public AskPromoCode(Context context, TextView promoDescription, TextView totalPrice, ProgressBar progressBar, TextView promoUse, EventDetailFragment fragment, TextInputLayout promo) {
        mContext = context;
        mProgressBar = progressBar;
        tvPromoDescription = promoDescription;
        tvTotalPrice = totalPrice;
        type = "none";
        tvPromoUse = promoUse;
        mFragment = fragment;
        tilPromo = promo;
    }

    @Override
    protected void onPreExecute() {

        tvPromoUse.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/ask_promo_code.php?";

            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("code", String.valueOf(params[0]))
                    .appendQueryParameter("event_id", String.valueOf(params[1]))
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
            publishProgress("error in connection");
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

        tvPromoUse.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            result=result.replace("\\u0080", "€");
            tvPromoUse.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);


            try {
                JSONObject jsonResult = new JSONObject(result);
                type = jsonResult.getString("type");
                tvPromoDescription.setText(jsonResult.getString("promotion_description"));
                type = jsonResult.getString("type");
                if (!type.equals("null")) {
                    mFragment.setOrderType("promotion_dni");
                    tvPromoDescription.setVisibility(View.VISIBLE);
                    tilPromo.setError("");
                } else {
                    tilPromo.setError("El código no es válido");
                    tvPromoDescription.setText("");
                    tvPromoDescription.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}


