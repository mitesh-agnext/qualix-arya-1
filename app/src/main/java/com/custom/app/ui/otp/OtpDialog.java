package com.custom.app.ui.otp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.app.ui.base.BaseDialog;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.custom.app.R;
import com.custom.app.data.model.login.LoginResponse;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.custom.app.util.Constants.KEY_CODE;

public class OtpDialog extends BaseDialog implements OtpView, ValidationListener {

    private String key;
    private Bitmap qrBitmap;
    private View dialogView;
    private Callback callback;
    private Unbinder unbinder;
    private Validator validator;
    private Disposable disposable;
    private RxPermissions rxPermissions;
    private BarcodeEncoder barcodeEncoder;

    @Inject
    OtpPresenter presenter;

    @BindView(R.id.iv_qrcode)
    ImageView ivQrCode;

    @OnLongClick(R.id.iv_qrcode)
    void copy() {
        Util.copyToClipboard(context(), "Authorisation key", key);
        AlertUtil.showToast(context(), "Authorisation key copied to clipboard!");
    }

    @BindView(R.id.iv_share)
    ImageView ivShare;

    @OnClick(R.id.iv_share)
    void share() {
        disposable = rxPermissions
                .request(WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        try {
                            String text = String.format("Authorisation key: %s", key);

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            qrBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(
                                    context().getContentResolver(), qrBitmap, "QRCode", null);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, text);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("image/*");

                            startActivity(Intent.createChooser(intent, "Share"));
                        } catch (Exception e) {
                            Timber.e(e);
                            showMessage(getString(R.string.unknown_error_msg));
                        }
                    }
                });
    }

    @Order(1)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_username_msg)
//  @Username(sequence = 2, messageResId = R.string.invalid_username_msg)
    @BindView(R.id.et_otp)
    EditText etOtp;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @OnClick(R.id.btn_submit)
    void submit(View view) {
        Util.hideSoftKeyboard(view);
        validator.validate();
    }

    public static OtpDialog newInstance(String... key) {
        OtpDialog dialog = new OtpDialog();
        if (key.length > 0 && !TextUtils.isEmpty(key[0])) {
            Bundle args = new Bundle();
            args.putString(KEY_CODE, key[0]);
            dialog.setArguments(args);
        }
        return dialog;
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
        barcodeEncoder = new BarcodeEncoder();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_otp, container, false);
        unbinder = ButterKnife.bind(this, dialogView);

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
//      validator.registerAnnotation(Username.class);
        validator.setValidationListener(this);

        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);

        if (getArguments() != null) {
            showQRCode(getArguments().getString(KEY_CODE));
        }
    }

    @Override
    public void onValidationSucceeded() {
        presenter.callVerifyOtp(etOtp.getText().toString().trim());
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
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(context, msg);
    }

    @Override
    public void showQRCode(String key) {
        try {
            this.key = key;
            showMessage(String.format("Generated authorisation key: %s", key));
            qrBitmap = barcodeEncoder.encodeBitmap(key, BarcodeFormat.QR_CODE, 800, 800);
            ivQrCode.setImageBitmap(qrBitmap);
            ivShare.setVisibility(View.VISIBLE);
            ivQrCode.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Timber.e(e);
            showMessage(getString(R.string.unknown_error_msg));
        }
    }

    @Override
    public void showLoginScreen() {
        dismiss();
    }

    @Override
    public void showHomeScreen(LoginResponse response) {
        if (callback != null) {
            callback.showHomeScreen(response);
        }

        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        unbinder.unbind();
        RxUtils.dispose(disposable);
    }

    public interface Callback {

        void showHomeScreen(LoginResponse response);

    }
}