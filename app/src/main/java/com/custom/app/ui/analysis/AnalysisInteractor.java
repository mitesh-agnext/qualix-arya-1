package com.custom.app.ui.analysis;

import com.specx.scan.data.model.analysis.AnalysisItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface AnalysisInteractor {

    Observable<List<AnalysisItem>> fetchAnalysesFromDb(String batchId);

    Single<String> removeAnalyses(String batchId);

    void removeAnalysesFromDb(String batchId);

}