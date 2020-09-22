package com.specx.scan.ui.chemical.result;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.core.app.ui.custom.SpannyText;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.data.app.reference.ReferenceType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.media.app.GlideApp;
import com.network.app.util.NetworkUtil;
import com.opencsv.CSVWriter;
import com.specx.device.ble.NIRScanSDK;
import com.specx.device.ble.NanoBleService;
import com.specx.scan.BuildConfig;
import com.specx.scan.R;
import com.specx.scan.R2;
import com.specx.scan.data.CanonicalMatrix;
import com.specx.scan.data.Preprocessing;
import com.specx.scan.data.ReferenceMatrix;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.analysis.AnalysisPayload;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.scan.ScanItem;
import com.specx.scan.ui.result.base.BaseResult;
import com.specx.scan.util.Constants;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import eo.view.batterymeter.BatteryMeterView;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimer.TimerListener;
import io.github.krtkush.lineartimer.LinearTimerStates;
import io.github.krtkush.lineartimer.LinearTimerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.os.Environment.getExternalStorageDirectory;
import static com.data.app.reference.ReferenceType.FACTORY;
import static com.opencsv.ICSVParser.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;
import static com.specx.device.util.Constants.DEVICE_NAME;

public class ChemicalResultActivity extends BaseResult implements TimerListener {

    private long startTime;
    private float totalTime;
    private Disposable disposable;
    private DataFactorPayload payload;
    private float humidity, temperature;
    private String uuid, firmware, modelName;
    private int battery = -1, counter, configDataSize, activeConfigIndex;

    private NIRScanSDK.ScanConfiguration activeConfig;
    private List<NIRScanSDK.ScanConfiguration> configs = new ArrayList<>();

    private List<Double> scales = new ArrayList<>();
    private List<Double> wavelengths = new ArrayList<>();
    private List<Integer> references = new ArrayList<>();
    private List<Double> absorbances = new ArrayList<>();

    private LinearTimer linearTimer;
    private ProgressDialog barProgressDialog;

    private final BroadcastReceiver infoReceiver = new InfoReceiver();
    private final BroadcastReceiver uuidReceiver = new UUIDReceiver();
    private final BroadcastReceiver statusReceiver = new StatusReceiver();
    private final BroadcastReceiver refReadyReceiver = new RefReadyReceiver();
    private final BroadcastReceiver notifyCompleteReceiver = new NotifyCompleteReceiver();
    private final BroadcastReceiver activeConfigReceiver = new ActiveConfigReceiver();
    private final BroadcastReceiver configSizeReceiver = new ConfigSizeReceiver();
    private final BroadcastReceiver configDataReceiver = new ConfigDataReceiver();
    private final BroadcastReceiver scanStartedReceiver = new ScanStartedReceiver();
    private final BroadcastReceiver requestCalCoeffReceiver = new RequestCalCoeffReceiver();
    private final BroadcastReceiver requestCalMatrixReceiver = new RequestCalMatrixReceiver();
    private final BroadcastReceiver scanDataReadyReceiver = new ScanDataReadyReceiver();
    private final BroadcastReceiver disconnectReceiver = new DisconnectReceiver();
    private final BroadcastReceiver unlockReceiver = new UnlockReceiver();
    private final BroadcastReceiver lockReceiver = new LockReceiver();

