package com.custom.app.ui.farmer.scan;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.base.app.ui.base.BaseFragment;
import com.core.app.ui.custom.SpannyText;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.custom.app.R;
import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.custom.app.ui.home.HomeActivity;
import com.custom.app.ui.sample.SampleFragment;
import com.custom.app.ui.scan.select.SelectScanFragment;
import com.google.zxing.ResultPoint;
import com.jakewharton.rxbinding2.view.RxView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.CAMERA;
import static com.custom.app.util.Constants.KEY_SCAN_ID;
import static com.custom.app.util.Constants.SAMPLE_FRAGMENT;
import static com.custom.app.util.Constants.SELECT_SCAN_FRAGMENT;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_NAME;

public class FarmerScanFragment extends BaseFragment implements FarmerScanView, ValidationListener,
        BarcodeCallback {

    private String scanId = "";
    private int deviceId;
    private String deviceName;
    private boolean isFlashOn;
    private Unbinder unbinder;
    private Callback callback;
    private Validator validator;
    private Disposable disposable;
    private RxPermissions rxPermissions;

    @Inject
    FarmerScanPresenter presenter;

    @BindView(R.id.tv_scan_id)
    TextView tvScanId;

    @Order(1)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_qrcode_msg)
    @BindView(R.id.et_query)
    EditText etQuery;

    @BindView(R.id.btn_permission)
    Button btnPermission;

    @BindView(R.id.qr_view)
    DecoratedBarcodeView qrView;

    @BindView(R.id.btn_flash)
    ImageButton btnFlash;

    @OnClick(R.id.btn_flash)
    void switchFlash() {
        if (isFlashOn) {
            isFlashOn = false;
            qrView.setTorchOff();
            btnFlash.setImageResource(R.drawable.ic_flash_off);
        } else {
            isFlashOn = true;
            qrView.setTorchOn();
            btnFlash.setImageResource(R.drawable.ic_flash_on);
        }
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        Util.hideSoftKeyboard(etQuery);
        validator.validate();
    }

    @OnClick(R.id.tv_generate_qr)
    void generateQrCode() {
        if (callback != null) {
            callback.showAddFarmerScreen();
        }
    }

    public static FarmerScanFragment newInstance(String scanId, int deviceId, String deviceName) {
        FarmerScanFragment fragment = new FarmerScanFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SCAN_ID, scanId);
        args.putInt(KEY_DEVICE_ID, deviceId);
        args.putString(KEY_DEVICE_NAME, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rxPermissions = new RxPermissions(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_farmer_scan, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.setValidationListener(this);

        disposable = RxView.clicks(btnPermission)
                .compose(rxPermissions.ensure(CAMERA))
                .subscribe(granted -> {
                    if (granted) {
                        showQrView();
                    } else {
                        hideQrView();
                    }
                });

        qrView.decodeContinuous(this);

        if (Util.isPermissionGranted(context(), CAMERA)) {
            showQrView();
        }

        setStep(0);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getArguments() != null) {
            scanId = getArguments().getString(KEY_SCAN_ID);
            deviceId = getArguments().getInt(KEY_DEVICE_ID);
            deviceName = getArguments().getString(KEY_DEVICE_NAME);

            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                Toolbar toolbar = ((HomeActivity) getActivity()).binding.includeAppbar.includeToolbar.toolbar;
                ((TextView) toolbar.findViewById(R.id.title)).setText(deviceName);
            }

            if(!TextUtils.isEmpty(scanId)){
                tvScanId.setText(String.format("Scan ID: #%s", scanId));
                tvScanId.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showQrView() {
        btnPermission.setVisibility(View.GONE);
        qrView.setVisibility(View.VISIBLE);
        btnFlash.setVisibility(View.VISIBLE);
        qrView.resume();
    }

    private void hideQrView() {
        qrView.pause();
        qrView.setVisibility(View.GONE);
        btnFlash.setVisibility(View.GONE);
        btnPermission.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        qrView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrView.pause();
    }

    @Override
    public void onValidationSucceeded() {
        presenter.verifyFarmer(etQuery.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String[] listMessage = error.getCollatedErrorMessage(context()).split("\n");
            String msg = listMessage[0];

            if (view instanceof EditText) {
                ((EditText) view).setError(msg);
                view.requestFocus();
            } else {
                showMessage(msg);
            }
        }
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context(), msg);
    }

    @Override
    public void showFarmerDetail(FarmerItem farmer) {
        SpannyText spanny = new SpannyText()
                .append("Farmer verified successfully!")
                .append("\n\n")
                .append("Code: ").append(farmer.getCode())
                .append("\n")
                .append("Name: ").append(farmer.getName())
                .append("\n")
                .append("Email: ").append(farmer.getEmail());

        AlertUtil.showActionAlertDialog(context(), getString(R.string.title_scan), spanny,
                getString(R.string.btn_cancel), getString(R.string.btn_proceed),
                (dialog, which) -> showSampleScreen(farmer));
    }

    @Override
    public void showSampleScreen(FarmerItem farmer) {
        fragmentTransition(R.id.layout_content,
                SampleFragment.newInstance(scanId, deviceId, deviceName, farmer), SAMPLE_FRAGMENT);
    }

    private void setStep(int step) {
        Fragment fragment = getParentFragmentManager().findFragmentByTag(SELECT_SCAN_FRAGMENT);
        if (fragment != null) {
            ((SelectScanFragment) fragment).setStep(step);
        }
    }

    @Override
    public void showFarmerVerificationError() {
        SpannyText spanny = new SpannyText()
                .append("Farmer verification failed!")
                .append("\n\n")
                .append("Please add farmer to proceed");

        AlertUtil.showActionAlertDialog(context(), getString(R.string.title_scan), spanny,
                getString(R.string.btn_cancel), getString(R.string.btn_ok), (dialog, which) -> generateQrCode());
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        String qrCode = result.getText();
        if (!TextUtils.isEmpty(qrCode)) {
            etQuery.setText(qrCode);
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> points) {
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem skip = menu.findItem(R.id.item_skip);

        if (skip != null && isVisible()) {
            skip.setVisible(true);
            SpannyText spanny = new SpannyText(skip.getTitle(), new RelativeSizeSpan(1.3f));
            skip.setTitle(spanny);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_skip) {
            showSampleScreen(new FarmerItem("X"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        presenter.destroy();
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

    public interface Callback {

        void showAddFarmerScreen();

    }
}