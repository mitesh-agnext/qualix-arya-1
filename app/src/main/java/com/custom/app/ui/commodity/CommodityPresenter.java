package com.custom.app.ui.commodity;

import com.base.app.ui.base.BasePresenter;

public abstract class CommodityPresenter extends BasePresenter<CommodityView> {

    abstract void fetchCommodities();

}