    private final IntentFilter infoFilter = new IntentFilter(NIRScanSDK.ACTION_INFO);
    private final IntentFilter uuidFilter = new IntentFilter(NIRScanSDK.SEND_DEVICE_UUID);
    private final IntentFilter statusFilter = new IntentFilter(NIRScanSDK.ACTION_STATUS);
    private final IntentFilter refReadyFilter = new IntentFilter(NIRScanSDK.REF_CONF_DATA);
    private final IntentFilter notifyCompleteFilter = new IntentFilter(NIRScanSDK.ACTION_NOTIFY_DONE);
    private final IntentFilter scanStartedFilter = new IntentFilter(NanoBleService.ACTION_SCAN_STARTED);
    private final IntentFilter requestCalCoeffFilter = new IntentFilter(NIRScanSDK.ACTION_REQ_CAL_COEFF);
    private final IntentFilter requestCalMatrixFilter = new IntentFilter(NIRScanSDK.ACTION_REQ_CAL_MATRIX);
    private final IntentFilter activeConfigFilter = new IntentFilter(NIRScanSDK.SEND_ACTIVE_CONF);
    private final IntentFilter configSizeFilter = new IntentFilter(NIRScanSDK.SCAN_CONF_SIZE);
    private final IntentFilter configDataFilter = new IntentFilter(NIRScanSDK.SCAN_CONF_DATA);
    private final IntentFilter scanDataReadyFilter = new IntentFilter(NIRScanSDK.SCAN_DATA);
    private final IntentFilter disconnectFilter = new IntentFilter(NIRScanSDK.ACTION_GATT_DISCONNECTED);
    private final IntentFilter unlockFilter = new IntentFilter(NIRScanSDK.ACTION_RETURN_ACTIVATE);
    private final IntentFilter lockFilter = new IntentFilter(NIRScanSDK.ACTION_RETURN_READ_ACTIVATE_STATE);

    private boolean connected;
    private NanoBleService nanoBleService;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler = new Handler();

    @BindView(R2.id.timerView)
    LinearTimerView timerView;

