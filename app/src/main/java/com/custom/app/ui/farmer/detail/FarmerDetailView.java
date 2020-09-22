package com.custom.app.ui.farmer.detail;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.farmer.upload.FarmerItem;

interface FarmerDetailView extends BaseView {

    void showQRCode(String farmerCode);

    void showAddFarmerDialog(FarmerItem farmer);

}