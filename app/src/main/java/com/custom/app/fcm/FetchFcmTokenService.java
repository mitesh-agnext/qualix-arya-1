package com.custom.app.fcm;

import android.app.IntentService;
import android.content.Intent;

import com.custom.app.CustomApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.user.app.data.UserManager;

import javax.inject.Inject;

import timber.log.Timber;

public class FetchFcmTokenService extends IntentService {

    private static final String TAG = "FetchFcmTokenService";

    @Inject
    UserManager userManager;

    public FetchFcmTokenService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((CustomApp) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(result -> {
            String token = result.getToken();
            Timber.d("Fcm token: %s", token);

            userManager.setFcmToken(token);
        });
    }
}