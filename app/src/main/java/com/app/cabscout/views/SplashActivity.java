package com.app.cabscout.views;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.cabscout.R;
import com.app.cabscout.controller.ModelManager;
import com.app.cabscout.model.CSPreferences;
import com.app.cabscout.model.Operations;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler;
    private Runnable runnable;
    Activity activity = this;

    private GoogleApiClient googleApiClient;

    final static int REQUEST_LOCATION = 199;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "device token: " +deviceToken);

        CSPreferences.putString(activity, "device_token", deviceToken);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            enableLoc();

        else
            handleSleep();

    }

    private void handleSleep() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                CSPreferences.putString(activity, "pickup_address", "");
                CSPreferences.putString(activity, "drop_address", "");

                if (CSPreferences.readString(activity, "login_status").equals("true")){
                    Intent i = new Intent(activity, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    Intent intent = new Intent(activity,CabCompaniesActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        int SPLASH_TIME_OUT = 3000;
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ModelManager.getInstance().getCabCompaniesManager().getCabCompanies(activity, Operations.getCabCompaniesTask(activity));
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            Log.e("Location error ", "" + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult()
                                status.startResolutionForResult(SplashActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                    case Activity.RESULT_OK:
                        handleSleep();
                        break;
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        finish();
    }
}