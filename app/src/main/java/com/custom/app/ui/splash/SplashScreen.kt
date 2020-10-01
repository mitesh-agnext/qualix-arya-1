package com.custom.app.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.base.app.ui.base.BaseActivity
import com.core.app.util.ActivityUtil
import com.core.app.util.Constants
import com.custom.app.R
import com.custom.app.fcm.FetchFcmTokenService
import com.custom.app.ui.home.HomeActivity
import com.custom.app.ui.login.LoginActivity
import com.custom.app.util.Utils.Companion.startActivityWithLoad
import com.firebase.app.util.FirebaseUtil
import timber.log.Timber

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val manager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT))
            } else {
                Timber.e("Notification Manager is null")
            }
        }

        fetchFcmToken()

        if (userManager.isLoggedIn) {
            showHomeScreen()
        } else {
            showLoginScreen()
        }
    }
    fun fetchFcmToken() {
        Timber.d("Fetching fcm token...!")
        if (FirebaseUtil.isGooglePlayServicesAvailable(this, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)) {
            val intent = Intent(this, FetchFcmTokenService::class.java)
            startService(intent)
        }
    }
    fun showLoginScreen() {
        ActivityUtil.startActivity(this, LoginActivity::class.java, true)
    }

    fun showHomeScreen() {
        val bundle = Bundle()
        bundle.putString(com.custom.app.util.Constants.FLOW, com.custom.app.util.Constants.NAV_SPLASH)
        startActivityWithLoad(this, HomeActivity::class.java, bundle, true)
    }
}