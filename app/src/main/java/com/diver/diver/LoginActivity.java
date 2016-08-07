package com.diver.diver;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import backend.Analytics;
import backend.LoginTask13;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private Context mContext = this;
    private LoginButton loginButton;
    private ProgressBar pbLogin;
    private TextView tvLogin;
    private ImageView ivLoginButton;
    private LinearLayout llLogin, llLogo;
    private ProfileTracker mProfileTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.diver.diver",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton) findViewById(R.id.bt_facebook_login);
        loginButton.setReadPermissions("email", "user_friends");


        /*pbLogin = (ProgressBar) findViewById(R.id.pb_login);
        tvLogin = (TextView) findViewById(R.id.tv_login_text);*/

        ivLoginButton = (ImageView) findViewById(R.id.iv_login_button);
        ivLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pbLogin.setVisibility(View.VISIBLE);
                //tvLogin.setText("");
                Analytics analytics = new Analytics(mContext);
                analytics.execute("log_in_attempt","none");
                loginButton.performClick();
            }
        });
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(final Profile profile, final Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.v("LoginActivity", response.toString());

                                            // Application code
                                            try {
                                                String email = object.getString("email");
                                                String gender = object.getString("gender");
                                                LogIn(profile2, email,gender);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle permissions = new Bundle();
                            permissions.putString("fields", "email,gender");
                            request.setParameters(permissions);
                            request.executeAsync();
                        }
                    };
                    mProfileTracker.startTracking();
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    final Profile profile = Profile.getCurrentProfile();
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());
                                    // Application code
                                    try {
                                        String email = object.getString("email");
                                        String gender = object.getString("gender");
                                        LogIn(profile, email,gender);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle permissions = new Bundle();
                    permissions.putString("fields", "email,gender");
                    request.setParameters(permissions);
                    request.executeAsync();

                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(mContext, "error al iniciar sesion en facebook", Toast.LENGTH_LONG).show();
            }
        });

        llLogo = (LinearLayout) findViewById(R.id.ll_logo);
        llLogin = (LinearLayout) findViewById(R.id.ll_login);
        llLogin.setVisibility(View.GONE);


        ViewTreeObserver vto = llLogin.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLogo.getLayoutParams();

                int height = getResources().getDisplayMetrics().heightPixels / 2;
                final int middleMargin = (int) ((height -StaticUtilities.convertDpToPixel(90,mContext))/1.5);
                params.topMargin = middleMargin;
                llLogo.setLayoutParams(params);

                if (Profile.getCurrentProfile() != null) {
                    LogIn(Profile.getCurrentProfile(), "nulo", "nulo");
                    ivLoginButton.setEnabled(false);
                } else {
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLogo.getLayoutParams();
                            params.topMargin = (int) ((1 - interpolatedTime) * (middleMargin - StaticUtilities.convertDpToPixel(40, mContext))
                                    + StaticUtilities.convertDpToPixel(40, mContext));
                            llLogo.setLayoutParams(params);
                        }
                    };
                    a.setDuration(1000);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            llLogin.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    llLogin.startAnimation(a);
                }
                ViewTreeObserver vto2 = llLogin.getViewTreeObserver();
                vto2.removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void LogIn(Profile profile, final String email, final String gender) {

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + profile.getId() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            LoginTask13 loginTask = new LoginTask13(mContext, pbLogin, tvLogin, ivLoginButton);
                            loginTask.execute(data.toString(), email, gender,data.length());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException",e.toString());
                        }
                    }
                }
        ).executeAsync();

    }
}

