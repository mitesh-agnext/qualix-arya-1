package com.specx.scan.ui.result.base;

import com.data.app.db.table.ResultTable;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.commodity.CommodityItem;
import com.specx.scan.data.model.sample.SampleItem;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResultPresenterImpl extends ResultPresenter {

    private ResultView view;
    private ResultInteractor interactor;

    public ResultPresenterImpl(ResultInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(ResultView view) {
        super.setView(view);
        this.view = view;
    }

    @Override
    void fetchResultFromDb(String batchId) {
        disposable = interactor.fetchResultFromDb(ResultTable.QUERY_BATCH_RESULT, batchId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (isViewAttached()) {
                        view.showResult(result);
                    }
                }, error -> showMessage(error.getMessage()));
    }

    @Override
    void fetchDataFactor(SampleItem sample, CommodityItem commodity, String serialNumber) {
        disposable = interactor.fetchDataFactorFromNetwork(sample, commodity, serialNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(payload -> {
                    if (isViewAttached()) {
                        view.onDataFactorLoaded(payload);
                    }
                }, error -> {
                    disposable = interactor.fetchDataFactorFromDb(sample, commodity, serialNumber)
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(payload -> {
                                if (isViewAttached()) {
                                    view.onDataFactorLoaded(payload);
                                }
                            }, e -> {
                                showMessage(e.getMessage());
                                close();
                            });
                });
    }

    @Override
    void uploadChemicalSpectra(String batchId, CommodityItem commodity, SampleItem sample, String filePath) {
        disposable = interactor.uploadChemicalSpectra(batchId, commodity, sample, filePath)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(analyses -> {
                    if (isViewAttached()) {
                        view.showAnalyses(false, analyses);
                        view.uploadScanResult(filePath);

                        for (AnalysisItem analysis : analyses) {
                            if (analysis.getTotalAmount().startsWith("-")) {
                                showMessage("An error occurred while processing the results, please try again!");
                                return;
                            }
                        }
                    }
                }, error -> {
                    showMessage(error.getMessage());
                    if (isViewAttached()) {
                        view.onUploadScanFailure();
                    }
                });
    }

    @Override
    public void uploadScanResult(String farmerCode, String batchId, String location, SampleItem sample,
                                 CommodityItem commodity, String serialNumber, String filePath,
                                 List<AnalysisItem> analyses) {
        disposable = interactor.uploadScanResult(farmerCode, batchId, location, sample, commodity,
                serialNumber, filePath, analyses)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(msg -> {
                    showMessage(msg);
                    if (isViewAttached()) {
                        view.onUploadScanSuccess(filePath);
                    }
                }, error -> {
                    showMessage(error.getMessage());
                    if (isViewAttached()) {
                        view.onUploadScanFailure();
                    }
                });
    }

    @Override
    void updateScanResult(String farmerCode, String batchId, String location, SampleItem sample,
                          CommodityItem commodity, String serialNumber, List<AnalysisItem> analyses) {
        disposable = interactor.updateScanResult(farmerCode, batchId, location, sample, commodity, serialNumber, analyses)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(msg -> {
                    showMessage(msg);
                    if (isViewAttached()) {
                        view.onUploadScanSuccess();
                    }
                }, error -> {
                    showMessage(error.getMessage());
                    if (isViewAttached()) {
                        view.onUploadScanFailure();
                    }
                });
    }

    @Override
    void addScanResultInDb(String farmerCode, String batchId, String location, SampleItem sample,
                           CommodityItem commodity, String avgCsvPath, String serialNumber,
                           List<AnalysisItem> analyses) {
        interactor.addScanResultInDb(farmerCode, batchId, location, sample, commodity, avgCsvPath, serialNumber, analyses);
    }
}