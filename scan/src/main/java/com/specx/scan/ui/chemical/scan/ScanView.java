package com.specx.scan.ui.chemical.scan;

import android.location.Location;

import com.base.app.ui.base.BaseView;

interface ScanView extends BaseView {

    void showScanScreen(Location location);

}