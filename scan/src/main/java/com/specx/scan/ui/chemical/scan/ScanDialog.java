package com.specx.scan.ui.chemical.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseDialog;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.skyfishjy.library.RippleBackground;
import com.specx.scan.R;
import com.specx.scan.R2;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.core.app.util.Constants.BLUETOOTH_SETTINGS_REQUEST;
import static com.core.app.util.Constants.LOCATION_SETTINGS_REQUEST;
import static com.data.app.db.table.ResultTable.BATCH_ID;
import static com.specx.scan.util.Constants.KEY_COMMODITY;
import static com.specx.scan.util.Constants.KEY_FARMER;
import static com.specx.scan.util.Constants.KEY_SAMPLE;

@SuppressLint("MissingPermission")
public class ScanDialog extends BaseDialog implements ScanView {

    private Unbinder unbinder;
    private Callback callback;
    private Disposable disposable;
    private RxPermissions rxPermissions;
    private Observable<Location> locationUpdatesObservable;

    @BindView(R2.id.ripple_background)
    RippleBackground rippleBackground;

    @OnClick(R2.id.ripple_background)
    void requestLocation() {
        Timber.d("Requesting for location...");
        RxUtils.dispose(disposable);
        rippleBackground.setEnabled(false);

        disposable = locationUpdatesObservable.subscribe(location -> {
            Timber.d("Found location: %s", location);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
                    showMessage("Connecting please wait...");
                    showScanScreen(location);
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

    @BindView(R2.id.btn_permission)
    Button btnPermission;

    @OnClick(R2.id.btn_permission)
    void enableLocation() {
        disposable = rxPermissions
                .request(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        showRippleView();
                        requestLocation();
                    } else {
                        hideRippleView();
                    }
                });
    }

    public static ScanDialog newInstance(String farmerCode, String batchId, SampleItem sample, CommodityItem commodity) {
        ScanDialog dialog = new ScanDialog();
        Bundle args = new Bundle();
        args.putString(BATCH_ID, batchId);
        args.putString(KEY_FARMER, farmerCode);
        args.putParcelable(KEY_SAMPLE, Parcels.wrap(sample));
        args.putParcelable(KEY_COMMODITY, Parcels.wrap(commodity));
        dialog.setArguments(args);
        return dialog;
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
                        } catch (SendIntentException e) {
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
        View dialogView = inflater.inflate(R.layout.dialog_scan, container, false);
        unbinder = ButterKnife.bind(this, dialogView);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (Util.isPermissionGranted(getActivity(), WRITE_EXTERNAL_STORAGE)
                && Util.isPermissionGranted(getActivity(), ACCESS_FINE_LOCATION)) {
            showRippleView();
            requestLocation();
        }

        return dialogView;
    }

    private void showRippleView() {
        btnPermission.setVisibility(View.GONE);
        rippleBackground.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
    }

    private void hideRippleView() {
        rippleBackground.stopRippleAnimation();
        rippleBackground.setVisibility(View.GONE);
        btnPermission.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void showScanScreen(Location location) {
        if (callback != null && getArguments() != null) {
            callback.showScanScreen(
                    getArguments().getString(BATCH_ID),
                    getLatLng("%.6f_%.6f", location),
                    getArguments().getString(KEY_FARMER),
                    Parcels.unwrap(getArguments().getParcelable(KEY_SAMPLE)),
                    Parcels.unwrap(getArguments().getParcelable(KEY_COMMODITY)));
        }

        dismiss();
    }

    private String getLatLng(String format, Location location) {
        return String.format(Locale.getDefault(), format, location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
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

        void showScanScreen(String batchId, String location, String farmerCode, SampleItem sample, CommodityItem commodity);

    }
}