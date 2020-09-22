package com.custom.app.ui.analysis;

import com.custom.app.network.RestService;
import com.data.app.db.table.ResultTable;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.BriteDatabase.Transaction;
import com.user.app.data.UserManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public class AnalysisInteractorImpl implements AnalysisInteractor {

    private RestService restService;
    private UserManager userManager;
    private BriteDatabase database;

    public AnalysisInteractorImpl(RestService restService, UserManager userManager,
                                  BriteDatabase database) {
        this.restService = restService;
        this.userManager = userManager;
        this.database = database;
    }

    @Override
    public Observable<List<AnalysisItem>> fetchAnalysesFromDb(String batchId) {
        return database.createQuery(ResultTable.TABLE_NAME,
                ResultTable.QUERY_BATCH_RESULT, userManager.getUserId(), batchId)
                .map(AnalysisParser::parse);
    }

    @Override
    public Single<String> removeAnalyses(String batchId) {
        return restService.removeAnalyses(batchId)
                .map(AnalysisParser::remove);
    }

    @Override
    public void removeAnalysesFromDb(String batchId) {
        Transaction transaction = database.newTransaction();
        try {
            database.delete(ResultTable.TABLE_NAME, ResultTable.BATCH_ID + " = ?", batchId);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}