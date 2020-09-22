package com.custom.app.ui.analysis;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.custom.app.R;
import com.data.app.db.Db;
import com.data.app.db.table.ResultTable;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.analysis.AnalysisResponse;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

class AnalysisParser {

    @NonNull
    static List<AnalysisItem> parse(SqlBrite.Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        List<AnalysisItem> analyses = new ArrayList<>();

        String scanResult = null;

        try (Cursor cursor = query.run()) {
            while (cursor != null && cursor.moveToNext()) {
                scanResult = Db.getString(cursor, ResultTable.SCAN_RESULT);
            }
        }

        analyses.add(new AnalysisItem("00", "Chemical",
                R.drawable.ic_chemical_scan, !TextUtils.isEmpty(scanResult)));

        return analyses;
    }

    @NonNull
    static String remove(AnalysisResponse body) throws NullPointerException {

        if (body.isStatus()) {
            if (!TextUtils.isEmpty(body.getMessage())) {
                return body.getMessage();
            } else {
                throw new RuntimeException("Response payload is empty!");
            }
        } else {
            throw new RuntimeException(body.getMessage());
        }
    }
}