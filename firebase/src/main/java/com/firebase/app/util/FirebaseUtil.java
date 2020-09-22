package com.firebase.app.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.core.app.util.AlertUtil;
import com.firebase.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import timber.log.Timber;

public class FirebaseUtil {

    private FirebaseUtil() {
    }

    public static boolean isGooglePlayServicesAvailable(AppCompatActivity activity, int requestCode) {
        Context context = activity.getApplicationContext();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, requestCode).show();
            } else {
                Timber.i("Google play services are not available!");
                AlertUtil.showToast(activity, context.getString(R.string.fcm_not_supported_msg));
                activity.finish();
            }
            return false;
        }
        return true;
    }
}