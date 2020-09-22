package com.custom.app.ui.quality;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.quality.QualityMapItem;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

interface QualityMapView extends BaseView {

    void onDateRangeSelected();

    void showCommodities(List<CommodityItem> commodities);

    void showAnalyses(List<AnalyticItem> analyses);

    void showQualityMap(List<QualityMapItem> qualities);

}