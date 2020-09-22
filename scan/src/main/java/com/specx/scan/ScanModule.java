package com.specx.scan;

import com.specx.scan.network.ScanService;
import com.specx.scan.ui.result.base.ResultInteractor;
import com.specx.scan.ui.result.base.ResultInteractorImpl;
import com.specx.scan.ui.result.base.ResultPresenter;
import com.specx.scan.ui.result.base.ResultPresenterImpl;
import com.specx.scan.ui.result.search.SearchResultPresenter;
import com.specx.scan.ui.result.search.SearchResultPresenterImpl;
import com.squareup.sqlbrite3.BriteDatabase;
import com.user.app.data.UserManager;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ScanModule {

    @Provides
    ScanService provideScanService(Retrofit retrofit) {
        return retrofit.create(ScanService.class);
    }

    @Provides
    ResultInteractor provideResultInteractor(ScanService scanService, UserManager userManager,
                                             BriteDatabase database) {
        return new ResultInteractorImpl(scanService, userManager, database);
    }

    @Provides
    ResultPresenter provideResultPresenter(ResultInteractor interactor) {
        return new ResultPresenterImpl(interactor);
    }

    @Provides
    SearchResultPresenter provideSearchResultPresenter(ResultInteractor interactor) {
        return new SearchResultPresenterImpl(interactor);
    }
}