package com.diver.diver;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.paypal.android.sdk.payments.PayPalConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Carlos on 14/06/2016.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


    }
}
