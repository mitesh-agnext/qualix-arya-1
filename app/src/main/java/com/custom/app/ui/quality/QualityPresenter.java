package com.custom.app.ui.quality;

import com.base.app.ui.base.BasePresenter;

public abstract class QualityPresenter extends BasePresenter<QualityMapView> {

    abstract void fetchCommodity(String... categoryId);

    abstract void fetchAnalyses(String commodityId);

    abstract void fetchQualityMap(String commodityId, String analysis, String from, String to);

}