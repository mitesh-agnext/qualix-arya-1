package com.custom.app.ui.sample;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import com.data.app.db.Db;
import com.data.app.db.table.VarietyTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.specx.scan.data.model.variety.VarietyItem;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.List;

public class SampleParser {

    @NonNull
    public static List<VarietyItem> parse(List<VarietyItem> varieties) throws NullPointerException {

        if (varieties != null && !varieties.isEmpty()) {
            return varieties;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }

    @NonNull
    public static List<VarietyItem> parse(SqlBrite.Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        try (Cursor cursor = query.run()) {
            if (cursor != null && cursor.moveToNext()) {
                String varietyId = Db.getString(cursor, VarietyTable.VARIETY_ID);
                String userId = Db.getString(cursor, VarietyTable.USER_ID);
                String commodityId = Db.getString(cursor, VarietyTable.COMMODITY_ID);
                String varietyList = Db.getString(cursor, VarietyTable.VARIETY_LIST);
                return new Gson().fromJson(varietyList, new TypeToken<List<VarietyItem>>() {
                }.getType());
            }

            throw new AssertionError("No records found!");
        }
    }
}