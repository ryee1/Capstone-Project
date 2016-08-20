package com.project.richard.insightjournal.ui.timerscreen;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.project.richard.insightjournal.events.OnAddressFetchedEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    public static final String LATITUDE_EXTRA = "LATITUDE_EXTRA";
    public static final String LONTITUDE_EXTRA = "LONGTITUDE_EXTRA";


    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double latitude = intent.getDoubleExtra(LATITUDE_EXTRA, 0);
        double longtitude = intent.getDoubleExtra(LONTITUDE_EXTRA, 0);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longtitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "ioException error");
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, "invalid lat or long: " + latitude + " " + longtitude);
        }
        if (addresses == null || addresses.size()  == 0) {
            Log.e(TAG, "empty address");
        } else {
            Address address = addresses.get(0);
            EventBus.getDefault().post(new OnAddressFetchedEvent(address));

        }
    }

}
