package com.printer.app;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.app.ui.base.BaseDialog;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.core.app.util.Constants.BLUETOOTH_SETTINGS_REQUEST;
import static com.core.app.util.Constants.LOCATION_SETTINGS_REQUEST;
import static com.printer.app.util.Constants.DEVICE_NAME;

@SuppressLint("MissingPermission")
public class SelectPrinterDialog extends BaseDialog implements SelectPrinterView {

    private Unbinder unbinder;
    private Callback callback;
    private Location location;
    private Disposable disposable;
    private Disposable bleDisposable;
    private RxPermissions rxPermissions;
    private Observable<Location> locationUpdatesObservable;

    private SelectPrinterController controller;
    private List<ScanResult> devices = new ArrayList<>();

    @BindView(R2.id.recyclerView)
    EmptyRecyclerView recyclerView;

    @BindView(R2.id.btn_permission)
    Button btnPermission;

    @OnClick(R2.id.btn_permission)
    void enableLocation() {
        disposable = rxPermissions
                .request(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        btnPermission.setVisibility(View.GONE);
                        requestLocation();
                    } else {
                        btnPermission.setVisibility(View.VISIBLE);
                    }
                });
    }

    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    public static SelectPrinterDialog newInstance() {
        return new SelectPrinterDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rxPermissions = new RxPermissions(this);

        locationUpdatesObservable = locationProvider
                .checkLocationSettings(locationSettings)
                .doOnNext(locationSettingsResult -> {
                    Status status = locationSettingsResult.getStatus();
                    if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            status.startResolutionForResult(getActivity(), LOCATION_SETTINGS_REQUEST);
                        } catch (IntentSender.SendIntentException e) {
                            Timber.e(e);
                            showMessage("Error opening location settings activity.");
                        }
                    }
                })
                .flatMap(locationSettingsResult -> locationProvider.getUpdatedLocation(locationRequest))
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_select_printer, container, false);
        unbinder = ButterKnife.bind(this, dialogView);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (Util.isPermissionGranted(getActivity(), WRITE_EXTERNAL_STORAGE)
                && Util.isPermissionGranted(getActivity(), ACCESS_FINE_LOCATION)) {
            btnPermission.setVisibility(View.GONE);
            requestLocation();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        controller = new SelectPrinterController(this);
        recyclerView.setAdapter(controller.getAdapter());

        return dialogView;
    }

    private void requestLocation() {
        Timber.d("Requesting for location...");
        RxUtils.dispose(disposable);

        showProgressBar();

        disposable = locationUpdatesObservable.subscribe(location -> {
            Timber.d("Found location: %s", location);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
                    if (this.location == null) {
                        this.location = location;
                        showMessage("Fetching devices please wait...");

                        bleDisposable = rxBleClient.scanBleDevices(
                                new ScanSettings.Builder()
                                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                        .build(),
                                new ScanFilter.Builder().build())
                                .timeout(10, TimeUnit.SECONDS)
                                .filter(result -> {
                                    RxBleDevice rxBleDevice = result.getBleDevice();
                                    String deviceName = rxBleDevice.getName();
                                    return deviceName != null && deviceName.startsWith(DEVICE_NAME);
                                })
                                .timeout(5, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> RxUtils.dispose(bleDisposable))
                                .subscribe(result -> {
                                            RxBleDevice rxBleDevice = result.getBleDevice();
                                            String deviceName = rxBleDevice.getName();
                                            String deviceMac = rxBleDevice.getMacAddress();
                                            Timber.d("Found device: %s (%s)", deviceName, deviceMac);

                                            if (devices.isEmpty()) {
                                                hideProgressBar();
                                            }

                                            devices.add(result);
                                            controller.setList(devices);
                                        }, error -> {
                                            if (error instanceof TimeoutException) {
                                                RxUtils.dispose(bleDisposable);
                                                showMessage("No device found!");
                                                dismiss();
                                            } else {
                                                showMessage(error.getMessage());
                                            }
                                        }
                                );
                    }
                } else {
                    if (callback != null) {
                        callback.showBluetoothDialog();
                    }
                }
            } else {
                showMessage("Bluetooth adapter not found!");
            }
        }, error -> showMessage(error.getMessage()));
    }

    @Override
    public void onItemClicked(RxBleDevice printer) {
        DisposableSingleObserver<BluetoothSocket> disposable = Single.just(printer.getBluetoothDevice())
                .map(device -> {
                    UUID uuid = device.getUuids()[0].getUuid();
                    BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                    return btSocket;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<BluetoothSocket>() {
                    @Override
                    public void onSuccess(BluetoothSocket btSocket) {
                        if (callback != null) {
                            callback.startPrinting(btSocket);
                        }

                        dismiss();
                    }

                    @Override
                    public void onError(Throwable error) {
                        showMessage(error.getMessage());
                    }
                });
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
        RxUtils.dispose(bleDisposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RxUtils.dispose(disposable);
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        Timber.d("User enabled location");
                        if (data != null) {
                            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
                            if (states.isGpsPresent() && states.isGpsUsable()) {
                                requestLocation();
                            }
                        }
                        break;
                    case RESULT_CANCELED:
                        Timber.d("User cancelled enabling location.");
                        dismiss();
                        break;
                }
                break;
            case BLUETOOTH_SETTINGS_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        Timber.d("User enabled bluetooth");
                        requestLocation();
                        break;
                    case RESULT_CANCELED:
                        Timber.d("User cancelled enabling bluetooth.");
                        dismiss();
                        break;
                }
                break;
        }
    }

    public interface Callback {

        void showBluetoothDialog();

        void startPrinting(BluetoothSocket btsocket);

    }
}