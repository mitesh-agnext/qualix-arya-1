package com.specx.scan.ui.result.base;

import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.factor.DataFactorPayload;
import com.specx.scan.data.model.result.ResultItem;
import com.specx.scan.data.model.sample.SampleItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ResultInteractor {

    Observable<List<ResultItem>> fetchResultsFromDb(String query);

    Observable<ResultItem> fetchResultFromDb(String query, String params);

    Single<DataFactorPayload> fetchDataFactorFromNetwork(SampleItem sample, CommodityItem commodity,
                                                         String serialNumber);

    Observable<DataFactorPayload> fetchDataFactorFromDb(SampleItem sample, CommodityItem commodity,
                                                        String serialNumber);

    void storeDataFactor(SampleItem sample, CommodityItem commodity, String serialNumber,
                         DataFactorPayload payload);

    Single<List<AnalysisItem>> uploadChemicalSpectra(String batchId, CommodityItem commodity,
                                                     SampleItem sample, String filePath);

    Single<String> uploadScanResult(String farmerCode, String batchId, String location,
                                        SampleItem sample, CommodityItem commodity, String serialNumber,
                                        String filePath, List<AnalysisItem> analyses);

    Single<String> updateScanResult(String farmerCode, String batchId, String location, SampleItem sample,
                                    CommodityItem commodity, String serialNumber, List<AnalysisItem> analyses);

    void updateScanResultInDb(String batchId, CommodityItem commodity, String filePath,
                                  List<AnalysisItem> analyses);

    void addScanResultInDb(String farmerCode, String batchId, String location, SampleItem sample,
                               CommodityItem commodity, String avgCsvPath, String serialNumber,
                               List<AnalysisItem> analyses);

    Observable<List<ResultItem>> searchResultInDb(String query);

}