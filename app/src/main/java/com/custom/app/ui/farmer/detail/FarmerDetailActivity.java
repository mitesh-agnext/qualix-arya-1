package com.custom.app.ui.farmer.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.base.app.ui.base.BaseActivity;
import com.core.app.rule.Email;
import com.core.app.util.AlertUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.custom.app.R;
import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.gisnext.lib.ui.boundary.MarkBoundaryActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import dagger.android.AndroidInjection;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.gisnext.lib.util.Constants.KEY_AREA;
import static com.gisnext.lib.util.Constants.KEY_COLOR;
import static com.gisnext.lib.util.Constants.KEY_POINTS;
import static com.specx.scan.util.Constants.KEY_LOCATION;

public class FarmerDetailActivity extends BaseActivity implements FarmerDetailView, ValidationListener {

    private Bitmap qrBitmap;
    private String farmerCode;
    private Validator validator;
    private Disposable disposable;
    private RxPermissions rxPermissions;
    private BarcodeEncoder barcodeEncoder;
    private ArrayList<LatLng> points;

    @Inject
    FarmerDetailPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Order(1)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_name_msg)
    @BindView(R.id.et_name)
    EditText etName;

    @Order(2)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_email_msg)
    @Email(sequence = 2, messageResId = R.string.invalid_email_msg)
    @BindView(R.id.et_email)
    EditText etEmail;

    @Order(3)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_phone_msg)
    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.til_field)
    TextInputLayout tilField;

    @Order(4)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_field_msg)
    @BindView(R.id.et_field)
    EditText etField;

    @BindView(R.id.layout_main)
    LinearLayout layout_main;

    @BindView(R.id.tv_plot)
    TextView tvPlot;

    @Order(5)
    @NotEmpty(sequence = 1, trim = true, messageResId = R.string.empty_village_msg)
    @BindView(R.id.et_village)
    EditText etVillage;

    @OnClick(R.id.tv_plot)
    void markPlot() {
        Intent intent = new Intent(this, MarkBoundaryActivity.class);
        intent.putExtra(KEY_COLOR, ContextCompat.getColor(this, R.color.green));
        startActivityForResult(intent, 101);
    }

    @BindView(R.id.iv_qrcode)
    ImageView ivQrCode;

    @OnLongClick(R.id.iv_qrcode)
    void copyFarmerCode() {
        Util.copyToClipboard(this, "Farmer code", farmerCode);
        AlertUtil.showSnackBar(layout_main, "Farmer ID copied to clipboard!", 2000);
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
                            String text = String.format("Farmer code: %s", farmerCode);

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            qrBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                    qrBitmap, "QRCode", null);

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

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @OnClick(R.id.btn_submit)
    void submit(View view) {
        if (TextUtils.isEmpty(farmerCode)) {
            Util.hideSoftKeyboard(view);
            ivShare.setVisibility(View.GONE);
            ivQrCode.setVisibility(View.INVISIBLE);
            ivQrCode.setImageResource(android.R.color.transparent);
            validator.validate();
        } else {
            copyFarmerCode();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_detail);
        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);
        barcodeEncoder = new BarcodeEncoder();

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            ((TextView) toolbar.findViewById(R.id.title)).setText(getString(R.string.title_generate_qr));
        }

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.registerAnnotation(Email.class);
        validator.setValidationListener(this);

        tilField.setEndIconOnClickListener(v -> {
            etField.getText().clear();
            tvPlot.setText(R.string.title_mark_plot);
        });

        etField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    tilField.setEndIconVisible(true);
                    tvPlot.setVisibility(View.VISIBLE);
                } else {
                    points = null;
                    tvPlot.setVisibility(View.GONE);
                }
            }
        });

        presenter.setView(this);
    }

    @Override
    public void onValidationSucceeded() {
        if (points != null && !points.isEmpty()) {
            FarmerItem farmer = new FarmerItem.Builder()
                    .setCode(String.format("%s_%s", userManager.getUserId(), Util.getDatetime()))
                    .setName(etName.getText().toString())
                    .setEmail(etEmail.getText().toString())
                    .setMobile(etPhone.getText().toString())
                    .setField(etField.getText().toString())
//                  .setPlot(PolyUtil.encode(points))
                    .setPlot(points)
                    .setLocation(etVillage.getText().toString())
                    .build();

            presenter.uploadFarmer(farmer);
        } else {
            tilField.setEndIconVisible(false);
            etField.setError("Please mark plot!");
            etField.requestFocus();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String[] listMessage = error.getCollatedErrorMessage(this).split("\n");
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
        AlertUtil.showToast(this, msg);
    }

    @Override
    public void showQRCode(String farmerCode) {
        try {
            this.farmerCode = farmerCode;
            showMessage(String.format("Generated farmer code: %s", farmerCode));
            qrBitmap = barcodeEncoder.encodeBitmap(farmerCode, BarcodeFormat.QR_CODE, 800, 800);
            ivQrCode.setImageBitmap(qrBitmap);
            ivShare.setVisibility(View.VISIBLE);
            ivQrCode.setVisibility(View.VISIBLE);

            etName.setEnabled(false);
            etEmail.setEnabled(false);
            etPhone.setEnabled(false);
            etField.setEnabled(false);
            tvPlot.setClickable(false);
            etVillage.setEnabled(false);
            tilField.setEndIconOnClickListener(null);

            btnSubmit.setText(R.string.btn_done);
        } catch (Exception e) {
            Timber.e(e);
            showMessage(getString(R.string.unknown_error_msg));
        }
    }

    @Override
    public void showAddFarmerDialog(FarmerItem farmer) {
        AlertUtil.showActionAlertDialog(this, "Do you want to save the farmer details in the cache?",
                getString(R.string.btn_cancel), getString(R.string.btn_yes), (dialog, arg) -> presenter.addFarmerInDb(farmer));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK && data != null) {
                points = data.getParcelableArrayListExtra(KEY_POINTS);
                Timber.d(points.toString());
                double area = data.getDoubleExtra(KEY_AREA, 0);
                Timber.d(String.valueOf(area));
                LatLng location = data.getParcelableExtra(KEY_LOCATION);
                if (location != null) {
                    if (points.isEmpty()) {
                        points.add(location);
                    }
                    Timber.d(location.toString());
                }
                etField.setError(null);
                tilField.setEndIconVisible(true);
                tvPlot.setText(R.string.title_plot_marked);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.destroy();
        RxUtils.dispose(disposable);
    }
}