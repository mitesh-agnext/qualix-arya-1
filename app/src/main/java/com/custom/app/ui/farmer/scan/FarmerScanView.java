package com.custom.app.ui.farmer.scan;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.farmer.upload.FarmerItem;

interface FarmerScanView extends BaseView {

    void showFarmerDetail(FarmerItem farmer);

    void showSampleScreen(FarmerItem farmer);

    void showFarmerVerificationError();

}