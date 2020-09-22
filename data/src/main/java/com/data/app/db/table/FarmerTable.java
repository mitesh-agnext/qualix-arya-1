package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FarmerTable implements Parcelable {

    public static final String TABLE_NAME = "FarmerList";

    public static final String USER_ID = "userId";
    public static final String FARMER_ID = "farmerId";
    public static final String FARMER_CODE = "farmerCode";
    public static final String FARMER_PHONE = "farmerPhone";
    public static final String FARMER_DETAIL = "farmerDetail";
    public static final String IS_UPLOADED = "isUploaded";

    public abstract String usreId();
    public abstract String farmerId();
    public abstract String farmerCode();
    public abstract String farmerPhone();
    public abstract String farmerDetail();
    public abstract boolean isUploaded();

    public static final String QUERY_CREATE_FARMER_TABLE = "CREATE TABLE "
            + FarmerTable.TABLE_NAME + "("
            + FarmerTable.FARMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FarmerTable.USER_ID + " TEXT NOT NULL, "
            + FarmerTable.FARMER_CODE + " TEXT NOT NULL UNIQUE, "
            + FarmerTable.FARMER_PHONE + " TEXT NOT NULL UNIQUE, "
            + FarmerTable.FARMER_DETAIL + " TEXT NOT NULL UNIQUE, "
            + FarmerTable.IS_UPLOADED + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    public static String QUERY_ALL_FARMERS = "SELECT *  FROM "
            + FarmerTable.TABLE_NAME
            + " WHERE "
            + FarmerTable.USER_ID
            + " = ?";

    public static String QUERY_CACHED_FARMERS = "SELECT *  FROM "
            + FarmerTable.TABLE_NAME
            + " WHERE "
            + FarmerTable.USER_ID
            + " = ?"
            + " AND "
            + FarmerTable.IS_UPLOADED
            + " = 0";

    public static final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder userId(String userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder farmerCode(String farmerCode) {
            values.put(FARMER_CODE, farmerCode);
            return this;
        }

        public Builder farmerPhone(String farmerPhone) {
            values.put(FARMER_PHONE, farmerPhone);
            return this;
        }

        public Builder farmerDetail(String farmerDetail) {
            values.put(FARMER_DETAIL, farmerDetail);
            return this;
        }

        public Builder isUploaded(boolean isUploaded) {
            values.put(IS_UPLOADED, isUploaded);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}