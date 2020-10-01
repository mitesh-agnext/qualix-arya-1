package com.custom.app.ui.splash;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.base.app.ui.base.BaseActivity;
import com.core.app.util.ActivityUtil;
import com.custom.app.R;
import com.custom.app.fcm.FetchFcmTokenService;
import com.custom.app.ui.home.HomeActivity;
import com.custom.app.ui.login.LoginActivity;
import com.custom.app.ui.qualityAnalysis.QualityAnalysisActivity;
import com.custom.app.util.Utils;
import com.firebase.app.util.FirebaseUtil;

import timber.log.Timber;

import static com.core.app.util.Constants.PLAY_SERVICES_RESOLUTION_REQUEST;
import static com.custom.app.util.Constants.FLOW;
import static com.custom.app.util.Constants.NAV_SPLASH;

public class SplashActivity extends BaseActivity implements SplashView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(new NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT));
            } else {
                Timber.e("Notification Manager is null");
            }
        }

        fetchFcmToken();

        if (userManager.isLoggedIn()) {
            showHomeScreen();
        } else {
            showLoginScreen();
        }
    }

    public void fetchFcmToken() {
        Timber.d("Fetching fcm token...!");

        if (FirebaseUtil.isGooglePlayServicesAvailable(this, PLAY_SERVICES_RESOLUTION_REQUEST)) {
            Intent intent = new Intent(this, FetchFcmTokenService.class);
            startService(intent);
        }
    }

    @Override
    public void showLoginScreen() {
        ActivityUtil.startActivity(this, LoginActivity.class, true);
    }

    @Override
    public void showHomeScreen() {
        Bundle bundle = new Bundle();
        bundle.putString(FLOW, NAV_SPLASH);
        Utils.Companion.startActivityWithLoad(this, HomeActivity.class, bundle, true);
        //ActivityUtil.startActivity(this, QualityAnalysisActivity.class, true);
    }
}