    @OnClick(R2.id.timerView)
    void pauseResumeTimer() {
        try {
            if (linearTimer.getState() == LinearTimerStates.ACTIVE) {
                ivPause.setImageDrawable(getDrawable(R.drawable.ic_play_white_24dp));
                linearTimer.pauseTimer();
            } else {
                ivPause.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24dp));
                linearTimer.resumeTimer();
            }
        } catch (Exception e) {
            Timber.e(e);
            showMessage(e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearTimer = new LinearTimer.Builder()
                .linearTimerView(timerView)
                .timerListener(this)
                .duration(4 * 1000)
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        startTime = System.currentTimeMillis();

        Intent gattServiceIntent = new Intent(this, NanoBleService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(infoReceiver, infoFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(uuidReceiver, uuidFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, statusFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(refReadyReceiver, refReadyFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(scanDataReadyReceiver, scanDataReadyFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(notifyCompleteReceiver, notifyCompleteFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(requestCalCoeffReceiver, requestCalCoeffFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(requestCalMatrixReceiver, requestCalMatrixFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(activeConfigReceiver, activeConfigFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(configSizeReceiver, configSizeFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(configDataReceiver, configDataFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(scanStartedReceiver, scanStartedFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(disconnectReceiver, disconnectFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(unlockReceiver, unlockFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(lockReceiver, lockFilter);
    }

    @Override
    public void performScan() {
        super.performScan();

        scans.clear();
        counter = count;
        avgCsvPath = null;
        speak(getString(R.string.scanning_msg));
        tvDesc.setText(R.string.fix_device_desc_msg);
        tvTitle.setText(R.string.fix_device_title_msg);
        GlideApp.with(this).load(R.drawable.anim_fix_device).into(imageView);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NIRScanSDK.START_SCAN));
    }

    @Override
    public void onDataFactorLoaded(DataFactorPayload payload) {
        this.scales = payload.getScaleFactor();
        this.payload = payload;
        performScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu_battery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (battery != -1) {
            MenuItem item = menu.findItem(R.id.item_battery);
            item.setVisible(true);
            TextView tvBatteryLevel = item.getActionView().findViewById(R.id.tv_battery_level);
            BatteryMeterView batteryView = item.getActionView().findViewById(R.id.batteryView);

            tvBatteryLevel.setText(getString(R.string.batt_level_value, battery));
            batteryView.setChargeLevel(battery);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void animationComplete() {
        ivPause.setVisibility(View.GONE);
        timerView.setVisibility(View.GONE);
        tvCounter.setVisibility(View.GONE);
        Timber.d("Timer completed!");
        progressBar.setVisibility(View.VISIBLE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(NIRScanSDK.START_SCAN));
        GlideApp.with(this).load(R.drawable.anim_fix_device).into(imageView);
        tvTitle.setText(R.string.fix_device_title_msg);
        tvDesc.setText(R.string.fix_device_desc_msg);
        Timber.d("Scan started!");
    }

    @Override
    public void timerTick(long tickUpdateInMillis) {
    }

    @Override
    public void onTimerReset() {
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            nanoBleService = ((NanoBleService.LocalBinder) service).getService();

            if (!nanoBleService.initialize()) {
                finish();
            }

            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                showMessage("Please ensure Bluetooth is enabled and try again");
                finish();
            }
            scanLeDevice(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            nanoBleService = null;
        }
    };

    private final ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            String deviceAddress = device.getAddress();
            if (name != null && name.startsWith(DEVICE_NAME)) {
                if (!TextUtils.isEmpty(userManager.getSavedDevice()) || !TextUtils.isEmpty(deviceMac)) {
                    if (deviceAddress.equals(userManager.getSavedDevice()) || deviceAddress.equals(deviceMac)) {
                        nanoBleService.connect(deviceAddress);
                        connected = true;
                        scanLeDevice(false);
                    }
                }
            }
        }
    };

    private final Runnable runnable = () -> {
        if (bluetoothLeScanner != null && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            bluetoothLeScanner.stopScan(leScanCallback);
            if (!connected) {
                AlertUtil.showActionAlertDialog(this,
                        getString(R.string.not_connected_title), getString(R.string.connect_again_msg),
                        false, getString(R.string.btn_ok), (dialog, which) -> finish());
            }
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(runnable, Constants.SCAN_PERIOD);
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.startScan(null, new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(), leScanCallback);
            } else {
                showMessage("Please ensure Bluetooth is enabled and try again");
                finish();
            }
        } else {
            if (bluetoothLeScanner != null && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                bluetoothLeScanner.stopScan(leScanCallback);
            }
        }
    }

    public class RequestCalCoeffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE, 0);
            boolean size = intent.getBooleanExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE_PACKET, false);
            if (size) {
                progressBar.setVisibility(View.GONE);
                barProgressDialog = new ProgressDialog(ChemicalResultActivity.this);

//              barProgressDialog.setTitle(getString(R.string.dl_ref_cal));
                barProgressDialog.setTitle(getString(R.string.connecting_msg));
                barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                barProgressDialog.setProgress(0);
                barProgressDialog.setMax(intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE, 0));
                barProgressDialog.setProgressNumberFormat(null);
                barProgressDialog.setCancelable(false);
                barProgressDialog.show();
            } else {
                barProgressDialog.setProgress(barProgressDialog.getProgress() + intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_COEFF_SIZE, 0));
            }
        }
    }

    public class RefReadyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] refCoeff = intent.getByteArrayExtra(NIRScanSDK.EXTRA_REF_COEF_DATA);
            byte[] refMatrix = intent.getByteArrayExtra(NIRScanSDK.EXTRA_REF_MATRIX_DATA);
            ArrayList<NIRScanSDK.ReferenceCalibration> refCal = new ArrayList<>();
            refCal.add(new NIRScanSDK.ReferenceCalibration(refCoeff, refMatrix));
            NIRScanSDK.ReferenceCalibration.writeRefCalFile(getApplicationContext(), refCal);
        }
    }

    public class RequestCalMatrixReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0);
            boolean size = intent.getBooleanExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE_PACKET, false);
            if (size) {
                barProgressDialog.dismiss();
                barProgressDialog = new ProgressDialog(ChemicalResultActivity.this);

//              barProgressDialog.setTitle(getString(R.string.dl_cal_matrix));
                barProgressDialog.setTitle(getString(R.string.connecting_msg));
                barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                barProgressDialog.setProgress(0);
                barProgressDialog.setMax(intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0));
                barProgressDialog.setProgressNumberFormat(null);
                barProgressDialog.setCancelable(false);
                barProgressDialog.show();
            } else {
                barProgressDialog.setProgress(barProgressDialog.getProgress() + intent.getIntExtra(NIRScanSDK.EXTRA_REF_CAL_MATRIX_SIZE, 0));
            }
            if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
                barProgressDialog.dismiss();

                progressBar.setVisibility(View.VISIBLE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.GET_ACTIVE_CONF));
            }
        }
    }

    public class ActiveConfigReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            activeConfigIndex = intent.getByteArrayExtra(NIRScanSDK.EXTRA_ACTIVE_CONF)[0];

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.GET_SCAN_CONF));
        }
    }

    public class ConfigSizeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            configDataSize = intent.getIntExtra(NIRScanSDK.EXTRA_CONF_SIZE, 0);

            if (configDataSize > 0) {
                configs.clear();
            }
        }
    }

    private class ConfigDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NIRScanSDK.ScanConfiguration config;
            if (intent.getByteArrayExtra(NIRScanSDK.EXTRA_DATA).length > 100) {
                config = NIRScanSDK.GetScanConfiguration(intent.getByteArrayExtra(NIRScanSDK.EXTRA_DATA));
            } else {
                config = NIRScanSDK.GetOneSectionScanConfiguration(intent.getByteArrayExtra(NIRScanSDK.EXTRA_DATA));
            }

            configs.add(config);

            if (configs.size() == configDataSize) {
                for (int i = 0; i < configs.size(); i++) {
                    int configIndex = (byte) configs.get(i).getScanConfigIndex();
                    if (configIndex == activeConfigIndex) {
                        activeConfig = configs.get(i);
                    }
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.GET_STATUS));
            }
        }
    }

    public class StatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            battery = intent.getIntExtra(NIRScanSDK.EXTRA_BATT, 0);
            humidity = intent.getFloatExtra(NIRScanSDK.EXTRA_HUMID, 0);
            temperature = intent.getFloatExtra(NIRScanSDK.EXTRA_TEMP, 0);

            invalidateOptionsMenu();

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.GET_INFO));
        }
    }

    public class InfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            modelName = intent.getStringExtra(NIRScanSDK.EXTRA_MODEL_NUM);
            serialNumber = intent.getStringExtra(NIRScanSDK.EXTRA_SERIAL_NUM);

            String hwRev = intent.getStringExtra(NIRScanSDK.EXTRA_HW_REV);
            String tivaRev = intent.getStringExtra(NIRScanSDK.EXTRA_TIVA_REV);
            String specRev = intent.getStringExtra(NIRScanSDK.EXTRA_SPECTRUM_REV);

            firmware = String.format("%s-%s-%s", hwRev, tivaRev, specRev);

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.GET_UUID));
        }
    }

    public class UUIDReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            byte[] uuidData = intent.getByteArrayExtra(NIRScanSDK.EXTRA_DEVICE_UUID);

            StringBuilder uuidBuilder = new StringBuilder();

            for (int i = 0; i < uuidData.length; i++) {
                uuidBuilder.append(Integer.toHexString(0xff & uuidData[i]));
                if (i != uuidData.length - 1) {
                    uuidBuilder.append(":");
                }
            }

            uuid = uuidBuilder.toString().toUpperCase();

            Intent lockState = new Intent(NIRScanSDK.ACTION_READ_ACTIVATE_STATE);
            LocalBroadcastManager.getInstance(context).sendBroadcast(lockState);
        }
    }

    public class LockReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            checkDeviceState(intent.getByteArrayExtra(NIRScanSDK.RETURN_READ_ACTIVATE_STATE));
        }
    }

    public class UnlockReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            checkDeviceState(intent.getByteArrayExtra(NIRScanSDK.RETURN_ACTIVATE_STATUS));
        }
    }

    public class NotifyCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NIRScanSDK.SET_TIME));
        }
    }

    public void checkDeviceState(byte[] state) {
        if (state[0] == 1 || serialNumber.contains("T")) {
            String msg = String.format("Connected to device %s", serialNumber);
            Timber.d(msg);
            showMessage(msg);

            if (isWarmup) {
                if (serialNumber.contains("T") || serialNumber.contains("R009")
                        || serialNumber.contains("R028")) {
                    userManager.setSavedDevice(deviceMac);
                    showMessage(String.format("Device %s ready, you can scan now!", deviceMac));
                    finish();
                    return;
                }

                switchOnTheLamp();

                imageView.setAnimation(userManager.isNightMode() ? R.raw.anim_timer_dark : R.raw.anim_timer_light);
                imageView.playAnimation();

                disposable = Single.timer(5, BuildConfig.DEBUG ? TimeUnit.SECONDS : TimeUnit.MINUTES)
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(success -> {
                            imageView.cancelAnimation();

                            userManager.setSavedDevice(deviceMac);
                            showMessage(String.format("Device %s ready, you can scan now!", deviceMac));
                            finish();
                        });
            } else {
                performScan();
            }
        } else {
            showUnlockDeviceDialog();
            showMessage("Please unlock the device to proceed!");
        }
    }

    public void showUnlockDeviceDialog() {
        View customView = LayoutInflater.from(this).inflate(R.layout.layout_device_unlock, null);
        final EditText etKey = customView.findViewById(R.id.et_key);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.btn_submit), null);
        builder.setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);

        alert.setOnShowListener(dialog -> {
            Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(view -> {
                String key = etKey.getText().toString();
                if (!TextUtils.isEmpty(key)) {
                    String filter = "[^0-9^A-Z^a-z]";
                    Pattern pattern = Pattern.compile(filter);
                    Matcher matcher = pattern.matcher(key);
                    key = matcher.replaceAll("").trim();
                    if (key.length() == 24) {
                        byte[] data = Util.convertHexToBytes(key);
                        Intent activateIntent = new Intent(NIRScanSDK.ACTION_ACTIVATE_STATE);
                        activateIntent.putExtra(NIRScanSDK.ACTIVATE_STATE_KEY, data);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(activateIntent);
                        dialog.dismiss();
                    } else {
                        AlertUtil.showToast(this, "Activation key has to be 24-digit!");
                    }
                } else {
                    AlertUtil.showToast(this, "Activation key can't be empty!");
                }
            });
        });

        alert.show();
    }

    private void switchOnTheLamp() {
        Intent lampIntent = new Intent(NIRScanSDK.ACTION_LAMP);
        lampIntent.putExtra(NIRScanSDK.LAMP_ON_OFF, 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(lampIntent);
    }

    public class ScanDataReadyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Scan data received!");
            progressBar.setVisibility(View.GONE);

            byte[] scanData = intent.getByteArrayExtra(NIRScanSDK.EXTRA_DATA);

            NIRScanSDK.ReferenceCalibration ref = NIRScanSDK.ReferenceCalibration.currentCalibration.get(0);

            double[] wavelength = new double[700];
            int[] uncalibratedIntensity = new int[700];
            int[] intensity = new int[700];
            int length = NIRScanSDK.dlpSpecScanInterpReference(scanData, ref.getRefCalCoefficients(),
                    ref.getRefCalMatrix(), wavelength, intensity, uncalibratedIntensity);
            NIRScanSDK.ScanResults results = new NIRScanSDK.ScanResults(wavelength, intensity, uncalibratedIntensity, length);

            if (results.getLength() == 0 || results.getWavelength().length == 0
                    || results.getIntensity().length == 0 || results.getUncalibratedIntensity().length == 0) {
                showMessage(getString(R.string.empty_result_msg));
                return;
            }

            if (wavelengths.isEmpty()) {
                for (int i = 0, j = results.getLength(); i < j; i++) {
                    wavelengths.add(results.getWavelength()[i]);
                }
            }

            if (references.isEmpty()) {
                switch (ReferenceType.from(userManager.getReferenceType())) {
                    case FACTORY:
                        for (int i = 0, j = results.getLength(); i < j; i++) {
                            references.add(results.getIntensity()[i]);
                        }
                        break;
                    case SPECTRALON:
                        references = ReferenceMatrix.spectralonReference;
                        break;
                    case COMPONENT:
                        references = ReferenceMatrix.componentReference;
                        break;
                    case ADULTERANT:
                        references = ReferenceMatrix.adulterantReference;
                        break;
                    case VETA:
                        references = ReferenceMatrix.vetaReference;
                        break;
                    case PREVIOUS:
                        if (!TextUtils.isEmpty(userManager.getScanReference())) {
                            try {
                                references = new Gson().fromJson(userManager.getScanReference(),
                                        new TypeToken<List<Integer>>() {}.getType());
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                        break;
                }
            }

            List<Integer> intensities = new ArrayList<>();
            List<Double> reflectances = new ArrayList<>();

            for (int i = 0, j = results.getLength(); i < j; i++) {
                intensities.add(results.getUncalibratedIntensity()[i]);
                if (references.size() > i) {
                    reflectances.add((double) intensities.get(i) / references.get(i));
                }
            }

            if (isReference) {
                if (!intensities.isEmpty()) {
                    String scanReference = new Gson().toJson(intensities);
                    if (!TextUtils.isEmpty(scanReference)) {
                        userManager.setScanReference(scanReference);
                        showMessage("Reference set successfully!");
                    }
                }
                finish();
                return;
            }

            ScanItem scan = new ScanItem();
            scan.setIntensities(intensities);
            scan.setReflectances(reflectances);

            scans.add(scan);

            if (counter == 0) {
                Timber.d("Counter completed!");

                hideTitleDescImage();

                analyses.clear();

                calculateAbsorbance();

//                for (AnalysisPayload analysis : payload.getAnalysisList()) {
//                    calculateAnalysisResult(analysis);
//                }

                totalTime = (float) (System.currentTimeMillis() - startTime) / 1000;

                String fileName = String.format("%s_r", batchId);

                avgCsvPath = writeAvgCsv(fileName);

                if (TextUtils.isEmpty(avgCsvPath)) {
                    showMessage("Couldn't save average csv file!");
                }

                Bundle params = new Bundle();
                params.putString("batch_id", batchId);
                params.putString("device", serialNumber);
                params.putInt("battery", battery);
                params.putFloat("humidity", humidity);
                params.putFloat("temperature", temperature);
                params.putFloat("total_time", totalTime);
                params.putString("location", location);
                params.putString("csv_path", avgCsvPath);
                params.putString("commodity", commodity.getName());
                params.putInt("scan_count", scans != null ? scans.size() : 0);
                params.putBoolean("factory", ReferenceType.from(userManager.getReferenceType()) == FACTORY);
                analytics.logEvent("chemical_result", params);

                storeScanResult(serialNumber, avgCsvPath);

                if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    uploadChemicalSpectra(avgCsvPath);
                } else {
                    onUploadScanFailure();
                }
            }
        }
    }

    private void calculateAbsorbance() {
        Timber.d("Total scans %d", scans.size());

        if (scans.size() != count) {
            String msg = String.format("Scan count mismatch! (%s / %s)", scans.size(), count);
            showMessage(msg);
            Timber.d(msg);
        }

        absorbances.clear();

        for (int i = 0, j = wavelengths.size(); i < j; i++) {
            double reflectance = 0;
            for (int p = 0, q = scans.size(); p < q; p++) {
                reflectance += scans.get(p).getReflectances().get(i);
            }
            reflectance = reflectance / scans.size();
            double absorbance = (-1) * Math.log10(reflectance);
            absorbances.add(absorbance);
        }

        if (false) {
            try {
                double[] w = Util.toPrimitive(wavelengths.toArray(new Double[0]));
                double[] a = Util.toPrimitive(absorbances.toArray(new Double[0]));
                double[] c = Util.toPrimitive(CanonicalMatrix.turmericPowder.toArray(new Double[0]));

                double[] result = Preprocessing.findCanonicalArray(w, a, c);

                absorbances.clear();

                for (double value : result) {
                    absorbances.add(value);
                }

                for (int i = 0, j = wavelengths.size(); i < j; i++) {
                    double scaleFactor = scales.size() > i ? scales.get(i) : 1;
                    double absorbance = absorbances.get(i) * scaleFactor;
                    absorbances.set(i, absorbance);
                }
            } catch (Exception e) {
                Timber.e(e);
                showMessage(getString(R.string.unknown_error_msg));
                finish();
            }
        }
    }

    private void calculateAnalysisResult(AnalysisPayload analysis) {
        double sum = 0;
        String algorithm = analysis.getAlgorithm();
        String algoConfig = analysis.getAlgoConfig();
        List<Double> betas = analysis.getBetaMatrix();
        List<Double> means = analysis.getMeanMatrix();
        try {
            List<Double> preAbsorbances = new Preprocessing().apply(algorithm, algoConfig, absorbances, means);
            preAbsorbances.add(0, 1.0);
            List<Double> sumMatrices = new ArrayList<>();
            for (int i = 0, j = betas.size(); i < j; i++) {
                if (preAbsorbances.size() > i) {
                    double absorbance = preAbsorbances.get(i);
                    double beta = betas.get(i);

                    sumMatrices.add(absorbance * beta);
                }
            }

            for (int i = 0, j = sumMatrices.size(); i < j; i++) {
                if (!Double.isNaN(sumMatrices.get(i))) {
                    sum += sumMatrices.get(i);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            showMessage(getString(R.string.unknown_error_msg));
        }

        String analysisName = analysis.getName();

        String id = String.valueOf(analyses.size() + 1);

        String amountUnit = "";
        String result;
        if (commodity.getName().contains("Milk") && analysisName.equalsIgnoreCase("Detergent")) {
            if (sum >= 2.14f) {
                result = "No";
            } else {
                result = "Yes";
            }
        } else if (commodity.getName().contains("Milk") && analysisName.equalsIgnoreCase("Urea")) {
            result = "No";
        } else if (commodity.getName().contains("Licorice") && analysisName.equalsIgnoreCase("Glabridin")) {
            if (sum >= 0.25f) {
                result = "Yes";
            } else {
                result = "No";
            }
        } else {
            result = String.format(Locale.getDefault(), "%.2f", sum);
            amountUnit = "%";
        }

        if (!TextUtils.isEmpty(userManager.getResultHardcode())) {
            String resultHardcode = userManager.getResultHardcode().trim();
            if (resultHardcode.contains("-")) {
                try {
                    float min = Float.parseFloat(resultHardcode.split("-")[0].trim());
                    float max = Float.parseFloat(resultHardcode.split("-")[1].trim());
                    result = String.format(Locale.getDefault(), "%.2f", Util.getRandomFloat(min, max));
                } catch (Exception e) {
                    Timber.e(e);
                }
                analyses.add(new AnalysisItem(id, analysisName, result, amountUnit, algorithm,
                        algoConfig, betas, means, analysis.getDarkThumbUrl(), analysis.getLightThumbUrl()));
            } else {
                analyses.add(new AnalysisItem(id, analysisName, resultHardcode, amountUnit, algorithm,
                        algoConfig, betas, means, analysis.getDarkThumbUrl(), analysis.getLightThumbUrl()));
            }
        } else {
            analyses.add(new AnalysisItem(id, analysisName, result, amountUnit, algorithm, algoConfig,
                    betas, means, analysis.getDarkThumbUrl(), analysis.getLightThumbUrl()));
        }
    }

    public class ScanStartedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Scan completed!");

            counter--;
            Timber.d("Counter %d", counter);
            if (counter > 0) {
                progressBar.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
                timerView.setVisibility(View.VISIBLE);
                tvCounter.setVisibility(View.VISIBLE);
                speak(String.valueOf(count - counter + 1));
                SpannyText spanny = new SpannyText(String.valueOf(count - counter + 1))
                        .append(String.format(Locale.getDefault(), "/%d", count),
                                new RelativeSizeSpan(0.5f));
                tvCounter.setText(spanny);
                if (linearTimer.getState() == LinearTimerStates.INITIALIZED) {
                    linearTimer.startTimer();
                    Timber.d("Timer started!");
                } else {
                    linearTimer.restartTimer();
                    Timber.d("Timer restarted!");
                }
                GlideApp.with(context).load(R.drawable.anim_move_device).into(imageView);
                tvTitle.setText(R.string.move_device_title_msg);
                tvDesc.setText(R.string.move_device_desc_msg);
            }
        }
    }

    private String writeAvgCsv(String fileName) {
        Timber.d("Writing average csv %s", fileName);

        String dirPath = getExternalStorageDirectory().getAbsolutePath() + "/CSV/" + batchId;

        File dir = new File(dirPath);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Timber.d("Failed to create directory");
                return null;
            }
        }

        String filePath = dirPath + "/" + fileName + ".csv";

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath), ',',
                    CSVWriter.NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);

            List<String[]> row = new ArrayList<>();

            List<String> headers = new ArrayList<>();

            headers.add("Version");
            headers.add("Android");
            headers.add("Phone");
            headers.add("Device");
            headers.add("UUID");
            headers.add("Model");
            headers.add("Firmware");
            headers.add("Config");
            headers.add("Battery");
            headers.add("Humidity");
            headers.add("Temperature");
            headers.add("Factory");
            headers.add("Commodity");
            headers.add("Count");
            headers.add("Duration");
            headers.add("Datetime");
            headers.add("Location");
            headers.add("Batch");
            headers.add("Header 1");
            headers.add("Header 2");
            headers.add("Header 3");
            headers.add("Header 4");
            headers.add("Header 5");

