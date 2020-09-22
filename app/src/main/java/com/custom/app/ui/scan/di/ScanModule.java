package com.custom.app.ui.scan.di;

import com.custom.app.network.ApiInterface;
import com.custom.app.network.RestService;
import com.custom.app.ui.analysis.AnalysisInteractor;
import com.custom.app.ui.analysis.AnalysisInteractorImpl;
import com.custom.app.ui.analysis.AnalysisPresenter;
import com.custom.app.ui.analysis.AnalysisPresenterImpl;
import com.custom.app.ui.commodity.CommodityInteractor;
import com.custom.app.ui.commodity.CommodityInteractorImpl;
import com.custom.app.ui.commodity.CommodityPresenter;
import com.custom.app.ui.commodity.CommodityPresenterImpl;
import com.custom.app.ui.sample.SamplePresenter;
import com.custom.app.ui.sample.SamplePresenterImpl;
import com.custom.app.ui.scan.list.history.ScanHistoryInteractor;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ScanModule {

    @Provides
    CommodityInteractor provideCommodityInteractor(RestService restService, UserManager userManager,
                                                   BriteDatabase database) {
        return new CommodityInteractorImpl(restService, userManager, database);
    }

    @Provides
    CommodityPresenter provideCommodityPresenter(CommodityInteractor interactor) {
        return new CommodityPresenterImpl(interactor);
    }

    @Provides
    SamplePresenter provideSamplePresenter(AnalysisInteractor analysisInteractor,
                                           CommodityInteractor commodityInteractor) {
        return new SamplePresenterImpl(analysisInteractor, commodityInteractor);
    }

    @Provides
    AnalysisInteractor provideAnalysisInteractor(RestService restService, UserManager userManager,
                                                 BriteDatabase database) {
        return new AnalysisInteractorImpl(restService, userManager, database);
    }

    @Provides
    AnalysisPresenter provideAnalysisPresenter(AnalysisInteractor interactor) {
        return new AnalysisPresenterImpl(interactor);
    }

    @Provides
    ScanHistoryInteractor provideScanHistoryInteractor(UserManager userManager, ApiInterface apiService) {
        return new ScanHistoryInteractor(userManager, apiService);
    }
}