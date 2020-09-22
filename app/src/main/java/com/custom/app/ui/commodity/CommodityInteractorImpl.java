package com.custom.app.ui.commodity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;

import com.custom.app.network.RestService;
import com.custom.app.ui.sample.SampleParser;
import com.data.app.db.table.CommodityTable;
import com.data.app.db.table.VarietyTable;
import com.google.gson.Gson;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.variety.VarietyItem;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class CommodityInteractorImpl implements CommodityInteractor {

    private RestService restService;
    private UserManager userManager;
    private BriteDatabase database;

    public CommodityInteractorImpl(RestService restService, UserManager userManager, BriteDatabase database) {
        this.restService = restService;
        this.userManager = userManager;
        this.database = database;
    }

    @Override
    public Observable<List<CommodityItem>> fetchCommoditiesFromDb(String... filter) {
        return database.createQuery(CommodityTable.TABLE_NAME,
                CommodityTable.QUERY_SELECT_COMMODITIES, userManager.getUserId())
                .map(CommodityParser::parse);
    }

    @Override
    public Single<List<CommodityItem>> fetchCommoditiesFromNetwork(String... filter) {
        Map<String, String> query = new HashMap<>();
        if (filter.length > 0) {
            query.put("commodityCategoryId", filter[0]);
            query.put("customer_id", "91");
        }

        return restService.commodities(query)
                .map(response -> {
                    List<CommodityItem> commodities = CommodityParser.parse(response);
                    storeCommodities(commodities);
                    return commodities;
                });
    }

    @Override
    public Single<List<AnalyticItem>> analyses(String commodityId) {
        Map<String, String> query = new HashMap<>();
        query.put("commodity_id", commodityId);
        query.put("customer_id", "91");

        return restService.analyses(query)
                .map(CommodityParser::analysis);
    }

    @Override
    public void storeCommodities(List<CommodityItem> commodities) {
        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            String commodityList = new Gson().toJson(commodities);

            ContentValues values = new CommodityTable.Builder()
                    .userId(userManager.getUserId())
                    .commodityList(commodityList)
                    .build();

            database.insert(CommodityTable.TABLE_NAME, CONFLICT_REPLACE, values);

            transaction.markSuccessful();
        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public Observable<List<VarietyItem>> fetchVarietiesFromDb(String commodityId) {
        return database.createQuery(VarietyTable.TABLE_NAME,
                VarietyTable.QUERY_SELECT_VARIETIES, userManager.getUserId(), commodityId)
                .map(SampleParser::parse);
    }

    @Override
    public Single<List<VarietyItem>> fetchVarietiesFromNetwork(String commodityId) {
        return restService.varieties(commodityId)
                .map(response -> {
                    List<VarietyItem> varieties = SampleParser.parse(response);
                    storeVarieties(commodityId, varieties);
                    return varieties;
                });
    }

    @Override
    public void storeVarieties(String commodityId, List<VarietyItem> varieties) {
        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            String varietyList = new Gson().toJson(varieties);

            ContentValues values = new VarietyTable.Builder()
                    .userId(userManager.getUserId())
                    .commodityId(commodityId)
                    .varietyList(varietyList)
                    .build();

            database.insert(VarietyTable.TABLE_NAME, CONFLICT_REPLACE, values);

            transaction.markSuccessful();
        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
        } finally {
            transaction.end();
        }
    }
}