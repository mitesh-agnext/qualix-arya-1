package com.custom.app.ui.sample;

import com.base.app.ui.base.BasePresenter;
import com.specx.scan.data.model.commodity.CommodityItem;

public abstract class SamplePresenter extends BasePresenter<SampleView> {

    abstract void fetchCommodities();

    abstract void fetchAnalyses(String batchId);

    abstract void updateAnalyses(String batchId);

    abstract void fetchVarieties(CommodityItem commodity);

}