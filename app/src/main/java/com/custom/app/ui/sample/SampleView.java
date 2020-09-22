package com.custom.app.ui.sample;

import com.base.app.ui.base.BaseView;
import com.custom.app.data.model.section.LocationItem;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.variety.VarietyItem;

import java.util.List;

interface SampleView extends BaseView {

    void showAnalysis(AnalysisItem analysis);

    void onLocationSelected(LocationItem location);

    void showCommodities(List<CommodityItem> commodities);

    void onCommoditySelected(CommodityItem commodity);

    void showVarieties(List<VarietyItem> varieties);

    void onVarietySelected(VarietyItem variety);

    void onQuantityUnitSelected();

    void onSampleIdChanged();

    void onBatchIdClicked();

    void hideVarieties();

    void onProceed();

}