package com.specx.scan.ui.result.base;

import com.base.app.ui.base.BasePresenter;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;

import java.util.List;

public abstract class ResultPresenter extends BasePresenter<ResultView> {

    abstract void fetchResultFromDb(String batchId);

    abstract void fetchDataFactor(SampleItem sample, CommodityItem commodity, String serialNumber);

    abstract void uploadChemicalSpectra(String batchId, CommodityItem commodity, SampleItem sample,
                                        String filePath);

    abstract void uploadScanResult(String farmerCode, String batchId, String location,
                                   SampleItem sample, CommodityItem commodity, String serialNumber,
                                   String filePath, List<AnalysisItem> analyses);

    abstract void updateScanResult(String farmerCode, String batchId, String location,
                                   SampleItem sample, CommodityItem commodity, String serialNumber,
                                   List<AnalysisItem> analyses);

    abstract void addScanResultInDb(String farmerCode, String batchId, String location,
                                    SampleItem sample, CommodityItem commodity, String avgCsvPath,
                                    String serialNumber, List<AnalysisItem> analyses);
}