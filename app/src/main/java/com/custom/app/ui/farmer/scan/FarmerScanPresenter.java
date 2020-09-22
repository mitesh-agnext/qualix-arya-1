package com.custom.app.ui.farmer.scan;

import com.base.app.ui.base.BasePresenter;

public abstract class FarmerScanPresenter extends BasePresenter<FarmerScanView> {

    abstract void verifyFarmer(String query);

}