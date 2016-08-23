/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.richard.insightjournal.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by richard on 8/4/16.
 */
public class PermissionsUtils {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static boolean checkLocationPermissions(Context c) {
        int permissionCheck = ActivityCompat.checkSelfPermission(c,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermissions(final Activity c, int viewId) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(c,
                        Manifest.permission.ACCESS_FINE_LOCATION);

//         Provide an additional rationale to the user. This would happen if the user denied the
//         request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.e("PermissionsUtils", "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    c.findViewById(viewId),
                    "Location permissions requested for recording the city in the meditation log",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(c,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
//         Request permission. It's possible this can be auto answered if device policy
//         sets the permission in a given state or the user denied the permission
//         previously and checked "Never ask again".
            ActivityCompat.requestPermissions(c,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        }
    }
}
