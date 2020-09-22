package com.custom.app.ui.collection;

import com.base.app.ui.base.BasePresenter;

public abstract class CollectionPresenter extends BasePresenter<CollectionView> {

    abstract void fetchCommodity(String... categoryId);

    abstract void fetchAnalyses(String commodityId);

    abstract void fetchQuality(String commodityId, String analysis, String from,
                               String to, String... filter);

    abstract void fetchQualityOverTime(String commodityId, String analysis, String from,
                                       String to, String... filter);

    abstract void fetchQuantity(String commodityId, String from, String to, String... filter);

    abstract void fetchCollectionByCenter(String commodityId, String from, String to, String... filter);

    abstract void fetchCollectionOverTime(String commodityId, String from, String to, String... filter);

    abstract void fetchCollectionRegion(String commodityId, String regionId, String centerId,
                                        String from, String to, String... filter);

    abstract void fetchCollectionWeekly(String commodityId, String from, String to, String... filter);

}