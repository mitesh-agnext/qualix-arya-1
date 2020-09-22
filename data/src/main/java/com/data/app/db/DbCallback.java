package com.data.app.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.data.app.db.table.AnalysisTable;
import com.data.app.db.table.CommodityTable;
import com.data.app.db.table.DeviceTable;
import com.data.app.db.table.FarmerTable;
import com.data.app.db.table.ResultTable;
import com.data.app.db.table.VarietyTable;

final class DbCallback extends SupportSQLiteOpenHelper.Callback {

    private static final int VERSION = 1;

    DbCallback() {
        super(VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        db.execSQL(FarmerTable.QUERY_CREATE_FARMER_TABLE);
        db.execSQL(CommodityTable.QUERY_CREATE_COMMODITY_TABLE);
        db.execSQL(VarietyTable.QUERY_CREATE_VARIETY_TABLE);
        db.execSQL(AnalysisTable.QUERY_CREATE_ANALYSIS_TABLE);
        db.execSQL(DeviceTable.QUERY_CREATE_DEVICE_TABLE);
        db.execSQL(ResultTable.QUERY_CREATE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
    }
}