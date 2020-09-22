package com.base.app.ui.base;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.base.app.BuildConfig;
import com.base.app.R;
import com.core.app.util.AlertUtil;
import com.core.app.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import timber.log.Timber;

import static com.base.app.util.Constants.BACK_PRESS_INTERVAL;
import static com.firebase.app.util.Constants.KEY_ALERT_BUTTON;
import static com.firebase.app.util.Constants.KEY_ALERT_MESSAGE;
import static com.firebase.app.util.Constants.KEY_ALERT_TITLE;
import static com.firebase.app.util.Constants.KEY_FORCE_SHOW_ALERT;
import static com.firebase.app.util.Constants.KEY_MIN_VERSION_CODE;
import static com.firebase.app.util.Constants.KEY_REDIRECT_URL;
import static com.firebase.app.util.Constants.KEY_TARGET_USERS;

public abstract class BaseHome extends BaseActivity {

    private AlertDialog remoteAlertDialog;

    private Handler backPressHandler = new Handler();
    private boolean doubleBackToExitPressedOnce = false;
    private final Runnable backPressRunnable = () -> doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();

        fetchRemoteConfig();
    }

    private void fetchRemoteConfig() {
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Timber.d("Remote config params updated: %b", task.getResult());
                    } else {
                        Timber.d("Remote config fetching failed!");
                    }

                    checkTargetUsers();
                });
    }

    private void checkTargetUsers() {
        try {
            String targetUsers = remoteConfig.getString(KEY_TARGET_USERS);
            if (!TextUtils.isEmpty(targetUsers)) {
                List<String> users = new Gson().fromJson(targetUsers, new TypeToken<List<String>>() {}.getType());
                if (users.contains(userManager.getUserId())) {
                    showRemoteAlert();
                } else {
                    dismissRemoteAlertIfShowing();
                }
            } else {
                showRemoteAlert();
            }
        } catch (Exception e) {
            Timber.e(e);
            AlertUtil.showToast(this, getString(R.string.unknown_error_msg));
        }
    }

    private void showRemoteAlert() {
        int minVersionCode = Integer.parseInt(remoteConfig.getString(KEY_MIN_VERSION_CODE));
        if (minVersionCode > 0) {
            if (BuildConfig.VERSION_CODE < minVersionCode) {
                String alertTitle = remoteConfig.getString(KEY_ALERT_TITLE);
                String alertButton = remoteConfig.getString(KEY_ALERT_BUTTON);
                String redirectUrl = remoteConfig.getString(KEY_REDIRECT_URL);
                String alertMessage = remoteConfig.getString(KEY_ALERT_MESSAGE);
                boolean forceShowAlert = remoteConfig.getBoolean(KEY_FORCE_SHOW_ALERT);

                if (remoteAlertDialog == null) {
                    remoteAlertDialog = new AlertDialog.Builder(this)
                            .setTitle(alertTitle)
                            .setMessage(alertMessage)
                            .setCancelable(!forceShowAlert)
                            .setPositiveButton(alertButton, null)
                            .show();
                } else {
                    remoteAlertDialog.setTitle(alertTitle);
                    remoteAlertDialog.setMessage(alertMessage);
                    remoteAlertDialog.setCancelable(!forceShowAlert);
                    remoteAlertDialog.getButton(Dialog.BUTTON_POSITIVE).setText(alertButton);

                    if (forceShowAlert && !remoteAlertDialog.isShowing()) {
                        remoteAlertDialog.show();
                    }
                }

                Button button = remoteAlertDialog.getButton(Dialog.BUTTON_POSITIVE);
                button.setOnClickListener(v -> Util.handleUrl(this, redirectUrl));
            } else {
                dismissRemoteAlertIfShowing();
            }
        }
    }

    private void dismissRemoteAlertIfShowing() {
        if (remoteAlertDialog != null && remoteAlertDialog.isShowing()) {
            remoteAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            return;
        } else {
            doubleBackToExitPressedOnce = true;
        }

        AlertUtil.showToast(this, getString(R.string.app_exit_msg), BACK_PRESS_INTERVAL);
        backPressHandler.postDelayed(backPressRunnable, BACK_PRESS_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (backPressHandler != null) {
            backPressHandler.removeCallbacks(backPressRunnable);
        }
    }
}