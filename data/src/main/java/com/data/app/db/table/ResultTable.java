package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ResultTable implements Parcelable {

    public static final String TABLE_NAME = "ResultList";

    public static final String RESULT_ID = "resultId";
    public static final String USER_ID = "userId";
    public static final String SAMPLE = "sample";
    public static final String BATCH_ID = "batchId";
    public static final String LOCATION = "location";
    public static final String COMMODITY = "commodity";
    public static final String AVG_CSV_PATH = "avgCsvPath";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String SCAN_RESULT = "scanResult";
    public static final String DATETIME = "datetime";
    public static final String FARMER_CODE = "farmerCode";
    public static final String IS_UPLOADED = "isUploaded";

    public abstract String userId();
    public abstract String sample();
    public abstract String batchId();
    public abstract String location();
    public abstract String commodity();
    public abstract String avgCsvPath();
    public abstract String serialNumber();
    public abstract String scanResult();
    public abstract String datetime();
    public abstract String farmerCode();
    public abstract boolean isUploaded();

    public static final String QUERY_CREATE_RESULT_TABLE = "CREATE TABLE "
            + ResultTable.TABLE_NAME + "("
            + ResultTable.RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ResultTable.USER_ID + " TEXT NOT NULL, "
            + ResultTable.SAMPLE + " TEXT NOT NULL, "
            + ResultTable.BATCH_ID + " TEXT NOT NULL UNIQUE, "
            + ResultTable.LOCATION + " TEXT, "
            + ResultTable.COMMODITY + " TEXT NOT NULL, "
            + ResultTable.AVG_CSV_PATH + " TEXT, "
            + ResultTable.SERIAL_NUMBER + " TEXT, "
            + ResultTable.SCAN_RESULT + " TEXT, "
            + ResultTable.DATETIME + " TEXT, "
            + ResultTable.FARMER_CODE + " TEXT, "
            + FarmerTable.IS_UPLOADED + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    public static String QUERY_SEARCH_RESULT = "SELECT *  FROM "
            + ResultTable.TABLE_NAME
            + " WHERE "
            + ResultTable.USER_ID
            + " = ?"
            + " AND "
            + ResultTable.BATCH_ID
            + " = ?"
            + " OR "
            + ResultTable.SERIAL_NUMBER
            + " = ?"
            + " OR "
            + ResultTable.FARMER_CODE
            + " = ?";

    public static String QUERY_BATCH_RESULT = "SELECT *  FROM "
            + ResultTable.TABLE_NAME
            + " WHERE "
            + ResultTable.USER_ID
            + " = ?"
            + " AND "
            + ResultTable.BATCH_ID
            + " = ?";

    public static String QUERY_CACHED_RESULT = "SELECT *  FROM "
            + ResultTable.TABLE_NAME
            + " WHERE "
            + ResultTable.USER_ID
            + " = ?"
            + " AND "
            + ResultTable.IS_UPLOADED
            + " = 0";

    public static final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder userId(String userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder sample(String sample) {
            values.put(SAMPLE, sample);
            return this;
        }

        public Builder batchId(String batchId) {
            values.put(BATCH_ID, batchId);
            return this;
        }

        public Builder location(String location) {
            values.put(LOCATION, location);
            return this;
        }

        public Builder commodity(String commodity) {
            values.put(COMMODITY, commodity);
            return this;
        }

        public Builder avgCsvPath(String filePath) {
            values.put(AVG_CSV_PATH, filePath);
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            values.put(SERIAL_NUMBER, serialNumber);
            return this;
        }

        public Builder scanResult(String result) {
            values.put(SCAN_RESULT, result);
            return this;
        }

        public Builder datetime(String datetime) {
            values.put(DATETIME, datetime);
            return this;
        }

        public Builder farmerCode(String farmerCode) {
            values.put(FARMER_CODE, farmerCode);
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