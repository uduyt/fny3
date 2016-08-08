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
import android.widget.Toast;

import com.diver.diver.EntryAdapter;
import com.diver.diver.Event;
import com.diver.diver.EventDetailFragment;
import com.diver.diver.OrdersFragment;
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

public class SetOrders extends AsyncTask<Object, String, String> {

    private Context mContext;
    private List<Bundle> mOrders;
    private OrdersFragment mFragment;

    public SetOrders(Context context, OrdersFragment fragment) {
        mContext = context;
        mFragment = fragment;
        mOrders=new ArrayList<>();
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

        try {
            final String REQUEST_BASE_URL =
                    "http://diverapp.es/functions/get_orders.php?";

            Uri builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("facebook_id", Profile.getCurrentProfile().getId())
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

            try {
                JSONArray jsonResult= new JSONArray(result);
                Bundle order= new Bundle();

                JSONObject jsonOrder;
                for(int i=0;i<jsonResult.length();i++){
                    jsonOrder=jsonResult.getJSONObject(i);

                    order.putString("event_name",jsonOrder.getString("event_name"));
                    order.putString("club_name",jsonOrder.getString("club_name"));
                    order.putString("order_people",jsonOrder.getString("people"));
                    order.putString("order_price",jsonOrder.getString("price"));
                    order.putString("order_state",jsonOrder.getString("state"));

                    mOrders.add(order);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }


            mFragment.LoadData(mOrders);
        }
    }
}


