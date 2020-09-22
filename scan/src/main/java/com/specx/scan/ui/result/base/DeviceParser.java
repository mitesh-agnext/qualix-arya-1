package com.specx.scan.ui.result.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import com.data.app.db.Db;
import com.data.app.db.table.DeviceTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.sqlbrite3.SqlBrite.Query;

import java.util.List;

class DeviceParser {

    @NonNull
    public static List<Double> parse(Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        try (Cursor cursor = query.run()) {
            if (cursor != null && cursor.moveToNext()) {
                String deviceId = Db.getString(cursor, DeviceTable.DEVICE_ID);
                String userId = Db.getString(cursor, DeviceTable.USER_ID);
                String commodityId = Db.getString(cursor, DeviceTable.COMMODITY_ID);
                String serialNumber = Db.getString(cursor, DeviceTable.SERIAL_NUMBER);
                String scaleFactor = Db.getString(cursor, DeviceTable.SCALE_FACTOR);
                return new Gson().fromJson(scaleFactor, new TypeToken<List<Double>>() {}.getType());
            }

            throw new AssertionError("An error occurred, please try again!");
        }
    }
}