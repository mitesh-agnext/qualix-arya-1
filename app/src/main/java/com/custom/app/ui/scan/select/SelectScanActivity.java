package com.custom.app.ui.scan.select;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.base.app.ui.base.BaseActivity;
import com.core.app.util.ActivityUtil;
import com.custom.app.CustomApp;
import com.custom.app.R;
import com.custom.app.ui.analysis.AnalysisFragment;
import com.custom.app.ui.commodity.CommodityFragment;
import com.custom.app.ui.farmer.detail.FarmerDetailActivity;
import com.custom.app.ui.farmer.scan.FarmerScanFragment;
import com.custom.app.ui.sample.SampleFragment;
import com.custom.app.util.Permissions;
import com.specx.device.ui.select.SelectDeviceDialog;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;
import com.specx.scan.ui.chemical.result.ChemicalResultActivity;
import com.specx.scan.ui.chemical.scan.ScanDialog;
import com.specx.scan.ui.reference.ReferenceDialog;
import com.specx.scan.ui.result.base.ResultActivity;
import com.specx.scan.ui.result.search.SearchResultActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.core.app.util.Constants.BLUETOOTH_SETTINGS_REQUEST;
import static com.custom.app.util.Constants.COMMODITY_FRAGMENT;
import static com.custom.app.util.Constants.FLC_FRAGMENT;
import static com.custom.app.util.Constants.KEY_SCANS;
import static com.data.app.db.table.ResultTable.BATCH_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_ID;
import static com.specx.device.util.Constants.KEY_DEVICE_MAC;
import static com.specx.device.util.Constants.SELECT_DEVICE_DIALOG;
import static com.specx.scan.util.Constants.KEY_APP_VERSION;
import static com.specx.scan.util.Constants.KEY_COMMODITY;
import static com.specx.scan.util.Constants.KEY_FARMER;
import static com.specx.scan.util.Constants.KEY_LOCATION;
import static com.specx.scan.util.Constants.KEY_REFERENCE;
import static com.specx.scan.util.Constants.KEY_SAMPLE;
import static com.specx.scan.util.Constants.KEY_WARMUP;
import static com.specx.scan.util.Constants.SCAN_DIALOG;

public class SelectScanActivity extends BaseActivity implements CommodityFragment.Callback,
        FarmerScanFragment.Callback, SampleFragment.Callback, SelectDeviceDialog.Callback,
        ReferenceDialog.Callback, ScanDialog.Callback {

    private ArrayList<String> scans = new ArrayList<>();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CustomApp) getApplication()).getHomeComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scan);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (getIntent() != null) {
            scans = getIntent().getStringArrayListExtra(KEY_SCANS);
        }

        if (scans.size() == 1) {
            if (scans.contains(Permissions.SCAN_NANO)) {
                fragmentTransition(CommodityFragment.newInstance(0), COMMODITY_FRAGMENT);
            } else if (scans.contains(Permissions.SCAN_VISIO)) {
                fragmentTransition(CommodityFragment.newInstance(1), COMMODITY_FRAGMENT);
            } else if (scans.contains(Permissions.SCAN_FLC)) {
                fragmentTransition(EmptyFragment.newInstance("No permission"), FLC_FRAGMENT);
            }
        } else {
//          fragmentTransition(SelectScanFragment.newInstance(2, "Nano"), SELECT_SCAN_FRAGMENT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layout_main);

                if (currentFragment instanceof CommodityFragment
                        || currentFragment instanceof FarmerScanFragment
                        || currentFragment instanceof SampleFragment
                        || currentFragment instanceof AnalysisFragment) {
                    return false;
                }
                break;
            case R.id.item_search:
                ActivityUtil.startActivity(this, SearchResultActivity.class, false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFarmerScanScreen(CommodityItem commodity, int deviceId) {
//      fragmentTransition(FarmerScanFragment.newInstance(deviceId), FARMER_SCAN_FRAGMENT);
    }

    @Override
    public void showAddFarmerScreen() {
        ActivityUtil.startActivity(this, FarmerDetailActivity.class, false);
    }

    @Override
    public void showSelectDeviceDialog() {
        SelectDeviceDialog.newInstance().show(getSupportFragmentManager(), SELECT_DEVICE_DIALOG);
    }

    @Override
    public void showScanDialog(String farmerCode, String batchId, SampleItem sample, CommodityItem commodity) {
        ScanDialog.newInstance(farmerCode, batchId, sample, commodity).show(getSupportFragmentManager(), SCAN_DIALOG);
    }

    @Override
    public void showBluetoothDialog() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BLUETOOTH_SETTINGS_REQUEST);
    }

    @Override
    public void showWarmupScreen(String deviceMac) {
        Intent intent = new Intent(this, ChemicalResultActivity.class);
        intent.putExtra(KEY_WARMUP, true);
        intent.putExtra(KEY_DEVICE_MAC, deviceMac);
        ActivityUtil.startActivity(this, intent, false);
    }

    @Override
    public void showReferenceScanScreen() {
        Intent intent = new Intent(this, ChemicalResultActivity.class);
        intent.putExtra(KEY_REFERENCE, true);
        ActivityUtil.startActivity(this, intent, false);
    }

    @Override
    public void showScanScreen(String batchId, String location, String farmerCode, SampleItem sample, CommodityItem commodity) {
        Intent intent = new Intent(this, ChemicalResultActivity.class);
        intent.putExtra(BATCH_ID, batchId);
        intent.putExtra(KEY_LOCATION, location);
        intent.putExtra(KEY_FARMER, farmerCode);
        intent.putExtra(KEY_SAMPLE, Parcels.wrap(sample));
        intent.putExtra(KEY_COMMODITY, Parcels.wrap(commodity));
        intent.putExtra(KEY_APP_VERSION, getString(R.string.app_version));
        ActivityUtil.startActivity(this, intent, false);
    }

    @Override
    public void showResultScreen(String batchId, int deviceId) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(BATCH_ID, batchId);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        ActivityUtil.startActivity(this, intent, false);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
        } else {
            finish();
        }
    }
}