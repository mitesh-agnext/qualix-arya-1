package com.data.app.db.table;

import android.content.ContentValues;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DeviceTable implements Parcelable {

    public static final String TABLE_NAME = "DeviceList";

    public static final String USER_ID = "userId";
    public static final String DEVICE_ID = "deviceId";
    public static final String COMMODITY_ID = "commodityId";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String SCALE_FACTOR = "scaleFactor";

    public abstract String userId();
    public abstract String commodityId();
    public abstract String serialNumber();
    public abstract String scaleFactor();

    public static final String QUERY_CREATE_DEVICE_TABLE = "CREATE TABLE "
            + DeviceTable.TABLE_NAME + "("
            + DeviceTable.DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DeviceTable.USER_ID + " TEXT NOT NULL, "
            + DeviceTable.COMMODITY_ID + " TEXT NOT NULL, "
            + DeviceTable.SERIAL_NUMBER + " TEXT NOT NULL, "
            + DeviceTable.SCALE_FACTOR + " TEXT NOT NULL"
            + ", UNIQUE("
            + DeviceTable.USER_ID + ", "
            + DeviceTable.COMMODITY_ID + ", "
            + DeviceTable.SERIAL_NUMBER + ")"
            + ")";

    public static String QUERY_SELECT_SCALE_FACTOR = "SELECT *  FROM "
            + DeviceTable.TABLE_NAME
            + " WHERE "
            + DeviceTable.USER_ID
            + " = ?"
            + " AND "
            + DeviceTable.COMMODITY_ID
            + " = ?"
            + " AND "
            + DeviceTable.SERIAL_NUMBER
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

        public Builder serialNumber(String serialNumber) {
            values.put(SERIAL_NUMBER, serialNumber);
            return this;
        }

        public Builder scaleFactor(String scaleFactor) {
            values.put(SCALE_FACTOR, scaleFactor);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}