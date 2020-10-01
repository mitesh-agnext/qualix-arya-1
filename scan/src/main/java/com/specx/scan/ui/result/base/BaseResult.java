package com.specx.scan.ui.result.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.base.app.ui.base.BaseActivity;
import com.base.app.ui.custom.StepProgressView;
import com.core.app.BuildConfig;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.core.app.util.FileUtil;
import com.core.app.util.RxUtils;
import com.core.app.util.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.opencsv.CSVWriter;
import com.printer.app.SelectPrinterDialog;
import com.specx.scan.R;
import com.specx.scan.R2;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.result.ResultItem;
import com.specx.scan.data.model.sample.SampleItem;
import com.specx.scan.data.model.scan.ScanItem;
import com.specx.scan.ui.payment.PrinterSdk;
import com.specx.scan.util.PicUtil;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.core.app.util.Constants.BLUETOOTH_SETTINGS_REQUEST;
import static com.data.app.db.table.ResultTable.BATCH_ID;
import static com.opencsv.ICSVParser.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;
import static com.printer.app.util.Constants.SELECT_PRINTER_DIALOG;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_MAC;
import static com.specx.scan.util.Constants.KEY_APP_VERSION;
import static com.specx.scan.util.Constants.KEY_COMMODITY;
import static com.specx.scan.util.Constants.KEY_FARMER;
import static com.specx.scan.util.Constants.KEY_LOCATION;
import static com.specx.scan.util.Constants.KEY_REFERENCE;
import static com.specx.scan.util.Constants.KEY_SAMPLE;
import static com.specx.scan.util.Constants.KEY_WARMUP;

public abstract class BaseResult extends BaseActivity implements ResultView, SelectPrinterDialog.Callback {

    protected int count;
    protected int deviceId;
    protected TextToSpeech tts;
    protected ResultItem result;
    protected SampleItem sample;
    protected CommodityItem commodity;
    protected ResultController controller;
    protected List<ScanItem> scans = new ArrayList<>();
    protected List<AnalysisItem> analyses = new ArrayList<>();
    protected boolean isWarmup, isReference, showPrint;
    protected String batchId, deviceMac, farmerCode, appVersion, serialNumber, location, avgCsvPath;

    private Disposable disposable;
    private BluetoothSocket btSocket;
    private PrinterSdk printerSdk = new PrinterSdk();

    @Inject
    ResultPresenter presenter;

    @BindView(R2.id.appbar_layout)
    protected AppBarLayout appbarLayout;

    @BindView(R2.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R2.id.stepsView)
    protected StepProgressView stepsView;

    @BindView(R2.id.tv_title)
    protected TextView tvTitle;

    @BindView(R2.id.tv_desc)
    protected TextView tvDesc;

    @BindView(R2.id.imageView)
    protected LottieAnimationView imageView;

    @BindView(R2.id.recyclerView)
    protected EmptyRecyclerView recyclerView;

    @BindView(R2.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R2.id.tv_counter)
    protected TextView tvCounter;

    @BindView(R2.id.iv_pause)
    protected ImageView ivPause;

    @BindView(R2.id.btn_upload)
    protected Button btnUpload;

    @OnClick(R2.id.btn_upload)
    public void uploadScans() {
        btnUpload.setVisibility(View.INVISIBLE);
        AlertUtil.showToast(this, "Uploading please wait...");

        if (deviceId == 1 || deviceId == 4) {
            updateScanResult();
        } else {
            if (!analyses.isEmpty()) {
                uploadScanResult(avgCsvPath);
            } else {
                uploadChemicalSpectra(avgCsvPath);
            }
        }
    }

    @BindView(R2.id.btn_share)
    protected Button btnShare;

