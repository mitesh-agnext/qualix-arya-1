package com.specx.scan.ui.result.base;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.core.app.BuildConfig;
import com.core.app.util.FileUtil;
import com.core.app.util.Util;
import com.data.app.db.table.AnalysisTable;
import com.data.app.db.table.DeviceTable;
import com.data.app.db.table.ResultTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qualix.rule.engine.QualixRuleEngine;
import com.qualix.rule.engine.util.QualixEngineCache;
import com.qualix.rule.engine.util.RuleResponse;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.analysis.AnalysisPayload;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.result.ResultItem;
import com.specx.scan.data.model.result.UploadResultRequest;
import com.specx.scan.data.model.sample.SampleItem;
import com.specx.scan.network.ScanService;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class ResultInteractorImpl implements ResultInteractor {

    private ScanService scanService;
    private UserManager userManager;
    private BriteDatabase database;
    private QualixRuleEngine engine;

    public ResultInteractorImpl(ScanService scanService, UserManager userManager, BriteDatabase database) {
        this.scanService = scanService;
        this.userManager = userManager;
        this.database = database;
        this.engine = QualixRuleEngine.getInstance();
    }

    @Override
    public Observable<List<ResultItem>> fetchResultsFromDb(String query) {
        return database.createQuery(ResultTable.TABLE_NAME, query, userManager.getUserId())
                .map(ResultParser::parse);
    }

    @Override
    public Observable<ResultItem> fetchResultFromDb(String query, String params) {
        return database.createQuery(ResultTable.TABLE_NAME, query, userManager.getUserId(), params)
                .map(ResultParser::result);
    }

    @Override
    public Single<DataFactorPayload> fetchDataFactorFromNetwork(SampleItem sample, CommodityItem commodity,
                                                                String serialNumber) {
        Map<String, Object> query = new HashMap<>();
        query.put("serialNo", serialNumber);
        query.put("cropId", commodity.getId());

        return scanService.dataFactor(query)
                .map(response -> {
                    DataFactorPayload payload = ResultParser.factor(response);
                    storeDataFactor(sample, commodity, serialNumber, payload);
                    return payload;
                });
    }

    @Override
    public Observable<DataFactorPayload> fetchDataFactorFromDb(SampleItem sample, CommodityItem commodity,
                                                               String serialNumber) {
        DataFactorPayload payload = new DataFactorPayload();
        return database.createQuery(DeviceTable.TABLE_NAME,
                DeviceTable.QUERY_SELECT_SCALE_FACTOR, userManager.getUserId(),
                commodity.getId(), serialNumber)
                .map(DeviceParser::parse)
                .flatMap(scales -> {
                    payload.setScaleFactor(scales);
                    return database.createQuery(AnalysisTable.TABLE_NAME,
                            AnalysisTable.QUERY_SELECT_ANALYSIS, userManager.getUserId(), commodity.getId());
                })
                .map(ResultParser::analysis)
                .map(analyses -> {
                    payload.setAnalysisList(analyses);
                    return payload;
                });
    }

    @Override
    public void storeDataFactor(SampleItem sample, CommodityItem commodity, String serialNumber,
                                DataFactorPayload payload) {
        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            String scaleFactor = new Gson().toJson(payload.getScaleFactor());

            ContentValues values = new DeviceTable.Builder()
                    .userId(userManager.getUserId())
                    .commodityId(commodity.getId())
                    .serialNumber(serialNumber)
                    .scaleFactor(scaleFactor)
                    .build();

            database.insert(DeviceTable.TABLE_NAME, CONFLICT_REPLACE, values);

            for (AnalysisPayload analysis : payload.getAnalysisList()) {
                String betaMatrix = new Gson().toJson(analysis.getBetaMatrix());
                String meanMatrix = new Gson().toJson(analysis.getMeanMatrix());

                ContentValues contentValues = new AnalysisTable.Builder()
                        .userId(userManager.getUserId())
                        .commodityId(analysis.getCropId())
                        .analysisName(analysis.getName())
                        .betaMatrix(betaMatrix)
                        .meanMatrix(meanMatrix)
                        .algorithm(analysis.getAlgorithm())
                        .algoConfig(analysis.getAlgoConfig())
                        .build();

                database.insert(AnalysisTable.TABLE_NAME, CONFLICT_REPLACE, contentValues);
            }

            transaction.markSuccessful();
        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public Single<List<AnalysisItem>> uploadChemicalSpectra(String batchId, CommodityItem commodity,
                                                            SampleItem sample, String filePath) {
        String hardcodedResult = userManager.getResultHardcode();

        if (!TextUtils.isEmpty(hardcodedResult)) {
            List<AnalysisItem> chemicalAnalyses = new Gson().fromJson(hardcodedResult,
                    new TypeToken<List<AnalysisItem>>() {
                    }.getType());
            return Single.just(chemicalAnalyses)
                    .map(analyses -> {
//                        calculateQualixPricing(commodity, sample, analyses);
                        updateScanResultInDb(batchId, commodity, filePath, analyses);
                        return analyses;
                    });
        } else {
            MediaType type = MediaType.parse("multipart/form-data");

            File file = new File(filePath);
            String fileName = file.getName();
            MultipartBody request = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(type, file))
                    .build();

            return scanService.uploadChemicalSpectra(request)
                    .map(response -> {
                        List<AnalysisItem> analyses = ResultParser.result(response);
//                        calculateQualixPricing(commodity, sample, analyses);
                        updateScanResultInDb(batchId, commodity, filePath, analyses);
                        return analyses;
                    });
        }
    }

    private void calculateQualixPricing(CommodityItem commodity, SampleItem sample, List<AnalysisItem> analyses) {
        try {
            QualixEngineCache.buildRulesCache(userManager.getQualixRules());
            Map<String, Double> params = new HashMap<>();
            for (AnalysisItem analysis : analyses) {
                params.put(analysis.getName(), Double.parseDouble(analysis.getTotalAmount()));
            }
            String crop = commodity.getName().toUpperCase();
            RuleResponse rule = engine.getPrice(crop, params, sample.getQuantity());
            commodity.setTotalAmount(String.format(Locale.getDefault(), "%.2f", rule.getAmount()));
            commodity.setAmountUnit(rule.getCurrency());
        } catch (Exception e) {
            commodity.setTotalAmount("-");
            Timber.e(e);
        }
    }

    @Override
    public Single<String> uploadScanResult(String farmerCode, String batchId, String location,
                                           SampleItem sample, CommodityItem commodity,
                                           String serialNumber, String filePath,
                                           List<AnalysisItem> analyses) {

        UploadResultRequest data = new UploadResultRequest.Builder()
                .setBatchId(batchId)
                .setLotId(sample.getLotId())
                .setSampleId(sample.getId())
                .setCommodityId(commodity.getId())
                .setCommodityName(commodity.getName())
                .setScanByUserCode(userManager.getUserId())
                .setLocation(location)
                .setDeviceSerialNo(serialNumber)
                .setQuantity(sample.getQuantity())
                .setQuantityUnit(sample.getQuantityUnit())
                .setVarietyId(sample.getVariety() != null ? sample.getVariety().getId() : null)
                .setVendorCode(1)
                .setFarmerCode(farmerCode)
                .setDeviceType("Nano")
                .setDeviceTypeId(2)
                .build();

        MultipartBody.Builder request = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("data", new Gson().toJson(data))
                .addFormDataPart("analyses", new Gson().toJson(analyses));

        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            String fileName = file.getName();
            String fileExt = FileUtil.getFileExt(filePath);
            if (!fileExt.equalsIgnoreCase("csv")) {
                fileName = String.format("%s_img.%s", batchId, fileExt);
            }

            request.addFormDataPart("file", fileName, RequestBody.create(MultipartBody.FORM, file));
        }

        return scanService.uploadChemicalResult(request.build())
                .map(response -> {
                    String msg = ResultParser.upload(response);
                    commodity.setUploaded(true);
                    updateScanResultInDb(batchId, commodity, filePath, analyses);
                    return msg;
                });
    }

    @Override
    public Single<String> updateScanResult(String farmerCode, String batchId, String location,
                                           SampleItem sample, CommodityItem commodity,
                                           String serialNumber, List<AnalysisItem> analyses) {

        UploadResultRequest data = new UploadResultRequest.Builder()
                .setBatchId(batchId)
                .setLotId(sample != null ? sample.getLotId() : null)
                .setSampleId(sample != null ? sample.getId() : null)
                .setScanId(sample != null ? sample.getScanId() : null)
                .setCommodityId(commodity.getId())
                .setScanByUserCode(userManager.getUserId())
                .setLocation(location)
                .setDeviceSerialNo(serialNumber)
                .setQuantity(sample != null ? sample.getQuantity() : null)
                .setQuantityUnit(sample != null ? sample.getQuantityUnit() : null)
                .setVarietyId(sample != null ? (sample.getVariety() != null ? sample.getVariety().getId() : null) : null)
                .setVendorCode(1)
                .setFarmerCode(farmerCode)
                .build();

        MultipartBody.Builder request = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("data", new Gson().toJson(data));

        return scanService.updateChemicalResult(request.build())
                .map(response -> {
                    String msg = ResultParser.upload(response);
                    commodity.setUploaded(true);
                    updateScanResultInDb(batchId, commodity, null, analyses);
                    return msg;
                });
    }

    @Override
    public void updateScanResultInDb(String batchId, CommodityItem commodity, String filePath,
                                     List<AnalysisItem> analyses) {
        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            ContentValues values = new ResultTable.Builder()
                    .isUploaded(commodity.isUploaded())
                    .commodity(new Gson().toJson(commodity))
                    .scanResult(new Gson().toJson(analyses))
                    .build();

            database.update(ResultTable.TABLE_NAME, CONFLICT_NONE, values,
                    ResultTable.BATCH_ID + " = ?", batchId);

            transaction.markSuccessful();

            if (commodity.isUploaded()) {
                if (!BuildConfig.DEBUG) {
                    FileUtil.delete(filePath);
                }
            }
        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public void addScanResultInDb(String farmerCode, String batchId, String location, SampleItem sample,
                                  CommodityItem commodity, String avgCsvPath, String serialNumber,
                                  List<AnalysisItem> analyses) {
        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            ContentValues values = new ResultTable.Builder()
                    .userId(userManager.getUserId())
                    .batchId(batchId)
                    .location(location)
                    .sample(new Gson().toJson(sample))
                    .commodity(new Gson().toJson(commodity))
                    .avgCsvPath(avgCsvPath)
                    .serialNumber(serialNumber)
//                  .scanResult(new Gson().toJson(analyses))
                    .datetime(Util.getDatetime())
                    .farmerCode(farmerCode)
                    .build();

            long id = database.insert(ResultTable.TABLE_NAME, CONFLICT_IGNORE, values);

            if (id == -1) {
                database.update(ResultTable.TABLE_NAME, CONFLICT_NONE, values,
                        ResultTable.BATCH_ID + " = ?", batchId);
            }

            transaction.markSuccessful();
        } catch (SQLiteException | NullPointerException e) {
            Timber.e(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public Observable<List<ResultItem>> searchResultInDb(String query) {
        return database.createQuery(ResultTable.TABLE_NAME,
                ResultTable.QUERY_SEARCH_RESULT, userManager.getUserId(), query, query, query)
                .map(ResultParser::search);
    }
}