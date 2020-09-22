package com.specx.scan.ui.result.base;

import com.base.app.ui.base.BaseView;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.result.ResultItem;

import java.util.List;

public interface ResultView extends BaseView {

    void updateScanResult();

    void uploadScanResult(String filePath);

    void uploadChemicalSpectra(String filePath);

    void onUploadScanSuccess(String... filePath);

    void onUploadScanFailure();

    void showAnalyses(boolean showPrice, List<AnalysisItem> analyses);

    void storeScanResult(String serialNumber, String avgCsvPath);

    void onDataFactorLoaded(DataFactorPayload payload);

    void fetchResultFromDb(String batchId);

    void showResult(ResultItem result);

}