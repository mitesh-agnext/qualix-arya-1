package com.custom.app.ui.collection;

import com.base.app.ui.base.BaseView;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionByCenterRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionCenterRegionRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionWeeklyMonthlyRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.QuantityRes;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;

import java.util.List;

interface CollectionView extends BaseView {

    void onDateRangeSelected();

    void showCommodities(List<CommodityItem> commodities);

    void showAnalyses(List<AnalyticItem> analyses);

    void showQuality(QualityRes quality);

    void showQuantity(QuantityRes quantity);

    void showCollection(CollectionWeeklyMonthlyRes collection);

    void showQualityOverTime(List<QualityOverTimeRes> qualities);

    void showCollectionByCenter(List<CollectionByCenterRes> collections);

    void showCollectionOverTime(List<CollectionOverTimeRes> collections);

    void showCollectionRegion(List<CollectionCenterRegionRes> collections);

}