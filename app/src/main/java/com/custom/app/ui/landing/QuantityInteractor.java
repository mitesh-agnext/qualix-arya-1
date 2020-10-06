package com.custom.app.ui.landing;

import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.category.CategoryDetailItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.data.model.supplier.SupplierItem;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionByCenterRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionCenterRegionRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionWeeklyMonthlyRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.QuantityRes;

import java.util.List;

import io.reactivex.Single;

public interface QuantityInteractor {

    Single<List<CategoryDetailItem>> categories();

    Single<QuantityRes> quantity(String commodityId, String from, String to, String... filter);

    Single<List<CollectionByCenterRes>> collectionByCenter(String commodityId, String from, String to, String... filter);

    Single<List<CollectionOverTimeRes>> collectionOverTime(String commodityId, String from, String to, String... filter);

//    Single<List<CollectionCenterRegionRes>> collectionRegion(String commodityId, String regionId,
//                                                             String centerId, String from,
//                                                             String to, String... filter);

    Single<CollectionWeeklyMonthlyRes> collectionWeekly(String commodityId, String from, String to, String... filter);

    Single<QuantityDetailRes> qualityDetail(String categoryId, String from, String to);

    Single<AcceptedAvgRes> scanCount(String categoryId, String from, String to, String... filter);

    Single<AcceptedAvgRes> varianceAvg(String categoryId, String from, String to, String... filter);

    Single<AcceptedAvgRes> acceptedAvg(String categoryId, String from, String to, String... filter);

    Single<List<SupplierItem>> suppliers(String commodityId, String regionId, String from, String to, String... filter);

}