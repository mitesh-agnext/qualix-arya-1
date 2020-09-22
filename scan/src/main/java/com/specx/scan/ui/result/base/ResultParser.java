package com.specx.scan.ui.result.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.data.app.db.Db;
import com.data.app.db.table.AnalysisTable;
import com.data.app.db.table.ResultTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.analysis.AnalysisPayload;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.factor.DataFactorResponse;
import com.specx.scan.data.model.result.ResultItem;
import com.specx.scan.data.model.result.UploadResultResponse;
import com.specx.scan.data.model.sample.SampleItem;
import com.specx.scan.data.model.upload.UploadScanResponse;
import com.squareup.sqlbrite3.SqlBrite.Query;

import java.util.ArrayList;
import java.util.List;

class ResultParser {

    @NonNull
    public static ResultItem result(Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        try (Cursor cursor = query.run()) {
            if (cursor == null || !cursor.moveToNext()) {
                throw new AssertionError("No records found!");
            }

            String resultId = Db.getString(cursor, ResultTable.RESULT_ID);
            String userId = Db.getString(cursor, ResultTable.USER_ID);
            String sample = Db.getString(cursor, ResultTable.SAMPLE);
            String batchId = Db.getString(cursor, ResultTable.BATCH_ID);
            String location = Db.getString(cursor, ResultTable.LOCATION);
            String commodity = Db.getString(cursor, ResultTable.COMMODITY);
            String avgCsvPath = Db.getString(cursor, ResultTable.AVG_CSV_PATH);
            String serialNumber = Db.getString(cursor, ResultTable.SERIAL_NUMBER);
            String scanResult = Db.getString(cursor, ResultTable.SCAN_RESULT);
            String datetime = Db.getString(cursor, ResultTable.DATETIME);
            String farmerCode = Db.getString(cursor, ResultTable.FARMER_CODE);
            List<AnalysisItem> analyses = new Gson().fromJson(scanResult, new TypeToken<List<AnalysisItem>>() {}.getType());

            return new ResultItem.Builder()
                    .setId(resultId)
                    .setUserId(userId)
                    .setBatchId(batchId)
                    .setLocation(location)
                    .setSample(new Gson().fromJson(sample, SampleItem.class))
                    .setCommodity(new Gson().fromJson(commodity, CommodityItem.class))
                    .setAvgCsvPath(avgCsvPath)
                    .setSerialNumber(serialNumber)
                    .setAnalyses(analyses)
                    .setFarmerCode(farmerCode)
                    .setDatetime(datetime)
                    .build();
        }
    }

    @NonNull
    public static List<ResultItem> parse(Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        List<ResultItem> results = new ArrayList<>();

        try (Cursor cursor = query.run()) {
            while (cursor != null && cursor.moveToNext()) {
                String resultId = Db.getString(cursor, ResultTable.RESULT_ID);
                String userId = Db.getString(cursor, ResultTable.USER_ID);
                String batchId = Db.getString(cursor, ResultTable.BATCH_ID);
                String location = Db.getString(cursor, ResultTable.LOCATION);
                String sample = Db.getString(cursor, ResultTable.SAMPLE);
                String commodity = Db.getString(cursor, ResultTable.COMMODITY);
                String avgCsvPath = Db.getString(cursor, ResultTable.AVG_CSV_PATH);
                String serialNumber = Db.getString(cursor, ResultTable.SERIAL_NUMBER);
                String scanResult = Db.getString(cursor, ResultTable.SCAN_RESULT);
                String datetime = Db.getString(cursor, ResultTable.DATETIME);
                String farmerCode = Db.getString(cursor, ResultTable.FARMER_CODE);
                boolean isUploaded = Db.getBoolean(cursor, ResultTable.IS_UPLOADED);
                List<AnalysisItem> analyses = new Gson().fromJson(scanResult, new TypeToken<List<AnalysisItem>>() {}.getType());

                if (!isUploaded) {
                    ResultItem result = new ResultItem.Builder()
                            .setId(resultId)
                            .setUserId(userId)
                            .setBatchId(batchId)
                            .setLocation(location)
                            .setSample(new Gson().fromJson(sample, SampleItem.class))
                            .setCommodity(new Gson().fromJson(commodity, CommodityItem.class))
                            .setAvgCsvPath(avgCsvPath)
                            .setSerialNumber(serialNumber)
                            .setAnalyses(analyses)
                            .setDatetime(datetime)
                            .setFarmerCode(farmerCode)
                            .build();

                    results.add(result);
                }
            }
        }

        return results;
    }

