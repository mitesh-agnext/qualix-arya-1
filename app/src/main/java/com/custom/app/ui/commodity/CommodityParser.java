package com.custom.app.ui.commodity;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import com.data.app.db.Db;
import com.data.app.db.table.CommodityTable;
import com.data.app.db.table.ResultTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.specx.scan.data.model.analytic.AnalyticItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.List;

class CommodityParser {

    @NonNull
    static List<CommodityItem> parse(List<CommodityItem> commodities) throws NullPointerException {

        if (commodities != null && !commodities.isEmpty()) {
            return commodities;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    static List<AnalyticItem> analysis(List<AnalyticItem> analyses) throws NullPointerException {

        if (analyses != null && !analyses.isEmpty()) {
            return analyses;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    public static List<CommodityItem> parse(SqlBrite.Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        try (Cursor cursor = query.run()) {
            if (cursor != null && cursor.moveToNext()) {
                String commodityId = Db.getString(cursor, CommodityTable.COMMODITY_ID);
                String userId = Db.getString(cursor, ResultTable.USER_ID);
                String commodityList = Db.getString(cursor, CommodityTable.COMMODITY_LIST);
                return new Gson().fromJson(commodityList, new TypeToken<List<CommodityItem>>() {}.getType());
            }

            throw new AssertionError("No records found!");
        }
    }
}