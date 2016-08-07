package backend;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diver.diver.Event;
import com.diver.diver.MainActivity;
import com.diver.diver.R;
import com.facebook.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendLists extends AsyncTask<Object, String, String> {

    private Context mContext;
    public int mEventId;
    private String finalMessage;
    private MaterialDialog progressDialog;
    private Bundle mEntry;

    public SendLists(Context context, Bundle entry) {
        mContext = context;

        mEntry = entry;

        if (mEntry.getString("payment_method") != null) {
            if (mEntry.getString("payment_method").equals("paypal") | mEntry.getString("payment_method").equals("credit_card")) {
                finalMessage = entry.getString("success_text_payment");
            } else {
                finalMessage = entry.getString("success_text");
            }
        }else{
            finalMessage = entry.getString("success_text");
        }


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new MaterialDialog.Builder(mContext)
                .title("Espera porfavor")
                .content("Esperando confirmación...")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Object... params) {
        String responseString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        if (mEntry.getString("data") == null) {
            mEntry.putString("data", "none");
        }
        if (mEntry.getString("contact_method") == null) {
            mEntry.putString("contact_method", "none");
        }
        if (mEntry.getString("contact_data") == null) {
            mEntry.putString("contact_data", "none");
        }
        if (mEntry.getString("promo_code") == null) {
            mEntry.putString("promo_code", "none");
        }
        if (mEntry.getString("payment_method") == null) {
            mEntry.putString("payment_method", "none");
        }
        if (mEntry.getString("payment_token") == null) {
            mEntry.putString("payment_token", "none");
        }
        if (mEntry.getString("payment_last_4") == null) {
            mEntry.putString("payment_last_4", "none");
        }

        try {
            final String REQUEST_BASE_URL;
            Uri builtUri;

            REQUEST_BASE_URL =
                    "http://diverapp.es/functions/send_lists13.php?";

            builtUri = Uri.parse(REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("facebook_id", Profile.getCurrentProfile().getId())
                    .appendQueryParameter("entry_id", mEntry.getString("entry_id"))
                    .appendQueryParameter("people", mEntry.getString("people"))
                    .appendQueryParameter("contact_method", mEntry.getString("contact_method"))
                    .appendQueryParameter("data", mEntry.getString("data"))
                    .appendQueryParameter("contact_data", mEntry.getString("contact_data"))
                    .appendQueryParameter("promo_code", mEntry.getString("promo_code"))
                    .appendQueryParameter("payment_method", mEntry.getString("payment_method"))
                    .appendQueryParameter("payment_token", mEntry.getString("payment_token"))
                    .appendQueryParameter("payment_last_4", mEntry.getString("payment_last_4"))
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

        Toast.makeText(mContext, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String result) {

        progressDialog.dismiss();

        if (result.equals("ok")) {

            MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);

            MaterialDialog dialog = dialogBuilder
                    .customView(R.layout.dialog_order_final, false)
                    .positiveText("ok")
                    .backgroundColorRes(R.color.colorPrimary)
                    .build();
            ((TextView) dialog.findViewById(R.id.tv_order_final)).setText(finalMessage);

            dialog.show();
        } else {
            MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);

            MaterialDialog dialog = dialogBuilder
                    .positiveText("ok")
                    .title("Error al pagar")
                    .titleColorRes(R.color.White)
                    .content("Lo sentimos, parece que ha habido un error en el pago. \n\nNo le deberíamos haber cobrado, si este fuera" +
                            " el caso, póngase en contacto con nuestro equipo en cualquier correo de diver como pagos@diverapp.es")
                    .contentColorRes(R.color.White)
                    .backgroundColorRes(R.color.colorPrimary)
                    .build();

            dialog.show();
        }
    }
}


