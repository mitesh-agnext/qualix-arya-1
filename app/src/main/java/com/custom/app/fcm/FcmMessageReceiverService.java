package com.custom.app.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.ui.home.HomeActivity;
import com.custom.app.ui.splash.SplashScreen;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.user.app.data.UserManager;

import javax.inject.Inject;

import timber.log.Timber;

import static com.custom.app.util.Constants.FLOW;
import static com.custom.app.util.Constants.KEY_SCAN_ID;
import static com.custom.app.util.Constants.KEY_SCAN_STATUS;
import static com.custom.app.util.Constants.NAV_NOTIFICATION;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_NAME;

public class FcmMessageReceiverService extends FirebaseMessagingService {

    @Inject
    UserManager userManager;

    @Override
    public void onCreate() {
        super.onCreate();

        ((CustomApp) getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Timber.d("New fcm token: %s", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Timber.d("Received push message: %s", remoteMessage);

        if (remoteMessage.getData() != null) {
            Timber.d("Remote message data: %s", remoteMessage.getData().toString());

            showNotification(remoteMessage);
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Timber.d("Showing notification...");

        String scanId = remoteMessage.getData().get("scan_id");
        String deviceId = remoteMessage.getData().get("type_id");
        String deviceName = remoteMessage.getData().get("type_name");
        String scanStatus = remoteMessage.getData().get("scan_status_id");

        Intent intent;
        PendingIntent pendingIntent;

        if (userManager.isLoggedIn()) {
            intent = new Intent(this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(FLOW, NAV_NOTIFICATION);
            bundle.putString(KEY_SCAN_ID, scanId);
            bundle.putString(KEY_DEVICE_ID, deviceId);
            bundle.putString(KEY_DEVICE_NAME, deviceName);
            bundle.putString(KEY_SCAN_STATUS, scanStatus);
            intent.putExtras(bundle);

            pendingIntent = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = new Intent(this, SplashScreen.class);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(remoteMessage.getData().get("body"))
                .setSubText(String.format("%s (%s)", deviceName, deviceId))
                .setContentText(String.format("Scan Id: %s", scanId))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                manager.createNotificationChannel(channel);
            }
            int oneTimeID = (int) SystemClock.uptimeMillis();
            manager.notify(oneTimeID, builder.build());
        } else {
            Timber.e("Notification manager is null");
        }
    }
}