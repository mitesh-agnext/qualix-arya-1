package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VarietyTable implements Parcelable {

    public static final String TABLE_NAME = "VarietyList";

    public static final String USER_ID = "userId";
    public static final String VARIETY_ID = "varietyId";
    public static final String COMMODITY_ID = "commodityId";
    public static final String VARIETY_LIST = "varietyList";

    public abstract String userId();
    public abstract String commodityId();
    public abstract String varietyList();

    public static final String QUERY_CREATE_VARIETY_TABLE = "CREATE TABLE "
            + VarietyTable.TABLE_NAME + "("
            + VarietyTable.VARIETY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VarietyTable.USER_ID + " TEXT NOT NULL UNIQUE, "
            + VarietyTable.COMMODITY_ID + " TEXT NOT NULL UNIQUE, "
            + VarietyTable.VARIETY_LIST + " TEXT NOT NULL"
            + ")";

    public static String QUERY_SELECT_VARIETIES = "SELECT *  FROM "
            + VarietyTable.TABLE_NAME
            + " WHERE "
            + VarietyTable.USER_ID
            + " = ?"
            + " AND "
            + VarietyTable.COMMODITY_ID
            + " = ?";

    public static final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder userId(String userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder commodityId(String commodityId) {
            values.put(COMMODITY_ID, commodityId);
            return this;
        }

        public Builder varietyList(String varietyList) {
            values.put(VARIETY_LIST, varietyList);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}