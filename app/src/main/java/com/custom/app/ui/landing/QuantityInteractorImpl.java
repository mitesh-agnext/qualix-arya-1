package com.custom.app.ui.landing;

import com.custom.app.data.model.business.AcceptedAvgRes;
import com.custom.app.data.model.category.CategoryDetailItem;
import com.custom.app.data.model.quantity.QuantityDetailRes;
import com.custom.app.data.model.supplier.SupplierItem;
import com.custom.app.network.RestService;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionByCenterRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionCenterRegionRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionOverTimeRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.CollectionWeeklyMonthlyRes;
import com.custom.app.ui.createData.analytics.analysis.quantity.QuantityRes;
import com.user.app.data.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

public class QuantityInteractorImpl implements QuantityInteractor {

    private RestService restService;
    private UserManager userManager;

    public QuantityInteractorImpl(RestService restService, UserManager userManager) {
        this.restService = restService;
        this.userManager = userManager;
    }

    @Override
    public Single<List<CategoryDetailItem>> categories() {
        return restService.categories(userManager.getCustomerId())
                .map(QuantityParser::category);
    }

    @Override
    public Single<QuantityRes> quantity(String commodityId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }
        return restService.quantity(query)
                .map(QuantityParser::quantity);
    }

    @Override
    public Single<List<CollectionByCenterRes>> collectionByCenter(String commodityId, String from,
                                                                  String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }
        return restService.collectionByCenter(query)
                .map(QuantityParser::center);
    }

    @Override
    public Single<List<CollectionOverTimeRes>> collectionOverTime(String commodityId, String from,
                                                                  String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }
        return restService.collectionOverTime(query)
                .map(QuantityParser::time);
    }

//    @Override
//    public Single<List<CollectionCenterRegionRes>> collectionRegion(String commodityId, String regionId,
//                                                                    String centerId, String from,
//                                                                    String to, String... filter) {
//        Map<String, String> query = new HashMap<>();
//        query.put("commodity_id", commodityId);
//        query.put("region_id", regionId);
//        query.put("inst_center_id", centerId);
//        query.put("customer_id", userManager.getCustomerId());
//        query.put("date_from", from);
//        query.put("date_to", to);
//
//        return restService.collectionRegion(query)
//                .map(QuantityParser::region);
//    }

    @Override
    public Single<CollectionWeeklyMonthlyRes> collectionWeekly(String commodityId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }
        return restService.collectionWeekly(query)
                .map(QuantityParser::weekly);
    }

    @Override
    public Single<QuantityDetailRes> qualityDetail(String categoryId, String from, String to) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_category_id", categoryId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        return restService.quantityDetail(query).map(QuantityParser::parse);
    }

    @Override
    public Single<AcceptedAvgRes> scanCount(String categoryId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_category_id", categoryId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }

        return restService.scanCount(query)
                .map(QuantityParser::business);
    }

    @Override
    public Single<AcceptedAvgRes> varianceAvg(String categoryId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_category_id", categoryId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }

        return restService.varianceAvg(query)
                .map(QuantityParser::business);
    }

    @Override
    public Single<AcceptedAvgRes> acceptedAvg(String categoryId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_category_id", categoryId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }

        return restService.acceptedAvg(query)
                .map(QuantityParser::business);
    }

    @Override
    public Single<List<SupplierItem>> suppliers(String commodityId, String regionId, String from, String to, String... filter) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
//        query.put("region_id", regionId);
        query.put("client_id", userManager.getCustomerId());
        query.put("date_from", from);
        query.put("date_to", to);

        if (filter.length > 1) {
            query.put("inst_center_id", filter[0]);
            query.put("device_type", filter[1]);
        }
        return restService.suppliers(query)
                .map(QuantityParser::supplier);
    }
}