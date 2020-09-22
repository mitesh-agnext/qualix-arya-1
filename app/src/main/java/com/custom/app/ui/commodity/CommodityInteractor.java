package com.custom.app.ui.commodity;

import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.variety.VarietyItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface CommodityInteractor {

    Observable<List<CommodityItem>> fetchCommoditiesFromDb(String... filter);

    Single<List<CommodityItem>> fetchCommoditiesFromNetwork(String... filter);

    Single<List<AnalyticItem>> analyses(String commodityId);

    void storeCommodities(List<CommodityItem> commodities);

    Observable<List<VarietyItem>> fetchVarietiesFromDb(String commodityId);

    Single<List<VarietyItem>> fetchVarietiesFromNetwork(String commodityId);

    void storeVarieties(String commodityId, List<VarietyItem> varieties);

}