package com.project.richard.insightjournal;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.project.richard.insightjournal.utils.PermissionsUtils;

/**
 * Created by richard on 8/4/16.
 */
public class MyApplication extends Application {

    private final static String TAG = MyApplication.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    @Override public void onCreate() {
        super.onCreate();

    }

    public GoogleApiClient getGoogleApiClient(final FragmentActivity activity) {
        Log.e(TAG, "getGoogleApiClient ran");
        if (mGoogleApiClient == null && PermissionsUtils.checkPermissions(activity)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {

                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.e(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.e(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(activity, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.e(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
//                            Snackbar.make(
//                                    activity.findViewById(R.id.main_activity_view),
//                                    "Exception while connecting to Google Play services: " +
//                                            result.getErrorMessage(),
//                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }
        return mGoogleApiClient;
    }
}