/*
            for (AnalysisItem analysis : analyses) {
                headers.add(analysis.getName());
            }

            for (AnalysisItem analysis : analyses) {
                headers.add(String.format("Algorithm %s", analysis.getId()));
            }
*/
            row.add(headers.toArray(new String[0]));

            List<String> results = new ArrayList<>();

            results.add(appVersion);
            results.add(String.valueOf(Build.VERSION.SDK_INT));
            results.add(Build.MODEL);
            results.add(serialNumber);
            results.add(uuid);
            results.add(modelName);
            results.add(firmware);
            results.add(String.valueOf(activeConfig.getConfigName()));
            results.add(String.valueOf(battery));
            results.add(String.valueOf(humidity));
            results.add(String.valueOf(temperature));
            results.add(String.valueOf(ReferenceType.from(userManager.getReferenceType()) == FACTORY));
            results.add(commodity.getName());
            results.add(String.format("%s (%s)", scans.size(), count));
            results.add(String.valueOf(totalTime));
            results.add(Util.getDatetime());
            results.add(location);
            results.add(batchId);

/*
            for (AnalysisItem analysis : analyses) {
                results.add(analysis.getTotalAmount());
            }

            for (AnalysisItem analysis : analyses) {
                results.add(String.format("%s (%s)", analysis.getAlgorithm(), analysis.getAlgoConfig()));
            }
*/

            row.add(results.toArray(new String[0]));

            row.add(new String[]{});

            List<String> tabs = new ArrayList<>();

            tabs.add("W");
            tabs.add("R");
            tabs.add("A0");

            for (int i = 0, j = scans.size(); i < j; i++) {
                tabs.add(String.format(Locale.getDefault(), "I-%d", i + 1));
                tabs.add(String.format(Locale.getDefault(), "A-%d", i + 1));
            }

            row.add(tabs.toArray(new String[0]));

            for (int i = 0, j = wavelengths.size(); i < j; i++) {

                List<String> column = new ArrayList<>();

                column.add(String.valueOf(wavelengths.get(i)));
                column.add(String.valueOf(references.get(i)));
                column.add(String.valueOf(absorbances.get(i)));

                for (int p = 0, q = scans.size(); p < q; p++) {
                    int intensity = scans.get(p).getIntensities().get(i);
                    double reflectance = (double) intensity / references.get(i);
                    double absorbance = (-1) * Math.log10(reflectance);
                    column.add(String.valueOf(intensity));
                    column.add(String.valueOf(absorbance));
                }

                for (AnalysisItem analysis : analyses) {
                    if (analysis.getBetas().size() > i) {
                        column.add(String.valueOf(analysis.getBetas().get(i)));
                    }
                }

                for (AnalysisItem analysis : analyses) {
                    if (analysis.getMeans() != null && !analysis.getMeans().isEmpty()) {
                        if (analysis.getMeans().size() > i) {
                            column.add(String.valueOf(analysis.getMeans().get(i)));
                        }
                    }
                }

                row.add(column.toArray(new String[0]));
            }

            writer.writeAll(row);
            writer.close();

            return filePath;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public class DisconnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AlertUtil.showActionAlertDialog(ChemicalResultActivity.this,
                    getString(R.string.not_connected_title), getString(R.string.not_connected_msg),
                    false, getString(R.string.btn_ok), (dialog, which) -> finish());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxUtils.dispose(disposable);

        unbindService(serviceConnection);
        handler.removeCallbacks(runnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(infoReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uuidReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refReadyReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(scanDataReadyReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyCompleteReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(requestCalCoeffReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(requestCalMatrixReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activeConfigReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(configSizeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(configDataReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(scanStartedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(disconnectReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unlockReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(lockReceiver);
    }
}