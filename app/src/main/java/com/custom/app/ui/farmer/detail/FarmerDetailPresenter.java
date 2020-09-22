package com.custom.app.ui.farmer.detail;

import com.base.app.ui.base.BasePresenter;
import com.custom.app.data.model.farmer.upload.FarmerItem;

public abstract class FarmerDetailPresenter extends BasePresenter<FarmerDetailView> {

    abstract void uploadFarmer(FarmerItem farmer);

    abstract void addFarmerInDb(FarmerItem farmer);

}