    @OnClick(R2.id.btn_share)
    public void shareResults() {
        ListPopupWindow popupWindow = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);
        popupWindow.setAdapter(new ArrayAdapter<String>(this, R.layout.item_device_type,
                getResources().getStringArray(R.array.share_types)));
        popupWindow.setAnchorView(btnShare);
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {
            popupWindow.dismiss();

            if (position == 0) {
                Instacapture.INSTANCE.capture(BaseResult.this, new SimpleScreenCapturingListener() {
                    @Override
                    public void onCaptureComplete(@NotNull Bitmap bitmap) {
                        RxUtils.dispose(disposable);
                        disposable = PicUtil.getScreenshotFileObservable(BaseResult.this, bitmap)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(file -> {
                                    Uri uri = FileProvider.getUriForFile(BaseResult.this,
                                            "com.qualix.app.provider", file);

                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    intent.setType("image/jpeg");
                                    startActivity(Intent.createChooser(intent, "Share"));
                                });
                    }
                }, appbarLayout, stepsView, btnShare, btnUpload, btnDone);
            } else {
                try {
                    StringWriter stringWriter = new StringWriter();
                    CSVWriter csvWriter = new CSVWriter(stringWriter, ',',
                            CSVWriter.NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
                    DateTimeFormatter milliFormat = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                    DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
                    String[] headers = {"S/N", "Date", "Sample ID", "Parameter tested", "Result", "Tested by", "Device ID"};
                    csvWriter.writeNext(headers);
                    String index = "1";

                    LocalDateTime date;
                    String datetime;
                    String sampleId;
                    String userId;

                    if (result != null) {
                        date = LocalDateTime.parse(result.getDatetime(), milliFormat);
                        sampleId = result.getSample().getId();
                        userId = result.getUserId();
                    } else {
                        date = LocalDateTime.parse(Util.getDatetime(), milliFormat);
                        sampleId = sample.getId();
                        userId = userManager.getUserId();
                    }

                    datetime = datetimeFormat.format(date);

                    for (AnalysisItem analysis : analyses) {
                        String name = analysis.getName();
                        String value = analysis.getTotalAmount();
                        if (analyses.indexOf(analysis) == 0) {
                            csvWriter.writeNext(new String[]{index, datetime, sampleId, name, value, userId, serialNumber});
                        } else {
                            csvWriter.writeNext(new String[]{"", "", "", name, value, "", ""});
                        }
                    }

                    csvWriter.close();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_TEXT, stringWriter.toString());

                    startActivity(Intent.createChooser(intent, "Share"));
                } catch (Exception e) {
                    Timber.e(e);
                    showMessage(e.getMessage());
                }
            }
        });
        popupWindow.show();
    }

    @BindView(R2.id.btn_done)
    protected Button btnDone;

    @OnClick(R2.id.btn_done)
    public void done() {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("FLOW", "NAV_SPLASH");
        try {
            intent = new Intent(this, Class.forName("com.custom.app.ui.home.HomeActivity"));
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stepsView.setStepTitles(Arrays.asList("Scan", "Data", "Results"));
        stepsView.setVisibility(View.VISIBLE);
        setStep(0);
        setStep(1);
        setStep(2);

        appVersion = getIntent().getStringExtra(KEY_APP_VERSION);
        deviceMac = getIntent().getStringExtra(KEY_DEVICE_MAC);
        isWarmup = getIntent().getBooleanExtra(KEY_WARMUP, false);
        isReference = getIntent().getBooleanExtra(KEY_REFERENCE, false);

        batchId = getIntent().getStringExtra(BATCH_ID);
        deviceId = getIntent().getIntExtra(KEY_DEVICE_ID, 0);
        location = getIntent().getStringExtra(KEY_LOCATION);
        farmerCode = getIntent().getStringExtra(KEY_FARMER);
        sample = Parcels.unwrap(getIntent().getParcelableExtra(KEY_SAMPLE));
        commodity = Parcels.unwrap(getIntent().getParcelableExtra(KEY_COMMODITY));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            if (isReference) {
                ((TextView) toolbar.findViewById(R.id.title)).setText("Reference Scan");
                count = 1;
            } else if (isWarmup) {
                ((TextView) toolbar.findViewById(R.id.title)).setText("Warmup Timer");
            } else if (commodity != null && sample != null) {
                ((TextView) toolbar.findViewById(R.id.title)).setText(commodity.getName());
                count = commodity.getCount();
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        controller = new ResultController(this, userManager.isNightMode());
        recyclerView.setAdapter(controller.getAdapter());

        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = tts.setLanguage(Locale.UK);
                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Timber.e("TTS language is not supported!");
                } else {
                    Timber.d("TTS language is supported!");
                }
                Timber.d("TTS initialized successfully!");
            } else {
                showMessage("TTS initialization failed!");
            }
        });

        presenter.setView(this);
    }

    public void setStep(int step) {
        if (stepsView != null) {
            if (step == 0) {
                stepsView.resetView();
            } else {
                stepsView.markCurrentAsDone();
                stepsView.selectStep(step);
            }
        }
    }

    public void performScan() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    protected void speak(String text) {
        int speechStatus = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        if (speechStatus == TextToSpeech.ERROR) {
            Timber.e("Error in converting text to speech!");
        }
    }

    @Override
    public void showResult(ResultItem result) {
        this.result = result;
        this.sample = result.getSample();
        this.commodity = result.getCommodity();
        this.farmerCode = result.getFarmerCode();
        this.serialNumber = result.getSerialNumber();
        this.location = result.getLocation();
        this.analyses = result.getAnalyses();

        ((TextView) toolbar.findViewById(R.id.title)).setText(commodity.getName());

        showAnalyses(false, analyses);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(getApplicationContext(), msg);
    }

    @Override
    public void updateScanResult() {
        presenter.updateScanResult(farmerCode, batchId, location, sample, commodity, serialNumber, analyses);
    }

    @Override
    public void uploadScanResult(String filePath) {
        presenter.uploadScanResult(farmerCode, batchId, location, sample, commodity,
                serialNumber, filePath, analyses);
    }

    @Override
    public void uploadChemicalSpectra(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            presenter.uploadChemicalSpectra(batchId, commodity, sample, filePath);
        } else {
            AlertUtil.showToast(this, getString(R.string.empty_filepath_msg));
        }
    }

    @Override
    public void onUploadScanSuccess(String... filePath) {
        if (filePath.length > 0 && !TextUtils.isEmpty(filePath[0])) {
            Bundle params = new Bundle();
            params.putString("batch_id", batchId);
            params.putString("csv_path", filePath[0]);
            analytics.logEvent("chemical_upload", params);

            if (!BuildConfig.DEBUG) {
                FileUtil.delete(filePath[0]);
            }
        }

        if (deviceId == 1 || deviceId == 4) {
            done();
        }
    }

    @Override
    public void onUploadScanFailure() {
        btnUpload.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAnalyses(boolean showPrice, List<AnalysisItem> analyses) {
        Timber.d("Showing analyses!");
        hideTitleDescImage();
        this.analyses = analyses;
        if (deviceId != 1 && deviceId != 4) {
            btnDone.setVisibility(View.VISIBLE);
        } else {
            btnUpload.setVisibility(View.VISIBLE);
        }
        btnShare.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        controller.setList(showPrice, commodity, analyses);

        showPrint = true;
        invalidateOptionsMenu();
    }

    public void hideTitleDescImage() {
        imageView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvDesc.setVisibility(View.GONE);
    }

    @Override
    public void showBluetoothDialog() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BLUETOOTH_SETTINGS_REQUEST);
    }

    @Override
    public void startPrinting(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
        printBill();
    }

    @Override
    public void storeScanResult(String serialNumber, String avgCsvPath) {
        presenter.addScanResultInDb(farmerCode, batchId, location, sample, commodity, avgCsvPath, serialNumber, analyses);
    }

    public void fetchDataFactor(String serialNumber) {
        presenter.fetchDataFactor(sample, commodity, serialNumber);
    }

    @Override
    public void onDataFactorLoaded(DataFactorPayload payload) {
    }

    @Override
    public void fetchResultFromDb(String batchId) {
        presenter.fetchResultFromDb(batchId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (showPrint) {
            MenuItem item = menu.findItem(R.id.item_print);
            if (item != null) {
                item.setVisible(true);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_print) {
            if (btSocket == null) {
                SelectPrinterDialog.newInstance().show(getSupportFragmentManager(), SELECT_PRINTER_DIALOG);
            } else {
                printBill();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void printBill() {
        printerSdk.printBill(btSocket, commodity, sample, analyses);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        printerSdk.cleanUp();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        presenter.destroy();

        RxUtils.dispose(disposable);
    }
}