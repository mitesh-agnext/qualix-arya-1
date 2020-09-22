package com.custom.app.ui.farmer;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import com.custom.app.data.model.farmer.upload.FarmerItem;
import com.data.app.db.Db;
import com.data.app.db.table.FarmerTable;
import com.google.gson.Gson;
import com.squareup.sqlbrite3.SqlBrite.Query;

import java.util.ArrayList;
import java.util.List;

class FarmerParser {

    @NonNull
    static List<FarmerItem> parse(Query query) throws SQLiteException, NullPointerException,
            NumberFormatException {

        List<FarmerItem> farmers = new ArrayList<>();

        try (Cursor cursor = query.run()) {
            while (cursor != null && cursor.moveToNext()) {
                String farmerCode = Db.getString(cursor, FarmerTable.FARMER_CODE);
                String farmerDetail = Db.getString(cursor, FarmerTable.FARMER_DETAIL);
                boolean isUploaded = Db.getBoolean(cursor, FarmerTable.IS_UPLOADED);
                FarmerItem farmer = new Gson().fromJson(farmerDetail, FarmerItem.class);
                farmer.setCode(farmerCode);
                farmer.setUploaded(isUploaded);
                farmers.add(farmer);
            }
        }

        return farmers;
    }

    @NonNull
    static FarmerItem status(FarmerItem farmer) throws NullPointerException {

        if (farmer != null) {
            return farmer;
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }
}