    @NonNull
    public static List<ResultItem> search(Query query) throws SQLiteException, NullPointerException, NumberFormatException {

        List<ResultItem> results = new ArrayList<>();

        try (Cursor cursor = query.run()) {
            while (cursor != null && cursor.moveToNext()) {
                String resultId = Db.getString(cursor, ResultTable.RESULT_ID);
                String userId = Db.getString(cursor, ResultTable.USER_ID);
                String batchId = Db.getString(cursor, ResultTable.BATCH_ID);
                String location = Db.getString(cursor, ResultTable.LOCATION);
                String sample = Db.getString(cursor, ResultTable.SAMPLE);
                String commodity = Db.getString(cursor, ResultTable.COMMODITY);
                String avgCsvPath = Db.getString(cursor, ResultTable.AVG_CSV_PATH);
                String serialNumber = Db.getString(cursor, ResultTable.SERIAL_NUMBER);
                String scanResult = Db.getString(cursor, ResultTable.SCAN_RESULT);
                String datetime = Db.getString(cursor, ResultTable.DATETIME);
                List<AnalysisItem> analyses = new Gson().fromJson(scanResult, new TypeToken<List<AnalysisItem>>() {}.getType());

                if (analyses != null && !analyses.isEmpty()) {
                    results.add(new ResultItem.Builder()
                            .setId(resultId)
                            .setUserId(userId)
                            .setBatchId(batchId)
                            .setLocation(location)
                            .setSample(new Gson().fromJson(sample, SampleItem.class))
                            .setCommodity(new Gson().fromJson(commodity, CommodityItem.class))
                            .setAvgCsvPath(avgCsvPath)
                            .setSerialNumber(serialNumber)
                            .setAnalyses(analyses)
                            .setDatetime(datetime)
                            .build());
                }
            }

            if (!results.isEmpty()) {
                return results;
            }

            throw new AssertionError("No records found!");
        }
    }

    @NonNull
    static DataFactorPayload factor(DataFactorResponse body) throws NullPointerException {

        if (body.isStatus()) {
            DataFactorPayload payload = body.getPayload();

            if (payload != null) {
                List<Double> scales = payload.getScaleFactor();
                List<AnalysisPayload> analyses = payload.getAnalysisList();
                if (scales != null && !scales.isEmpty() && analyses != null && !analyses.isEmpty()) {
                    return payload;
                } else {
                    throw new RuntimeException("Response payload is empty!");
                }
            } else {
                throw new RuntimeException("Response payload is empty!");
            }
        } else {
            throw new RuntimeException(body.getMessage());
        }
    }

    @NonNull
    static List<AnalysisPayload> analysis(Query query) throws SQLiteException, NullPointerException, NumberFormatException {
        List<AnalysisPayload> analyses = new ArrayList<>();

        try (Cursor cursor = query.run()) {
            while (cursor != null && cursor.moveToNext()) {
                String analysisId = Db.getString(cursor, AnalysisTable.ANALYSIS_ID);
                String userId = Db.getString(cursor, AnalysisTable.USER_ID);
                String commodityId = Db.getString(cursor, AnalysisTable.COMMODITY_ID);
                String analysisName = Db.getString(cursor, AnalysisTable.ANALYSIS_NAME);
                String darkThumbUrl = Db.getString(cursor, AnalysisTable.DARK_THUMB_URL);
                String lightThumbUrl = Db.getString(cursor, AnalysisTable.LIGHT_THUMB_URL);
                String algorithm = Db.getString(cursor, AnalysisTable.ALGORITHM);
                String algoConfig = Db.getString(cursor, AnalysisTable.ALGO_CONFIG);
                String betaValue = Db.getString(cursor, AnalysisTable.BETA_MATRIX);
                String meanValue = Db.getString(cursor, AnalysisTable.MEAN_MATRIX);
                List<Double> betaMatrix = new Gson().fromJson(betaValue, new TypeToken<List<Double>>() {}.getType());
                List<Double> meanMatrix = new Gson().fromJson(meanValue, new TypeToken<List<Double>>() {}.getType());

                analyses.add(new AnalysisPayload.Builder()
                        .setCropId(commodityId)
                        .setAnalysisName(analysisName)
                        .setDarkThumbUrl(darkThumbUrl)
                        .setLightThumbUrl(lightThumbUrl)
                        .setAlgorithm(algorithm)
                        .setAlgoConfig(algoConfig)
                        .setBetaMatrix(betaMatrix)
                        .setMeanMatrix(meanMatrix)
                        .build());
            }

            if (!analyses.isEmpty()) {
                return analyses;
            }

            throw new AssertionError("No records found!");
        }
    }

    @NonNull
    static List<AnalysisItem> result(UploadResultResponse body) throws NullPointerException {

        if (body.isStatus()) {
            if (body.getAnalyses() != null && !body.getAnalyses().isEmpty()) {
                return body.getAnalyses();
            } else {
                throw new RuntimeException("Response payload is empty!");
            }
        } else {
            throw new RuntimeException(body.getMessage());
        }
    }

    @NonNull
    static String upload(UploadScanResponse body) throws NullPointerException {

        String msg = body.getMessage();
        if (!TextUtils.isEmpty(msg)) {
            return body.getMessage();
        } else {
            throw new RuntimeException("Response payload is empty!");
        }
    }
}