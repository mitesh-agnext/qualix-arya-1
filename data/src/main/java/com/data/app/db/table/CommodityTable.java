package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CommodityTable implements Parcelable {

    public static final String TABLE_NAME = "CommodityList";

    public static final String USER_ID = "userId";
    public static final String COMMODITY_ID = "commodityId";
    public static final String COMMODITY_LIST = "commodityList";

    public abstract String userId();
    public abstract String commodityList();

    public static final String QUERY_CREATE_COMMODITY_TABLE = "CREATE TABLE "
            + CommodityTable.TABLE_NAME + "("
            + CommodityTable.COMMODITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CommodityTable.USER_ID + " TEXT NOT NULL UNIQUE, "
            + CommodityTable.COMMODITY_LIST + " TEXT NOT NULL"
            + ")";

    public static String QUERY_SELECT_COMMODITIES = "SELECT *  FROM "
            + CommodityTable.TABLE_NAME
            + " WHERE "
            + CommodityTable.USER_ID
            + " = ?";

    public static final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder userId(String userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder commodityList(String commodityList) {
            values.put(COMMODITY_LIST, commodityList);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}